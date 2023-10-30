import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm")
    idea
    id("com.google.protobuf") version "0.8.17"
}

val versionOfProtoc: String by project
val versionOfProtocGenJava: String by project
val versionOfProtocGenKotlin: String by project
val versionOfGrpcStubKt: String by project
val versionOfGrpcStub: String by project
val versionOfGrpcProtobuf: String by project
val versionOfProtobuf: String by project
val versionOfJavaxAnnotation: String by project
val versionOfKotlinxCoroutinesCore: String by project
val versionOfKotlin: String by project

idea {
    module {
        sourceDirs.plusAssign(file("$projectDir/src/"))
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$versionOfKotlin")
    api("io.grpc:grpc-kotlin-stub:$versionOfGrpcStubKt")
    api("io.grpc:grpc-stub:$versionOfGrpcStub")
    api("io.grpc:grpc-protobuf:$versionOfGrpcStub")
    api("javax.annotation:javax.annotation-api:$versionOfJavaxAnnotation")
    api("com.google.protobuf:protobuf-kotlin:$versionOfProtobuf")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$versionOfKotlinxCoroutinesCore")
}

sourceSets {
    val main by getting
    main.java.srcDirs("$projectDir/src/main/java")
    main.java.srcDirs("$projectDir/src/main/kotlin")
    main.java.srcDirs("$projectDir/src/main/grpckt")
    main.java.srcDirs("$projectDir/src/main/grpc")
}

protobuf {
    generatedFilesBaseDir = "$projectDir/src/"
    protoc {
        artifact = "com.google.protobuf:protoc:$versionOfProtoc"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$versionOfProtocGenJava"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$versionOfProtocGenKotlin:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}