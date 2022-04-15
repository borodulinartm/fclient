package ru.iu3.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iu3.backend.models.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
}
