package com.example.childrentracking.Database;

import java.util.List;

public interface BaseRepository<T> {
    int insert(T object);
    void update(T object);
    void delete(int id);
    List<T> findAll();
    T findById(int id);
    T findBySpecialProperty(String column, Object value);
    List<T> findListBySpecialProperty(String column, Object value);
    T findByTwoSpecialProperties(String column1, Object value1, String column2, Object value2);
    List<T> findListByTwoSpecialProperties(String column1, Object value1, String column2, Object value2);
    void createTable(T type);
    void updateTableStructureWithForeignKeys(T type);
}
