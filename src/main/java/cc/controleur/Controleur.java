package cc.controleur;

import cc.modele.*;
import cc.modele.data.*;
import cc.modele.data.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/api/gestionprojets")
public class Controleur {

    @Autowired
    FacadeModele facadeModele;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @PostMapping("/utilisateurs")
    public ResponseEntity<UtilisateurDTO> register(@RequestBody UtilisateurDTO utilisateurDTO) {
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

    @GetMapping("/utilisateurs/{idUtilisateur}")
    public ResponseEntity<Utilisateur> findUtilisateurById(Principal principal, @PathVariable int idUtilisateur) {


        try {

            String mail = principal.getName();
            Utilisateur utilisateurCo = facadeModele.getUtilisateurByEmail(mail);
            Utilisateur utilisateurWanted = facadeModele.getUtilisateurByIntId(idUtilisateur);

            // si c'est un prof osef il a a acces à tout
            if (utilisateurCo.getRoles()[0].equals("PROFESSEUR")) {
                return ResponseEntity.ok(utilisateurWanted);
            }
            if (!utilisateurCo.equals(utilisateurWanted))
                return ResponseEntity.status(403).build();
            return ResponseEntity.ok(utilisateurWanted);

        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.status(404).build();
        }


    }

    @GetMapping("/utilisateurs")
    public ResponseEntity<Collection<Utilisateur>> getAll() {

        return ResponseEntity.ok(facadeModele.getAllUtilisateurs());
    }

    @PostMapping("/projets")
    public ResponseEntity<Projet> creerProjet(Principal principal, @RequestParam String nomProjet, @RequestParam int nbGroupes) {


        try {
            Utilisateur userCo = facadeModele.getUtilisateurByEmail(principal.getName());
            Projet projet = facadeModele.creationProjet(userCo, nomProjet, nbGroupes);
            URI nextLocation = ServletUriComponentsBuilder.fromCurrentRequestUri()
                    .path("/{id}")
                    .buildAndExpand(projet.getIdProjet())
                    .toUri();
            return ResponseEntity.created(nextLocation).body(projet);

        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.notFound().build();
        } catch (DonneeManquanteException | NbGroupesIncorrectException e) {
            return ResponseEntity.status(406).build();
        }

    }

    @GetMapping("/projets/{idprojet}")
    public ResponseEntity<Projet> findProjetById( @PathVariable String idprojet) {


        try {
            return ResponseEntity.ok(facadeModele.getProjetById(idprojet));

        } catch (ProjetInexistantException e) {
            return ResponseEntity.notFound().build();
        }


    }

    @GetMapping("/projets/{idprojet}/groupes")
    public ResponseEntity<List<Groupe>> findGroupesByProject(@PathVariable String idprojet){

        try {
            Groupe[] groupes = facadeModele.getGroupeByIdProjet(idprojet);
            List<Groupe> listeGroupes = Arrays.stream(groupes).toList();
            return ResponseEntity.ok().body(listeGroupes);

        } catch (ProjetInexistantException e) {
            return ResponseEntity.notFound().build();
        }

    }
@PutMapping("/projets/{idprojet}/groupes/{idGroupe}")
    public ResponseEntity rejoindreGroupe(Principal principal,@PathVariable String idprojet, @PathVariable int idGroupe){

    try {
        Utilisateur utilisateurCo = facadeModele.getUtilisateurByEmail(principal.getName());
        facadeModele.rejoindreGroupe(utilisateurCo,idprojet,idGroupe);
        return ResponseEntity.status(202).build();
    } catch (UtilisateurInexistantException | ProjetInexistantException | MauvaisIdentifiantDeGroupeException e) {
        return ResponseEntity.notFound().build();
    } catch (EtudiantDejaDansUnGroupeException e) {
        return ResponseEntity.status(409).build();
    }

}

    @DeleteMapping("/gestionprojets/projets/{idProjet}/groupes/{idGroupe}")
    public ResponseEntity<String> quitterGroupe(Principal principal, @PathVariable String idProjet, @PathVariable int idGroupe) {
        try {
            Utilisateur utilisateur = facadeModele.getUtilisateurByEmail(principal.getName());
            facadeModele.quitterGroupe(utilisateur, idProjet, idGroupe);
            return ResponseEntity.status(202).build();
        } catch (UtilisateurInexistantException | MauvaisIdentifiantDeGroupeException | ProjetInexistantException e) {
            return ResponseEntity.status(404).build();
        } catch (EtudiantPasDansLeGroupeException e) {
            return ResponseEntity.status(406).build();
        }
    }







}
