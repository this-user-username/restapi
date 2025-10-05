package com.github.thisuserusername.restapi.service;

import com.github.thisuserusername.restapi.dto.DeviceDTO;
import com.github.thisuserusername.restapi.model.Device;
import com.github.thisuserusername.restapi.model.DeviceState;
import com.github.thisuserusername.restapi.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;
    private DeviceService deviceService;

    private Device testDevice;
    private Device inUseDevice;
    private Device anotherDevice;

    @BeforeEach
    void setUp() {
        deviceService = new DeviceServiceImpl(deviceRepository);

        testDevice = Device.builder()
                .id(1L)
                .name("Test Device")
                .brand("Test Brand")
                .state(DeviceState.AVAILABLE)
                .createdOn(LocalDateTime.now().minusDays(1))
                .build();

        inUseDevice = Device.builder()
                .id(2L)
                .name("In Use Device")
                .brand("Test Brand")
                .state(DeviceState.IN_USE)
                .createdOn(LocalDateTime.now().minusDays(1))
                .build();

        anotherDevice = Device.builder()
                .id(3L)
                .name("Another Device")
                .brand("Another Brand")
                .state(DeviceState.INACTIVE)
                .createdOn(LocalDateTime.now().minusDays(2))
                .build();
    }

    @Test
    void create_device_should_create_successfully() {
        // Given
        DeviceDTO newDevice = DeviceDTO.builder()
                .name("New Device")
                .brand("New Brand")
                .state(DeviceState.AVAILABLE.name())
                .build();
        
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);

        // When
        Device result = deviceService.createDevice(newDevice);

        // Then
        assertThat(result).isNotNull();
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void get_device_by_id_when_exists_should_return_device() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));

        // When
        Optional<Device> result = deviceService.getDeviceById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testDevice);
    }

    @Test
    void get_device_by_id_when_not_exists_should_return_empty() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Device> result = deviceService.getDeviceById(1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void update_device_when_not_in_use_should_update_successfully() {
        // Given
        DeviceDTO updatedDeviceDTO = DeviceDTO.builder()
                .id(3L)
                .name("Updated Name")
                .brand("Updated Brand")
                .state(DeviceState.INACTIVE.name())
                .build();
        Device existingDevice = Device.builder()
                .id(3L)
                .name("Name")
                .brand("Brand")
                .state(DeviceState.AVAILABLE)
                .build();

        when(deviceRepository.findById(3L)).thenReturn(Optional.of(existingDevice));

        // When
        Device result = deviceService.updateDevice(3L, updatedDeviceDTO);

        // Then
        assertThat(result).isNotNull();
        verify(deviceRepository).flush();
    }

    @Test
    void update_device_when_in_use_and_changing_name_should_throw_exception() {
        // Given
        DeviceDTO updatedDeviceDTO = DeviceDTO.builder()
                .name("Updated Name")
                .brand("Test Brand")
                .state(DeviceState.IN_USE.name())
                .build();
        
        when(deviceRepository.findById(2L)).thenReturn(Optional.of(inUseDevice));

        // When & Then
        assertThatThrownBy(() -> deviceService.updateDevice(2L, updatedDeviceDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot update name of device that is in use");
    }

    @Test
    void update_device_when_in_use_and_changing_brand_should_throw_exception() {
        // Given
        DeviceDTO updatedDeviceDTO = DeviceDTO.builder()
                .name("In Use Device")
                .brand("Updated Brand")
                .state(DeviceState.IN_USE.name())
                .build();
        
        when(deviceRepository.findById(2L)).thenReturn(Optional.of(inUseDevice));

        // When & Then
        assertThatThrownBy(() -> deviceService.updateDevice(2L, updatedDeviceDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot update brand of device that is in use");
    }

    @Test
    void update_device_when_in_use_and_changing_state_should_throw_exception() {
        // Given
        DeviceDTO updatedDeviceDTO = DeviceDTO.builder()
                .name("In Use Device")
                .brand("Updated Brand")
                .state(DeviceState.AVAILABLE.name())
                .build();

        when(deviceRepository.findById(2L)).thenReturn(Optional.of(inUseDevice));

        // When & Then
        assertThatThrownBy(() -> deviceService.updateDevice(2L, updatedDeviceDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot update brand of device that is in use");
    }

    @Test
    void update_device_when_device_not_found_should_throw_exception() {
        // Given
        DeviceDTO updatedDeviceDTO = DeviceDTO.builder().build();
        when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> deviceService.updateDevice(1L, updatedDeviceDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Device with ID 1 does not exist");
    }

    @Test
    void delete_device_when_not_in_use_should_delete_successfully() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));

        // When
        deviceService.deleteDevice(1L);

        // Then
        verify(deviceRepository).deleteById(1L);
    }

    @Test
    void delete_device_when_in_use_should_throw_exception() {
        // Given
        when(deviceRepository.findById(2L)).thenReturn(Optional.of(inUseDevice));

        // When & Then
        assertThatThrownBy(() -> deviceService.deleteDevice(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot delete device that is in use");
        
        verify(deviceRepository, never()).deleteById(2L);
    }

    @Test
    void delete_device_when_device_not_found_should_throw_exception() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> deviceService.deleteDevice(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Device with ID 1 does not exist");
    }

    @Test
    void get_filtered_devices_as_page_when_no_filters_should_return_all_devices() {
        // Given
        DeviceFilter filter = DeviceFilter.builder().build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Device> expectedPage = new PageImpl<>(Arrays.asList(testDevice, inUseDevice, anotherDevice));
        
        when(deviceRepository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(expectedPage);

        // When
        Page<Device> result = deviceService.getFilteredDevicesAsPage(filter, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).containsExactly(testDevice, inUseDevice, anotherDevice);
        verify(deviceRepository).findAll(any(Example.class), eq(pageRequest));
    }

    @Test
    void get_filtered_devices_as_page_when_brand_filter_should_return_filtered_devices() {
        // Given
        DeviceFilter filter = DeviceFilter.builder().brand("Test Brand").build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Device> expectedPage = new PageImpl<>(Arrays.asList(testDevice, inUseDevice));
        
        when(deviceRepository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(expectedPage);

        // When
        Page<Device> result = deviceService.getFilteredDevicesAsPage(filter, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(testDevice, inUseDevice);
        verify(deviceRepository).findAll(any(Example.class), eq(pageRequest));
    }

    @Test
    void get_filtered_devices_as_page_when_state_filter_should_return_filtered_devices() {
        // Given
        DeviceFilter filter = DeviceFilter.builder().state(DeviceState.AVAILABLE).build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Device> expectedPage = new PageImpl<>(Collections.singletonList(testDevice));
        
        when(deviceRepository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(expectedPage);

        // When
        Page<Device> result = deviceService.getFilteredDevicesAsPage(filter, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(testDevice);
        verify(deviceRepository).findAll(any(Example.class), eq(pageRequest));
    }

    @Test
    void get_filtered_devices_as_page_when_multiple_filters_should_return_filtered_devices() {
        // Given
        DeviceFilter filter = DeviceFilter.builder()
                .brand("Test Brand")
                .state(DeviceState.IN_USE)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Device> expectedPage = new PageImpl<>(Collections.singletonList(inUseDevice));
        
        when(deviceRepository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(expectedPage);

        // When
        Page<Device> result = deviceService.getFilteredDevicesAsPage(filter, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(inUseDevice);
        verify(deviceRepository).findAll(any(Example.class), eq(pageRequest));
    }

    @Test
    void get_filtered_devices_as_page_when_custom_pagination_should_use_correct_page_request() {
        // Given
        DeviceFilter filter = DeviceFilter.builder().build();
        PageRequest pageRequest = PageRequest.of(1, 5);
        Page<Device> expectedPage = new PageImpl<>(Collections.singletonList(anotherDevice));
        
        when(deviceRepository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(expectedPage);

        // When
        Page<Device> result = deviceService.getFilteredDevicesAsPage(filter, 1, 5);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(deviceRepository).findAll(any(Example.class), eq(pageRequest));
    }

    @Test
    void get_filtered_devices_as_page_when_no_matches_should_return_empty_page() {
        // Given
        DeviceFilter filter = DeviceFilter.builder().brand("Nonexistent Brand").build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Device> expectedPage = new PageImpl<>(Collections.emptyList());
        
        when(deviceRepository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(expectedPage);

        // When
        Page<Device> result = deviceService.getFilteredDevicesAsPage(filter, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(deviceRepository).findAll(any(Example.class), eq(pageRequest));
    }
}