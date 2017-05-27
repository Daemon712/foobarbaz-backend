package ru.foobarbaz.logic.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.entity.user.UserAccount;
import ru.foobarbaz.entity.user.UserRole;
import ru.foobarbaz.logic.UserService;
import ru.foobarbaz.repo.UserAccountRepository;
import ru.foobarbaz.repo.UserRepository;

import java.util.Collections;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserAccountRepository accountRepository;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           UserAccountRepository accountRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public UserAccount createUser(UserAccount template) {
        User user = new User();
        String username = template.getUser().getUsername().trim().toLowerCase();
        user.setUsername(username);
        String encryptedPassword = passwordEncoder.encode(template.getUser().getPassword());
        user.setPassword(encryptedPassword);
        user.setName(template.getUser().getName());
        user.setRole(UserRole.USER);
        userRepository.save(user);

        UserAccount account = new UserAccount();
        BeanUtils.copyProperties(template, account);
        account.setUsername(username);
        account.setRegistrationDate(new Date());
        accountRepository.save(account);

        log.debug("Created User: {}", user.getUsername());
        return account;
    }

    @Override
    public UserAccount modifyUserInfo(UserAccount template) {
        UserAccount userAccount = accountRepository.findById(template.getUsername())
                .orElseThrow(ResourceNotFoundException::new);

        userAccount.setDescription(template.getDescription());
        userAccount.getUser().setName(template.getUser().getName());
        return accountRepository.save(userAccount);
    }

    @Override
    public User modifyUserPassword(User template) {
        User user = userRepository.findById(template.getUsername())
                .orElseThrow(ResourceNotFoundException::new);
        String encryptedPassword = passwordEncoder.encode(template.getPassword());
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username).orElse(null);
        if (user == null){
            throw new UsernameNotFoundException(username + " not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(user.getRole())
        );
    }
}
