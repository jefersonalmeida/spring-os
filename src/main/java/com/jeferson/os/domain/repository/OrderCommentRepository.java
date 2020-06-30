package com.jeferson.os.domain.repository;

import com.jeferson.os.domain.model.OrderComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderCommentRepository extends JpaRepository<OrderComment, UUID> {
}
