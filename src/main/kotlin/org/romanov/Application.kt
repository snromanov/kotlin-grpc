package org.romanov

const val SERVER_PORT = 8080

fun main() {
    val server = PetShopServer(SERVER_PORT)

    server.apply {
        start()
        blockUntilShutdown()
    }
}
