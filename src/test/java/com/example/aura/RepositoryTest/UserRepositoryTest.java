package com.example.aura.RepositoryTest;

import com.example.aura.Entity.User.Domain.User;
import com.example.aura.Entity.User.Repository.UserRepository;
import com.example.aura.Security.Domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@test.com");
        testUser.setPasswordHash("hashedpassword123");
        testUser.setPhone("123456789");
        testUser.setRegisterDate(LocalDate.now());
        testUser.setRole(Role.USER);
        testUser.setEnabled(true);
    }

    @Test
    void shouldSaveUser_always() {
        User savedUser = userRepository.save(testUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@test.com");
        assertThat(savedUser.getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldFindUserByEmail_whenExists() {
        entityManager.persistAndFlush(testUser);

        Optional<User> found = userRepository.findByEmail("john.doe@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@test.com");
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldReturnEmpty_whenEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@test.com");

        assertThat(found).isEmpty();
    }

    @Test
    void shouldReturnTrue_whenEmailExists() {
        entityManager.persistAndFlush(testUser);

        boolean exists = userRepository.existsByEmail("john.doe@test.com");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalse_whenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("nonexistent@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    void shouldFindUserByPhone_whenExists() {
        entityManager.persistAndFlush(testUser);

        Optional<User> found = userRepository.findByPhone("123456789");

        assertThat(found).isPresent();
        assertThat(found.get().getPhone()).isEqualTo("123456789");
    }

    @Test
    void shouldDeleteUser_successfully() {
        User savedUser = entityManager.persistAndFlush(testUser);
        Long userId = savedUser.getId();

        userRepository.deleteById(userId);
        entityManager.flush();

        Optional<User> found = userRepository.findById(userId);
        assertThat(found).isEmpty();
    }
}