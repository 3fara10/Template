package rest;


import models.Configuration;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import persistance.IConfigurationRepository;
import persistance.IUserRepository;

@CrossOrigin
@RestController
@RequestMapping("/question")
public class UserController {
    private final IUserRepository userRepository;

    private final IConfigurationRepository configurationRepository;

    @Autowired
    public UserController(IUserRepository userRepository, IConfigurationRepository configurationRepository) {
        this.userRepository = userRepository;
        this.configurationRepository = configurationRepository;
    }

    @RequestMapping(method= RequestMethod.GET)
    public User[] getAll(){
        return userRepository.getAll().toArray(User[]::new);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Configuration addTrial(@RequestBody Configuration config) {
        this.configurationRepository.add(config);
        return config;
    }

}
