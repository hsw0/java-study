package io.syscall.hsw.study.apiserver.example.model

public fun interface EntityIdFactory<T, E : EntityId<T>> {
    public fun create(value: T): E
}
