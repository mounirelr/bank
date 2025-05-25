package ma.bank.controllers.admin;


import jakarta.transaction.Transactional;
import ma.bank.entities.*;
import ma.bank.repository.CompteRepository;
import ma.bank.repository.CreditRepository;
import ma.bank.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")

public class AdminController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;
    private CompteRepository compteRepository;
    private CreditRepository creditRepository;
    @Autowired
    public AdminController(UserRepository userRepository ,CompteRepository compteRepository,CreditRepository creditRepository) {
        this.userRepository = userRepository;
        this.compteRepository = compteRepository;
        this.creditRepository = creditRepository;
    }






    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails userDetails,Model model) {
        User ConnectedUser = userRepository.findByUsername(userDetails.getUsername()).get();
        model.addAttribute("ConnectedUser", ConnectedUser);
        return "admin/home";
    }



    @GetMapping("/clients")
    public String listClients(@AuthenticationPrincipal UserDetails userDetails,Model model) {
        User ConnectedUser = userRepository.findByUsername(userDetails.getUsername()).get();
        List<User> clients = userRepository.findAll().stream().filter(e->e.getRole()==Role.Client).toList();
        model.addAttribute("ConnectedUser", ConnectedUser);
        model.addAttribute("listClients",clients);
        model.addAttribute("user", new User());
        model.addAttribute("compte", new Compte());

        return "admin/clients";
    }

    @PostMapping("/save")
    public String saveClient(@ModelAttribute User user ,@ModelAttribute Compte compte) {
        if(user.getId()!=null  && userRepository.existsById(user.getId()) ) {
            User existingUser = userRepository.findById(user.getId()).orElse(null);
            Compte existingCompte = compteRepository.findById(existingUser.getCompte().getId()).orElse(null);
            if(existingUser!=null && existingCompte!=null) {
                existingUser.setFirstname(user.getFirstname());
                existingUser.setLastname(user.getLastname());
                existingUser.setUsername(user.getUsername());
                if (user.getPassword() != null && !user.getPassword().isBlank()) {
                    existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                }

                existingCompte.setBalance(compte.getBalance());
                existingCompte.setCurrency(compte.getCurrency());

                userRepository.save(existingUser);
                compteRepository.save(existingCompte);

            }

        }
        else {
            user.setRole(Role.Client);
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepository.save(user);
            compte.setUser(user);
            compteRepository.save(compte);

        }
        return "redirect:/admin/clients";
    }

    @GetMapping("/clients/edit/{id}")
    public String editClient(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long id, Model model) {
        User ConnectedUser = userRepository.findByUsername(userDetails.getUsername()).get();
        List<User> clients = userRepository.findAll().stream().filter(e->e.getRole()==Role.Client).toList();
        User editedUser = userRepository.findById(id).orElse(null);


        model.addAttribute("ConnectedUser", ConnectedUser);
        model.addAttribute("listClients",clients);
            model.addAttribute("editedUser", editedUser);
            model.addAttribute("editedCompte", editedUser.getCompte());
            return "admin/clients";

    }

    @GetMapping("/clients/delete/{id}")
    public String deleteClient(@PathVariable Long id, Model model) {
        User editedUser = userRepository.findById(id).orElse(null);
        if(editedUser!=null) {
            compteRepository.deleteById(editedUser.getCompte().getId());
            userRepository.delete(editedUser);

        }
        return "redirect:/admin/clients";

    }

    @GetMapping("/credits")
    public String credits(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User ConnectedUser = userRepository.findByUsername(userDetails.getUsername()).get();
       List<Credit> credits= creditRepository.findAll();
       model.addAttribute("ConnectedUser", ConnectedUser);
       model.addAttribute("credits",credits);
       return "admin/credits";

    }

    @GetMapping("/credits/accepte/{id}")
    @Transactional
    public String accepteCredit(@PathVariable Long id) {
        Credit credit = creditRepository.findById(id).orElseThrow();
        if (credit != null && credit.getStatus().toString().equals("Pending")) {
            credit.setStatus(Status.Accepted);
            creditRepository.save(credit);
        }
        return "redirect:/admin/credits";
    }





    @GetMapping("/credits/refuse/{id}")
    @Transactional

    public String refuseCredit(@PathVariable Long id) {
        Credit credit = creditRepository.findById(id).orElse(null);
        if(credit!=null && credit.getStatus().toString().equals("Pending")) {
            credit.setStatus(Status.Rejected);
            creditRepository.save(credit);
        }
        return "redirect:/admin/credits";
    }



}
