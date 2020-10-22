package ru.yusdm.training.city.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class WebConfigs {

    @Bean
    fun restTemplate() = RestTemplate()

}