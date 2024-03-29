package es.carlosabel.tmdad.trabajo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Application class. Spring dark magic.
 */
@SpringBootApplication
@EnableOAuth2Sso
public class Application extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/", "/login**", "/webjars/**")
                .permitAll()
                .antMatchers("/configuration", "/functions_admin.js", "/app/settings")
                .authenticated()
                .and().logout().logoutSuccessUrl("/").permitAll();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
