package es.unizar.tmdad.lab0;

import java.security.Principal;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

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
                .antMatchers("/admin","/functions_admin.js","/app/settings")
                .authenticated()
                .and().logout().logoutSuccessUrl("/").permitAll();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);        
    }

}
