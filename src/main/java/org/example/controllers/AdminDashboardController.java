package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import org.example.models.User;
import org.example.models.Article;
import org.example.service.UserService;
import org.example.service.ArticleService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AdminDashboardController {
    @FXML public Text Articles;
    @FXML private LineChart<Number, Number> userSignUpChart;
    @FXML public Text Psy;
    @FXML public Text Admins;
    @FXML private Text totalUsersText;

    private UserService userService;

    private void updateSignupChart(int year, int month) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(month + "/" + year + " Signups");

        Map<LocalDate, Integer> data = userService.getDailySignupData(year, month);
        int cumulativeCount = 0;
        for (Map.Entry<LocalDate, Integer> entry : data.entrySet()) {
            // Only add up to today's date
            if (entry.getKey().isAfter(LocalDate.now())) {
                break;
            }
            cumulativeCount += entry.getValue();
            series.getData().add(new XYChart.Data<>(entry.getKey().getDayOfMonth(), cumulativeCount));
        }

        userSignUpChart.getData().clear(); // Clear previous data
        userSignUpChart.getData().add(series);
    }



    public void initialize() {
        userService = new UserService();
        updateTotalUsers();
        updateArticles();
        LocalDate now = LocalDate.now();
        updateSignupChart(now.getYear(), now.getMonthValue()); // Automatically load data for the current month

    }

    private void updateTotalUsers() {
        try {
            List<User> users = userService.select();
            totalUsersText.setText(String.valueOf(users.size()));
        } catch (SQLException e) {
            System.out.println("Error while fetching users: " + e.getMessage());
        }
    }

    public void updateArticles() {
        ArticleService articleService = new ArticleService();
        try {
            List<Article> articles = articleService.selectAll();
            Articles.setText(String.valueOf(articles.size()));
        } catch (SQLException e) {
            System.out.println("Error while fetching articles: " + e.getMessage());
        }
    }
}
