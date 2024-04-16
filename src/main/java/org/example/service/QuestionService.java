package org.example.service;

import javafx.util.converter.LocalDateTimeStringConverter;
import org.example.models.Question;
import org.example.models.RendezVous;
import org.example.models.User;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuestionService implements IService<Question>{
    Connection connection;
    public QuestionService() {
        connection= MyDataBase.getInstance().getConnection();
    }
    public void add(Question question) throws SQLException {
        String sql = "INSERT INTO question (question,date,active) VALUES (?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, question.getQuestion());
        preparedStatement.setObject(2, LocalDateTime.now());
        preparedStatement.setBoolean(3, false);
        preparedStatement.executeUpdate();
    }


    @Override
    public void update(Question question) throws SQLException {
        String sql = "UPDATE question SET question = ?, date = ? , active=? WHERE id = ? ";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, question.getQuestion());
        preparedStatement.setObject(2, LocalDateTime.now());
        preparedStatement.setBoolean(3, false);
        preparedStatement.setInt(4, question.getId());
        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM question WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public List<Question> select() throws SQLException {
        List<Question > question = new ArrayList<>();
        String sql = "SELECT * FROM question";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String questions  = resultSet.getString("question");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                boolean active = resultSet.getBoolean("active");
                // Assuming you have constructors or setter methods in the RendezVous class
                question.add(new Question (id,questions,date,active));
            }
        }
        return question;
    }

    @Override
    public Question selectWhere(int id) throws SQLException {
        Question question = new Question();
        String sql = "SELECT * FROM question WHERE id = "+ String.valueOf(id);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int idq = resultSet.getInt("id");
                String questions  = resultSet.getString("question");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                boolean active = resultSet.getBoolean("active");
                // Assuming you have constructors or setter methods in the RendezVous class
                question = new Question (idq,questions,date,active);
            }
        }
        return question;
    }
}
