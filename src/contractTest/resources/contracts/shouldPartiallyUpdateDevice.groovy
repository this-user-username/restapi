package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should partially update an existing device"
    request {
        method "PATCH"
        url "/api/v1/devices/2"
        headers {
            contentType applicationJson()
        }
        body(
            name: "Partially Updated Device"
        )
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body(
            id: 2,
            name: "Partially Updated Device",
            brand: "Test Brand",
            state: "available",
            creationTime: anyDateTime()
        )
    }
}