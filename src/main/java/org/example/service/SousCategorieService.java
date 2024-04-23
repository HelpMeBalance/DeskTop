package org.example.service;

import org.example.models.Categorie;
import org.example.models.SousCategorie;
import org.example.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SousCategorieService {
    Connection connect;
    CategorieService cS = new CategorieService();
    public SousCategorieService()
    {
        connect= MyDataBase.getInstance().getConnection();
    }
    public void add(SousCategorie sousCategorie) throws SQLException {
        String sql = "INSERT INTO sous_categorie (categorie_id,nom, description) VALUES (?,?, ?)";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setInt(1, sousCategorie.getCategorie().getId());
            preparedStatement.setString(2, sousCategorie.getNom());
            preparedStatement.setString(3, sousCategorie.getDescription());
            preparedStatement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        PreparedStatement preparedStatement = connect.prepareStatement("DELETE FROM sous_categorie WHERE id = ?");
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }
    public void update(SousCategorie sousCategorie) throws SQLException {
        String sql = "UPDATE sous_categorie SET nom = ?, description = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {

            preparedStatement.setString(1, sousCategorie.getNom());
            preparedStatement.setString(2, sousCategorie.getDescription());
            preparedStatement.setInt(3, sousCategorie.getId());
            preparedStatement.executeUpdate();
        }
    }
    public List<SousCategorie> select() throws SQLException {
        List<SousCategorie> categories = new ArrayList<>();
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  sous_categorie");
        try (preparedStatement)
        {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    categories.add(new SousCategorie(resultSet.getInt("id"),
                            cS.selectWhere(resultSet.getInt("categorie_id")),
                            resultSet.getString("nom"),
                            resultSet.getString("description")
                    ));
                }
            }
        }

        return categories;
    }
    public List<SousCategorie> select(Categorie categorie) throws SQLException {
        List<SousCategorie> categories = new ArrayList<>();
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  sous_categorie WHERE categorie_id = ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, categorie.getId());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    categories.add(new SousCategorie(resultSet.getInt("id"),
                            cS.selectWhere(resultSet.getInt("categorie_id")),
                            resultSet.getString("nom"),
                            resultSet.getString("description")
                    ));
                }
            }
        }

        return categories;
    }
    public SousCategorie selectWhere(int id) throws SQLException{
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM sous_categorie WHERE id = ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new SousCategorie(resultSet.getInt("id"),
                            cS.selectWhere(resultSet.getInt("categorie_id")),
                            resultSet.getString("nom"),
                            resultSet.getString("description")
                    );
                }
            }
        }

        return null ;
    }
    public SousCategorie selectWhere(String nom) throws SQLException{
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM sous_categorie WHERE nom = ? ");
        try (preparedStatement)
        {
            preparedStatement.setString(1, nom);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new SousCategorie(resultSet.getInt("id"),
                            cS.selectWhere(resultSet.getInt("categorie_id")),
                            resultSet.getString("nom"),
                            resultSet.getString("description")
                    );
                }
            }
        }

        return null ;
    }

    public int countSubcategories(Categorie categorie) throws SQLException {
        int count = 0;
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT COUNT(*) FROM sous_categorie WHERE categorie_id = ?");
        try (preparedStatement) {
            preparedStatement.setInt(1, categorie.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            }
        }
        return count;
    }

}
