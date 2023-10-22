package io.syscall.hsw.study.lambda;

import jakarta.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleInfo;
import java.util.Objects;

public final class MethodReferenceInfo {

    private final RefKind kind;
    private final MethodHandleInfo mhi;
    private final MethodHandle mh;

    private final @Nullable Object receiver;

    public MethodReferenceInfo(RefKind kind, @Nullable Object receiver, MethodHandleInfo mhi, MethodHandle mh) {
        this.kind = kind;
        this.receiver = receiver;
        this.mhi = mhi;
        this.mh = mh;
    }

    public RefKind getKind() {
        return kind;
    }

    @Nullable
    public Object getReceiver() {
        return receiver;
    }

    public MethodHandleInfo getMethodHandleInfo() {
        return mhi;
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
        return Objects.equals(this.mhi, that.receiver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mhi, receiver);
    }

    @Override
    public String toString() {
        if (kind == RefKind.LAMBDA) {
            return "Lambda: " + prettyMethod();
        }
        if (kind == RefKind.BOUND_METHOD) {
            var receiverClassName = receiver.getClass().getName();
            return "Bound method: " + prettyMethod().replace(receiverClassName + ".", "") + " to " + receiver;
        }

        return prettyMethod();
    }

    private String prettyMethod() {
        return mhi.getDeclaringClass().getSimpleName()
                + "::"
                + mhi.getName()
                + mhi.getMethodType().toString().replace(")", "): ");
    }
}
