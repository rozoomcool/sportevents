package com.govzcode.sportevents.auth

import com.govzcode.sportevents.service.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.lang.NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


@Component
class JwtAuthFilter(
        private val accessTokenService: AccessTokenService,
        private val userService: CustomUserDetailsService
) : OncePerRequestFilter() {

    companion object {
        const val BEARER_PREFIX = "Bearer "
        const val AUTHORIZATION_HEADER_NAME = "Authorization"
        private val logger: Logger = LoggerFactory.getLogger(JwtAuthFilter::class.java)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
            @NonNull request: HttpServletRequest,
            @NonNull response: HttpServletResponse,
            @NonNull filterChain: FilterChain
    ) {
        Companion.logger.info("JWT FILTER, SOME REQUEST TRY")
        val authHeader = request.getHeader(AUTHORIZATION_HEADER_NAME)
        if (!StringUtils.hasLength(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader, BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }
        val jwt = authHeader.substring(BEARER_PREFIX.length)
        val username: String = try {
            accessTokenService.extractUsername(jwt)
        } catch (e: Exception) {
            println("::::" + e.message)
            filterChain.doFilter(request, response)
            return
        }
        if (StringUtils.hasLength(username) && SecurityContextHolder.getContext().authentication == null) {
            val userDetails: UserDetails = try {
                userService
                        .loadUserByUsername(username)
            } catch (e: Exception) {
                println("::::" + e.message)
                filterChain.doFilter(request, response)
                return
            }

            if (accessTokenService.isTokenValid(jwt, userDetails)) {
                val context = SecurityContextHolder.createEmptyContext()
                val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                context.authentication = authToken
                SecurityContextHolder.setContext(context)
            }
        }
        filterChain.doFilter(request, response)
    }
}