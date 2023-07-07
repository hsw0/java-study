package io.syscall.commons.entityid

import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

public fun interface EntityIdFactory<T, E : EntityId<T>> {
    public fun create(value: T): E

    public companion object {
        internal fun <T, E : EntityId<T>> initializeConstructor(type: KClass<E>, dummyValue: T): KFunction<E> {
            runCatching { require(type.isSubclassOf(EntityId::class)) }.onFailure {
                throw UnsupportedEntityIdImplementationException("Unsupported EntityId type: $type", it)
            }

            if (type.companionObjectInstance is EntityIdFactory<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val factory = type.companionObjectInstance as EntityIdFactory<T, E>
                return factory::create
            }

            val ctor: KFunction<E>
            try {
                ctor = type.primaryConstructor!!
            } catch (e: Exception) {
                throw UnsupportedEntityIdImplementationException("Unsupported EntityId implementation: $type", e)
            }

            try {
                ctor.call(dummyValue)
            } catch (e: Throwable) {
                if (e !is InvocationTargetException || e.targetException !is RuntimeException) {
                    throw UnsupportedEntityIdImplementationException("Unsupported EntityId implementation: $type", e)
                }
                // ignored
            }
            return ctor
        }

        internal fun <T, E : EntityId<T>> fromStringConstructor(type: KClass<E>): KFunction<E>? {
            if (type.companionObjectInstance !is FromStringEntityIdFactory<*, *>) {
                return null
            }

            @Suppress("UNCHECKED_CAST")
            val factory = type.companionObjectInstance as FromStringEntityIdFactory<T, E>
            return factory::create
        }

        internal fun <T, E : EntityId<T>> invokeConstructor(ctor: KFunction<E>, value: T): E {
            try {
                return ctor.call(value)
            } catch (e: InvocationTargetException) {
                if (e.targetException is RuntimeException || e.targetException is Error) {
                    throw e.targetException
                }
                throw e
            }
        }
    }
}
