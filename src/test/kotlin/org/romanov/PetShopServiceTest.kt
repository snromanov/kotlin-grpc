package org.romanov

import com.google.protobuf.empty
import io.github.serpro69.kfaker.faker
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.shouldBe
import mu.KotlinLogging

class PetShopServiceTest : ShouldSpec({
    val fake = faker { }
    val log = KotlinLogging.logger {}

    val serverName = InProcessServerBuilder.generateName()

    val server = InProcessServerBuilder
        .forName(serverName)
        .directExecutor()
        .addService(PetShopServiceImpl())
        .build()

    val channel = InProcessChannelBuilder.forName(serverName).directExecutor().build()
    val stub = PetShopServiceGrpcKt.PetShopServiceCoroutineStub(channel)

    lateinit var petResponse: PetResponse

    val petData = petRequest {
        id = petId { id = 1 }
        type = PetType.DOG
        name = fake.animal.name()
        gender = Gender.MALE
    }.apply {
        log.info { "PetData: $this" }
    }

    beforeSpec {
        server.start()
        petResponse = stub
            .createPet(petData)
            .apply { log.info { "PetResponse: $this" } }
    }

    afterSpec {
        server.shutdown()
    }

    context("PetShopService") {
        should("create a pet") {
            assertSoftly {
                petResponse.id shouldBe petData.id
                petResponse.type shouldBe petData.type
                petResponse.name shouldBe petData.name
                petResponse.gender shouldBe petData.gender
            }
        }

        should("get a pet by id") {
            val petRequestById = petRequestById {
                id = petData.id
            }
                .apply { log.info { "PetRequestById: $this" } }
            val response = stub
                .getPetById(petRequestById)
                .apply { log.info { "PetResponse: $this" } }

            response.id shouldBe petRequestById.id
        }

        should("get all pets") {
            val petsResponse = stub
                .getPets(empty { })
                .apply { log.info { "GetPetsResponse: $this" } }

            petsResponse
                .petsList
                .shouldBeSingleton()
                .first()
                .apply {
                    id shouldBe petData.id
                    type shouldBe petData.type
                    name shouldBe petData.name
                    gender shouldBe petData.gender

                }
        }
    }
})
