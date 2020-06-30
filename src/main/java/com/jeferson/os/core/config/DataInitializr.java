package com.jeferson.os.core.config;

import com.jeferson.os.domain.Const;
import com.jeferson.os.domain.model.Role;
import com.jeferson.os.domain.model.User;
import com.jeferson.os.domain.repository.RoleRepository;
import com.jeferson.os.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class DataInitializr implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            createUser("Admin", "admin@os.com", "123456", Const.ROLE_ADMIN);
            createUser("Office", "office@os.com", "123456", Const.ROLE_OFFICE);
            createUser("Company", "company@os.com", "123456", Const.ROLE_COMPANY);
            createUser("Professional", "professional@os.com", "123456", Const.ROLE_PROFESSIONAL);
            createUser("Employee", "employee@os.com", "123456", Const.ROLE_EMPLOYEE);
            createUser("Client", "client@os.com", "123456", Const.ROLE_CLIENT);
        }
    }

    public void createUser(String name, String email, String password, String roleName) {

        Optional<Role> exists = roleRepository.findByName(roleName);

        Role role;
        if(exists.isEmpty()) {
            role = new Role();
            role.setName(roleName);
            this.roleRepository.save(role);
        } else {
            role = exists.get();
        }

        User user = new User(name, email, password, Collections.singletonList(role));
        user.setActive(true);
        user.setActivationToken(null);
        user.setEmailVerifiedAt(OffsetDateTime.now());
        userRepository.save(user);
    }
}
