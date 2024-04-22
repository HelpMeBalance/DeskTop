package org.example.service;

import org.example.models.Commentaire;
import org.example.models.Publication;
import org.example.models.User;
import org.example.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommentaireService implements IService <Commentaire> {
    Connection connect;
    UserService uS = new UserService();
    PublicationService pS = new PublicationService();
    public CommentaireService()
    {
        connect= MyDataBase.getInstance().getConnection();
    }
    @Override
    public void add(Commentaire commentaire) throws SQLException {
        String sql = "INSERT INTO commentaire (user_id, publication_id, contenu, anonyme, likes, valide, date_c, date_m) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setInt(1, commentaire.getUser().getId());
            preparedStatement.setInt(2, commentaire.getPublication().getId());
            preparedStatement.setString(3, commentaire.getContenu());
            preparedStatement.setBoolean(4, commentaire.getAnonyme());
            preparedStatement.setInt(5, commentaire.getLikes());
            preparedStatement.setBoolean(6, commentaire.getValide());
            preparedStatement.setObject(7, commentaire.getDate_c());
            preparedStatement.setObject(8, commentaire.getDate_m());
            preparedStatement.executeUpdate();
        }
    }
    private Commentaire createCommentaire(ResultSet resultSet) throws SQLException {
        return  new Commentaire(
                resultSet.getInt("id"),
                uS.selectWhere(resultSet.getInt("user_id")),
                pS.selectWhere(resultSet.getInt("publication_id")),
                resultSet.getString("contenu"),
                resultSet.getBoolean("anonyme"),
                resultSet.getInt("likes"),
                resultSet.getBoolean("valide"),
                resultSet.getObject("date_c", LocalDateTime.class),
                resultSet.getObject("date_m", LocalDateTime.class)
        );
    }

    @Override
    public void update(Commentaire commentaire) throws SQLException {
        String sql = "UPDATE commentaire SET contenu = ?, anonyme = ?, date_m = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setString(1, commentaire.getContenu());
            preparedStatement.setBoolean(2, commentaire.getAnonyme());
            preparedStatement.setObject(3, LocalDateTime.now());
            preparedStatement.setInt(4, commentaire.getId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM commentaire WHERE id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }
    @Override
    public Commentaire selectWhere(int id) throws SQLException {
        String sql = "SELECT * FROM commentaire WHERE id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return createCommentaire(resultSet);
                }
            }
        }
        return null;
    }
    @Override
    public List<Commentaire> select() throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        String sql = "SELECT * FROM commentaire";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                commentaires.add(createCommentaire(resultSet));
            }
        }
        return commentaires;
    }
    public List<Commentaire> search(String searchTerm,int pubId) throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        String sql = "SELECT * FROM commentaire ";
        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql += " WHERE contenu LIKE ? AND  publication_id = ?";
        }
        PreparedStatement preparedStatement = connect.prepareStatement(sql);
        try (preparedStatement)
        {
            if (searchTerm != null && !searchTerm.isEmpty()) {
                String likePattern = searchTerm + "%";
                preparedStatement.setString(1, likePattern);
                preparedStatement.setInt(2, pubId);
            }
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    commentaires.add(createCommentaire(resultSet));
                }
            }
        }
        return commentaires;
    }
    public List<Commentaire> search(String searchTerm,int pubId,int pageNumber, int pageSize) throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        int offset = (pageNumber) * pageSize;
        String sql = "SELECT * FROM commentaire ";
        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql += " WHERE contenu LIKE ? AND  publication_id = ?  ORDER BY date_m DESC LIMIT ? OFFSET ?";
        }
        PreparedStatement preparedStatement = connect.prepareStatement(sql);
        try (preparedStatement)
        {
            if (searchTerm != null && !searchTerm.isEmpty()) {
                String likePattern = searchTerm + "%";
                preparedStatement.setString(1, likePattern);
                preparedStatement.setInt(2, pubId);
                preparedStatement.setInt(3, pageSize);
                preparedStatement.setInt(4, offset);
            }
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    commentaires.add(createCommentaire(resultSet));
                }
            }
        }
        return commentaires;
    }
    public List<Commentaire> select(int pageNumber, int pageSize) throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        int offset = (pageNumber) * pageSize;
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  commentaire ORDER BY date_m DESC LIMIT ? OFFSET ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, offset);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    commentaires.add(createCommentaire(resultSet));
                }
            }
        }
        return commentaires;
    }
    public List<Commentaire> select(User user,int pageNumber, int pageSize) throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  commentaire  WHERE user_id = ? AND valide = true ORDER BY date_m DESC LIMIT ? OFFSET ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, offset);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    commentaires.add(createCommentaire(resultSet));
                }
            }
        }
        return commentaires;
    }
    public List<Commentaire> select(int pubId,int pageNumber, int pageSize) throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        int offset = (pageNumber) * pageSize;
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  commentaire  WHERE publication_id = ? AND valide = true ORDER BY date_m DESC LIMIT ? OFFSET ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, pubId);
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, offset);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    commentaires.add(createCommentaire(resultSet));
                }
            }
        }
        return commentaires;
    }
    public List<Commentaire> selectAdmin(int pubId,int pageNumber, int pageSize) throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        int offset = (pageNumber) * pageSize;
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  commentaire  WHERE publication_id = ?  ORDER BY date_m DESC LIMIT ? OFFSET ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, pubId);
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, offset);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    commentaires.add(createCommentaire(resultSet));
                }
            }
        }
        return commentaires;
    }
    public List<Commentaire> select(int pubId) throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  commentaire  WHERE publication_id = ? ORDER BY date_m DESC");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, pubId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    commentaires.add(createCommentaire(resultSet));
                }
            }
        }
        return commentaires;
    }
    public List<Commentaire> select(String sortField, String sortOrder,int pageNumber, int pageSize) throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();

        int offset = (pageNumber - 1) * pageSize;
        if (!Arrays.asList( "contenu", "valide", "date_c", "date_m").contains(sortField)) {
            sortField = "date_m"; // Default sort field
        }
        if (!Arrays.asList("asc", "desc").contains(sortOrder)) {
            sortOrder = "asc"; // Default sort order
        }
        String sql = "SELECT * FROM commentaire ORDER BY " + sortField + " " + sortOrder.toUpperCase()+" LIMIT ? OFFSET ? ";
        PreparedStatement preparedStatement = connect.prepareStatement(sql);
        try (preparedStatement)
        {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, offset);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    commentaires.add(createCommentaire(resultSet));
                }
            }
        }
        return commentaires;
    }

    public void validate(int id) throws SQLException {
        PreparedStatement preparedStatement = connect.prepareStatement("UPDATE commentaire SET valide = ? WHERE id = ?");
        preparedStatement.setBoolean(1,!(selectWhere(id).getValide()));
        preparedStatement.setInt(2,id);
        preparedStatement.executeUpdate();
    }
    public int countComments(Publication publication) throws SQLException {
        int count = 0;
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT COUNT(*) FROM commentaire WHERE publication_id = ?");
        try (preparedStatement) {
            preparedStatement.setInt(1, publication.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            }
        }
        return count;
    }
}
