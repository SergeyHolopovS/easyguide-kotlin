package com.easyguide.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

// UserDetailsServiceAutoConfiguration отключается только если есть бин UserDetailsService/
// AuthenticationManager/AuthenticationProvider — одного кастомного SecurityFilterChain
// недостаточно. Аутентификация в приложении полностью своя (JWT, без UserDetailsService),
// поэтому дефолтный in-memory пользователь не нужен и только шумит в логах.
@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
@EnableScheduling
class BackendApplication

fun main(args: Array<String>) {
	runApplication<BackendApplication>(*args)
}
