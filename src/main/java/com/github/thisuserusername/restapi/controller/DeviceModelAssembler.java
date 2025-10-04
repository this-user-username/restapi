package com.github.thisuserusername.restapi.controller;

import com.github.thisuserusername.restapi.dto.DeviceDTO;
import com.github.thisuserusername.restapi.model.Device;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DeviceModelAssembler extends RepresentationModelAssemblerSupport<Device, DeviceDTO> {

    public DeviceModelAssembler() {
        super(DeviceController.class, DeviceDTO.class);
    }

    @Override
    public @NotNull DeviceDTO toModel(@NotNull Device entity) {
        DeviceDTO deviceModel = instantiateModel(entity);

        deviceModel.setId(entity.getId());
        deviceModel.setName(entity.getName());
        deviceModel.setBrand(entity.getBrand());
        deviceModel.setCreationTime(entity.getCreatedOn().format(DateTimeFormatter.ISO_DATE_TIME));
        deviceModel.setState(entity.getState().name().toLowerCase());

        Link selfRel = linkTo(methodOn(DeviceController.class).getDevice(deviceModel.getId())).withSelfRel();
        deviceModel.add(selfRel);
        Link devicesLink = linkTo(methodOn(DeviceController.class).getAllDevices(null, null, 0, 100)).withRel("devices");
        deviceModel.add(devicesLink);

        return deviceModel;
    }
}
