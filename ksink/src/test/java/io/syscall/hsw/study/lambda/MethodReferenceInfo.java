package io.syscall.hsw.study.lambda;

import jakarta.annotation.Nullable;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Objects;

public final class MethodReferenceInfo {

    private final RefKind kind;
    private final Method method;

    private final @Nullable Object receiver;

    public MethodReferenceInfo(RefKind kind, Method method, @Nullable Object receiver) {
        this.kind = kind;
        this.method = method;
        this.receiver = receiver;
    }

    public RefKind getKind() {
        return kind;
    }

    public Method getMethod() {
        return method;
    }

    @Nullable
    public Object getReceiver() {
        return receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (MethodReferenceInfo) o;
        return Objects.equals(this.method, that.receiver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, receiver);
    }

    @Override
    public String toString() {
        if (kind == RefKind.LAMBDA) {
            if (method.isSynthetic()) {
                return "Lambda: " + prettyMethod(method);
            }
            return "Lambda: " + prettyMethod(method);
        }
        if (kind == RefKind.BOUND_METHOD) {
            var receiverClassName = receiver.getClass().getName();
            return "Bound method: " + prettyMethod(method).replace(receiverClassName + ".", "") + " to " + receiver;
        }

        return prettyMethod(method);
    }

    private static String prettyMethod(Method method) {
        if (method.isSynthetic()) {
            return MethodType.methodType(method.getReturnType(), method.getParameterTypes()).toString();
        }
        return method.toGenericString().replaceAll("java\\.(lang|util\\.function)\\.", "");
    }
}
