package com.revature.project3backend.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class UserRepoTest {

    @Autowired
    UserRepo userRepo;

    @Test
    void findByUsername() {
    }
}