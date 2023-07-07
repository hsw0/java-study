package io.syscall.commons.entityid

public interface EntityId<out T> {
    public val value: T

    public fun asString(): String = value.toString()
}
