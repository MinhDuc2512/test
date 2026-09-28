package com.tracking.system.repository;

import com.tracking.system.entity.StatusCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusCodeRepository extends JpaRepository<StatusCode, String> {
    List<StatusCode> findAllByOrderBySortOrderAsc();
}
