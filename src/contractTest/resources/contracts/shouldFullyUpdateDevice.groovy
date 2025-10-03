package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should fully update an existing device"
    request {
        method PUT()
        url "/api/v1/devices/1"
        headers {
            contentType applicationJson()
        }
        body(
            name: "Updated Device",
            brand: "Updated Brand",
            state: "IN_USE"
        )
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body(
            id: 1,
            name: "Updated Device",
            brand: "Updated Brand",
            state: "IN_USE",
            creationTime: anyDateTime(),
            modificationTime: anyDateTime(),
            version: anyPositiveInt()
        )
    }
}