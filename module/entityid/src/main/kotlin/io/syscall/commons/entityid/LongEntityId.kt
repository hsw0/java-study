package io.syscall.commons.entityid

import io.syscall.commons.entityid.LongEntityId.LongEntityIdFactory
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

public interface LongEntityId : EntityId<Long> {

    public fun interface LongEntityIdFactory<E : LongEntityId> : EntityIdFactory<Long, E>

    public interface LongEntityIdFactoryWithString<E : LongEntityId> :
        LongEntityIdFactory<E>,
        FromStringEntityIdFactory<Long, E>

    public companion object {

        @JvmStatic
        public fun <E : LongEntityId> factory(type: KClass<E>): LongEntityIdFactory<E> {
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

        @JvmStatic
        public fun <E : LongEntityId> factory(type: Class<E>): LongEntityIdFactory<E> {
            return factory(type.kotlin)
        }

        public inline fun <reified E : LongEntityId> factory(): LongEntityIdFactory<E> {
            return factory(E::class)
        }
    }
}
