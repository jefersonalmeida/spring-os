package com.jeferson.os.domain.service;

import com.jeferson.os.core.util.Util;
import com.jeferson.os.domain.exception.ApiModelExistsException;
import com.jeferson.os.domain.exception.ApiModelNotFoundException;
import com.jeferson.os.domain.model.User;
import com.jeferson.os.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Value("${api.pagination.size}")
    private int paginationSize;

    public Page<User> paginate(Optional<Integer> page, Optional<Integer> size, Optional<String> order, Optional<String> sort) {
        PageRequest pageRequest = getPageRequest(page, size, order, sort);
        return userRepository.findAll(pageRequest);
    }

    private PageRequest getPageRequest(Optional<Integer> page, Optional<Integer> size, Optional<String> order, Optional<String> sort) {
        return PageRequest.of(
                page.orElse(0),
                getSize(size),
                getDirection(sort),
                Util.toCamelCase(order.orElse("created_at"))
        );
    }

    private Sort.Direction getDirection(Optional<String> sort) {
        return sort.filter(s -> s.toUpperCase().equals("ASC"))
                .map(s -> Sort.Direction.ASC)
                .orElse(Sort.Direction.DESC);
    }

    private int getSize(Optional<Integer> size) {
        return size.orElseGet(() -> this.paginationSize);
    }

    public User find(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiModelNotFoundException(id.toString()));
    }

    public User register(User user) {
        this.emailExistIfNotEquals(user);
        user.setActive(false);
        user.generateActivationToken();
        return userRepository.save(user);
    }

    public User store(User user) {
        this.emailExistIfNotEquals(user);
        return userRepository.save(user);
    }

    private void emailExistIfNotEquals(User user) {
        Optional<User> exists = userRepository.findByEmail(user.getEmail());
        if (exists.isPresent() && !exists.get().equals(user)) throw new ApiModelExistsException("Email já cadastrado.");
    }

    public void destroy(UUID id) {
        userRepository.deleteById(id);
    }
}
