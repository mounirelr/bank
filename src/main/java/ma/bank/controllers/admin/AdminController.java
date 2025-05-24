package ma.bank.controllers.admin;

import ch.qos.logback.core.net.server.Client;
import ma.bank.entities.Compte;
import ma.bank.entities.Role;
import ma.bank.entities.User;
import ma.bank.repository.CompteRepository;
import ma.bank.repository.UserRepository;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")

public class AdminController {

    private UserRepository userRepository;
    private CompteRepository compteRepository;
    @Autowired
    public AdminController(UserRepository userRepository ,CompteRepository compteRepository) {
        this.userRepository = userRepository;
        this.compteRepository = compteRepository;
    }

    @GetMapping("/home")
    public String home() {
        return "admin/home";
    }

    @GetMapping("/clients")
    public String listClients(Model model) {
        List<User> clients = userRepository.findAll().stream().filter(e->e.getRole()==Role.Client).toList();
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
                existingUser.setPassword(user.getPassword()); //to change
                existingCompte.setBalance(compte.getBalance());
                existingCompte.setCurrency(compte.getCurrency());

                userRepository.save(existingUser);
                compteRepository.save(existingCompte);

            }

        }
        else {
            user.setRole(Role.Client);
            userRepository.save(user);
            compte.setUser(user);
            compteRepository.save(compte);

        }
        return "redirect:/admin/clients";
    }

    @GetMapping("/clients/edit/{id}")
    public String editClient(@PathVariable Long id, Model model) {
        List<User> clients = userRepository.findAll().stream().filter(e->e.getRole()==Role.Client).toList();
        User editedUser = userRepository.findById(id).orElse(null);

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



}
