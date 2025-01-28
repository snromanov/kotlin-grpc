package org.romanov

import io.grpc.Server
import io.grpc.ServerBuilder
import mu.KotlinLogging

class PetShopServer(private val port: Int) {
    private val server: Server = ServerBuilder
        .forPort(port)
        .addService(PetShopServiceImpl())
        .build()
    private val logger = KotlinLogging.logger {}

    fun start() {
        server.start()
        logger.info { "Server started on port $port" }

        Runtime.getRuntime().addShutdownHook(
            Thread {
                logger.info { "Shutting down gRPC server" }
                server.shutdown()
                logger.info { "Server shut down successfully" }
            }
        )
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }
}
