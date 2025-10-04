package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should create a new device"
    request {
        method POST()
        url "/api/v1/devices"
        headers {
            contentType applicationJson()
        }
        body(
            name: "New Device",
            brand: "Test Brand",
            state: "available"
        )
    }
    response {
        status CREATED()
        headers {
            contentType applicationJson()
        }
        body(
            id: anyPositiveInt(),
            name: "New Device",
            brand: "Test Brand",
            state: "available",
            creationTime: anyDateTime()
        )
    }
}