package com.github.thisuserusername.restapi.repository;

import com.github.thisuserusername.restapi.model.Device;
import com.github.thisuserusername.restapi.model.DeviceState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Device entity operations.
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Page<Device> findByBrandIgnoreCaseAndState(String brand, DeviceState state, Pageable pageable);
}