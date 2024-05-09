package org.example.service;

import org.example.models.Article;
import org.example.models.Panier;
import org.example.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PanierService implements IService<Panier> {
    Connection connect;

    public PanierService() {
        connect = MyDataBase.getInstance().getConnection();
    }


    @Override
    public void add(Panier panier) throws SQLException {
        String sql = "INSERT INTO panier (prix_tot, quantity, article_id, user_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setDouble(1, panier.getPrix_tot());
            preparedStatement.setInt(2, panier.getQuantity());
            preparedStatement.setInt(3, panier.getArticle_id());
            preparedStatement.setInt(4, panier.getUser_id());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void update(Panier panier) throws SQLException {
        // Update logic here if needed
    }

    public void update(int id, int newQuantity) throws SQLException {
        String sql = "UPDATE panier SET quantity = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setInt(1, newQuantity);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM panier WHERE id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Panier> select() throws SQLException {
        // Select all paniers logic here if needed
        return new ArrayList<>();
    }

    @Override
    public Panier selectWhere(int id) throws SQLException {
        // Select panier by id logic here if needed
        return null;
    }
    public List<Article> getPanierItems(int userId) throws SQLException {
        List<Article> panierItems = new ArrayList<>();
        String sql = "SELECT article.*, SUM(panier.quantity) AS total_quantity " +
                "FROM panier " +
                "INNER JOIN article ON panier.article_id = article.id " +
                "WHERE user_id = ? " +
                "GROUP BY article.id";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Article article = new Article();
                article.setId(resultSet.getInt("id"));
                article.setNom(resultSet.getString("nom"));
                article.setPrix(resultSet.getDouble("prix"));
                article.setDescription(resultSet.getString("description"));
                article.setQuantite(resultSet.getInt("total_quantity")); // Set the total quantity of the article in the cart
                // Set other properties of the article as needed
                panierItems.add(article);
            }
        }
        return panierItems;
    }


    public void updateQuantity(int articleId, int userId, int newQuantity) throws SQLException {
        String sql = "UPDATE panier SET quantity = ? WHERE article_id = ? AND user_id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setInt(1, newQuantity);
            preparedStatement.setInt(2, articleId);
            preparedStatement.setInt(3, userId);
            preparedStatement.executeUpdate();
        }
    }

    public void removeFromCart(int articleId, int userId) throws SQLException {
        String sql = "DELETE FROM panier WHERE article_id = ? AND user_id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setInt(1, articleId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        }
    }

}
