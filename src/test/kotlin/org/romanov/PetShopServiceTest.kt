package org.romanov

import com.google.protobuf.empty
import io.github.serpro69.kfaker.faker
import io.grpc.Channel
import io.grpc.Server
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import java.util.concurrent.TimeUnit

class PetShopServiceTest : ShouldSpec({

    val serverName = InProcessServerBuilder.generateName()
    lateinit var server: Server
    lateinit var channel: Channel
    lateinit var stub: PetShopServiceGrpcKt.PetShopServiceCoroutineStub
    val petShopService = PetShopServiceImpl()

    beforeSpec {
        server = InProcessServerBuilder
            .forName(serverName)
            .directExecutor()
            .addService(petShopService)
            .build()
            .start()

        channel = InProcessChannelBuilder
            .forName(serverName)
            .directExecutor()
            .build()

        stub = PetShopServiceGrpcKt.PetShopServiceCoroutineStub(channel)
    }

    beforeTest {
        petShopService.clearPets()
    }

    afterSpec {
        server.shutdown()
        server.awaitTermination(5, TimeUnit.SECONDS)
    }

    context("PetShopService") {
        context("createPet") {
            should("successfully create a valid pet") {
                val petData = createValidPetRequest()
                val response = stub.createPet(petData)

                assertSoftly {
                    response.id shouldBe petData.id
                    response.type shouldBe petData.type
                    response.name shouldBe petData.name
                    response.gender shouldBe petData.gender
                }
            }

            should("fail when creating pet with empty name") {
                val petData = createValidPetRequest().copy {
                    name = ""
                }

                shouldThrow<StatusException> {
                    stub.createPet(petData)
                }.status.code shouldBe Status.Code.INVALID_ARGUMENT
            }

            should("fail when creating pet with duplicate ID") {
                val petData = createValidPetRequest()
                stub.createPet(petData)

                val exception = shouldThrow<StatusException> {
                    stub.createPet(petData)
                }

                assertSoftly {
                    exception.status.code shouldBe Status.Code.ALREADY_EXISTS
                    exception.status.description?.replace("\n", "")?.trim()
                        ?.removePrefix("ALREADY_EXISTS:")
                        ?.trim() shouldBe "Pet with ID id: 1 already exists"
                }
            }
        }

        context("getPetById") {
            should("successfully get existing pet") {
                val petData = createValidPetRequest()
                stub.createPet(petData)

                val response = stub.getPetById(petRequestById {
                    id = petData.id
                })

                response.id shouldBe petData.id
            }

            should("fail when getting non-existent pet") {
                shouldThrow<StatusException> {
                    stub.getPetById(petRequestById {
                        id = petId { id = 999 }
                    })
                }.status.code shouldBe Status.Code.NOT_FOUND
            }
        }

        context("getPets") {
            should("return empty list when no pets exist") {
                val response = stub.getPets(empty { })
                response.petsList.shouldBeEmpty()
            }

            should("return all created pets") {
                val pets = List(3) {
                    createValidPetRequest(id = it + 1)
                }
                pets.forEach { stub.createPet(it) }

                val response = stub.getPets(empty { })
                response.petsCount shouldBe 3
                response.petsList.map { it.id } shouldContainAll pets.map { it.id }
            }
        }
    }
}) {
    companion object {
        private fun createValidPetRequest(id: Int = 1) = petRequest {
            this.id = petId { this.id = id }
            type = PetType.DOG
            name = faker { }.animal.name()
            gender = Gender.MALE
        }
    }
}