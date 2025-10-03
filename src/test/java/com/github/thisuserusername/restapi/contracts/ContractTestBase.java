package com.github.thisuserusername.restapi.contracts;

import com.github.thisuserusername.restapi.config.RestConfig;
import com.github.thisuserusername.restapi.controller.DeviceController;
import com.github.thisuserusername.restapi.controller.DeviceModelAssembler;
import com.github.thisuserusername.restapi.dto.DeviceDTO;
import com.github.thisuserusername.restapi.model.Device;
import com.github.thisuserusername.restapi.model.DeviceState;
import com.github.thisuserusername.restapi.service.DeviceFilter;
import com.github.thisuserusername.restapi.service.DeviceService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Base class for Spring Cloud Contract tests.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = {
        DeviceController.class,
        RestConfig.class,
        DeviceService.class,
        DeviceModelAssembler.class
})
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.CLASSPATH)
public abstract class ContractTestBase {

    @MockitoBean
    private DeviceService deviceService;

    @Autowired
    private DeviceController deviceController;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.standaloneSetup(deviceController);

        // Setup mock responses for contracts
        LocalDateTime now = LocalDateTime.of(2023, 10, 1, 12, 0, 0);
        
        Device testDevice = Device.builder()
                .id(1L)
                .name("Test Device")
                .brand("Test Brand")
                .state(DeviceState.AVAILABLE)
                .createdOn(now)
                .build();

        Device createdDevice =  Device.builder()
                .id(1L)
                .name("New Device")
                .brand("Test Brand")
                .state(DeviceState.AVAILABLE)
                .createdOn(now)
                .build();

        Device updatedDevice = Device.builder()
                .id(1L)
                .name("Updated Device")
                .brand("Updated Brand")
                .state(DeviceState.IN_USE)
                .createdOn(now)
                .build();

        Device partiallyUpdatedDevice = Device.builder()
                .id(2L)
                .name("Partially Updated Device")
                .brand("Test Brand")
                .state(DeviceState.AVAILABLE)
                .createdOn(now)
                .build();

        Device testDevice2 = Device.builder()
                .id(2L)
                .name("Test Device 2")
                .brand("Another Brand")
                .state(DeviceState.IN_USE)
                .createdOn(now)
                .build();

        Device testDevice3 = Device.builder()
                .id(3L)
                .name("Test Device 3")
                .brand("Test Brand")
                .state(DeviceState.INACTIVE)
                .createdOn(now)
                .build();

        Device testDevice4 = Device.builder()
                .id(4L)
                .name("Test Device 4")
                .brand("Another Brand")
                .state(DeviceState.AVAILABLE)
                .createdOn(now)
                .build();

        // Mock for getting a device by ID
        when(deviceService.getDeviceById(eq(1L)))
                .thenReturn(Optional.of(testDevice));

        // Mock for creating a device
        when(deviceService.createDevice(any(DeviceDTO.class)))
                .thenReturn(createdDevice);

        // Mock for updating a device (PUT)
        when(deviceService.updateDevice(eq(1L), any(DeviceDTO.class)))
                .thenReturn(updatedDevice);

        // Mock for patching a device (PATCH)
        when(deviceService.updateDevice(eq(2L), any(DeviceDTO.class)))
                .thenReturn(partiallyUpdatedDevice);

        // Mock for getting all devices
        when(deviceService.getFilteredDevicesAsPage(eq(DeviceFilter.builder().build()), eq(0), eq(100)))
                .thenReturn(new PageImpl<>(List.of(testDevice, testDevice2)));

        // Mock for getting devices by brand
        when(deviceService.getFilteredDevicesAsPage(eq(DeviceFilter.builder().brand("Test Brand").build()), eq(0), eq(100)))
                .thenReturn(new PageImpl<>(List.of(testDevice, testDevice3)));

        // Mock for getting devices by state
        when(deviceService.getFilteredDevicesAsPage(eq(DeviceFilter.builder().state("available").build()), eq(0), eq(100)))
                .thenReturn(new PageImpl<>(List.of(testDevice, testDevice4)));

        // Mock for deleting device
        doNothing().when(deviceService).deleteDevice(eq(1L));
    }
}