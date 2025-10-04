package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return devices by state"
    request {
        method GET()
        url "/api/v1/devices?state=available"
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
                id: 4,
                name: "Test Device 4",
                brand: "Another Brand",
                state: "available",
                creationTime: anyDateTime()
            ]
        ])
    }
}