package ru.CloudStorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.CloudStorage.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public User findUserByUsername(String userName);
}
