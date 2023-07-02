package io.syscall.gradle.plugin.mapstruct.dsl

import org.gradle.api.provider.Property

/**
 * @see <a href="https://mapstruct.org/documentation/stable/reference/html/#configuration-options">Table 1. MapStruct processor options</a>
 */
interface MapStructExtension {
    val version: Property<String>

    val verbose: Property<Boolean>
    val defaultComponentModel: Property<String>
    val unmappedTargetPolicy: Property<String>
    val suppressGeneratorTimestamp: Property<Boolean>
}
