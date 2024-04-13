package org.example.service;

import java.sql.SQLException;
import java.util.List;

public interface IService <T>{

    public void add(T t) throws SQLException;
    public void update (T t) throws SQLException;
    public void delete (int id) throws SQLException;
    public List<T> select()throws SQLException;
    T selectWhere(int id)throws SQLException;
}
