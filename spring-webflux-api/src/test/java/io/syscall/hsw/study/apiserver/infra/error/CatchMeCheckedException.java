package io.syscall.hsw.study.apiserver.infra.error;

public class CatchMeCheckedException extends Throwable {

    public CatchMeCheckedException() {
        super("Did you?");
    }
}
