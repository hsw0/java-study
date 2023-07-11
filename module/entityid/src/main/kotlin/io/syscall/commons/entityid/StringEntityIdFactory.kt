package io.syscall.commons.entityid

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

public fun interface StringEntityIdFactory<E : StringEntityId> : EntityIdFactory<String, E>

public object StringEntityIdSupport {

    public fun <E : StringEntityId> StringEntityId.Companion.factory(type: KClass<E>): StringEntityIdFactory<E> {
        val ctor: KFunction<E> = EntityIdFactory.initializeConstructor(type, "dummy")
        return StringEntityIdFactory {
            EntityIdFactory.invokeConstructor(ctor, it)
        }
    }

    public inline fun <reified E : StringEntityId> StringEntityId.Companion.factory(): StringEntityIdFactory<E> =
        factory(E::class)

    @JvmStatic
    public fun <E : StringEntityId> factory(type: Class<E>): StringEntityIdFactory<E> =
        StringEntityId.factory(type.kotlin)

}
