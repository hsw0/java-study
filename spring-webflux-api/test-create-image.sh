#!/bin/bash

main() {
  set -eu -o pipefail

  local -r SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

  umask 0022
  export LANG=C
  export LC_COLLATE=C
  export LC_CTYPE=C.UTF-8

  export SOURCE_DATE_EPOCH="${SOURCE_DATE_EPOCH:-0}"

  local TAR_CMD=tar
  [[ "$OSTYPE" == *'darwin'* ]] && TAR_CMD=gtar

  local -r src_jar_path="$SCRIPT_DIR"/build/libs/spring-webflux-api.jar

  local -r APPLICATION_NAME=$(basename "$src_jar_path" .jar)
  local -r TMP_IMAGE_TAG="localhost/build/${APPLICATION_NAME}:latest"

  if [[ ! -r "$src_jar_path" ]]; then
    echo "[-] Source spring boot fat jar not found at $src_jar_path"
    return 1
  fi

  readonly tmpdir=$(mktemp -d -t buildimage-"$APPLICATION_NAME")
  #trap '[[ -d "$tmpdir" ]] && rm -rf "$tmpdir"' EXIT

  layers=( $(java -Djarmode=tools -jar "$src_jar_path" list-layers) )
  for layer in "${layers[@]}"; do
    echo "[*] Extracting: '$layer'..."
    java -Djarmode=tools -jar "$src_jar_path" extract --destination "$tmpdir"/layer --layers "$layer" --force
  done

  ln -s "$(basename "$src_jar_path")" "$tmpdir"/layer/application/application.jar

  if unzip -l "$src_jar_path" 'javaagent/*.jar' ; then
    echo "[*] Extracting: 'javaagent'..."
    mkdir -p "$tmpdir"/layer/javaagent/javaagent
    unzip -n "$src_jar_path" -d "$tmpdir"/layer/javaagent 'javaagent/*.jar' || true
    layers+=(javaagent)
  fi

  find "$tmpdir"/layer -type d -depth 1 -empty -delete

  image_layers=()
  for layer in "${layers[@]}"; do
    [[ ! -d "$tmpdir"/layer/"$layer" ]] && continue
    echo "[*] Adding layer: '$layer'..."

    local layer_tar="$tmpdir/$layer.tar"

    # https://reproducible-builds.org/docs/archives/
    "$TAR_CMD" \
      --sort=name \
      --mtime="@${SOURCE_DATE_EPOCH}" \
      --owner=0 --group=0 --numeric-owner \
      --pax-option=exthdr.name=%d/PaxHeaders/%f,delete=atime,delete=ctime \
      -c -f "$layer_tar" \
      --directory "$tmpdir"/layer/"$layer" \
      --transform "s/^\./\/app/" \
      . \
    ;

    rm -rf "$tmpdir"/layer/"$layer"

    image_layers+=("$layer_tar")
  done

  local -r container_baseimage_ref="docker.io/library/eclipse-temurin:25.0.1_8-jdk@sha256:10331564d9ae41b6a534ddea472f37270a3c286e89857261631a0d772a4d8617"
  #(set -x; crane pull --cache_path "$TMPDIR/crane" --format oci --platform linux/amd64 --platform linux/arm64 "$container_baseimage_ref" "$tmpdir"/container-baseimage)

  new_layer_csv="$(IFS=,; echo "${image_layers[*]}")"
  (set -x; crane append \
    --output "$tmpdir"/image-merged.tar \
    --new_tag "$TMP_IMAGE_TAG" \
    --new_layer "$new_layer_csv" \
    --base "$container_baseimage_ref" \
    --platform linux/amd64 --platform linux/arm64 \
  ;)

  rm -f "${image_layers[@]}"

  # TODO: OCI compatibility check. apple container 에서는 로드안됨
  skopeo copy \
    --format oci \
    docker-archive://"$tmpdir"/image-merged.tar \
    oci-archive://"$tmpdir"/image-final.tar:"$TMP_IMAGE_TAG" \
  ;

  rm -f "$tmpdir"/image-merged.tar

  ls -al "$tmpdir"/image-final.tar
  tar tvf "$tmpdir"/image-final.tar

}


main "$@"
exit $?
