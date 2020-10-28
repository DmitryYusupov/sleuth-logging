package ru.yusdm.training.city.api

import org.springframework.stereotype.Component
import ru.yusdm.training.common.logger
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

val log = CityWebFilter::class.logger

@Component
class CityWebFilter: Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest

        httpRequest.headerNames.asSequence().forEach {
            log.info("Header '{}'; Value '{}'", it, httpRequest.getHeader(it))
        }

        chain.doFilter(request, response);
    }

}