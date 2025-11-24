package com.example.aura.TestContainer;

import com.example.aura.Entity.User.Domain.User;
import com.example.aura.Entity.User.Repository.UserRepository;
import com.example.aura.Security.Domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("Integration");
        testUser.setLastName("Test");
        testUser.setEmail("integration@test.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setPhone("987654321");
        testUser.setRegisterDate(LocalDate.now());
        testUser.setRole(Role.USER);
        testUser.setEnabled(true);
    }

    @Test
    void shouldPersistUser_inPostgreSQLContainer() {
        User savedUser = userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();

        Optional<User> found = userRepository.findById(savedUser.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("integration@test.com");
        assertThat(found.get().getFirstName()).isEqualTo("Integration");
    }

    @Test
    void shouldFindUserByEmail_inPostgreSQLContainer() {
        entityManager.persistAndFlush(testUser);
        entityManager.clear();

        Optional<User> found = userRepository.findByEmail("integration@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("integration@test.com");
    }

    @Test
    void shouldDeleteUser_fromPostgreSQLContainer() {
        User savedUser = entityManager.persistAndFlush(testUser);
        Long userId = savedUser.getId();
        entityManager.clear();

        userRepository.deleteById(userId);
        entityManager.flush();

        Optional<User> found = userRepository.findById(userId);
        assertThat(found).isEmpty();
    }

    @Test
    void shouldHandleUniqueConstraint_onEmail() {
        entityManager.persistAndFlush(testUser);

        User duplicateUser = new User();
        duplicateUser.setFirstName("Duplicate");
        duplicateUser.setLastName("User");
        duplicateUser.setEmail("integration@test.com"); // Mismo email
        duplicateUser.setPasswordHash("hashed");
        duplicateUser.setRegisterDate(LocalDate.now());
        duplicateUser.setRole(Role.USER);
        duplicateUser.setEnabled(true);

        try {
            userRepository.save(duplicateUser);
            entityManager.flush();
            assertThat(false).as("Should have thrown exception").isTrue();
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }
}