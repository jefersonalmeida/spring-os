package com.jeferson.os.api.controller;

import com.jeferson.os.api.model.OrderCommentModel;
import com.jeferson.os.api.controller.request.OrderCommentRequest;
import com.jeferson.os.api.controller.response.ResponseResult;
import com.jeferson.os.domain.model.Order;
import com.jeferson.os.domain.model.OrderComment;
import com.jeferson.os.domain.service.OrderService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "orders/{orderId}/comments")
@AllArgsConstructor
public class OrderCommentController {

    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<ResponseResult<List<OrderCommentModel>>> index(@PathVariable UUID orderId) {
        Order order = orderService.find(orderId);
        ResponseResult<List<OrderCommentModel>> responseResult = new ResponseResult<>();
        responseResult.setData(toCollectionModel(order.getComments()));
        return ResponseEntity.ok().body(responseResult);
    }

    @PostMapping
    public ResponseEntity<ResponseResult<OrderCommentModel>> store(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderCommentRequest orderInput) {

        OrderComment orderComment = orderService.createComment(orderId, orderInput.getDescription());
        ResponseResult<OrderCommentModel> responseResult = new ResponseResult<>();
        responseResult.setData(toModel(orderComment));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseResult);
    }

    private OrderCommentModel toModel(OrderComment orderComment) {
        return modelMapper.map(orderComment, OrderCommentModel.class);
    }

    private List<OrderCommentModel> toCollectionModel(List<OrderComment> orderComments) {
        return orderComments.stream().map(this::toModel).collect(Collectors.toList());
    }
}
