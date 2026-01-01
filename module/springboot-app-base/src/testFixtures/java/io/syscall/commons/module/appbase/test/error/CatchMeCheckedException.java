package io.syscall.commons.module.appbase.test.error;

public class CatchMeCheckedException extends Throwable {

    public CatchMeCheckedException() {
        super("Did you?");
    }
}
