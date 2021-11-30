package com.yu;

import com.yu.filter.JwtTokenBasedSecurityFilter;
import com.yu.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${people-service.api-base-url}")
    protected String apiBaseUrl;

    @Value("${people-service.options.enable-debug-endpoint}")
    protected boolean enableDebugEndpoint;

    @Value("${people-service.options.disable-permission-check}")
    protected boolean disablePermissionCheck;

    private static final Logger logger = LoggerFactory.getLogger(AppSecurityConfig.class);

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // ensure the service is completely stateless (and never set cookie)
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.csrf().disable();

        // access control for debug endpoints
        if (this.enableDebugEndpoint) {
            http.authorizeRequests()
                    .antMatchers(apiBaseUrl+"/debug/**").permitAll();
        }

        // actuator endpoints
        http.authorizeRequests()
                .antMatchers("/actuator/**").permitAll();

        // access control for endpoints
        if (disablePermissionCheck) {
            logger.warn("app started with disablePermissionCheck.");
            http.authorizeRequests().anyRequest().permitAll();
        } else {
            http
                .authorizeRequests()
                .antMatchers(apiBaseUrl+"/about")
                    .permitAll()

                .antMatchers(HttpMethod.GET, apiBaseUrl+"/people")
                    .hasAnyAuthority(Permission.ANYTHING.name(), Permission.PEOPLE_SEARCH.name())
                .antMatchers(HttpMethod.POST, apiBaseUrl+"/people")
                    .hasAnyAuthority(Permission.ANYTHING.name(), Permission.PEOPLE_ADD.name())
                .antMatchers(HttpMethod.GET, apiBaseUrl+"/people/count")
                    .hasAnyAuthority(Permission.ANYTHING.name(), Permission.PEOPLE_SEARCH.name())
                .antMatchers(HttpMethod.GET, apiBaseUrl+"/people/*")
                    .hasAnyAuthority(Permission.ANYTHING.name(), Permission.PEOPLE_GET.name())
                .antMatchers(HttpMethod.PUT, apiBaseUrl+"/people/*")
                    .hasAnyAuthority(Permission.ANYTHING.name(), Permission.PEOPLE_UPDATE.name())
                .antMatchers(HttpMethod.GET, apiBaseUrl+"/people/search/*")
                    .hasAnyAuthority(Permission.ANYTHING.name(), Permission.PEOPLE_SEARCH.name())

                .antMatchers(HttpMethod.GET, apiBaseUrl+"/dict/*")
                    .authenticated()

                .anyRequest().denyAll()
            ;
        }

        http.addFilterBefore(new JwtTokenBasedSecurityFilter(), UsernamePasswordAuthenticationFilter.class);
    }

}
