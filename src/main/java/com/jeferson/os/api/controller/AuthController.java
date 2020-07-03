package com.jeferson.os.api.controller;

import com.jeferson.os.api.model.TokenModel;
import com.jeferson.os.api.model.UserModel;
import com.jeferson.os.api.request.LoginRequest;
import com.jeferson.os.api.request.RegisterRequest;
import com.jeferson.os.api.response.ResponseResult;
import com.jeferson.os.core.jwt.JwtToken;
import com.jeferson.os.domain.Const;
import com.jeferson.os.domain.model.User;
import com.jeferson.os.domain.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping(path = "auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtToken jwtToken;
    private final ModelMapper modelMapper;

    @PostMapping(path = "register")
    public ResponseEntity<ResponseResult<UserModel>> store(@Valid @RequestBody RegisterRequest registerInput) {
        ResponseResult<UserModel> responseResult = new ResponseResult<>();

        User user = userService.register(toEntity(registerInput), Const.ROLE_CLIENT);
        responseResult.setData(toModel(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseResult);
    }

    @PostMapping(path = "login")
    public ResponseEntity<ResponseResult<TokenModel>> login(
            @Valid
            @RequestBody LoginRequest loginRequest) throws AuthenticationException {

        ResponseResult<TokenModel> responseResult = new ResponseResult<>();

        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
        String token = jwtToken.getToken(userDetails);

        Optional<User> user = userService.findByEmail(loginRequest.getEmail());
        responseResult.setData(new TokenModel(token, toModel(user.orElse(null))));
        return ResponseEntity.ok(responseResult);
    }

    @GetMapping(path = "whoami")
    public ResponseEntity<ResponseResult<UserModel>> whoami() {
        ResponseResult<UserModel> responseResult = new ResponseResult<>();
        Optional<User> user = userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        responseResult.setData(toModel(user.orElse(null)));
        return ResponseEntity.ok().body(responseResult);
    }

    private UserModel toModel(User model) {
        return modelMapper.map(model, UserModel.class);
    }

    private User toEntity(RegisterRequest input) {
        return modelMapper.map(input, User.class);
    }
}
