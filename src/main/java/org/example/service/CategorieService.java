package org.example.service;

import org.example.models.Categorie;
import org.example.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategorieService {
    Connection connect;
    public CategorieService()
    {
        connect= MyDataBase.getInstance().getConnection();
    }
    public void add(Categorie categorie) throws SQLException {
        String sql = "INSERT INTO categorie (nom, description) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setString(1, categorie.getNom());
            preparedStatement.setString(2, categorie.getDescription());
            preparedStatement.executeUpdate();
        }
    }
    public void delete(int id) throws SQLException {
        PreparedStatement preparedStatement = connect.prepareStatement("DELETE FROM categorie WHERE id = ?");
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }
    public void update(Categorie categorie) throws SQLException {
        String sql = "UPDATE categorie SET nom = ?, description = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setString(1, categorie.getNom());
            preparedStatement.setString(2, categorie.getDescription());
            preparedStatement.setInt(3, categorie.getId());
            preparedStatement.executeUpdate();
        }
    }
    public List<Categorie> select() throws SQLException {
        List<Categorie> categories = new ArrayList<>();
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  categorie");
        try (preparedStatement)
        {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    categories.add(new Categorie(resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("description")
                    ));
                }
            }
        }

        return categories;
    }
    public List<Categorie> selectCategoriesWithSubcategories() throws SQLException {
        List<Categorie> categoriesWithSubcategories = new ArrayList<>();
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT DISTINCT categorie.* FROM categorie INNER JOIN sous_categorie ON categorie.id = sous_categorie.categorie_id");
        try (preparedStatement) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    categoriesWithSubcategories.add(new Categorie(
                            resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("description")
                    ));
                }
            }
        }

        return categoriesWithSubcategories;
    }

    public Categorie selectWhere(int id) throws SQLException{
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM categorie WHERE id = ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Categorie(resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("description")
                    );
                }
            }
        }

        return null ;
    }
    public Categorie selectWhere(String nom) throws SQLException{
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM categorie WHERE nom = ? ");
        try (preparedStatement)
        {
            preparedStatement.setString(1, nom);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Categorie(resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("description")
                    );
                }
            }
        }

        return null ;
    }
}
