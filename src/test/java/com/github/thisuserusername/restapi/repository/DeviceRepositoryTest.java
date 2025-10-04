package com.github.thisuserusername.restapi.repository;

import com.github.thisuserusername.restapi.model.Device;
import com.github.thisuserusername.restapi.model.DeviceState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DeviceRepositoryTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Test
    void should_find_devices_by_brand() {
        // Given
        String brand = "Apple";
        Example<Device> example = Example.of(Device.builder().brand(brand).build());

        // When
        Page<Device> appleDevices = deviceRepository.findAll(example, PageRequest.of(0, 3));

        // Then
        assertThat(appleDevices)
                .isNotNull()
                .hasSize(3);

        // When
        Page<Device> appleDevices2 = deviceRepository.findAll(example, PageRequest.of(1, 3));

        // Then
        assertThat(appleDevices2)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void should_find_devices_by_state() {
        // Given
        var state = DeviceState.IN_USE;
        Example<Device> example = Example.of(Device.builder().state(state).build());

        // When
        Page<Device> inUseDevices = deviceRepository.findAll(example, PageRequest.of(0, 4));

        // Then
        assertThat(inUseDevices)
                .isNotNull()
                .hasSize(4);

        // When
        Page<Device> inUseDevices2 = deviceRepository.findAll(example, PageRequest.of(1, 4));

        // Then
        assertThat(inUseDevices2)
                .isNotNull()
                .hasSize(3);
    }
}