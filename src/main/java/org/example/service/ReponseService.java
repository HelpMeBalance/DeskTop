package org.example.service;

import org.example.models.Question;
import org.example.models.Reponse;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReponseService implements IService<Reponse>{
    Connection connection;
    public ReponseService () {
        connection= MyDataBase.getInstance().getConnection();
    }
    public void add(Reponse reponse) throws SQLException {
        String sql = "INSERT INTO reponse (reponse,question_id ) VALUES (?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, reponse.getReponse());
        preparedStatement.setInt(2, reponse.get$question().getId());
        preparedStatement.executeUpdate();
    }


    @Override
    public void update(Reponse reponse) throws SQLException {
        String sql = "UPDATE reponse SET  reponse = ? ";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, reponse.getReponse());
        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM reponse WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public List<Reponse> select() throws SQLException {
        List<Reponse> reponses = new ArrayList<>();
        String sql = "SELECT * FROM reponse";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String reponse = resultSet.getString("reponse"); // Removed extra spaces
                Question question = new QuestionService().selectWhere(resultSet.getInt("question_id"));
                reponses.add(new Reponse(id, reponse, question));
            }
        }
        return reponses;
    }

    @Override
    public Reponse selectWhere(int id) throws SQLException {
        return null;
    }
}
