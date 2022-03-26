package ru.iu3.backend.models;

import javax.persistence.*;

@Entity
@Table(name = "artists")
@Access(AccessType.FIELD)

/**
 * Класс-модель, описывающая артистов
 * @author artem
 */
public class Artists {
    // Поле ID (not null, auto increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    // Поле с именем
    @Column(name = "name", nullable = false)
    public String name;

    // Поле-внешний ключ countryID
    @Column(name = "countryid")
    public Long countryid;

    // Поле "Возраст"
    @Column(name = "age")
    public String age;

    // Конструктор без параметров
    public Artists() {}

    /**
     * Конструктор с параметром id
     * @param id
     */
    public Artists(Long id) {
        this.id = id;
    }
}
