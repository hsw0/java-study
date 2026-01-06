package io.syscall.commons.module.persistence.jpa.multidatasource.testentities.orange;

import io.syscall.commons.module.persistence.jpa.EntityExtensions;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/**
 * Test entity for the orange datasource.
 */
@Entity
@Table(name = "orange_entity")
public class OrangeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id;

    private @Nullable String name;

    public OrangeEntity() {}

    public OrangeEntity(String name) {
        this.name = name;
    }

    public @Nullable Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @Nullable String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (EntityExtensions.getEffectiveClass(this) != EntityExtensions.getEffectiveClass(o)) {
            return false;
        }
        var that = (OrangeEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "OrangeEntity{id=" + id + ", name='" + name + "'}";
    }
}
