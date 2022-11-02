/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author tom18
 */
public abstract class IDAO<T> {

    Statement statement;
    PreparedStatement preStatement;
    Connection connection;
    ResultSet rs;

    public abstract List<T> getAll();

    public abstract List<T> getByName(String name);

    public abstract int insert(T object);

    public abstract int update(T object);

    public abstract int delete(T object);
}
