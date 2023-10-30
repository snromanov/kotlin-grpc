import com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA
import com.google.protobuf.gradle.id
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    idea
    id("com.google.protobuf") version "0.9.4"
    kotlin("jvm") version "1.7.20"
    application
    id("io.gitlab.arturbosch.detekt").version("1.19.0")
    id("com.adarshr.test-logger") version "3.2.0"
}

subprojects {
    group = "org.romanov"
    version = "0.0.1"
}

val versionOfDetect: String by project
val versionOfLogback: String by project
val versionOfCfg4j: String by project
val versionOfKonfig: String by project
val versionOfKotest: String by project
val kotlinxVersion: String by project
val kotestDateTimeVersion: String by project
val kotestPropertyArbs: String by project
val versionOfKotlinLogging: String by project
val versionOfGrpcStubKt: String by project
val versionOfGrpcStub: String by project
val versionOfGrpcProtobuf: String by project
val versionOfProtobuf: String by project
val versionOfJavaxAnnotation: String by project
val nettyVersion: String by project
val versionOfJacksonDataType: String by project
val versionOfKotestArbsProperty: String by project
val versionOfFaker: String by project
val versionOfKotestJson: String by project
val versionOfProtoc: String by project
val versionOfProtocGenJava: String by project
val versionOfProtocGenKotlin: String by project
val versionOfKotlinxCoroutinesCore: String by project
val versionOfKotlin: String by project

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$versionOfDetect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$versionOfKotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$versionOfKotlin")
    implementation("com.natpryce:konfig:$versionOfKonfig")
    implementation(platform("io.kotest:kotest-bom:$versionOfKotest"))
    implementation("io.kotest:kotest-assertions-core")
    implementation("io.kotest:kotest-runner-junit5")
    implementation("io.kotest:kotest-property")
    implementation("io.kotest:kotest-framework-datatest")
    implementation("io.kotest:kotest-assertions-json-jvm:$versionOfKotestJson")
    implementation("io.kotest.extensions:kotest-property-arbs:$versionOfKotestArbsProperty")
    implementation("ch.qos.logback:logback-classic:$versionOfLogback")
    implementation("org.cfg4j:cfg4j-core:$versionOfCfg4j")
    implementation("io.github.microutils:kotlin-logging-jvm:$versionOfKotlinLogging")
    implementation("com.google.protobuf:protobuf-kotlin:$versionOfProtobuf")
    implementation("io.grpc:grpc-stub:$versionOfGrpcStub")
    implementation("javax.annotation:javax.annotation-api:$versionOfJavaxAnnotation")
    implementation("io.grpc:grpc-testing:$versionOfGrpcProtobuf")
    implementation("io.grpc:grpc-netty:$versionOfGrpcStub")
    implementation("io.grpc:grpc-all:$versionOfGrpcStub")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$versionOfJacksonDataType")
    implementation("io.github.serpro69:kotlin-faker:$versionOfFaker")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$versionOfKotlin")
    implementation("io.grpc:grpc-kotlin-stub:$versionOfGrpcStubKt")
    implementation("io.grpc:grpc-stub:$versionOfGrpcStub")
    implementation("io.grpc:grpc-protobuf:$versionOfGrpcStub")
    implementation("javax.annotation:javax.annotation-api:$versionOfJavaxAnnotation")
    implementation("com.google.protobuf:protobuf-java:$versionOfProtobuf")
    implementation("com.google.protobuf:protobuf-kotlin:$versionOfProtobuf")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$versionOfKotlinxCoroutinesCore")
}

sourceSets {
    val main by getting
    main.java.srcDirs("$projectDir/src/main/java")
    main.java.srcDirs("$projectDir/src/main/kotlin")
    main.java.srcDirs("$projectDir/src/main/grpckt")
    main.java.srcDirs("$projectDir/src/main/grpc")
}

testlogger {
    theme = MOCHA
    showExceptions = true
    showStackTraces = true
    showFullStackTraces = false
    showCauses = true
    slowThreshold = 2000
    showSummary = true
    showSimpleNames = false
    showPassed = true
    showSkipped = true
    showFailed = true
    showStandardStreams = false
    showPassedStandardStreams = true
    showSkippedStandardStreams = true
    showFailedStandardStreams = true
    logLevel = LogLevel.LIFECYCLE
}

detekt {
    autoCorrect = true
    toolVersion = versionOfDetect
    source = files("src/main/kotlin")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-java-parameters",
                "-Xjsr305=strict",
                "-progressive",
                "-Xopt-in=kotlin.RequiresOptIn"
            )
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Detekt> {
        this.jvmTarget = JavaVersion.VERSION_11.toString()
    }

    withType<Test> {
        java.sourceSets["test"].java {
            srcDir("src/main/kotlin")
        }
        useJUnitPlatform()
        systemProperty("java.net.preferIPv4Stack", "true")
        systemProperties(System.getProperties().map { it.key.toString() to it.value }.toMap())
        testLogging {
            events = setOf(
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
                TestLogEvent.STANDARD_OUT,
                TestLogEvent.STANDARD_ERROR
            )
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }


    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:$versionOfProtoc"
        }
        plugins {
            id("grpc") {
                artifact = "io.grpc:protoc-gen-grpc-java:$versionOfProtocGenJava"
            }
            id("grpckt") {
                artifact = "io.grpc:protoc-gen-grpc-kotlin:$versionOfProtocGenKotlin:jdk8@jar"
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
}

allprojects {
    repositories {
        mavenCentral()
    }
}


dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("script-runtime"))

}
