package jurisprudence_hub_be.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String[] allowedOrigins;

    @Value("${cors.allowed-origin-patterns:}")
    private String[] allowedOriginPatterns;

    @Value("${cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
    private String[] allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${cors.exposed-headers:Authorization}")
    private String[] exposedHeaders;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> explicitOrigins = new ArrayList<>();
        List<String> patternOrigins = new ArrayList<>();

        if (allowedOriginPatterns != null) {
            for (String pattern : allowedOriginPatterns) {
                if (pattern != null && !pattern.isBlank()) {
                    patternOrigins.add(pattern.trim());
                }
            }
        }

        if (allowedOrigins != null) {
            for (String origin : allowedOrigins) {
                if (origin != null && !origin.isBlank()) {
                    String trimmed = origin.trim();
                    if (allowCredentials && trimmed.contains("*")) {
                        patternOrigins.add(trimmed);
                    } else {
                        explicitOrigins.add(trimmed);
                    }
                }
            }
        }

        if (!explicitOrigins.isEmpty()) {
            configuration.setAllowedOrigins(explicitOrigins);
        }

        if (!patternOrigins.isEmpty()) {
            configuration.setAllowedOriginPatterns(patternOrigins);
        }

        if (explicitOrigins.isEmpty() && patternOrigins.isEmpty()) {
            if (allowCredentials) {
                configuration.setAllowedOriginPatterns(List.of("http://localhost:[*]"));
            } else {
                configuration.setAllowedOrigins(List.of("*"));
            }
        }

        @SuppressWarnings("null")
        List<String> methods = allowedMethods != null ? Arrays.stream(allowedMethods).filter(s -> s != null).map(s -> s.trim()).toList() : List.of();
        @SuppressWarnings("null")
        List<String> headers = allowedHeaders != null ? Arrays.stream(allowedHeaders).filter(s -> s != null).map(s -> s.trim()).toList() : List.of();
        @SuppressWarnings("null")
        List<String> exposed = exposedHeaders != null ? Arrays.stream(exposedHeaders).filter(s -> s != null).map(s -> s.trim()).toList() : List.of();
        configuration.setAllowedMethods(methods);
        configuration.setAllowedHeaders(headers);
        configuration.setExposedHeaders(exposed);
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    @SuppressWarnings("null")
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
