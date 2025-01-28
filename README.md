# Pet Shop gRPC Service

A gRPC-based service for managing a pet shop inventory. This service provides basic CRUD operations for pets, implemented using Kotlin and gRPC.

## Features

- Create new pets with unique IDs
- Retrieve pet information by ID
- List all available pets
- Input validation
- Thread-safe operations
- Comprehensive error handling
- Extensive test coverage

## Technical Stack

- Kotlin
- gRPC
- Protocol Buffers
- KotlinLogging
- Kotest for testing

## Service Operations

### CreatePet

Creates a new pet in the system. Each pet must have:
- Unique ID
- Valid name (non-empty)
- Pet type
- Gender

```protobuf
rpc CreatePet(PetRequest) returns (PetResponse)
```

### GetPetById

Retrieves a specific pet by its ID.

```protobuf
rpc GetPetById(PetRequestById) returns (PetResponse)
```

### GetPets

Retrieves all pets in the system.

```protobuf
rpc GetPets(Empty) returns (GetPetsResponse)
```

## Error Handling

The service implements comprehensive error handling for various scenarios:

- `INVALID_ARGUMENT`: When input validation fails (empty name, invalid type, etc.)
- `ALREADY_EXISTS`: When attempting to create a pet with an existing ID
- `NOT_FOUND`: When requesting a pet that doesn't exist

## Running Tests

The project includes extensive test coverage using Kotest. To run the tests:

```bash
./gradlew test
```

Tests include:
- Successful pet creation
- Duplicate ID handling
- Invalid input validation
- Pet retrieval
- Empty state handling

## Thread Safety

All operations on the pet list are synchronized to ensure thread safety in a concurrent environment.

## Future Improvements

Potential enhancements for the service:
- Pagination for getPets operation
- Update and delete operations
- Database integration
- Metrics and monitoring
- Rate limiting
- Health check endpoint


## License

This project is licensed under the MIT License - see the LICENSE file for details.