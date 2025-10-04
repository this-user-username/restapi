package com.github.thisuserusername.restapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thisuserusername.restapi.dto.DeviceDTO;
import com.github.thisuserusername.restapi.model.Device;
import com.github.thisuserusername.restapi.model.DeviceState;
import com.github.thisuserusername.restapi.service.DeviceFilter;
import com.github.thisuserusername.restapi.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceService deviceService;

    @MockitoBean
    private PagedResourcesAssembler<Device> assembler;

    @MockitoBean
    private RepresentationModelAssembler<Device, DeviceDTO> deviceModelAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    private Device testDevice;
    private DeviceDTO testDeviceDTO;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder()
                .id(1L)
                .name("Test Device")
                .brand("Test Brand")
                .state(DeviceState.AVAILABLE)
                .createdOn(LocalDateTime.now())
                .build();

        testDeviceDTO = DeviceDTO.builder()
                .id(1L)
                .name("Test Device")
                .brand("Test Brand")
                .state("available")
                .creationTime("2024-01-01T12:00:00")
                .build();
    }

    @Test
    void should_create_new_device() throws Exception {
        // Given
        DeviceDTO newDeviceDTO = DeviceDTO.builder()
                .name("New Device")
                .brand("Test Brand")
                .state("available")
                .build();
        
        when(deviceService.createDevice(any(DeviceDTO.class))).thenReturn(testDevice);
        when(deviceModelAssembler.toModel(testDevice)).thenReturn(testDeviceDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDeviceDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Device"))
                .andExpect(jsonPath("$.brand").value("Test Brand"))
                .andExpect(jsonPath("$.state").value("available"));
    }

    @Test
    void should_return_device_by_id() throws Exception {
        // Given
        when(deviceService.getDeviceById(1L)).thenReturn(Optional.of(testDevice));
        when(deviceModelAssembler.toModel(testDevice)).thenReturn(testDeviceDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/devices/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Device"))
                .andExpect(jsonPath("$.brand").value("Test Brand"))
                .andExpect(jsonPath("$.state").value("available"));
    }

    @Test
    void should_return_not_found_when_device_does_not_exist() throws Exception {
        // Given
        when(deviceService.getDeviceById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/devices/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_all_devices() throws Exception {
        // Given
        Device device2 = Device.builder()
                .id(2L)
                .name("Test Device 2")
                .brand("Another Brand")
                .state(DeviceState.IN_USE)
                .createdOn(LocalDateTime.now())
                .build();
        
        Page<Device> devicePage = new PageImpl<>(Arrays.asList(testDevice, device2));
        when(deviceService.getFilteredDevicesAsPage(any(DeviceFilter.class), eq(0), eq(100)))
                .thenReturn(devicePage);

        // When & Then
        mockMvc.perform(get("/api/v1/devices")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void should_return_devices_by_brand() throws Exception {
        // Given
        Page<Device> devicePage = new PageImpl<>(Collections.singletonList(testDevice));
        when(deviceService.getFilteredDevicesAsPage(any(DeviceFilter.class), eq(0), eq(100)))
                .thenReturn(devicePage);

        // When & Then
        mockMvc.perform(get("/api/v1/devices")
                        .param("brand", "Test Brand")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void should_return_devices_by_state() throws Exception {
        // Given
        Page<Device> devicePage = new PageImpl<>(Collections.singletonList(testDevice));
        when(deviceService.getFilteredDevicesAsPage(any(DeviceFilter.class), eq(0), eq(100)))
                .thenReturn(devicePage);

        // When & Then
        mockMvc.perform(get("/api/v1/devices")
                        .param("state", "available")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void should_fully_update_device() throws Exception {
        // Given
        DeviceDTO updatedDeviceDTO = DeviceDTO.builder()
                .id(1L)
                .name("Updated Device")
                .brand("Updated Brand")
                .state("inactive")
                .build();
        
        Device updatedDevice = Device.builder()
                .id(1L)
                .name("Updated Device")
                .brand("Updated Brand")
                .state(DeviceState.INACTIVE)
                .createdOn(LocalDateTime.now())
                .build();
        
        DeviceDTO updatedDeviceResponse = DeviceDTO.builder()
                .id(1L)
                .name("Updated Device")
                .brand("Updated Brand")
                .state("inactive")
                .creationTime("2024-01-01T12:00:00")
                .build();

        when(deviceService.updateDevice(eq(1L), any(DeviceDTO.class))).thenReturn(updatedDevice);
        when(deviceModelAssembler.toModel(updatedDevice)).thenReturn(updatedDeviceResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDeviceDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.name").value("Updated Device"))
                .andExpect(jsonPath("$.brand").value("Updated Brand"))
                .andExpect(jsonPath("$.state").value("inactive"));
    }

    @Test
    void should_partially_update_device() throws Exception {
        // Given
        DeviceDTO patchDeviceDTO = DeviceDTO.builder()
                .name("Patched Name")
                .build();
        
        Device patchedDevice = Device.builder()
                .id(1L)
                .name("Patched Name")
                .brand("Test Brand")
                .state(DeviceState.AVAILABLE)
                .createdOn(LocalDateTime.now())
                .build();
        
        DeviceDTO patchedDeviceResponse = DeviceDTO.builder()
                .id(1L)
                .name("Patched Name")
                .brand("Test Brand")
                .state("available")
                .creationTime("2024-01-01T12:00:00")
                .build();

        when(deviceService.updateDevice(eq(1L), any(DeviceDTO.class))).thenReturn(patchedDevice);
        when(deviceModelAssembler.toModel(patchedDevice)).thenReturn(patchedDeviceResponse);

        // When & Then
        mockMvc.perform(patch("/api/v1/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDeviceDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.name").value("Patched Name"))
                .andExpect(jsonPath("$.brand").value("Test Brand"))
                .andExpect(jsonPath("$.state").value("available"));
    }

    @Test
    void should_delete_device() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/devices/1"))
                .andExpect(status().isNoContent());
    }
}