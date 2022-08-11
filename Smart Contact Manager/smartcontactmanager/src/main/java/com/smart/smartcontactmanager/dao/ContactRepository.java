package com.smart.smartcontactmanager.dao;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.smartcontactmanager.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    
    //We are implementing the methods for the pagination...

    // Page = A page is a sublist of a list of objects. it allows gain information about the position of the entire lists.
    // Pageable = Abstract interface for pagination information.

    @Query("from Contact as c where c.user.id = :userId")
    //currentPage - page
    //Contacts per page - 5
    public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);
}
