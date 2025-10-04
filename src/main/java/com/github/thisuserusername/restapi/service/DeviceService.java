package com.github.thisuserusername.restapi.service;

import com.github.thisuserusername.restapi.dto.DeviceDTO;
import com.github.thisuserusername.restapi.model.Device;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface DeviceService {

    Device createDevice(DeviceDTO device);

    Optional<Device> getDeviceById(long id);

    Page<Device> getFilteredDevicesAsPage(DeviceFilter filter, int page, int size);

    Device updateDevice(long id, DeviceDTO updatedDevice);

    void deleteDevice(long id);
}
