package ru.foobarbaz.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.foobarbaz.entity.User;
import ru.foobarbaz.entity.UserAccount;
import ru.foobarbaz.logic.UserService;
import ru.foobarbaz.repo.UserAccountRepository;
import ru.foobarbaz.repo.UserRepository;
import ru.foobarbaz.web.dto.NewUser;

import javax.validation.Valid;

@RestController
@RequestMapping("api/users")
public class UserRestService {
    private final UserRepository userRepository;
    private final UserAccountRepository accountRepository;
    private final UserService userService;

    @Autowired
    public UserRestService(UserService userService, UserRepository userRepository, UserAccountRepository accountRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUp(@Valid @RequestBody NewUser user){
        User existingUser = userRepository.findOne(user.getUsername()).orElse(null);
        if (existingUser != null){
            return new ResponseEntity<>("username already in use", HttpStatus.BAD_REQUEST);
        }
        UserAccount newAccount = new UserAccount();
        newAccount.setUser(new User(user.getUsername(), user.getPassword()));
        newAccount.setDescription(user.getDescription());
        System.out.println(user.getDescription());
        UserAccount createdUser = userService.createUser(newAccount);
        return new ResponseEntity<>(createdUser.getUser(), HttpStatus.CREATED);
    }


    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/current", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findOne(username).orElse(null);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<UserAccount> getAllUsers(){
        return accountRepository.findAll();
    }
}
