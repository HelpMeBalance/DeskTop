package org.example.service;



import org.example.models.Personne;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonneService implements IService<Personne> {
    Connection connection;
    public  PersonneService(){
        connection= MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Personne personne)throws SQLException {
        String sql="insert into presonne (nom,prenom,age) values( '"+personne.getNom()+"','"+personne.getPrenom()+"',"+personne.getAge()+")";
        Statement statement =connection.createStatement();
        statement.executeUpdate(sql);
    }

    @Override
    public void update(Personne personne)throws SQLException {
        String sql ="update presonne set nom=?,prenom=?,age=? where id=? ";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setString(1, personne.getNom());
        preparedStatement.setString(2, personne.getPrenom());
        preparedStatement.setInt(3, personne.getAge());
        preparedStatement.setInt(4,personne.getId());
        preparedStatement.executeUpdate();



    }

    @Override
    public void delete(int id)throws SQLException {

    }

    @Override
    public List<Personne> select() throws SQLException{
        List<Personne>personnes=new ArrayList<>();
        String sql="select * from presonne";
        Statement statement=connection.createStatement();
      ResultSet resultSet= statement.executeQuery(sql);
      while (resultSet.next()){
          Personne personne =new Personne();
          personne.setId(resultSet.getInt("id"));
          personne.setNom(resultSet.getString("nom"));
          personne.setPrenom(resultSet.getString("prenom"));
          personne.setAge(resultSet.getInt("age"));
          personnes.add(personne);
      }
        return personnes;
    }
}
