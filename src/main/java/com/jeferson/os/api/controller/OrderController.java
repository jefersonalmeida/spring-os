package com.jeferson.os.api.controller;

import com.jeferson.os.api.model.OrderModel;
import com.jeferson.os.api.controller.request.OrderRequest;
import com.jeferson.os.api.controller.response.ResponsePage;
import com.jeferson.os.api.controller.response.ResponseResult;
import com.jeferson.os.domain.model.Order;
import com.jeferson.os.domain.service.OrderService;
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
@RequestMapping(value = "orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<ResponseResult<OrderModel>> store(@Valid @RequestBody OrderRequest orderRequest) {
        Order order = orderService.create(toEntity(orderRequest));
        ResponseResult<OrderModel> responseResult = new ResponseResult<>();
        responseResult.setData(toModel(order));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseResult);
    }

    @GetMapping
    public ResponseEntity<ResponsePage<List<OrderModel>>> index(
            @RequestParam(name = "page", required = false) Optional<Integer> page,
            @RequestParam(name = "size", required = false) Optional<Integer> size,
            @RequestParam(name = "order", required = false) Optional<String> order,
            @RequestParam(name = "sort", required = false) Optional<String> sort
    ) {
        Page<Order> items = orderService.paginate(page, size, order, sort);
        return ResponseEntity.ok().body(toPaginateModel(items));
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseResult<OrderModel>> show(@PathVariable(name = "id") UUID id) {
        Order entity = orderService.find(id);
        ResponseResult<OrderModel> responseResult = new ResponseResult<>();
        responseResult.setData(toModel(entity));
        return ResponseEntity.ok().body(responseResult);
    }

    @PutMapping("{id}/finalize")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void finalize(@PathVariable(name = "id") UUID id) {
        orderService.finalize(id);
    }

    private OrderModel toModel(Order order) {
        return modelMapper.map(order, OrderModel.class);
    }

    private List<OrderModel> toCollectionModel(List<Order> orders) {
        return orders.stream().map(this::toModel).collect(Collectors.toList());
    }

    private ResponsePage<List<OrderModel>> toPaginateModel(Page<Order> pg) {
        ResponsePage<List<OrderModel>> page = new ResponsePage<>();
        page.setSize(pg.getSize());
        page.setNumber(pg.getNumber());
        page.setTotalElements(pg.getTotalElements());
        page.setTotalPages(pg.getTotalPages());
        page.setData(toCollectionModel(pg.getContent()));
        return page;
    }

    private Order toEntity(OrderRequest orderRequest) {
        return modelMapper.map(orderRequest, Order.class);
    }
}
