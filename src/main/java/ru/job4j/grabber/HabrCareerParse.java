package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HarbCareerDateTimeParser;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
     *
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
     *
     * @param link ссылка на страницу для парсинга
     * @return возвращает список объектов типа Post
     * @throws IOException
     */
    @Override
    public List<Post> list(String link) {
        List<Post> postsList = new ArrayList<>();
        for (int i = 5; i <= 5; i++) {
            Connection connection = Jsoup.connect(link + "?page=" + i);
            try {
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                for (Element row : rows) {
                    postsList.add(getPost(row));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return postsList;
    }

    public static void main(String[] args) {
        HarbCareerDateTimeParser parser = new HarbCareerDateTimeParser();
        HabrCareerParse hcp = new HabrCareerParse(parser);
        List<Post> list = hcp.list(PAGE_LINK);
        try (InputStream in = HabrCareerParse.class.getClassLoader()
                .getResourceAsStream("grabber.properties")) {
            Properties config = new Properties();
            config.load(in);
            PsqlStore psqlStore = new PsqlStore(config);
            for (Post post : list) {
                psqlStore.save(post);
            }
            System.out.println(psqlStore.findById(5).toString());
            System.out.println(psqlStore.getAll().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
