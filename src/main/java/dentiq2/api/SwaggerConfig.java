package dentiq2.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.*;


@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("greetings")
				.select()
                .apis(RequestHandlerSelectors.basePackage("dentiq2.api.controller"))
                .paths(PathSelectors.any())
                .build();
		
	}
	
//	private ApiInfo apiInfo() {
//		return new ApiInfoBuilder()
//                .title("DentalPlus REST API Document System")
//                .description("DentalPlus REST Documents with Swagger")
//                .termsOfServiceUrl("http://www-03.ibm.com/software/sla/sladb.nsf/sla/bm?Open")
//                .contact(new Contact("Ju-hyeon Lee", "http://walkonthenet.com", "leejuhyeon@gmail.com"))
//                .license("Apache License Version 2.0")
//                .licenseUrl("https://github.com/IBM-Bluemix/news-aggregator/blob/master/LICENSE")
//                .version("1.0")
//                .build();
//	}

}
