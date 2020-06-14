package it.uniroma3.siw.taskmanager.controller;

import it.uniroma3.siw.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.taskmanager.controller.validation.CredentialsValidator;
import it.uniroma3.siw.taskmanager.controller.validation.UserValidator;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.UserRepository;
import it.uniroma3.siw.taskmanager.service.CredentialsService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The UserController handles all interactions involving User data.
 */
@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserValidator userValidator;
    
    @Autowired
    CredentialsValidator credentialsValidator;

    @Autowired
    CredentialsService credentialService;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
    CredentialsService credentialsService;

    @Autowired
    SessionData sessionData;

    
    @RequestMapping(value = { "/home" }, method = RequestMethod.GET)
    public String home(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        model.addAttribute("user", loggedUser);
        return "home";
    }

   
    @RequestMapping(value = { "/users/me" }, method = RequestMethod.GET)
    public String me(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        Credentials credentials = sessionData.getLoggedCredentials();
        System.out.println(credentials.getPassword());
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("credentials", credentials);

        return "userProfile";
    }

    
    @RequestMapping(value = { "/admin" }, method = RequestMethod.GET)
    public String admin(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        model.addAttribute("user", loggedUser);
        return "admin";
    }

    @RequestMapping(value = { "/admin/users" }, method = RequestMethod.GET)
    public String usersList(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        List<Credentials> allCredentials = this.credentialsService.getAllCredentials();
        
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("credentialsList", allCredentials);
        return "allUsers";
    }
    
    @RequestMapping(value = { "/admin/users/{username}/delete" }, method = RequestMethod.POST)
    public String removeUser(Model model, @PathVariable String username) {
        this.credentialsService.deleteCredentials(username);
        return "redirect:/admin/users";
    }
    
    @RequestMapping(value = { "/users/me/update" }, method = RequestMethod.GET)
    public String showUpdateUserForm(Model model) {
    	
    	model.addAttribute("user", sessionData.getLoggedUser());
    	model.addAttribute("userCredentials", sessionData.getLoggedCredentials());
        return "updateUser";
    }
    
    @RequestMapping(value = { "/users/me/update" }, method = RequestMethod.POST)
	public String updateUser(	@Validated @ModelAttribute("user") User user,
								BindingResult userBindingResult,
								@Validated @ModelAttribute("userCredentials") Credentials credentials,
								BindingResult credentialsBindingResult,
								Model model) {
		this.userValidator.validate(user, userBindingResult);
		this.credentialsValidator.validate(credentials, credentialsBindingResult);
		
		if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
			credentials.setUser(sessionData.getLoggedUser());
			credentialService.updateCredentials(credentials);
			return "updateSuccessful";
		}
		return "updateUser";
	}
    
    
    
}
