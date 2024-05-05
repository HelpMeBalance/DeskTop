package org.example.service;

import org.example.models.Article;
import org.example.models.CategorieProduit;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleService {
    private final Connection connect;



    public ArticleService() {
        this.connect = MyDataBase.getInstance().getConnection();
    }

    public List<Article> selectAll() throws SQLException {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM article";
        try (Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                articles.add(new Article(
                        rs.getInt("id"),
                        rs.getInt("categorie_id"),
                        rs.getDouble("prix"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getInt("quantite"),
                        rs.getString("article_picture")
                ));
            }
        }
        return articles;
    }

    public void add(Article article) throws SQLException {
        String sql = "INSERT INTO article (categorie_id, prix, nom, description, quantite, article_picture) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, article.getCategorieId());
            pstmt.setDouble(2, article.getPrix());
            pstmt.setString(3, article.getNom());
            pstmt.setString(4, article.getDescription());
            pstmt.setInt(5, article.getQuantite());
            pstmt.setString(6, article.getArticlePicture());
            pstmt.executeUpdate();
        }
    }

    public void update(Article article) throws SQLException {
        String sql = "UPDATE article SET categorie_id = ?, prix = ?, nom = ?, description = ?, quantite = ?, article_picture = ? WHERE id = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, article.getCategorieId());
            pstmt.setDouble(2, article.getPrix());
            pstmt.setString(3, article.getNom());
            pstmt.setString(4, article.getDescription());
            pstmt.setInt(5, article.getQuantite());
            pstmt.setString(6, article.getArticlePicture());
            pstmt.setInt(7, article.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(Article article) throws SQLException {
        String sql = "DELETE FROM article WHERE id = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, article.getId());
            pstmt.executeUpdate();
        }
    }

    public List<Article> selectByCategory(CategorieProduit category) throws SQLException {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM article WHERE categorie_id = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, category.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    articles.add(new Article(
                            rs.getInt("id"),
                            rs.getInt("categorie_id"),
                            rs.getDouble("prix"),
                            rs.getString("nom"),
                            rs.getString("description"),
                            rs.getInt("quantite"),
                            rs.getString("article_picture")
                    ));
                }
            }
        }
        return articles;
    }

    public List<Article> searchArticles(String searchTerm, String searchField) throws SQLException {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM article WHERE " + searchField + " LIKE ?";        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    articles.add(new Article(
                            rs.getInt("id"),
                            rs.getInt("categorie_id"),
                            rs.getDouble("prix"),
                            rs.getString("nom"),
                            rs.getString("description"),
                            rs.getInt("quantite"),
                            rs.getString("article_picture")
                    ));
                }
            }
        }
        return articles;
    }



}
