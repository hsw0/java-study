package io.syscall.commons.entityid

public fun interface FromStringEntityIdFactory<T, E : EntityId<T>> {

    public fun create(value: String): E
}
