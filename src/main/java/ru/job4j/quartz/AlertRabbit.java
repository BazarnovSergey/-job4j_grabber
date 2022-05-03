package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    private static Connection getConnection(Properties properties) throws ClassNotFoundException, SQLException {
        Class.forName(properties.getProperty("driver-class-name"));
        return DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("username"),
                properties.getProperty("password")
        );
    }

    private static Properties loadConfiguration(String fileName) {
        Properties cfg = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream(fileName)) {
            cfg.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cfg;
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Properties properties = loadConfiguration("rabbit.properties");
        try (Connection connection = getConnection(properties)) {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(
                            Integer.parseInt(properties.getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO rabbit (created_date) VALUES (?)");
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}