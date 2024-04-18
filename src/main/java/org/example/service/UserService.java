package org.example.service;


import org.example.models.User;
import org.example.utils.MyDataBase;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserService implements IService <User>{
    Connection connect;
    public UserService()
    {
        connect= MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(User user) throws SQLException {
        String sql = "INSERT INTO user (email, roles, password, firstname, lastname, profile_picture, is_banned, ban_expires_at, created_at, google_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2,getRolesjson(user.getRoles()));
            preparedStatement.setString(3, new BCryptPasswordEncoder().encode(user.getPassword()));
            preparedStatement.setString(4, user.getFirstname());
            preparedStatement.setString(5, user.getLastname());
            preparedStatement.setString(6, user.getProfile_picture());
            preparedStatement.setBoolean(7, user.getIs_banned());
            preparedStatement.setObject(8, user.getBan_expires_at());
            preparedStatement.setObject(9, user.getCreated_at());
            preparedStatement.setString(10, user.getGoogle_id());
            preparedStatement.executeUpdate();
        }
        System.out.println("user added ");
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE user SET firstname = ?, lastname = ?, profile_picture = ?  , roles = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setString(1,user.getFirstname());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3,user.getProfile_picture());
            preparedStatement.setString(4,getRolesjson(user.getRoles()));
            preparedStatement.setInt(5, user.getId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<User> select() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                users.add(createUser(resultSet));
            }
        }
        return users;
    }
    public List<User> select(String role) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user where roles LIKE ?";
        PreparedStatement preparedStatement = connect.prepareStatement(sql);
        try (preparedStatement)
        {
             preparedStatement.setString(1, "%"+role+"%");
             try(ResultSet resultSet = preparedStatement.executeQuery()){
            while (resultSet.next()) {
                users.add(createUser(resultSet));
            }
        }
        }
        return users;
    }
    public List<User> select(int pageNumber, int pageSize) throws SQLException {
        List<User> users = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;
        String sql = "SELECT * FROM user ORDER BY firstname ASC LIMIT ? OFFSET ?";
        PreparedStatement preparedStatement = connect.prepareStatement(sql);
        try (preparedStatement)
        {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, offset);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    users.add(createUser(resultSet));
                }
            }
        }
        return users;
    }
    public List<User> select(String sortField, String sortOrder,int pageNumber, int pageSize) throws SQLException {
        List<User> users = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;
        if (!Arrays.asList("firstname","lastname", "email").contains(sortField)) {
            sortField = "firstname"; // Default sort field
        }
        if (!Arrays.asList("asc", "desc").contains(sortOrder)) {
            sortOrder = "asc"; // Default sort order
        }
        String sql = "SELECT * FROM user ORDER BY " + sortField + " " + sortOrder.toUpperCase()+" LIMIT ? OFFSET ?";
        PreparedStatement preparedStatement = connect.prepareStatement(sql);
        try (preparedStatement)
        {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, offset);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    users.add(createUser(resultSet));
                }
            }
        }
        return users;
    }
    @Override
    public User selectWhere(int id) throws SQLException{
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM user WHERE id = ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return createUser(resultSet);
                }
            }
        }

        return null ;
    }
    public User selectWhere(String email,String password) throws SQLException{
        String sql ="SELECT * FROM user WHERE email = ? ";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql))
        {
            preparedStatement.setString(1, email);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {

                    if (new BCryptPasswordEncoder().matches(password, resultSet.getString("password"))) {
                        return createUser(resultSet);
                    }
                }
            }
        }

        return null ;
    }
    public void updateProfil(User user) throws SQLException {
        String sql = "UPDATE user SET firstname = ?, lastname = ?, profile_picture = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setString(1,user.getFirstname());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3,user.getProfile_picture());
            preparedStatement.setInt(4, user.getId());
            preparedStatement.executeUpdate();
        }
    }
    public List<User> search(String searchTerm) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user ";
        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql += " WHERE firstname LIKE ? OR lastname LIKE ? OR email LIKE ?";
        }
        PreparedStatement preparedStatement = connect.prepareStatement(sql);
        try (preparedStatement)
        {
            if (searchTerm != null && !searchTerm.isEmpty()) {
                String likePattern = searchTerm + "%";
                preparedStatement.setString(1, likePattern);
                preparedStatement.setString(2, likePattern);
                preparedStatement.setString(3, likePattern);
            }
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    users.add(createUser(resultSet));
                }
            }
        }
        return users;
    }
    public boolean changePassword(User user, String currentPassword, String newPassword) throws SQLException {
        // Verify the current password
        String sql = "SELECT password FROM user WHERE id = ?";
        try (PreparedStatement stmt = connect.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (new BCryptPasswordEncoder().matches(currentPassword, storedPassword)) {
                    // If the current password is correct, update to new password
                    return updatePasswordInDatabase(user.getId(), newPassword);
                }
            }
        }
        return false; // Return false if current password is incorrect or user not found
    }
    private boolean updatePasswordInDatabase(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE user SET password = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setString(1, new BCryptPasswordEncoder().encode(newPassword));
            preparedStatement.setInt(2, userId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0; // Return true if the update was successful
        }
    }



    public void updatePassword(User user) throws SQLException {
        String sql = "UPDATE user SET password = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
            preparedStatement.setString(1, new BCryptPasswordEncoder().encode(user.getPassword()));
            preparedStatement.setInt(2, user.getId());
            preparedStatement.executeUpdate();
        }
    }
    public void isBanned(int id,int banDuration) throws SQLException {
        PreparedStatement preparedStatement = connect.prepareStatement("UPDATE user SET is_banned = ? , ban_expires_at = ? WHERE id = ?");
        preparedStatement.setBoolean(1,!(selectWhere(id).getIs_banned()));
        preparedStatement.setObject(2,LocalDateTime.now().plusDays(banDuration));
        preparedStatement.setInt(3,id);
        preparedStatement.executeUpdate();
    }
    public void unBanned(int id) throws SQLException {
        PreparedStatement preparedStatement = connect.prepareStatement("UPDATE user SET is_banned = ? , ban_expires_at = ? WHERE id = ?");
        preparedStatement.setBoolean(1,!(selectWhere(id).getIs_banned()));
        preparedStatement.setObject(2,null);
        preparedStatement.setInt(3,id);
        preparedStatement.executeUpdate();
    }
    private  String getRolesjson (List<String> roles) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < roles.size(); i++) {
            stringBuilder.append("\"").append(roles.get(i)).append("\"");
            if (i < roles.size() - 1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
    private List<String> parseRolesJson(String rolesJson) {
        List<String> roles = new ArrayList<>();
        // Remove square brackets and split the string by commas
        String[] roleTokens = rolesJson.substring(1, rolesJson.length() - 1).split(",");
        for (String roleToken : roleTokens) {
            // Remove surrounding quotes and add the role to the list
            roles.add(roleToken.trim().substring(1, roleToken.trim().length() - 1));
        }
        return roles;
    }
    private User createUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setEmail(resultSet.getString("email"));
        user.setRoles(parseRolesJson(resultSet.getString("roles")));
        user.setPassword(resultSet.getString("password"));
        user.setFirstname(resultSet.getString("firstname"));
        user.setLastname(resultSet.getString("lastname"));
        user.setProfile_picture(resultSet.getString("profile_picture"));
        user.setIs_banned(resultSet.getBoolean("is_banned"));
        user.setBan_expires_at(resultSet.getObject("ban_expires_at", LocalDateTime.class));
        user.setCreated_at(resultSet.getObject("created_at", LocalDateTime.class));
        user.setGoogle_id(resultSet.getString("google_id"));
        return user;
    }
}
