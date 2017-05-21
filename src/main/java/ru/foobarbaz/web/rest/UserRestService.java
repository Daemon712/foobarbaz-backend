package ru.foobarbaz.web.rest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.entity.user.UserAccount;
import ru.foobarbaz.logic.UserPhotoService;
import ru.foobarbaz.logic.UserService;
import ru.foobarbaz.repo.UserAccountRepository;
import ru.foobarbaz.repo.UserRepository;
import ru.foobarbaz.web.dto.NewUser;
import ru.foobarbaz.web.dto.UpdateUserInfo;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserRestService {
    private final UserRepository userRepository;
    private final UserAccountRepository accountRepository;
    private final UserService userService;
    private final UserPhotoService userPhotoService;

    @Autowired
    public UserRestService(
            UserRepository userRepository,
            UserAccountRepository accountRepository,
            UserService userService,
            UserPhotoService userPhotoService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.userPhotoService = userPhotoService;
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

    @GetMapping(value = "account/{username}/photo/{size}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getUserPhoto(@PathVariable String username, @PathVariable String size) throws IOException {
        return userPhotoService.downloadPhoto(username, UserPhotoService.PhotoSize.valueOf(size.toUpperCase()));
    }

    @PostMapping(value = "account/{username}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated() && hasPermission(#username, 'User', 'w')")
    public void uploadUserPhoto(@PathVariable String username, @RequestParam MultipartFile file) throws IOException {
        userPhotoService.uploadPhoto(username, file.getBytes());
    }

    @RequestMapping(value = "account/{username}", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() && hasPermission(#username, 'UserAccount', 'w')")
    public UserAccount modifyUserInfo(@PathVariable String username, @RequestBody UpdateUserInfo userInfo){
        User user = new User();
        user.setUsername(username);
        user.setName(userInfo.getName());
        UserAccount userAccount = new UserAccount();
        userAccount.setUser(user);
        userAccount.setUsername(username);
        userAccount.setDescription(userInfo.getDescription());
        return userService.modifyUserInfo(userAccount);
    }

    @RequestMapping(value = "account/{username}/password", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated() && hasPermission(#username, 'User', 'w')")
    public void modifyUserPassword(@PathVariable String username, @RequestBody String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userService.modifyUserPassword(user);
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
