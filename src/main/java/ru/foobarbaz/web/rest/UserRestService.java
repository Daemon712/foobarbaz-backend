package ru.foobarbaz.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.foobarbaz.entity.User;
import ru.foobarbaz.logic.UserService;
import ru.foobarbaz.repo.UserRepository;
import ru.foobarbaz.web.dto.UserDTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
public class UserRestService {
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserRestService(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/signUp", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUp(@Valid @RequestBody UserDTO user){
        User existingUser = userRepository.findOne(user.getUsername());
        if (existingUser != null){
            return new ResponseEntity<>("username already in use", HttpStatus.BAD_REQUEST);
        }
        userService.createUser(user.getUsername(), user.getPassword());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "user", method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getCurrentUser(HttpServletRequest httpRequest){
        String username = httpRequest.getUserPrincipal().getName();
        User user = userRepository.getOne(username);
        return new ResponseEntity<>(new UserDTO(user.getUsername()), HttpStatus.OK);
    }

    //For testing user creation
    @RequestMapping(value = "/user/list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
