package org.example.service;

import org.example.models.Like;
import org.example.models.Publication;
import org.example.models.User;
import org.example.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LikeService {
    Connection connect;
    UserService uS = new UserService();
    PublicationService pS = new PublicationService();
    public LikeService (){connect= MyDataBase.getInstance().getConnection();}

    public void add(Like like) throws SQLException {
        PreparedStatement preparedStatement  = connect.prepareStatement("INSERT INTO `like` (user_id, publication_id) VALUES (?,?)");
        preparedStatement.setInt(1, like.getUser().getId());
        preparedStatement.setInt(2, like.getPublication().getId());
        preparedStatement .executeUpdate();
    }
    public void delete(int id) throws SQLException {
        PreparedStatement preparedStatement = connect.prepareStatement("DELETE FROM `like` WHERE id = ?");
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    public List<Like> select() throws SQLException {
        List<Like> likes = new ArrayList<>();
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  `like`");
        try (preparedStatement)
        {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    likes.add(new Like(resultSet.getInt("id"),
                            uS.selectWhere(resultSet.getInt("user_id")),
                            pS.selectWhere(resultSet.getInt("publication_id"))
                    ));
                }
            }
        }

        return likes;
    }
    public List<Like> select(User user) throws SQLException {
        List<Like> likes = new ArrayList<>();
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  `like`  WHERE user_id = ? ");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, user.getId());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    likes.add(new Like(resultSet.getInt("id"),
                            uS.selectWhere(resultSet.getInt("user_id")),
                            pS.selectWhere(resultSet.getInt("publication_id"))
                            ));
                }
            }
        }

        return likes;
    }
    public List<Like> select(Publication publication) throws SQLException {
        List<Like> likes = new ArrayList<>();
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  `like`  WHERE publication_id = ? ");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, publication.getId());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    likes.add(new Like(resultSet.getInt("id"),
                            uS.selectWhere(resultSet.getInt("user_id")),
                            pS.selectWhere(resultSet.getInt("publication_id"))
                    ));
                }
            }
        }

        return likes;
    }
    public Like selectWhere(int userId, int publicationId) throws SQLException{
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  `like`  WHERE user_id = ? AND publication_id = ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, publicationId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Like(resultSet.getInt("id"),
                            uS.selectWhere(resultSet.getInt("user_id")),
                            pS.selectWhere(resultSet.getInt("publication_id"))
                    );
                }
            }
        }

        return null ;
    }
}
