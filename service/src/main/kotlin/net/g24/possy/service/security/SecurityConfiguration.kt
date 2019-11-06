/*
 * This file is part of possy.
 *
 * possy is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * possy is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with possy. If not, see <http://www.gnu.org/licenses/>.
 */

package net.g24.possy.service.security

import net.g24.possy.service.PossyConfigurationProperties
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

@EnableWebSecurity
@Configuration
internal class SecurityConfiguration(
        securityProperties: SecurityProperties,
        possyConfigurationProperties: PossyConfigurationProperties,
        auth: AuthenticationManagerBuilder
) {

    companion object {
        const val ROLE_ACTUATOR = "ROLE_ACTUATOR"
        const val ROLE_APPLICATION = "ROLE_APPLICATION"

        val PUBLIC_URLS = arrayOf(
                "/error",
                "/images/**",
                "/icons/**",
                "/manifest.webmanifest",
                "/offline.html",
                "/sw.js",
                "/frontend/**",
                "/frontend-es5/**",
                "/frontend-es6/**",
                "/VAADIN/**",
                "/vaadinServlet/PUSH*",

                // swagger
                "/webjars/**",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/v2/api-docs"
        )
    }

    init {
        auth.inMemoryAuthentication()
                .withUser(securityProperties.user.name)
                .password(securityProperties.user.password)
                .authorities(ROLE_APPLICATION)
                .and()
                .withUser(possyConfigurationProperties.admin.username)
                .password(possyConfigurationProperties.admin.password)
                .authorities(ROLE_ACTUATOR, ROLE_APPLICATION)
    }

    /**
     * As we have currently only one user and no persistence layer we use
     * [org.springframework.security.crypto.password.NoOpPasswordEncoder] here.
     * This is necessary for the not so secure (user credentials are stored in the cookie)
     * token based remember-me functionality we use. Furthermore we need unsalted passwords
     * to be able to restart the application without invalidating the remember-me cookies.
     *
     * In future, if we have an persistence layer, we can use
     * [org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices]
     * in order to check for valid remember-me cookies. With this approach it's not necessary to check
     * against the stored passwords in the cookie anymore.
     */
    @Suppress("DEPRECATION")
    @Bean
    fun defaultPasswordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
    }

    /** rest api & actuator security config */
    @Configuration
    @Order(1)
    internal class ApiAndActuatorWebSecurityConfigurationAdapter : WebSecurityConfigurerAdapter() {

        override fun configure(http: HttpSecurity) {
            http
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .requestMatchers()
                    .antMatchers("/api/**")
                    .requestMatchers(EndpointRequest.toAnyEndpoint())
                    .and()
                    .authorizeRequests()
                    .antMatchers("/api/**")
                    .hasAuthority(ROLE_APPLICATION)
                    .requestMatchers(EndpointRequest.to("health", "info"))
                    .permitAll()
                    .requestMatchers(EndpointRequest.toAnyEndpoint().excluding("health", "info"))
                    .hasAuthority(ROLE_ACTUATOR)
                    .and()
                    .httpBasic()
        }
    }

    /** form login for Vaadin */
    @Configuration
    @Order(2)
    internal class FormLoginWebSecurityConfigurationAdapter(
            private val possyConfigurationProperties: PossyConfigurationProperties
    ) : WebSecurityConfigurerAdapter() {

        override fun configure(http: HttpSecurity) {
            // Not using Spring CSRF here to be able to use plain HTML for the login page.
            // Use Vaadin's CSRF mechanism.
            http.csrf().disable()

            http.requestCache().requestCache(CustomRequestCache())

            http
                    .authorizeRequests()

                    .antMatchers("/login*")
                    .permitAll()

                    .antMatchers(*PUBLIC_URLS)
                    .permitAll()

                    .requestMatchers(RequestMatcher { SecurityUtils.isFrameworkInternalRequest(it) })
                    .permitAll()

                    .antMatchers("/**") // all other routes
                    .hasAuthority(ROLE_APPLICATION)

                    .anyRequest()
                    .fullyAuthenticated()

            http.formLogin()
                    .loginPage("/login")
                    .successHandler(SavedRequestAwareAuthenticationSuccessHandler())

            http.logout()
                    .addLogoutHandler(VaadinSessionClosingLogoutHandler())
                    // disable crsf for logoremberut, see https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#csrf-logout
                    .logoutRequestMatcher(AntPathRequestMatcher("/logout"))

            http.rememberMe()
                    .key(possyConfigurationProperties.encryptionKey)
                    .tokenValiditySeconds(possyConfigurationProperties.rememberMeValiditySeconds!!)

            http.sessionManagement().sessionFixation().newSession()

            // necessary for preview w/ frames
            http.headers().frameOptions().sameOrigin()
        }
    }
}
