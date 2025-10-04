package com.github.thisuserusername.restapi.service;

import com.github.thisuserusername.restapi.model.DeviceState;
import lombok.Builder;

@Builder
public record DeviceFilter(String brand, DeviceState state) {}
