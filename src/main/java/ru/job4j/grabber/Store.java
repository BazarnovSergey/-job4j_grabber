package ru.job4j.grabber;

import java.util.List;

/**
 * Интерфейс содержит методы осуществляющие связь с базой данных
 */
public interface Store {
    /**
     * сохраняет объявление в базе
     */
    void save(Post post);

    /**
     * позволяет извлечь объявления из базы
     */
    List<Post> getAll();

    /**
     * озволяет извлечь объявление из базы по id
     * @param id
     */
    Post findById(int id);
}
