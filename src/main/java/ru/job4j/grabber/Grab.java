package ru.job4j.grabber;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Интерфейс содержит метод для периодического запуска парсера
 * с использованием quartz.
 */
public interface Grab {

    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
