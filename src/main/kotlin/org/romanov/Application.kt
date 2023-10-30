package org.romanov

import io.grpc.ServerBuilder

const val SERVER_PORT = 8080

fun main() {
    val server = ServerBuilder
        .forPort(SERVER_PORT)
        .addService(PetShopServiceImpl())
        .build()

    server.start()
    server.awaitTermination()
}
