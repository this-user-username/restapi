package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should delete a device"
    request {
        method DELETE()
        url "/api/v1/devices/1"
    }
    response {
        status NO_CONTENT()
    }
}