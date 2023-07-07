package io.syscall.commons.entityid

import java.util.concurrent.ConcurrentHashMap

/**
 * @param E Corresponding [EntityId]
 * @param T [E]'s value
 * @param ctor [E]'s Constructor
 */
public class WellKnownValueSupport<T, E : EntityId<T>>(
    private val ctor: (T) -> E,
) {

    private val nameToInstance: MutableMap<String, E> = ConcurrentHashMap()
    private val instanceToName: MutableMap<E, String> = ConcurrentHashMap()

    public fun declare(name: String, value: T): E {
        require(name.isNotBlank())

        val added = nameToInstance.compute(name) { key, existingValue ->
            require(existingValue == null) {
                "${key}: Already mapped to ${existingValue!!.value}"
            }
            return@compute ctor(value)
        }!!
        instanceToName.compute(added) { me, existingName ->
            require(existingName == null) {
                "${name}: Duplicate value ${me.value} under ${existingName!!}"
            }
            return@compute name
        }
        return added
    }

    public fun internIfExists(instance: E): E {
        val name = this[instance]
        if (name != null) {
            return this[name]!!
        }
        return instance
    }

    public operator fun get(name: String): E? = nameToInstance[name]

    public operator fun get(instance: E): String? = instanceToName[instance]

}
