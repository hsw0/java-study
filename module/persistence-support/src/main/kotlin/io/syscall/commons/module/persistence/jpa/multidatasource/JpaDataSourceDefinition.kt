package io.syscall.commons.module.persistence.jpa.multidatasource

/**
 * Configuration holder for a single datasource's JPA setup.
 *
 * @property name unique identifier for this datasource (e.g., "primary", "secondary")
 * @property dataSourceBeanName the bean name of the DataSource to use
 * @property entityPackages packages to scan for JPA entities
 * @property entityClasses explicit entity classes (alternative to package scanning)
 * @property jpaProperties additional JPA properties (spring.jpa.properties.*)
 */
public data class JpaDataSourceDefinition(
    val name: String,
    val dataSourceBeanName: String,
    val entityPackages: List<String> = emptyList(),
    val entityClasses: List<Class<*>> = emptyList(),
    val jpaProperties: Map<String, String> = emptyMap(),
) {
    init {
        require(name.isNotBlank()) { "name must not be blank" }
        require(dataSourceBeanName.isNotBlank()) { "dataSourceBeanName must not be blank" }
    }

    /** Bean name for JpaProperties. */
    public val jpaPropertiesBeanName: String get() = "${name}JpaProperties"

    /** Bean name for HibernateProperties. */
    public val hibernatePropertiesBeanName: String get() = "${name}HibernateProperties"

    /** Bean name for HibernateJpaConfiguration. */
    public val hibernateJpaConfigurationBeanName: String get() = "${name}HibernateJpaConfiguration"

    /** Bean name for EntityManagerFactory. */
    public val entityManagerFactoryBeanName: String get() = "${name}EntityManagerFactory"

    /** Bean name for TransactionManager. */
    public val transactionManagerBeanName: String get() = "${name}TransactionManager"

    /** Bean name for PersistenceManagedTypes. */
    public val persistenceManagedTypesBeanName: String get() = "${name}PersistenceManagedTypes"

    /** Bean name for EntityManagerFactoryBuilder. */
    public val entityManagerFactoryBuilderBeanName: String get() = "${name}EntityManagerFactoryBuilder"

    /** Bean name for JpaVendorAdapter. */
    public val jpaVendorAdapterBeanName: String get() = "${name}JpaVendorAdapter"

    public companion object {
        /**
         * Creates a builder for Java interop.
         */
        @JvmStatic
        public fun builder(): JpaDataSourceDefinitionBuilder = JpaDataSourceDefinitionBuilder()
    }
}

/**
 * DSL builder for [JpaDataSourceDefinition].
 */
@JpaDataSourceDefinitionDsl
public class JpaDataSourceDefinitionBuilder
    @PublishedApi
    internal constructor() {
        public var name: String? = null
        public var dataSourceBeanName: String? = null
        private val entityPackages: MutableList<String> = mutableListOf()
        private val entityClasses: MutableList<Class<*>> = mutableListOf()

        /** Mutable map for JPA properties. Use `jpaProperties[key] = value` to add properties. */
        public val jpaProperties: MutableMap<String, String> = mutableMapOf()

        public fun name(name: String): JpaDataSourceDefinitionBuilder = apply { this.name = name }

        public fun dataSourceBeanName(beanName: String): JpaDataSourceDefinitionBuilder =
            apply {
                this.dataSourceBeanName =
                    beanName
            }

        public fun entityPackage(packageName: String): JpaDataSourceDefinitionBuilder =
            apply {
                entityPackages.add(packageName)
            }

        public fun entityPackages(vararg packages: String): JpaDataSourceDefinitionBuilder =
            apply {
                entityPackages.addAll(packages)
            }

        public fun entityPackages(packages: List<String>): JpaDataSourceDefinitionBuilder =
            apply {
                entityPackages.addAll(packages)
            }

        public fun entityClass(clazz: Class<*>): JpaDataSourceDefinitionBuilder = apply { entityClasses.add(clazz) }

        public fun entityClasses(vararg classes: Class<*>): JpaDataSourceDefinitionBuilder =
            apply {
                entityClasses.addAll(classes)
            }

        public fun entityClasses(classes: List<Class<*>>): JpaDataSourceDefinitionBuilder =
            apply {
                entityClasses.addAll(classes)
            }

        /** Adds a JPA property. Alternative to `jpaProperties[key] = value`. */
        public fun jpaProperty(
            key: String,
            value: String,
        ): JpaDataSourceDefinitionBuilder =
            apply {
                jpaProperties[key] = value
            }

        /** Adds multiple JPA properties. */
        public fun jpaProperties(properties: Map<String, String>): JpaDataSourceDefinitionBuilder =
            apply { jpaProperties.putAll(properties) }

        public fun build(): JpaDataSourceDefinition {
            val name = checkNotNull(name) { "name is required" }
            val dataSourceBeanName = checkNotNull(dataSourceBeanName) { "dataSourceBeanName is required" }

            return JpaDataSourceDefinition(
                name = name,
                dataSourceBeanName = dataSourceBeanName,
                entityPackages = entityPackages.toList(),
                entityClasses = entityClasses.toList(),
                jpaProperties = jpaProperties.toMap(),
            )
        }
    }

@DslMarker
public annotation class JpaDataSourceDefinitionDsl

/**
 * DSL function to create [JpaDataSourceDefinition].
 *
 * Example:
 * ```kotlin
 * jpaDataSourceDefinition {
 *     name = "sample"
 *     dataSourceBeanName = "sampleDataSource"
 *     entityPackage("com.example.entities")
 *     jpaProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
 * }
 * ```
 */
public inline fun jpaDataSourceDefinition(block: JpaDataSourceDefinitionBuilder.() -> Unit): JpaDataSourceDefinition =
    JpaDataSourceDefinitionBuilder().apply(block).build()
