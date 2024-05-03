package org.example.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.models.CategorieProduit;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieProduitService {
    private final Connection connect;

    public CategorieProduitService() {
        this.connect = MyDataBase.getInstance().getConnection();
    }

    public List<CategorieProduit> selectAll() throws SQLException {
        List<CategorieProduit> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorie_produit";
        try (Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(new CategorieProduit(
                        rs.getInt("id"),
                        rs.getString("nom")
                ));
            }
        }
        return categories;
    }

    public List<CategorieProduit> select() throws SQLException {
        List<CategorieProduit> categories = new ArrayList<>();
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  categorie_produit");
        try (preparedStatement) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    categories.add(new CategorieProduit(
                            resultSet.getInt("id"),
                            resultSet.getString("nom")
                    ));
                }
            }
        }

        return categories;

    }

    public ObservableList<CategorieProduit> getAllCategories() {
        ObservableList<CategorieProduit> categories = FXCollections.observableArrayList();
        String query = "SELECT * FROM categorie_produit";
        try (PreparedStatement ps = connect.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CategorieProduit categorie = new CategorieProduit();
                categorie.setId(rs.getInt("id"));
                categorie.setNom(rs.getString("nom"));
                categories.add(categorie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public void add(CategorieProduit category) {
        String query = "INSERT INTO categorie_produit (nom) VALUES (?)";
        try (PreparedStatement ps = connect.prepareStatement(query)) {
            ps.setString(1, category.getNom());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(CategorieProduit category) throws SQLException {
        String query = "UPDATE categorie_produit SET nom = ? WHERE id = ?";
        try (PreparedStatement pst = connect.prepareStatement(query)) {
            pst.setString(1, category.getNom());
            pst.setInt(2, category.getId());
            pst.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM categorie_produit WHERE id = ?";
        try (PreparedStatement pst = connect.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    public List<CategorieProduit> selectAllCategories() throws SQLException {
        List<CategorieProduit> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorie_produit";
        try (Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(new CategorieProduit(
                        rs.getInt("id"),
                        rs.getString("nom")
                ));
            }
        }
        return categories;
    }

}
