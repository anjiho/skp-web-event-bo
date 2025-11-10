package kr.co.syrup.adreport.framework.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	private static final String API_NAME = "광고리포팅 API";
	private static final String API_VERSION = "0.0.1";
	private static final String API_DESCRIPTION = "광고리포팅 API 명세서";

	@Bean
	@Profile({"local", "dev", "alp"})
	public Docket api() {

		return new Docket(DocumentationType.SWAGGER_2)
				.consumes(getConsumeContentTypes())
				.produces(getProduceContentTypes())
				.apiInfo(getApiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("kr.co.syrup.adreport.controller.rest"))
				.paths(PathSelectors.ant("/api/**"))
				//.paths(Predicates.not(PathSelectors.ant("/api/mail/**")))
				.build();

/*
		ParameterBuilder parameterBuilder = new ParameterBuilder();

		parameterBuilder.name(HttpHeaders.AUTHORIZATION)
				//.description("Access Tocken")
				.modelRef(new ModelRef("string"))
				.parameterType("header")
				.required(false)
				.build();

		List<Parameter> globalParamters = new ArrayList<>();
		globalParamters.add(parameterBuilder.build());

		return new Docket(DocumentationType.SWAGGER_2)
				.globalOperationParameters(globalParamters)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("kr.co.syrup.adreport.controller.rest.adreport"))
				//.apis(RequestHandlerSelectors.any())
				//.paths(PathSelectors.regex("/api/*"))
				.paths(PathSelectors.any())
				.build();
	*/}

	private Set<String> getConsumeContentTypes() {
		Set<String> consumes = new HashSet<>();
		consumes.add("application/json;charset=UTF-8");
		consumes.add("application/x-www-form-urlencoded");
		return consumes;
	}

	private Set<String> getProduceContentTypes() {
		Set<String> produces = new HashSet<>();
		produces.add("application/json;charset=UTF-8");
		return produces;
	}

	public ApiInfo getApiInfo() {
		return new ApiInfoBuilder()
				.title(API_NAME)
				.version(API_VERSION)
				.description(API_DESCRIPTION)
				.build();
	}
}
