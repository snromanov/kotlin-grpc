package org.romanov

import com.google.protobuf.Empty
import io.grpc.Status
import io.grpc.StatusRuntimeException

/**
 * Implementation of the PetShopService gRPC service.
 *
 * This class manages a list of pets and provides methods to create a pet,
 * get a pet by its ID, and get all pets.
 */
class PetShopServiceImpl : PetShopServiceGrpcKt.PetShopServiceCoroutineImplBase() {
    private val listOfPets = mutableListOf<PetResponse>()

    /**
     * Creates a pet and adds it to the list of pets.
     *
     * @param request The request containing the details of the pet to be created.
     * @return The created pet.
     */
    override suspend fun createPet(request: PetRequest): PetResponse {
        val pet = petResponse {
            id = request.id
            type = request.type
            name = request.name
            gender = request.gender
        }
        listOfPets.add(pet)
        return pet
    }

    /**
     * Gets a pet by its ID.
     *
     * @param request The request containing the ID of the pet to be retrieved.
     * @return The pet with the given ID.
     * @throws StatusRuntimeException if no pet with the given ID is found.
     */
    override suspend fun getPetById(request: PetRequestById): PetResponse {
        return listOfPets.find { it.id == request.id } ?: throw StatusRuntimeException(Status.NOT_FOUND)
    }

    /**
     * Gets all pets.
     *
     * @param request The request to get all pets.
     * @return A response containing all pets.
     */
    override suspend fun getPets(request: Empty): GetPetsResponse =
        getPetsResponse {
            this.pets.addAll(listOfPets)
        }
}
