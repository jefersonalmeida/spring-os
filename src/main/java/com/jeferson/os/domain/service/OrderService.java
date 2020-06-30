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

import java.time.OffsetDateTime;
import java.util.Optional;
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

    public Page<Order> paginate(Optional<Integer> page, Optional<Integer> size, Optional<String> order, Optional<String> sort) {
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
        order.setOpenedAt(OffsetDateTime.now());

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

    private PageRequest getPageRequest(Optional<Integer> page, Optional<Integer> size, Optional<String> order, Optional<String> sort) {
        return PageRequest.of(
                page.orElse(0),
                getSize(size),
                getDirection(sort),
                Util.toCamelCase(order.orElse("opened_at"))
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
}
