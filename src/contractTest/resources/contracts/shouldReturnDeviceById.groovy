package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return device by id"
    request {
        method GET()
        url "/api/v1/devices/1"
        headers {
            accept applicationJson()
        }
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body(
            id: 1,
            name: "Test Device",
            brand: "Test Brand",
            state: "AVAILABLE",
            creationTime: anyDateTime()
        )
    }
}