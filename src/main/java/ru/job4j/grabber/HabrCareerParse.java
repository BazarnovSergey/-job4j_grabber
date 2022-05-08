package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    /**
     * Метод для загрузки деталей объявления
     *
     * @param link ссылка на вакансию
     * @return Возвращает описание вакансии в текстовом формате
     * @throws IOException
     */
    private String retrieveDescription(String link) throws IOException {
        Document document = Jsoup.connect(link).get();
        Elements elementsDescription = document.getElementsByAttributeValue("class", "style-ugc");
        return elementsDescription.text();
    }

    public static void main(String[] args) throws IOException {
        HabrCareerParse habrCareerParse = new HabrCareerParse();
        for (int i = 1; i <= 5; i++) {
            Connection connection = Jsoup.connect(PAGE_LINK + "?page=" + i);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            for (Element row : rows) {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first();
                Element dateTimeElement = dateElement.child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String description = habrCareerParse.retrieveDescription(link);
                //  System.out.printf("%s %s %s %s%n", vacancyName, link, description, dateTimeElement.attr("datetime"));
                System.out.println("Название вакансии : " + vacancyName
                        + "\n" + "Дата : " + dateTimeElement.attr("datetime")
                        + "\n" + "Ссылка : " + link
                        + "\n" + "Описание : " + description);
                System.out.println("----------------------------");
            }
        }
    }
}