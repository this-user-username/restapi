package com.github.thisuserusername.restapi.controller;

import com.github.thisuserusername.restapi.dto.DeviceDTO;
import com.github.thisuserusername.restapi.model.Device;
import com.github.thisuserusername.restapi.model.DeviceState;
import com.github.thisuserusername.restapi.service.DeviceFilter;
import com.github.thisuserusername.restapi.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for device management operations.
 */
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Device Management", description = "APIs for managing device resources")
public class DeviceController {

    private final DeviceService deviceService;
    private final PagedResourcesAssembler<Device> assembler;
    private final RepresentationModelAssembler<Device, DeviceDTO> deviceModelAssembler;

    @Operation(summary = "Create a new device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Device created successfully",
                    content = @Content(schema = @Schema(implementation = DeviceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<DeviceDTO> createDevice(
            @Valid @RequestBody DeviceDTO device) {
        LOG.debug("Request to create device: {}", device.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deviceModelAssembler.toModel(deviceService.createDevice(device)));
    }

    @Operation(summary = "Get a device by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device found",
                    content = @Content(schema = @Schema(implementation = DeviceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeviceDTO> getDevice(
            @Parameter(description = "Device ID") @PathVariable Long id) {
        LOG.debug("Request to get device: {}", id);
        return deviceService.getDeviceById(id)
                .map(device -> ResponseEntity.ok().body(deviceModelAssembler.toModel(device)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get devices represented as pageable view. Result set can be optionally filtered by brand and state")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of (optionally filtered) devices retrieved successfully")
    })
    @GetMapping
    @Valid
    public ResponseEntity<PagedModel<DeviceDTO>> getAllDevices(
            @RequestParam(required = false) String brand,
            @Pattern(regexp = "available|in_use|inactve")
            @RequestParam(required = false) String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        LOG.debug("Request to get devices list page with filters {}, {}, {}, {}", brand, state, page, size);
        Page<Device> devices = deviceService.getFilteredDevicesAsPage(DeviceFilter.builder()
                .brand(StringUtils.trimToNull(brand))
                .state(Optional.ofNullable(StringUtils.trimToNull(state))
                        .map(String::toUpperCase)
                        .map(DeviceState::valueOf)
                        .orElse(null))
                .build(), page, size);
        return ResponseEntity.ok(assembler.toModel(devices, deviceModelAssembler));
    }

    @Operation(summary = "Update/replace a device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device updated successfully",
                    content = @Content(schema = @Schema(implementation = DeviceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(
            @Min(1) @Parameter(description = "Device ID") @PathVariable Long id,
            @Valid @RequestBody DeviceDTO device) {
        LOG.debug("Request to update device: {}", device);
        return ResponseEntity.ok(deviceModelAssembler.toModel(deviceService.updateDevice(id, device)));
    }

    @Operation(summary = "Partially update a device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device updated successfully",
                    content = @Content(schema = @Schema(implementation = DeviceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<DeviceDTO> patchDevice(
            @Min(1) @Parameter(description = "Device ID") @PathVariable Long id,
            @RequestBody DeviceDTO updates) {
        LOG.debug("Request to patch device: {}", id);
        return ResponseEntity.ok(deviceModelAssembler.toModel(deviceService.updateDevice(id, updates)));
    }

    @Operation(summary = "Delete a device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Device deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot delete device that is in use"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(
            @Min(1) @Parameter(description = "Device ID") @PathVariable Long id) {
        LOG.debug("Request to delete device: {}", id);
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}