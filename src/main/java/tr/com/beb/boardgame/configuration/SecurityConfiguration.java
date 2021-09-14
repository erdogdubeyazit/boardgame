package tr.com.beb.boardgame.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import tr.com.beb.boardgame.domain.common.security.AccessDeniedHandlerImpl;
import tr.com.beb.boardgame.web.api.authenticate.AuthenticationFilter;
import tr.com.beb.boardgame.web.api.authenticate.DefaultAuthenticationFailureHandler;
import tr.com.beb.boardgame.web.api.authenticate.DefaultAuthenticationSuccessHandler;
import tr.com.beb.boardgame.web.api.authenticate.DefaultLogoutSuccessHandler;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private static final String[] PUBLIC = new String[] { "/error", "/login", "/logout", "/register",
      "/api/registrations" };

      @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
      .and()
        .authorizeRequests()
        .antMatchers(PUBLIC).permitAll()
        .anyRequest().authenticated()
      .and()
        .addFilterAt(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .formLogin()
        .loginPage("/login")
      .and()
        .logout()
        .logoutUrl("/api/me/logout")
        .logoutSuccessHandler(logoutSuccessHandler())
      .and()
      .csrf().disable();
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers("/static/**", "/js/**", "/css/**", "/images/**", "/favicon.ico");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationFilter authenticationFilter() throws Exception {
    AuthenticationFilter authenticationFilter = new AuthenticationFilter();
    authenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
    authenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
    authenticationFilter.setAuthenticationManager(authenticationManagerBean());
    return authenticationFilter;
  }

  @Bean
  public AuthenticationSuccessHandler authenticationSuccessHandler() {
    return new DefaultAuthenticationSuccessHandler();
  }

  @Bean
  public AuthenticationFailureHandler authenticationFailureHandler() {
    return new DefaultAuthenticationFailureHandler();
  }

  @Bean
  public LogoutSuccessHandler logoutSuccessHandler() {
    return new DefaultLogoutSuccessHandler();
  }

  public AccessDeniedHandler accessDeniedHandler() {
    return new AccessDeniedHandlerImpl();
  }

}
