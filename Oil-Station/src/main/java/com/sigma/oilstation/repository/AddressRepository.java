package com.sigma.oilstation.repository;

import com.sigma.oilstation.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    @Override
    Page<Address> findAll(Pageable pageable);
}
