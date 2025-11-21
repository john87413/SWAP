package com.example.demo.repository;

import com.example.demo.entity.FlowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FlowDefinitionRepository extends JpaRepository<FlowDefinition, UUID> {
}