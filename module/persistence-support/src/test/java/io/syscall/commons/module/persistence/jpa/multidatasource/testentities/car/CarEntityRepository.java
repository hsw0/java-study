package io.syscall.commons.module.persistence.jpa.multidatasource.testentities.car;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository for CarEntity, uses car datasource. */
public interface CarEntityRepository extends JpaRepository<CarEntity, Long> {

    Optional<CarEntity> findByData(String data);
}
