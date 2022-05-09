package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HarbCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

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

    /**
     * Метод создает объект Post с заполненными полями
     * @param row элемент вакансии типа Element
     * @return возваращает объект типа Post
     */
    private Post getPost(Element row) throws IOException {
        Element titleElement = row.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        Element dateTitle = row.select(".vacancy-card__date").first().child(0);
        String vacancyName = titleElement.text();
        String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        LocalDateTime localDateTime = dateTimeParser.parse(dateTitle.attr("datetime"));
        return new Post(vacancyName, link, retrieveDescription(link), localDateTime);
    }

    /**
     * Метод list загружает список всех постов
     * @param link ссылка на страницу для парсинга
     * @return возвращает список объектов типа Post
     * @throws IOException
     */
    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> postsList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Connection connection = Jsoup.connect(link + "?page=" + i);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            for (Element row : rows) {
                postsList.add(getPost(row));
            }
        }
        return postsList;
    }

    public static void main(String[] args) throws IOException {
        HarbCareerDateTimeParser parser = new HarbCareerDateTimeParser();
        HabrCareerParse hcp = new HabrCareerParse(parser);
        List<Post> list = hcp.list(PAGE_LINK);
        for (Post post : list) {
            System.out.println(post.toString());
        }
    }
}
