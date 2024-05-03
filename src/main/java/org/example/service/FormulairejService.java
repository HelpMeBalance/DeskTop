package org.example.service;

import org.example.models.Formulairej;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormulairejService implements IService<Formulairej> {
    Connection connection;

    public FormulairejService() {
        connection = MyDataBase.getInstance().getConnection();
    }


    public void add1(int idf, String qj, String rj,int idr) throws SQLException {
        String sql = "INSERT INTO formulairej (idf, question, reponse,idr) VALUES (?, ?, ?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idf);
            preparedStatement.setString(2, qj);
            preparedStatement.setString(3, rj);
            preparedStatement.setInt(4,idr);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void add(Formulairej formulairej) throws SQLException {

    }

    @Override
    public void update(Formulairej formulairej) throws SQLException {
        String sql = "UPDATE formulairej SET question = ?, reponse = ? WHERE idf = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, formulairej.getQuestion());
            preparedStatement.setString(2, formulairej.getReponse());
            preparedStatement.setInt(3, formulairej.getIdf());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void delete(int idf) throws SQLException {
        String sql = "DELETE FROM formulairej WHERE idf = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idf);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Formulairej> select() throws SQLException {
        List<Formulairej> formulairejList = new ArrayList<>();
        String sql = "SELECT * FROM formulairej";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int idf = resultSet.getInt("idf");
                String question = resultSet.getString("question");
                String reponse = resultSet.getString("reponse");
                Formulairej formulairej = new Formulairej(idf, question, reponse);
                formulairejList.add(formulairej);
            }
        }
        return formulairejList;
    }

    @Override
    public Formulairej selectWhere(int idf) throws SQLException {
        String sql = "SELECT * FROM formulairej WHERE idr = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idf);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String question = resultSet.getString("question");
                    String reponse = resultSet.getString("reponse");
                    return new Formulairej(idf, question, reponse);
                }
            }
        }
        return null;
    }
    public List<Formulairej> selectWhere1(int idf) throws SQLException {
        List<Formulairej> formulairejList = new ArrayList<>();
        String sql = "SELECT * FROM formulairej WHERE idr = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idf);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String question = resultSet.getString("question");
                    String reponse = resultSet.getString("reponse");
                    formulairejList.add(new Formulairej(idf, question, reponse));
                }
            }
        }
        return formulairejList;
    }



}
