package com.ims.backend.repository;

import com.ims.backend.entity.WorkItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {
}