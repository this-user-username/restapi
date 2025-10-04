package com.github.thisuserusername.restapi.service;

import com.github.thisuserusername.restapi.dto.DeviceDTO;
import com.github.thisuserusername.restapi.model.Device;
import com.github.thisuserusername.restapi.model.DeviceState;
import com.github.thisuserusername.restapi.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(transactionManager = "transactionManager")
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    @Override
    public Device createDevice(DeviceDTO device) {
        Device entry = new Device();
        BeanUtils.copyProperties(device, entry);
        entry.setState(DeviceState.valueOf(device.getState().toUpperCase()));
        return deviceRepository.save(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Device> getDeviceById(long id) {
        return deviceRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Device> getFilteredDevicesAsPage(DeviceFilter filter, int page, int size) {
        Device.DeviceBuilder deviceBuilder = Device.builder();
        Optional.ofNullable(filter.brand()).ifPresent(deviceBuilder::brand);
        Optional.ofNullable(filter.state()).ifPresent(deviceBuilder::state);

        return deviceRepository.findAll(Example.of(deviceBuilder.build()), PageRequest.of(page, size));
    }

    @Override
    public Device updateDevice(long id, DeviceDTO updatedDevice) {
        Optional<Device> ex = deviceRepository.findById(id);
        if (ex.isEmpty()) {
            throw new IllegalArgumentException("Device with ID " + id + " does not exist");
        }

        Device device = ex.get();
        if (device.getState() == DeviceState.IN_USE) {
            if (ObjectUtils.notEqual(device.getBrand(), ObjectUtils.firstNonNull(updatedDevice.getBrand(), device.getBrand()))) {
                throw new IllegalArgumentException("Cannot update brand of device that is in use");
            }
            if (ObjectUtils.notEqual(device.getName(), ObjectUtils.firstNonNull(updatedDevice.getName(), updatedDevice.getName()))) {
                throw new IllegalArgumentException("Cannot update name of device that is in use");
            }
        }

        device.setName(ObjectUtils.firstNonNull(updatedDevice.getName(), device.getName()));
        device.setBrand(ObjectUtils.firstNonNull(updatedDevice.getBrand(), device.getBrand()));
        DeviceState newState = Optional.ofNullable(updatedDevice.getState())
                .filter(Objects::nonNull)
                .map(DeviceState::valueOf)
                .orElseGet(device::getState);
        device.setState(newState);
        deviceRepository.flush();

        return device;
    }

    @Override
    public void deleteDevice(long id) {
        Optional<Device> ex = deviceRepository.findById(id);
        if (ex.isEmpty()) {
            throw new IllegalArgumentException("Device with ID " + id + " does not exist");
        }

        Device device = ex.get();
        if (device.getState() == DeviceState.IN_USE) {
            throw new IllegalArgumentException("Cannot delete device that is in use");
        }

        deviceRepository.deleteById(id);
    }
}
