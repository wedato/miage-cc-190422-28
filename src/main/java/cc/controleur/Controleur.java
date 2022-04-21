package cc.controleur;

import cc.modele.*;
import cc.modele.data.*;
import cc.modele.data.exceptions.*;
import cc.utils.EmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.Objects;


@RestController
@RequestMapping("/api/gestionprojets")
public class Controleur {

    @Autowired
    FacadeModele facadeModele;


    @PostMapping("/utilisateurs")
    public ResponseEntity<UtilisateurDTO> register(@RequestBody UtilisateurDTO utilisateurDTO){
        try {
            int utilisateur = facadeModele.enregistrerUtilisateur(utilisateurDTO.getLogin(), utilisateurDTO.getPassword());
            URI nextLocation = ServletUriComponentsBuilder.fromCurrentRequestUri()
                    .path("/{id}")
                    .buildAndExpand(utilisateur)
                    .toUri();

            return ResponseEntity.created(nextLocation).body(utilisateurDTO);
        } catch (DonneeManquanteException | EmailMalFormeException e) {
            return ResponseEntity.status(406).build();
        } catch (EmailDejaPrisException e) {
            return ResponseEntity.status(409).build();
        }

    }


}
