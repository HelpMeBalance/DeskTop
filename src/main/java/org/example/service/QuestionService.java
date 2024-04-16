package org.example.service;

import org.example.models.RendezVous;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Question {
    public void add(Question question) throws SQLException {
        String sql = "INSERT INTO Question (question) VALUES (?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(2, question.get);
        ;

        preparedStatement.executeUpdate();
    }
}
