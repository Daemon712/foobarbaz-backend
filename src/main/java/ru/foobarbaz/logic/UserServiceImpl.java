package ru.foobarbaz.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.foobarbaz.entity.User;
import ru.foobarbaz.entity.UserAccount;
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
        String username = template.getUsername();
        if (username == null && template.getUser() != null){
            username = template.getUser().getUsername();
        }
        String encryptedPassword = passwordEncoder.encode(template.getUser().getPassword());
        User user = new User(username, encryptedPassword);
        userRepository.save(user);

        UserAccount account = new UserAccount();
        BeanUtils.copyProperties(template, account);
        account.setUser(user);
        account.setUsername(username);
        account.setRegistrationDate(new Date());
        accountRepository.save(account);

        log.debug("Created User: {}", username);
        return account;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findOne(username).orElse(null);
        if (user == null){
            throw new UsernameNotFoundException(username + " not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
