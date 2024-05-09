package org.example.service;

import org.example.models.likee;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class likeeService   {

    // Connection to the database
    private final Connection connection;

    public likeeService() {
        connection= MyDataBase.getInstance().getConnection();
    }

    // Create a new Likee record
    public void addLikee(likee newLikee,boolean l) throws SQLException {
        String query = "INSERT INTO likee (id, likee) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, newLikee.getId());
            stmt.setBoolean(2, l);
            stmt.executeUpdate();
        }
    }

    // Retrieve a list of all Likee records
    public List<likee> getAllLikees() throws SQLException {
        List<likee> likees = new ArrayList<>();
        String query = "SELECT * FROM likee";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                boolean like = rs.getBoolean("likee");
                likee likeeObj = new likee(id, like);
                likees.add(likeeObj);
            }
        }
        return likees;
    }

    // Update the like status
    public void updateLikee(likee updatedLikee) throws SQLException {
        String query = "UPDATE likee SET likee = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, updatedLikee.isLike());
            stmt.setInt(2, updatedLikee.getId());
            stmt.executeUpdate();
        }
    }

    // Delete a Likee record by ID
    public void deleteLikee(int id) throws SQLException {
        String query = "DELETE FROM likee WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
