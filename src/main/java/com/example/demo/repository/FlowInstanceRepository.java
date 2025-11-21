package com.example.demo.repository;

import com.example.demo.entity.FlowInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FlowInstanceRepository extends JpaRepository<FlowInstance, UUID> {
}