package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return devices by state"
    request {
        method GET()
        url "/api/v1/devices/state/AVAILABLE"
        headers {
            accept applicationJson()
        }
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([
            [
                id: 1,
                name: "Test Device 1",
                brand: "Test Brand",
                state: "AVAILABLE",
                creationTime: anyDateTime(),
                modificationTime: anyDateTime(),
                version: anyPositiveInt()
            ],
            [
                id: 4,
                name: "Test Device 4",
                brand: "Another Brand",
                state: "AVAILABLE",
                creationTime: anyDateTime(),
                modificationTime: anyDateTime(),
                version: anyPositiveInt()
            ]
        ])
    }
}