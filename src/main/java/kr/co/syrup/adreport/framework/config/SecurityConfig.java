package kr.co.syrup.adreport.framework.config;

import kr.co.syrup.adreport.framework.config.properties.ProfileProperties;
import kr.co.syrup.adreport.framework.filters.CORSFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
//@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {

	public static class Roles {
		public static final String ANONYMOUS = "ANONYMOUS";
		public static final String USER = "USER";
		public static final String ADMIN = "ADMIN";

		private static final String ROLE_ = "ROLE_";
		public static final String ROLE_ANONYMOUS = ROLE_ + ANONYMOUS;
	}

	private static final String[] AUTH_LIST = {
			"/configuration/ui",
			"/configuration/security",

			// -- swagger ui
			"/api/v2/api-docs",
			"/v2/api-docs",
			"/swagger-ui.html",
			"/webjars/**",
			"/v2/swagger.json",
			"**/swagger-resources/**",
			"/swagger-resources",

			"/css/**",
			"/js/**",
			"/images/**",
			"/resources/**",
			"/actuator/**",
			"/health/**",
			"/api/v1/web-event-test/**"

	};


	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Configuration
	protected class AuthenticationSecurity extends GlobalAuthenticationConfigurerAdapter {

		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication();
		}
	}

	@Configuration
	// SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
	// https://www.anycodings.com/1questions/5152264/ordersecuritypropertiesaccessoverrideorder-vs-managementserverpropertiesaccessoverrideorder-in-spring-security
	// https://docs.spring.io/spring-boot/docs/2.0.0.M3/api/constant-values.html#org.springframework.boot.autoconfigure.security.SecurityProperties.ACCESS_OVERRIDE_ORDER
	// 두 사이트 참조하여 해당 값으로 세팅함
	//	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	@Order(SecurityProperties.BASIC_AUTH_ORDER - 2)
	protected class ApplicationSecurity extends WebSecurityConfigurerAdapter {

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers(AUTH_LIST);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// UTF-8 설정
			CharacterEncodingFilter filter = new CharacterEncodingFilter();
			filter.setEncoding("UTF-8");
			filter.setForceEncoding(true);
			http.addFilterBefore(filter, CsrfFilter.class);

			CORSFilter corsFilter = new CORSFilter();
			http.addFilterBefore(corsFilter, CsrfFilter.class);

//			http.authorizeRequests()
//					.antMatchers("/api/auth/**").authenticated()
//					.anyRequest().permitAll();

			//.addFilterBefore(new JwtTokenFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
			// PROD 가 아닐 경우
			if (!ProfileProperties.isProd()) {
				http.httpBasic().and().authorizeRequests().antMatchers("/**").permitAll() //
						.and().csrf().disable()//
						.anonymous().authorities(Roles.ROLE_ANONYMOUS);//

				return;
			}

//			// PROD 설정
//			http.addFilterBefore(tokenAuthorizationFilter(), BasicAuthenticationFilter.class)
//
//					/*
//					 * .and().logout() .logoutSuccessHandler(null)
//					 */
//
//					.authorizeRequests()
//					.antMatchers("/user/login", "/user/logout", "/user/signup", "/user/hasAccount",
//							"/user/getCertificationTxt", "/user/certify", "/test/**", "/etc/**", "/admin/login",
//							"/admin/loginProcess", "/admin/main")
//					.permitAll()
//
//					// ANONYMOUS
//					.antMatchers("/user/signup", "/user/hasAccount").hasAnyRole(Roles.ANONYMOUS)
//
//					// USER
//					.antMatchers("/user/registrationFCMToken", "/api/**").hasAnyRole(Roles.USER)
//
//					// Admin
//					.antMatchers("/admin/**", "/api/report/**", "/api/admin/**").hasAnyRole(Roles.ADMIN)
//
//					// 그외엔 deny
//					.antMatchers("/**").denyAll()
//
//					.and().csrf().disable().anonymous().authorities(Roles.ROLE_ANONYMOUS)
//
//			// .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//			//
//			;

		}

		@Bean
		public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
			return authenticationConfiguration.getAuthenticationManager();
		}

//		@Bean
//		public UserDetailsService userDetailsService() {
//			return new CustomUserDetailsService(userRepository);
//		}


		/**
		 * Error page 설정했을 경우 spring security principal이 error page 에서 null로 되어 버리는 문제점을
		 * 해결
		 *
		 * @return
		 */
/*
		@Bean
		public FilterRegistrationBean getSpringSecurityFilterChainBindedToError() {

			FilterRegistrationBean registration = new FilterRegistrationBean();
			registration.setFilter(springSecurityFilterChain);
			registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));

			return registration;
		}
*/

		private OncePerRequestFilter tokenAuthorizationFilter() {

			return new OncePerRequestFilter() {

				@Override
				protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
						FilterChain filterChain) throws ServletException, IOException {
					// TODO Auto-generated method stub

				}
			};
		}

	}
}
