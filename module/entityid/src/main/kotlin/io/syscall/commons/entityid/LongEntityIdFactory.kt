package io.syscall.commons.entityid

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

public fun interface LongEntityIdFactory<E : LongEntityId> : EntityIdFactory<Long, E> {
}

public object LongEntityIdSupport {

    public fun <E : LongEntityId> LongEntityId.Companion.factory(type: KClass<E>): LongEntityIdFactory<E> {
        val ctor: KFunction<E> = EntityIdFactory.initializeConstructor(type, 1L)
        val fromStringCtor: KFunction<E>? = EntityIdFactory.fromStringConstructor(type)

        if (fromStringCtor != null) {
            return object : LongEntityIdFactoryWithString<E> {
                override fun create(value: Long): E {
                    return EntityIdFactory.invokeConstructor(ctor, value)
                }

                override fun create(value: String): E {
                    return EntityIdFactory.invokeConstructor(fromStringCtor, value)
                }
            }
        }

        return LongEntityIdFactory { it: Long ->
            EntityIdFactory.invokeConstructor(ctor, it)
        }
    }

    public inline fun <reified E : LongEntityId> LongEntityId.Companion.factory(): LongEntityIdFactory<E> =
        factory(E::class)

    @JvmStatic
    public fun <E : LongEntityId> factory(type: Class<E>): LongEntityIdFactory<E> = LongEntityId.factory(type.kotlin)

}
