package com.easyguide.backend.shared.infrastructure.web

import com.easyguide.backend.shared.presentation.security.CurrentUserIdArgumentResolver
import com.easyguide.backend.shared.presentation.security.CurrentUserIdOrNullArgumentResolver
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val currentUserIdArgumentResolver: CurrentUserIdArgumentResolver,
    private val currentUserIdOrNullArgumentResolver: CurrentUserIdOrNullArgumentResolver,
    @Value($$"${app.storage.local.directory:./uploads}") private val uploadsDirectory: String,
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(currentUserIdArgumentResolver)
        resolvers.add(currentUserIdOrNullArgumentResolver)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/uploads/**")
            .addResourceLocations("file:$uploadsDirectory/")
    }

}
