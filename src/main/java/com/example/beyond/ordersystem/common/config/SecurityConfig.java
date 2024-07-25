//package com.beyond.board.common;
//
//import com.beyond.board.author.service.LoginSuccessHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity // spring security 설정을 customizing 화기 위함.
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SecurityConfig { // pre : 사전 post : 사후 인증검사
//
//    @Bean
//    public SecurityFilterChain myFilter(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
////                csrf 공격에 대한 설정은 하지 않겠다라는 의뭇
//                .csrf().disable()
//                .authorizeRequests()
//                // 아래 antMatchers -> 인증 제외
//                .antMatchers("/", "/author/create", "/author/login-screen")
//                .permitAll()
//                .anyRequest().authenticated()
//                .and()
//                // 만약에 세션로그인이 아니라, 토큰로그인일 경우에는
//                // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .formLogin()
//                .loginPage("/author/login-screen")
//                // doLogin 메서드는 스프링에서 미리 구현
//                .loginProcessingUrl("/doLogin")
//                // 다만, doLogin에 넘겨줄 email, password 속성명은 별도 지정
//                .usernameParameter("email")
//                .passwordParameter("password")
//                .successHandler(new LoginSuccessHandler())
//                .and()
//                .logout()
//                // security 에서 구현된 doLogOut 그대로 사용
//                .logoutUrl("/doLogout")
//                .and()
//                .build();
//    }
//}
