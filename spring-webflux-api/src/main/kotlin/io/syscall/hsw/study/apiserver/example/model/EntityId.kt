package io.syscall.hsw.study.apiserver.example.model

public interface EntityId<out T> {
    public val value: T
}
