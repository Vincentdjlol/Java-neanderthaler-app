plugins {
    id 'java'
}

group = 'nl.neanderthaler'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    testImplementation platform('org.junit:junit-bom:6.0.0')
    testImplementation 'org.junit.jupiter:junit-jupiter'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    // Source: https://mvnrepository.com/artifact/info.picocli/picocli
    implementation("info.picocli:picocli:4.7.7")
}

test {
    useJUnitPlatform()
}