package com.kwatanabe.portfoliov1backend.repository;

import com.kwatanabe.portfoliov1backend.entity.Contact;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    boolean existsById(@NonNull Long id);

    List<Contact> findByNameContainingIgnoreCase(String name);

    List<Contact> findByCompanyNameContainingIgnoreCase(String companyName);

    List<Contact> findAllByOrderByIdDesc();

}

