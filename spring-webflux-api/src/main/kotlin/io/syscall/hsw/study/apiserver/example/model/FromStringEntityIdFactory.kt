package io.syscall.hsw.study.apiserver.example.model

public fun interface FromStringEntityIdFactory<T, E : EntityId<T>> {

    public fun create(value: String): E
}
