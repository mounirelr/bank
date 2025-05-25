package ma.bank.controllers.client;


import ma.bank.entities.Compte;
import ma.bank.entities.Credit;
import ma.bank.entities.User;
import ma.bank.repository.CompteRepository;
import ma.bank.repository.CreditRepository;
import ma.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {

    private UserRepository userRepository;
    private CompteRepository compteRepository;
    private CreditRepository creditRepository;
    @Autowired
    public ClientController(UserRepository userRepository ,CompteRepository compteRepository,CreditRepository creditRepository) {
        this.userRepository = userRepository;
        this.compteRepository = compteRepository;
        this.creditRepository = creditRepository;
    }





    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User ConnectedUser = userRepository.findByUsername(userDetails.getUsername()).get();
        Compte compte = ConnectedUser.getCompte();
        model.addAttribute("ConnectedUser", ConnectedUser);
        model.addAttribute("balance", compte.getBalance());
        model.addAttribute("currency", String.valueOf(compte.getCurrency()));


        return "client/home";
    }



    @GetMapping("/credits")
    public String credits(@AuthenticationPrincipal UserDetails userDetails,Model model) {
        User ConnectedUser = userRepository.findByUsername(userDetails.getUsername()).get();
        List<Credit> userCredits = ConnectedUser.getCredits();
        model.addAttribute("ConnectedUser", ConnectedUser);
        model.addAttribute("credits", userCredits);
        return "client/credits";
    }

    @PostMapping("/saveCredit")

    public String saveCredit(@ModelAttribute Credit credit, @AuthenticationPrincipal UserDetails userDetails) {
        User ConnectedUser = userRepository.findByUsername(userDetails.getUsername()).get();
        credit.setUser(ConnectedUser);
        creditRepository.save(credit);
        return "redirect:/client/credits";
    }
}
