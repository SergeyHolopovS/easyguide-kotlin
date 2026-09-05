package com.easyguide.backend

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Service

private val HAVE_EXACTLY_ONE_PUBLIC_EXECUTE_METHOD = object : ArchCondition<JavaClass>(
    "have exactly one public method named 'execute'"
) {
    override fun check(item: JavaClass, events: ConditionEvents) {
        val executeMethods = item.methods.filter {
            it.name == "execute" && it.modifiers.contains(JavaModifier.PUBLIC)
        }
        if (executeMethods.size != 1) {
            events.add(
                SimpleConditionEvent.violated(
                    item,
                    "${item.name} declares ${executeMethods.size} public 'execute' method(s), expected exactly 1",
                )
            )
        }
    }
}

/** domain.model, за исключением enum'ов — их использование в контроллерах как типов @RequestParam/@PathVariable
 *  оправдано (например, TourCategory/BookingStatus для фильтров): это штатный Spring-биндинг с автоматическим
 *  400 на невалидное значение, замена на String с ручным парсингом ничего не выигрывает. */
private val RESIDE_IN_DOMAIN_MODEL_AND_NOT_ENUM = object : DescribedPredicate<JavaClass>(
    "reside in a domain.model package and are not enums"
) {
    override fun test(input: JavaClass): Boolean =
        input.packageName.contains(".domain.model") && !input.isAssignableTo(Enum::class.java)
}

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

    @Test
    @DisplayName("Use-case (@Service) называется *UseCase и имеет единственный публичный метод execute")
    fun `use cases are named UseCase and expose a single execute method`() {
        classes()
            .that().areAnnotatedWith(Service::class.java)
            .should().haveSimpleNameEndingWith("UseCase")
            .andShould(HAVE_EXACTLY_ONE_PUBLIC_EXECUTE_METHOD)
            .allowEmptyShould(true)
            .check(classes)
    }

    @Test
    @DisplayName("Controller не зависит от domain.model напрямую (кроме enum'ов) — только Result/Response")
    fun `controllers do not depend on domain model classes directly`() {
        noClasses()
            .that().haveSimpleNameEndingWith("Controller")
            .should().dependOnClassesThat(RESIDE_IN_DOMAIN_MODEL_AND_NOT_ENUM)
            .allowEmptyShould(true)
            .check(classes)
    }
}
