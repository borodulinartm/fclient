package ru.iu3.backend.controllers;

// Импортируем необходимые модули
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.models.Artists;
import ru.iu3.backend.models.Country;
import ru.iu3.backend.repositories.CountryRepository;
import ru.iu3.backend.tools.DataValidationException;

import java.util.*;

/**
 * Класс-контроллер таблицы "стран"
 * @author artem
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/v1")
public class CountryController {
    // Получаем ссылку на репозиторий
    @Autowired
    CountryRepository countryRepository;

    /**
     * Метод, который возвращает просто список стран
     * @return - Список стран, которые есть в базе данных
     */
    @GetMapping("/countries")
    public Page<Country> getAllCountries(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return countryRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    /**
     * Метод, который извлекает информацию из таблицы country, вместе с информацией по конкретному artists
     * @param countryID - ID страны
     * @return - Возвращает artists
     */
    @GetMapping("/countries/{id}/artists")
    public ResponseEntity<List<Artists>> getCountryArtists(@PathVariable(value = "id") Long countryID) {
        Optional<Country> cc = countryRepository.findById(countryID);
        if (cc.isPresent()) {
            return ResponseEntity.ok(cc.get().artists);
        }

        return ResponseEntity.ok(new ArrayList<Artists>());
    }

    @GetMapping("/countries/{id}")
    public ResponseEntity<Country> getCountry(@PathVariable(value = "id") Long countryId)
            throws DataValidationException {
        Country country = countryRepository.findById(countryId).
                orElseThrow(() -> new DataValidationException("Not founding"));

        return ResponseEntity.ok(country);
    }

     /**
     * Метод, который добавляет country в таблиц
     * RequestBody - это наш экземпляр (через curl передаётся в виде JSON)
     * @param country - наш экземпляр класса country
     * @return - статус (ОК/НЕ ОК)
     */
    @PostMapping("/countries")
    public ResponseEntity<Object> createCountry(@RequestBody Country country)
        throws DataValidationException {
        try {
            // Попытка сохранить что-либо в базу данных
            Country newCountry = countryRepository.save(country);
            return new ResponseEntity<Object>(newCountry, HttpStatus.OK);
        } catch (Exception exception) {
            if (exception.getMessage().contains("countries.name_UNIQUE")) {
                throw new DataValidationException("Эта страна уже есть в базе");
            } else {
                throw new DataValidationException("Неизвестная ошибка");
            }
        }
    }

    /**
     * Метод, который обновляет данные в таблице
     * @param countryID - указываем id по которому будем обновлять данные
     * @param countryDetails - сводки по Country
     * @return - ОК/НЕ ОК
     */
    @PutMapping("/countries/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable(value = "id") Long countryID,
                                     @Validated @RequestBody Country countryDetails) throws DataValidationException {
        try {
            Country country = countryRepository.findById(countryID).
                    orElseThrow(() -> new DataValidationException("Страна с таким индексом не найдена"));
            country.name = countryDetails.name;
            countryRepository.save(country);
            return ResponseEntity.ok(country);
        } catch (Exception exception) {
            if (exception.getMessage().contains("ConstraintViolationException")) {
                throw new DataValidationException("Эта страна уже есть в базе");
                //throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Эта страна уже есть в базе");
            } else {
                //throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Неизвестная ошибка");
                throw new DataValidationException("Неизвестная ошибка");
            }
        }
    }

    // Удалять через пользовательский интерфейс проще, чем по одному
    @PostMapping("/deletecountries")
    public ResponseEntity deleteCountries(@Validated @RequestBody List<Country> countries) {
        countryRepository.deleteAll(countries);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Метод, который удаляет информацию из базы данных
     * @param countryId - по какому ID-шнику удаляем информацию
     * @return - возвращает true, если удалено успешно, false - в противном случае
     */
    @DeleteMapping("/countries/{id}")
    public ResponseEntity<Object> deleteCountry(@PathVariable(value = "id") Long countryId) {
        Optional<Country> country = countryRepository.findById(countryId);
        Map<String, Boolean> resp = new HashMap<>();

        // Возвратит true, если объект существует (не пустой)
        if (country.isPresent()) {
            countryRepository.delete(country.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }

        return ResponseEntity.ok(resp);
    }
}
