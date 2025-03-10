package ru.CloudStorage.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.boot.test.context.SpringBootTest;
import ru.CloudStorage.models.User;
import ru.CloudStorage.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Testcontainers
class UserServiceTest {

    @Autowired

    UserRepository userRepository;

    @Autowired

    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testDB")
            .withUsername("test")
            .withPassword("test");



    @Test
    void testContainerIsRunning() {
        assertTrue(postgresContainer.isRunning());
    }

    @Test
    public void registerUserTest() {
        String username = "test";
        String rawPassword = "test";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        userService.registerUser(username, rawPassword);
        userService.loadUserByUsername(username);
        assertEquals(user.getUsername(), userService.loadUserByUsername(username).getUsername());

    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void loadUserByUsername() {
    }

    @Test
    void registerUser() {
    }
}