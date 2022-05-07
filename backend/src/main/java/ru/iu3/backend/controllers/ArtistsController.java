package ru.iu3.backend.controllers;

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
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.repositories.ArtistRepository;
import ru.iu3.backend.repositories.CountryRepository;
import ru.iu3.backend.tools.DataValidationException;

import java.util.*;

/**
 * Метод, который отражает логику работы таблицы артистов
 * @author artem
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/v1")
public class ArtistsController {
    // Здесь используется два репозитория: репозиторий артистов и репозиторий стран
    @Autowired
    ArtistRepository artistsRepository;

    @Autowired
    CountryRepository countryRepository;

    /**
     * Метод, который возвращает список артистов для данной БД
     * @return - список артистов, который представлен в JSON
     */
    @GetMapping("/artists")
    public Page<Artists> getAllArtists(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return artistsRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity getArtist(@PathVariable(value = "id") Long artistId) throws DataValidationException {
        Artists artists = artistsRepository.findById(artistId).
                orElseThrow(() -> new DataValidationException("Художник с таким индексом не найден"));
        return ResponseEntity.ok(artists);
    }

    /**
     * Метод, который возвращает по значению артиста список картин, которые он создал
     * @param artistID - ID артиста (передаётся через JSON)
     * @return - ок, если информация была найдена. Выведет пустой список, если ничего не было найдено
     */
    @GetMapping("/artists/{id}/paintings")
    public ResponseEntity<Object> getMuseumsFromArtist(@PathVariable(value = "id") Long artistID) {
        Optional<Artists> optionalArtists = artistsRepository.findById(artistID);

        if (optionalArtists.isPresent()) {
            return ResponseEntity.ok(optionalArtists.get().paintings);
        }

        return ResponseEntity.ok(new ArrayList<Museum>());
    }

    /**
     * Метод, который добавляет артистов в базу данных
     * @param artists - Структура данных, которая поступает из PostMan в виде JSON-файла
     *                где распарсивается и представлется в нужном для нас виде
     * @return - Статус. 404, если ок. В противном случае, будет выдавать ошибку
     * @throws Exception - выброс исключения. Обязательное требование
     */
    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artists artists) throws Exception {
        try {
            // Попытка сохранить что-либо в базу данных
            Artists newArtists = artistsRepository.save(artists);
            return new ResponseEntity<Object>(newArtists, HttpStatus.OK);
        } catch (Exception exception) {
            if (exception.getMessage().contains("artists.name_UNIQUE")) {
                throw new DataValidationException("Эта страна уже есть в базе");
            } else {
                throw new DataValidationException("Неизвестная ошибка");
            }
        }
    }

    @PostMapping("/deleteartists")
    public ResponseEntity deleteArtists(@Validated @RequestBody List<Artists> artists) {
        artistsRepository.deleteAll(artists);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Метод, который обновляет данные для артистов
     * @param artistsID - ID артиста, по которому будет осуществляться собственно поиск
     * @param artistDetails - детальная информация по артистам
     * @return - возвращает заголовок. Если всё ок, то 200. Иначе будет ошибка
     */
    @PutMapping("/artists/{id}")
    public ResponseEntity<Artists> updateArtist(@PathVariable(value = "id") Long artistsID,
                                                @RequestBody Artists artistDetails) {
        Artists artist = null;
        Optional<Artists> cc = artistsRepository.findById(artistsID);

        if (cc.isPresent()) {
            artist = cc.get();

            // Обновляем информацию по артистам
            artist.name = artistDetails.name;
            artist.age = artistDetails.age;
            artist.countryid = artistDetails.countryid;

            artistsRepository.save(artist);
            return ResponseEntity.ok(artist);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "artist not found");
        }
    }

    /**
     * Метод, который удаляет артистов
     * @param artistID - ID артиста, который будет удалён из базы данных
     * @return - вернёт 200, если всё было ок
     */
    @DeleteMapping("/artists/{id}")
    public ResponseEntity<Object> deleteArtist(@PathVariable(value = "id") Long artistID) {
        Optional<Artists> artists = artistsRepository.findById(artistID);
        Map<String, Boolean> resp = new HashMap<>();

        // Возвратит true, если объект существует (не пустой)
        if (artists.isPresent()) {
            artistsRepository.delete(artists.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }

        return ResponseEntity.ok(resp);
    }
}
