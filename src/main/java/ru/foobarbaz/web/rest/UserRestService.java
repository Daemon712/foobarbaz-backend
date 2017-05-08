package ru.foobarbaz.web.rest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.entity.user.UserAccount;
import ru.foobarbaz.logic.UserService;
import ru.foobarbaz.repo.UserAccountRepository;
import ru.foobarbaz.repo.UserRepository;
import ru.foobarbaz.web.dto.NewUser;

import javax.validation.Valid;
import java.util.List;

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

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> signUp(@Valid @RequestBody NewUser input){
        User existingUser = userRepository.findById(input.getUsername()).orElse(null);
        if (existingUser != null){
            return new ResponseEntity<>("username already in use", HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        BeanUtils.copyProperties(input, user);
        UserAccount newAccount = new UserAccount();
        newAccount.setUser(user);
        newAccount.setDescription(input.getDescription());
        UserAccount createdUser = userService.createUser(newAccount);
        return new ResponseEntity<>(createdUser.getUser(), HttpStatus.CREATED);
    }


    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "current")
    public ResponseEntity<User> getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(username).orElse(null);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "top/{property}")
    public List<UserAccount> getTopUsers(@PathVariable String property){
        return accountRepository.findTop3By(Sort.by(Sort.Direction.DESC, property));
    }

    @RequestMapping(value = "account/{username}")
    public ResponseEntity<UserAccount> getUser(@PathVariable String username){
        UserAccount user = accountRepository.findById(username).orElseThrow(ResourceNotFoundException::new);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping
    public Page<UserAccount> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false) String search){
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "rating"));
        return search == null
                ? accountRepository.findAll(pageRequest)
                : accountRepository.findAllByUsernameContainsIgnoreCaseOrUserNameContainsIgnoreCase(search, search, pageRequest);
    }}
