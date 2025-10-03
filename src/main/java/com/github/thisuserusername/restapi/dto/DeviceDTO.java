package com.github.thisuserusername.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceDTO extends RepresentationModel<DeviceDTO> {
    private Long id;
    private @NotBlank(message = "Device name is required") String name;
    private @NotBlank(message = "Device brand is required") String brand;
    private @NotNull(message = "Device state is required") String state;
    private String creationTime;
}
