package com.example.inventory.repository;

import com.example.inventory.domain.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, String> {}
