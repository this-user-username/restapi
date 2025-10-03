package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return devices by brand"
    request {
        method GET()
        url "/api/v1/devices?brand=Test Brand"
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
                name: "Test Device",
                brand: "Test Brand",
                state: "available",
                creationTime: anyDateTime()
            ],
            [
                id: 3,
                name: "Test Device 3",
                brand: "Test Brand",
                state: "inactive",
                creationTime: anyDateTime()
            ]
        ])
    }
}