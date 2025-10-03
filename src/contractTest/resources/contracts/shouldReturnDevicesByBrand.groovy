package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return devices by brand"
    request {
        method GET()
        url "/api/v1/devices/brand/Test Brand"
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
                id: 3,
                name: "Test Device 3",
                brand: "Test Brand",
                state: "INACTIVE",
                creationTime: anyDateTime(),
                modificationTime: anyDateTime(),
                version: anyPositiveInt()
            ]
        ])
    }
}