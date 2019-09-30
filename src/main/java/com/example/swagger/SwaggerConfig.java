package com.example.swagger;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Allows for swagger documentation.
 * 
 * @author devon
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	/**
	 * Provides basic information about the service, such as contact information and
	 * licensing.
	 * 
	 * @return
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build()
				.apiInfo(new ApiInfo("Thing API", "We got things.", "thing TOS", "Terms of service",
						new Contact("Devon Virden", "https://i.imgur.com/g3MdE5u.gif", "devonjvirden@hotmail.com"),
						"License of API", "API license URL", Collections.emptyList()))
				.enable(true);
	}

}
