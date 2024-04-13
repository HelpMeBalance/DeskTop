package org.example.service;

import org.example.models.Categorie;
import org.example.models.Publication;
import org.example.models.SousCategorie;
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

public class PublicationService implements IService <Publication>{
    Connection connect;
    UserService uS = new UserService();
    CategorieService cS = new CategorieService();
    SousCategorieService scS = new SousCategorieService();
    public PublicationService()
    {
        connect= MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Publication publication) throws SQLException {
    PreparedStatement preparedStatement  = connect.prepareStatement("INSERT INTO publication (user_id, categorie_id, sous_categorie_id, contenu, com_ouvert, anonyme, valide, date_c, date_m, vues, titre, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        preparedStatement.setInt(1, publication.getUser().getId());
        preparedStatement.setInt(2, publication.getCategorie().getId());
        preparedStatement.setInt(3, publication.getSous_categorie().getId());
        preparedStatement.setString(4, publication.getContenu());
        preparedStatement.setBoolean(5, publication.getCom_ouvert());
        preparedStatement.setBoolean(6, publication.getAnonyme());
        preparedStatement.setBoolean(7, publication.getValide());
        preparedStatement.setObject(8, publication.getDate_c());
        preparedStatement.setObject(9, publication.getDate_m());
        preparedStatement.setInt(10, publication.getVues());
        preparedStatement.setString(11, publication.getTitre());
        preparedStatement.setString(12, publication.getImage());
        preparedStatement .executeUpdate();
    }
    @Override
    public void update(Publication publication)throws SQLException {
        PreparedStatement preparedStatement = connect.prepareStatement("UPDATE publication SET titre = ?, contenu = ?, com_ouvert = ?, anonyme = ?,date_m=? WHERE id = ?");
        preparedStatement.setString(1, publication.getTitre());
        preparedStatement.setString(2, publication.getContenu());
        preparedStatement.setBoolean(3, publication.getCom_ouvert());
        preparedStatement.setBoolean(4, publication.getAnonyme());
        preparedStatement.setObject(5, LocalDateTime.now());
        preparedStatement.setInt(6, publication.getId());
        preparedStatement.executeUpdate();
    }
    @Override
    public void delete(int id) throws SQLException{
        PreparedStatement preparedStatement = connect.prepareStatement("DELETE FROM publication WHERE id = ?");
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }
    private Publication createPublication(ResultSet resultSet) throws SQLException {
        return  new Publication(
                resultSet.getInt("id"),
                uS.selectWhere(resultSet.getInt("user_id")),
                cS.selectWhere(resultSet.getInt("categorie_id")),
                scS.selectWhere(resultSet.getInt("sous_categorie_id")),
                resultSet.getString("contenu"),
                resultSet.getBoolean("com_ouvert"),
                resultSet.getBoolean("anonyme"),
                resultSet.getBoolean("valide"),
                resultSet.getObject("date_c", LocalDateTime.class),
                resultSet.getObject("date_m", LocalDateTime.class),
                resultSet.getInt("vues"),
                resultSet.getString("titre"),
                resultSet.getString("image")
        );
    }
    public Publication selectWhere( int id ) throws SQLException{
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  publication WHERE id = ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return createPublication(resultSet);
                }
            }
        }

        return null ;
    }
    @Override
    public List<Publication> select() throws SQLException{
        List<Publication> publications = new ArrayList<>();
        String sql = "SELECT * FROM publication";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                publications.add(createPublication(resultSet));
            }
        }
        return publications;
    }
    public List<Publication> select(String searchTerm) throws SQLException{
        List<Publication> publications = new ArrayList<>();
        String sql = "SELECT * FROM publication";
        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql += " WHERE contenu LIKE ? OR vues LIKE ? OR titre LIKE ?";
        }
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql))
        {
            if (searchTerm != null && !searchTerm.isEmpty()) {
                String likePattern = searchTerm + "%";
                preparedStatement.setString(1, likePattern);
                preparedStatement.setString(2, likePattern);
                preparedStatement.setString(3, likePattern);
            }
             try(ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                publications.add(createPublication(resultSet));
            }
            }
        }
        return publications;
    }
    public List<Publication> select(int pageNumber, int pageSize) throws SQLException{
        List<Publication> publications = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;
        String sql = "SELECT * FROM publication  WHERE valide = true ORDER BY vues DESC, date_m DESC LIMIT ? OFFSET ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql))
        {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, offset);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                publications.add(createPublication(resultSet));
            }
        }
        }
        return publications;
    }
    public List<Publication> select(User user,int pageNumber, int pageSize) throws SQLException{
        List<Publication> publications = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  publication  WHERE user_id = ? AND valide = true ORDER BY vues DESC, date_m DESC LIMIT ? OFFSET ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, offset);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    publications.add(createPublication(resultSet));
                }
            }
        }

        return publications;
    }
    public List<Publication> select(Categorie categorie,int pageNumber, int pageSize) throws SQLException{
        List<Publication> publications = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  publication  WHERE categorie_id = ? AND valide = true ORDER BY vues DESC, date_m DESC  LIMIT ? OFFSET ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, categorie.getId());
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, offset);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    publications.add(createPublication(resultSet));
                }
            }
        }

        return publications;
    }
    public List<Publication> select(SousCategorie sousCategorie,int pageNumber, int pageSize) throws SQLException{
        List<Publication> publications = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM  publication  WHERE sous_categorie_id = ? AND valide = true ORDER BY vues DESC, date_m DESC LIMIT ? OFFSET ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, sousCategorie.getId());
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, offset);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    publications.add(createPublication(resultSet));
                }
            }
        }

        return publications;
    }
    public List<Publication> select(String sortField, String sortOrder,int pageNumber, int pageSize) throws SQLException{
        List<Publication> publications = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;
        if (!Arrays.asList("anonyme","titre", "contenu", "valide", "vues", "date_c", "date_m","com_ouvert").contains(sortField)) {
            sortField = "date_m"; // Default sort field
        }
        if (!Arrays.asList("asc", "desc").contains(sortOrder)) {
            sortOrder = "asc"; // Default sort order
        }
        String sql = "SELECT * FROM publication ORDER BY " + sortField + " " + sortOrder.toUpperCase()+" LIMIT ? OFFSET ?";
        PreparedStatement preparedStatement = connect.prepareStatement(sql );
        try (preparedStatement)
        {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, offset);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    publications.add(createPublication(resultSet));
                }
            }
        }

        return publications;
    }

    public void addView(int id) throws SQLException {
        PreparedStatement preparedStatement = connect.prepareStatement("UPDATE publication SET vues = vues + 1 WHERE id = ?");
        preparedStatement.setInt(1,id);
        preparedStatement.executeUpdate();
    }
    public void validate(int id) throws SQLException {
        PreparedStatement preparedStatement = connect.prepareStatement("UPDATE publication SET valide = ? WHERE id = ?");
        preparedStatement.setBoolean(1,!(selectWhere(id).getValide()));
        preparedStatement.setInt(2,id);
        preparedStatement.executeUpdate();
    }


}
