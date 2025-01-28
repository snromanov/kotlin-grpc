package org.romanov

import com.google.protobuf.Empty
import io.grpc.Status
import io.grpc.StatusRuntimeException
import mu.KotlinLogging

class PetShopServiceImpl : PetShopServiceGrpcKt.PetShopServiceCoroutineImplBase() {
    private val listOfPets = mutableListOf<PetResponse>()
    private val logger = KotlinLogging.logger {}

    /**
     * Creates a new pet in the store.  Duplicate pets are allowed, but
     * updates to existing pets will be ignored.
     */
    override suspend fun createPet(request: PetRequest): PetResponse {
        validatePetRequest(request)

        val pet = petResponse {
            id = request.id
            type = request.type
            name = request.name
            gender = request.gender
        }

        synchronized(listOfPets) {
            if (listOfPets.any { it.id == request.id }) {
                throw StatusRuntimeException(
                    Status.ALREADY_EXISTS
                        .withDescription("Pet with ID ${request.id} already exists")
                )
            }
            listOfPets.add(pet)
        }

        logger.info { "Created new pet: $pet" }
        return pet
    }

    /**
     * Returns the pet with the given ID. Returns NOT_FOUND if the pet does not exist.
     */
    override suspend fun getPetById(request: PetRequestById): PetResponse {
        validatePetId(request.id)

        return listOfPets.find { it.id == request.id }
            ?: throw StatusRuntimeException(
                Status.NOT_FOUND
                    .withDescription("Pet with ID ${request.id.id} not found")
            )
    }

    /** get all pets */
    override suspend fun getPets(request: Empty): GetPetsResponse {
        return getPetsResponse {
            synchronized(listOfPets) {
                this.pets.addAll(listOfPets)
            }
        }.also {
            logger.debug { "Returning ${it.petsCount} pets" }
        }
    }

    /**
     * Verifies that the pet is valid and can be created.
     */
    private fun validatePetRequest(request: PetRequest) {
        val validationError = when {
            request.name.isBlank() -> "Pet name cannot be empty"
            request.type == PetType.UNRECOGNIZED -> "Invalid pet type"
            request.gender == Gender.UNRECOGNIZED -> "Invalid gender"
            else -> null
        }

        if (validationError != null) {
            throw StatusRuntimeException(
                Status.INVALID_ARGUMENT.withDescription(validationError)
            )
        }
    }

    /**
     * Verifies that the pet ID is valid.
     */
    private fun validatePetId(id: PetId) {
        if (id.id <= 0) {
            throw StatusRuntimeException(
                Status.INVALID_ARGUMENT.withDescription("Pet ID must be positive")
            )
        }
    }

    /**
     *
     */
    fun clearPets() {
        synchronized(listOfPets) {
            listOfPets.clear()
        }
    }
}
