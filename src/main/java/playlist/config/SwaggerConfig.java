package playlist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider;
import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;
import com.wordnik.swagger.jaxrs.listing.ResourceListingProvider;

@Configuration
public class SwaggerConfig {

	@Bean
	public ApiListingResourceJSON getApiListingResourceJSON(){
		return new ApiListingResourceJSON();
	}
	
	@Bean
	public ApiDeclarationProvider getApiDeclarationProvider(){
		return new ApiDeclarationProvider();
	}
	
	@Bean
	public ResourceListingProvider getResourceListingProvider(){
		return new ResourceListingProvider();
	}
}
