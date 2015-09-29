package playlist.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {	
	
	public JerseyConfig() {
		
		final String restEndpointsPackge = "playlist.api";
		final String jacksonPackage = "org.codehaus.jackson.jaxrs";
		final String swaggerJaxrsJsonPackage = "com.wordnik.swagger.jaxrs.json";
		final String swaggerJaxrsListingPackage = "com.wordnik.swagger.jaxrs.listing";
		
		packages(restEndpointsPackge, swaggerJaxrsJsonPackage, swaggerJaxrsListingPackage, jacksonPackage);		                
	}
}
