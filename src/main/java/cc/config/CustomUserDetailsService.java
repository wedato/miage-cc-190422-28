package cc.config;

import cc.modele.FacadeModele;
import cc.modele.data.Utilisateur;
import cc.modele.data.exceptions.UtilisateurInexistantException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private FacadeModele facadeModele;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur;
        try {
             utilisateur = facadeModele.getUtilisateurByEmail(email);
            return User.builder()
                    .username(utilisateur.getLogin())
                    .password(bCryptPasswordEncoder
                            .encode(utilisateur.getPassword()))
                    .roles(utilisateur.getRoles())
                    .build();
        } catch (UtilisateurInexistantException e) {
            throw new UsernameNotFoundException(email);
        }



    }
}
