package io.syscall.commons.module.persistence.jpa.multidatasource.testentities.car;

import io.syscall.commons.module.persistence.jpa.EntityExtensions;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/**
 * Test entity for the car datasource.
 */
@Entity
@Table(name = "car_entity")
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id;

    private @Nullable String data;

    public CarEntity() {}

    public CarEntity(String data) {
        this.data = data;
    }

    public @Nullable Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @Nullable String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
        var that = (CarEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "CarEntity{id=" + id + ", data='" + data + "'}";
    }
}
