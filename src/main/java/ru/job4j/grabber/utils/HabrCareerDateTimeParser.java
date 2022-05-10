package ru.job4j.grabber.utils;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class HabrCareerDateTimeParser implements DateTimeParser {
    /**
     *  Метод, преобразующий дату из формата career.habr.com
     *  в формат LocalDateTime
     * @param parse дата полученная в результате парсинга
     * @return возвращает объект типа LocalDateTime
     */
    @Override
    public LocalDateTime parse(String parse) {
        return LocalDateTime.parse(parse, ISO_DATE_TIME);
    }
}
