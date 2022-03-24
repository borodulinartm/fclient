package ru.iu3.backend.models;

import javax.persistence.*;

// Описываем доступ к нашему классу (раздел - модель)
// Таблица в базе
@Entity

// Название таблицы
@Table(name = "countries")

// Даём прямой доступ к полям класса (не нужно писать методы чтения и записи)
@Access(AccessType.FIELD)

public class Country {
    // Указываем дополнительные свойства
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    public Long id;

    @Column(name = "name", nullable = false, unique = true)
    public String name;

    /**
     * Конструктор без параметров
     */
    public Country() {}

    /**
     * Конструктор с параметром IDF
     * @param id - поле, являющееся главным идентификатором
     */
    public Country(Long id) {
        this.id = id;
    }
}
