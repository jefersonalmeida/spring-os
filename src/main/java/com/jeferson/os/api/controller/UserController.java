package com.jeferson.os.api.controller;

import com.jeferson.os.api.model.UserModel;
import com.jeferson.os.api.controller.request.RegisterRequest;
import com.jeferson.os.api.controller.response.ResponsePage;
import com.jeferson.os.api.controller.response.ResponseResult;
import com.jeferson.os.domain.model.User;
import com.jeferson.os.domain.service.UserService;
import com.jeferson.os.domain.validation.UUIDValidator;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<ResponsePage<List<UserModel>>> index(
            @RequestParam(name = "page", required = false) Optional<Integer> page,
            @RequestParam(name = "size", required = false) Optional<Integer> size,
            @RequestParam(name = "order", required = false) Optional<String> order,
            @RequestParam(name = "sort", required = false) Optional<String> sort
    ) {

        Page<User> items = userService.paginate(page, size, order, sort);
        return ResponseEntity.ok().body(toPaginateModel(items));
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseResult<UserModel>> show(@UUIDValidator @PathVariable UUID id) {
        User entity = userService.find(id);
        ResponseResult<UserModel> responseResult = new ResponseResult<>();
        responseResult.setData(toModel(entity));
        return ResponseEntity.ok().body(responseResult);
    }

    @PostMapping
    public ResponseEntity<ResponseResult<UserModel>> store(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(toEntity(request));
        ResponseResult<UserModel> responseResult = new ResponseResult<>();
        responseResult.setData(toModel(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseResult);
    }

    @PutMapping("{id}")
    public ResponseEntity<ResponseResult<UserModel>> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody RegisterRequest request) {

        User entity = userService.find(id);
        ResponseResult<UserModel> responseResult = new ResponseResult<>();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        User user = userService.store(entity);
        responseResult.setData(toModel(user));
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> destroy(@PathVariable("id") UUID id) {
        User entity = userService.find(id);
        userService.destroy(entity.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private UserModel toModel(User model) {
        return modelMapper.map(model, UserModel.class);
    }

    private List<UserModel> toCollectionModel(List<User> list) {
        return list.stream().map(this::toModel).collect(Collectors.toList());
    }

    private ResponsePage<List<UserModel>> toPaginateModel(Page<User> pg) {
        ResponsePage<List<UserModel>> page = new ResponsePage<>();
        page.setSize(pg.getSize());
        page.setNumber(pg.getNumber());
        page.setTotalElements(pg.getTotalElements());
        page.setTotalPages(pg.getTotalPages());
        page.setData(toCollectionModel(pg.getContent()));
        return page;
    }

    private User toEntity(RegisterRequest input) {
        return modelMapper.map(input, User.class);
    }
}
