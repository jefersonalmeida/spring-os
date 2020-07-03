package com.jeferson.os.domain.service;

import com.jeferson.os.core.util.Util;
import com.jeferson.os.domain.exception.ApiModelExistsException;
import com.jeferson.os.domain.exception.ApiModelNotFoundException;
import com.jeferson.os.domain.model.Role;
import com.jeferson.os.domain.model.User;
import com.jeferson.os.domain.repository.RoleRepository;
import com.jeferson.os.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${api.pagination.size}")
    private int paginationSize;

    public Page<User> paginate(Integer page, Integer size, String order, String sort) {
        PageRequest pageRequest = getPageRequest(page, size, order, sort);
        return userRepository.findAll(pageRequest);
    }

    private PageRequest getPageRequest(Integer page, Integer size, String order, String sort) {
        return PageRequest.of(
                page,
                size != null ? size : this.paginationSize,
                sort.toUpperCase().equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC,
                Util.toCamelCase(order != null ? order : "created_at")
        );
    }

    public User find(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiModelNotFoundException(id.toString()));
    }

    public User register(User user, String roleName) {
        this.emailExistIfNotEquals(user);
        user.setActive(false);
        user.generateActivationToken();

        Optional<Role> exists = roleRepository.findByName(roleName);
        exists.ifPresent(role -> user.setRoles(Collections.singletonList(role)));

        return userRepository.save(user);
    }

    public User store(User user) {
        this.emailExistIfNotEquals(user);
        return userRepository.save(user);
    }

    private void emailExistIfNotEquals(User user) {
        Optional<User> exists = findByEmail(user.getEmail());
        if (exists.isPresent() && !exists.get().equals(user)) throw new ApiModelExistsException("Email já cadastrado.");
    }

    public void destroy(UUID id) {
        userRepository.deleteById(id);
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailEquals(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = findByEmail(email);
        return user.orElseThrow(() -> new UsernameNotFoundException(String.format("Email %s não encontrado.", email)));
    }
}
