package com.github.thisuserusername.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDTO extends RepresentationModel<DeviceDTO> {
    private Long id;
    private @NotBlank(message = "Device name is required") String name;
    private @NotBlank(message = "Device brand is required") String brand;
    private @NotNull(message = "Device state is required") @Pattern(regexp = "available|in_use|inactve") String state;
    private String creationTime;
}
