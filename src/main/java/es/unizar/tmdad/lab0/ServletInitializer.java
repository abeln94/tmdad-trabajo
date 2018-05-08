package es.unizar.tmdad.lab0;

import java.io.IOException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * Spring dark magic
 */
@Configuration
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    // To redirect http:8080 -> https:8443
    //help from https://drissamri.be/blog/java/enable-https-in-spring-boot/
    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {

            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };

        tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
        return tomcat;
    }

    private Connector initiateHttpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);

        return connector;
    }

    //load the other machines on startup
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {

        String[] machines = new String[]{
            "https://carlos-abel-tmdad-trabajo-2.herokuapp.com/",
            "https://carlos-abel-tmdad-trabajo-3.herokuapp.com/",
            "https://carlos-abel-tmdad-trabajo-4.herokuapp.com/"
        };

        for (String machine : machines) {
            new Thread(() -> {
                try {
                    System.out.println("Pinging machine " + machine);
                    HttpsURLConnection connection = (HttpsURLConnection) new URL(machine).openConnection();
                    connection.setRequestMethod("HEAD");
                    connection.setConnectTimeout(100 * 1000);
                    int responseCode = connection.getResponseCode();
                    if (responseCode != 200) {
                        System.out.println("Oh oh, couldn't akawe machine " + machine + " (" + responseCode + ")");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }
}
