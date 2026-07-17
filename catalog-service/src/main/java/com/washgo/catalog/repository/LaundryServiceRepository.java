package com.washgo.catalog.repository;

import com.washgo.catalog.entity.LaundryPartner;
import com.washgo.catalog.entity.LaundryService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LaundryServiceRepository extends JpaRepository<LaundryService, Long> {

    List<LaundryService> findByPartner(LaundryPartner partner);

}