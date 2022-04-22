package cc.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class CryptoConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/api/gestionprojets/utilisateurs").permitAll()
                .antMatchers(HttpMethod.POST,"/api/gestionprojets/projets").hasRole("PROFESSEUR")
                .antMatchers(HttpMethod.GET,"/api/gestionprojets/projets/*").authenticated()
                .antMatchers(HttpMethod.GET,"/api/gestionprojets/projets/*/groupes").authenticated()
                .antMatchers(HttpMethod.GET,"/api/gestionprojets/utilisateurs").hasRole("PROFESSEUR")
                .antMatchers(HttpMethod.GET,"/api/gestionprojets/utilisateurs/*").authenticated()
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
