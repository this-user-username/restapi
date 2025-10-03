package com.github.thisuserusername.restapi.service;

import lombok.Builder;

@Builder
public record DeviceFilter(String brand, String state) {}
