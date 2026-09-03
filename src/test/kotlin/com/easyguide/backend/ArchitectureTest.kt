package com.easyguide.backend

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ArchitectureTest {

    private val classes = ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("com.easyguide.backend")

    @Test
    @DisplayName("Domain не зависит от фреймворков")
    fun `domain does not depend on frameworks`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "org.springframework..",
                "jakarta.persistence..",
                "com.fasterxml..",
            )
            .allowEmptyShould(true)
            .check(classes)
    }

    @Test
    @DisplayName("Application не зависит от инфраструктуры")
    fun `application does not depend on infrastructure or presentation`() {
        noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..infrastructure..")
            .allowEmptyShould(true)
            .check(classes)
    }

    @Test
    @DisplayName("Domain/repository содержит только интерфейсы")
    fun `domain repository classes are interfaces`() {
        classes()
            .that().resideInAPackage("..domain.repository..")
            .should().beInterfaces()
            .allowEmptyShould(true)
            .check(classes)
    }
}
