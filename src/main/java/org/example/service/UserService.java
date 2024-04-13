package org.example.service;

import org.example.models.User;
import org.example.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserService implements IService <User>{
    Connection connect;
    public UserService()
    {
        connect= MyDataBase.getInstance().getConnection();
    }
    @Override
    public void add(User user) throws SQLException {

    }

    @Override
    public void update(User user) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public List<User> select() throws SQLException {
        return null;
    }

    @Override
    public User selectWhere(int id) throws SQLException{
        PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM user WHERE id = ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(resultSet.getInt("id")
                    );
                }
            }
        }

        return null ;
    }
}
