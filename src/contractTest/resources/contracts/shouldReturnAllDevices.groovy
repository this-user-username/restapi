package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return all devices"
    request {
        method GET()
        url "/api/v1/devices"
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
                id: 2,
                name: "Test Device 2",
                brand: "Another Brand",
                state: "IN_USE",
                creationTime: anyDateTime(),
                modificationTime: anyDateTime(),
                version: anyPositiveInt()
            ]
        ])
    }
}