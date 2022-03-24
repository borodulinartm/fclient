package ru.iu3.backend.repositories;

// Импортируем репозиторий
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Импортируем нашу модель
import ru.iu3.backend.models.Country;

/**
 * Это интерфейс, реализующий базовый набор функций для работы с базой данных
 * (то есть чтение, удаление, обновление, добавление)
 * Этот интерфейс нам нужен, если вдруг понадобятся собственные запросы к БД
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
}
