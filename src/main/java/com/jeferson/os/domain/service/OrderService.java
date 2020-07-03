package com.jeferson.os.domain.service;

import com.jeferson.os.core.util.Util;
import com.jeferson.os.domain.exception.ApiInvalidArgumentException;
import com.jeferson.os.domain.exception.ApiModelNotFoundException;
import com.jeferson.os.domain.model.Order;
import com.jeferson.os.domain.model.OrderComment;
import com.jeferson.os.domain.model.OrderStatus;
import com.jeferson.os.domain.model.User;
import com.jeferson.os.domain.repository.OrderCommentRepository;
import com.jeferson.os.domain.repository.OrderRepository;
import com.jeferson.os.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderCommentRepository orderCommentRepository;
    @Autowired
    private UserRepository userRepository;

    @Value("${api.pagination.size}")
    private int paginationSize;

    public Page<Order> paginate(Integer page, Integer size, String order, String sort) {
        PageRequest pageRequest = getPageRequest(page, size, order, sort);
        return orderRepository.findAll(pageRequest);
    }

    public Order find(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ApiModelNotFoundException(id.toString()));
    }

    public Order create(Order order) {
        User user = userRepository
                .findById(order.getUser().getId())
                .orElseThrow(() -> new ApiInvalidArgumentException("Usuário não encontrado."));

        order.setUser(user);
        order.setStatus(OrderStatus.OPEN);
        return orderRepository.save(order);
    }

    public void finalize(UUID orderId) {
        Order order = find(orderId);
        order.finalizeOrder();
        orderRepository.save(order);
    }

    public OrderComment createComment(UUID orderId, String description) {
        Order order = find(orderId);

        OrderComment comment = new OrderComment();
        comment.setDescription(description);
        comment.setOrder(order);

        return orderCommentRepository.save(comment);
    }

    private PageRequest getPageRequest(Integer page, Integer size, String order, String sort) {
        return PageRequest.of(
                page,
                size != null ? size : this.paginationSize,
                sort.toUpperCase().equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC,
                Util.toCamelCase(order != null ? order : "created_at")
        );
    }
}
