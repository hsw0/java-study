package io.syscall.commons.entityid

@Suppress("ConvertSecondaryConstructorToPrimary")
public class UnsupportedEntityIdImplementationException : UnsupportedOperationException {

    public constructor(message: String, cause: Throwable) : super(message, cause)
}
