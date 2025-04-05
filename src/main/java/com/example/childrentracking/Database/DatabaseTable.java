package com.example.childrentracking.Database;

import com.example.childrentracking.Models.ForeignKey;
import com.example.childrentracking.Models.PrimaryKey;
import com.example.childrentracking.Models.Unique;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class DatabaseTable<T> implements BaseRepository<T> {
    private final String tableName;
    private final Class<T> entityType;
    private final Connection connection;

    public DatabaseTable(String tableName, Class<T> entityType, Connection connection) {
        this.tableName = tableName;
        this.entityType = entityType;
        this.connection = connection;
    }

    @Override
    public void insert(T entity) {
        try {
            Field[] fields = entityType.getDeclaredFields();
            StringBuilder columns = new StringBuilder();
            StringBuilder values = new StringBuilder();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    continue;
                }
                columns.append(field.getName()).append(",");
                values.append("'").append(field.get(entity)).append("',");
            }
            columns.setLength(columns.length() - 1);
            values.setLength(values.length() - 1);
            String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(sql);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(T entity) {
        try {
            Field[] fields = entityType.getDeclaredFields();
            StringBuilder updates = new StringBuilder();
            Object idValue = null;
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getName().equalsIgnoreCase("id")) {
                    idValue = field.get(entity);
                    continue;
                }
                updates.append(field.getName()).append("='").append(field.get(entity)).append("',");
            }
            updates.setLength(updates.length() - 1);
            String sql = "UPDATE " + tableName + " SET " + updates + " WHERE id='" + idValue + "'";
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public T findById(int id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<T> findAll() {
        List<T> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private T mapResultSetToEntity(ResultSet rs) {
        try {
            T entity = entityType.getDeclaredConstructor().newInstance();
            Field[] fields = entityType.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                field.set(entity, rs.getObject(field.getName()));
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public T findBySpecialProperty(String column, Object value) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + column + " = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, value);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<T> findListBySpecialProperty(String column, Object value) {
        List<T> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " WHERE " + column + " = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, value);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public T findByTwoSpecialProperties(String column1, Object value1, String column2, Object value2) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + column1 + " = ? AND " + column2 + " = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, value1);
            stmt.setObject(2, value2);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<T> findListByTwoSpecialProperties(String column1, Object value1, String column2, Object value2) {
        List<T> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " WHERE " + column1 + " = ? AND " + column2 + " = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, value1);
            stmt.setObject(2, value2);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void createTable(T type) {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        Field[] fields = type.getClass().getDeclaredFields();
        List<String> columns = new ArrayList<>();
        List<String> foreignKeys = new ArrayList<>();

        for (Field field : fields) {
            field.setAccessible(true);
            String columnName = field.getName();
            String columnType = mapJavaTypeToSQLType(field);
            if (field.isAnnotationPresent(ForeignKey.class)) {
                ForeignKey fk = field.getAnnotation(ForeignKey.class);
                String fkConstraint = String.format(
                        "FOREIGN KEY (%s) REFERENCES %s(%s) ON UPDATE CASCADE ON DELETE CASCADE",
                        columnName, fk.referenceTable(), fk.referenceColumn()
                );
                foreignKeys.add(fkConstraint);
            }
            columns.add(columnName + " " + columnType);
        }
        columns.addAll(foreignKeys);
        sql.append(String.join(", ", columns)).append(")");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String mapJavaTypeToSQLType(Field field) {
        Class<?> type = field.getType();
        String sqlType;
        if (type == int.class || type == Integer.class) {
            sqlType = "INT";
        } else if (type == long.class || type == Long.class) {
            sqlType = "BIGINT";
        } else if (type == double.class || type == Double.class) {
            sqlType = "DOUBLE";
        } else if (type == float.class || type == Float.class) {
            sqlType = "FLOAT";
        } else if (type == boolean.class || type == Boolean.class) {
            sqlType = "BOOLEAN";
        } else if (type == String.class) {
            sqlType = "VARCHAR(255)";
        } else if (type == Date.class) {
            sqlType = "DATETIME";
        } else {
            sqlType = "TEXT";
        }
        if (field.isAnnotationPresent(PrimaryKey.class)) {
            sqlType += " PRIMARY KEY AUTO_INCREMENT";
        }
        else {
            if (field.isAnnotationPresent(Unique.class)) {
                sqlType += " UNIQUE";
            }
        }
        return sqlType;
    }

    @Override
    public void updateTableStructureWithForeignKeys(T type) {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            Set<String> existingColumns = new HashSet<>();
            ResultSet columnRs = meta.getColumns(null, null, tableName, null);
            while (columnRs.next()) {
                existingColumns.add(columnRs.getString("COLUMN_NAME"));
            }
            Set<String> existingForeignKeys = new HashSet<>();
            ResultSet fkRs = meta.getImportedKeys(null, null, tableName);
            while (fkRs.next()) {
                String fkKey = fkRs.getString("FKCOLUMN_NAME") + "->" + fkRs.getString("PKTABLE_NAME");
                existingForeignKeys.add(fkKey);
            }
            Field[] fields = type.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String columnName = field.getName();
                if (!existingColumns.contains(columnName)) {
                    String columnType = mapJavaTypeToSQLType(field);
                    String alterSql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType;
                    try (Statement stmt = connection.createStatement()) {
                        stmt.executeUpdate(alterSql);
                        System.out.println("Added column: " + columnName);
                    }
                }
                if (field.isAnnotationPresent(ForeignKey.class)) {
                    ForeignKey fk = field.getAnnotation(ForeignKey.class);
                    String referenceTable = fk.referenceTable();
                    String referenceColumn = fk.referenceColumn();
                    String fkKey = columnName + "->" + referenceTable;
                    if (!existingForeignKeys.contains(fkKey)) {
                        String constraintName = "fk_" + tableName + "_" + columnName;
                        String alterSql = String.format(
                                "ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s(%s) ON UPDATE CASCADE ON DELETE CASCADE",
                                tableName, constraintName, columnName, referenceTable, referenceColumn
                        );
                        try (Statement stmt = connection.createStatement()) {
                            stmt.executeUpdate(alterSql);
                            System.out.println("Added foreign key: " + constraintName);
                        } catch (SQLException ex) {
                            System.err.println("Failed to add foreign key " + constraintName + ": " + ex.getMessage());
                        }
                    }
                }
                Set<String> existingUniqueConstraints = new HashSet<>();
                if (field.isAnnotationPresent(Unique.class)) {
                    if (!existingUniqueConstraints.contains(columnName)) {
                        String alterSql = "ALTER TABLE " + tableName + " ADD CONSTRAINT unique_" + columnName + " UNIQUE (" + columnName + ")";
                        try (Statement stmt = connection.createStatement()) {
                            stmt.executeUpdate(alterSql);
                            System.out.println("Added UNIQUE constraint for: " + columnName);
                        } catch (SQLException ex) {
                            System.err.println("Failed to add UNIQUE constraint for " + columnName + ": " + ex.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
