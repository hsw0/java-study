package io.syscall.hsw.study.apiserver.infra.reactor.netty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.result.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

@Component
public class SimpleRequestMappingPatternResolver {

    private static final Logger log = LoggerFactory.getLogger(SimpleRequestMappingPatternResolver.class);

    private final Map<HttpMethod, Patterns> methodPatterns = new HashMap<>();

    private final Patterns allPatterns = Patterns.create();

    @Nullable
    public String resolve(String requestPath, @Nullable String method) {
        @Nullable String resolved = null;
        if (method != null) {
            var forMethod = methodPatterns.get(HttpMethod.valueOf(method));
            if (forMethod != null) {
                resolved = forMethod.resolve(requestPath);
            }
        }

        if (resolved == null) {
            return allPatterns.resolve(requestPath);
        }

        return resolved;
    }

    record Patterns(Set<String> directPathMappings, SortedSet<PathPattern> pathPatterns) {

        static Patterns create() {
            // NOTE: NO Interior Immutability
            return new Patterns(new HashSet<>(), new TreeSet<>());
        }

        @Nullable
        public String resolve(String requestPath) {
            if (directPathMappings.contains(requestPath)) {
                return requestPath;
            }
            var pathContainer = RequestPath.parse(requestPath, null);
            for (var pattern : pathPatterns) {
                if (pattern.matches(pathContainer)) {
                    return pattern.getPatternString();
                }
            }

            return null;
        }
    }

    @Autowired
    public void setHandlerMapping(ObjectProvider<RequestMappingInfoHandlerMapping> handlerMappingBeans) {
        for (var bean : (Iterable<RequestMappingInfoHandlerMapping>) handlerMappingBeans.orderedStream()::iterator) {
            for (var entry : bean.getHandlerMethods().entrySet()) {
                var info = entry.getKey();
                if (log.isDebugEnabled()) {
                    var entries = new LinkedHashSet<String>();
                    entries.addAll(info.getDirectPaths());
                    entries.addAll(info.getPatternsCondition().getPatterns().stream()
                            .map(PathPattern::getPatternString)
                            .toList());

                    log.debug(
                            "{} -> \"{} {}\"",
                            entry.getValue(),
                            info.getMethodsCondition().getMethods(),
                            entries);
                }

                for (var requestMethod : info.getMethodsCondition().getMethods()) {
                    var forMethod =
                            methodPatterns.computeIfAbsent(requestMethod.asHttpMethod(), ignored -> Patterns.create());
                    forMethod.directPathMappings().addAll(info.getDirectPaths());
                    forMethod.pathPatterns().addAll(info.getPatternsCondition().getPatterns());
                }

                allPatterns.directPathMappings().addAll(info.getDirectPaths());
                allPatterns.pathPatterns().addAll(info.getPatternsCondition().getPatterns());
            }
        }
    }
}
