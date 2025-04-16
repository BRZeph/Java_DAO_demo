package demo01.model.impl;

import core.DBConnections;
import core.exceptions.DBException;
import core.exceptions.DBExceptionsCode;
import core.utils.Constants;
import core.utils.Log;
import demo01.model.dao.DepartmentDao;
import demo01.model.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJDBC implements DepartmentDao {

    private Connection conn;

    public DepartmentDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Department obj) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(
                    "INSERT INTO department " +
                            "(Name) " +
                            "VALUES " +
                            "(?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, obj.getName());

            int rowsAffected = ps.executeUpdate();

            Log.registerLog(Constants.logConstants.DB, "Attempting to insert object " + obj);

            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
                DBConnections.closeResultSet(rs);
                Log.registerLog(Constants.logConstants.DB, "Successfully inserted object in id " + obj.getId());
            } else if (rowsAffected == 0) {
                Log.registerLog(Constants.logConstants.DB, "Operation successful but did not insert any object");
            } else {
                Log.registerLog(Constants.logConstants.DB, "Failed to insert object " + obj);
                Log.registerLog(Constants.logConstants.DB,
                        "Error code " + DBExceptionsCode.errorNumber.DB_GENERIC_ERROR +
                                ", error message: " + DBExceptionsCode.errorLogMessage.DB_GENERIC_ERROR_LOG);
            }

        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        } finally {
            DBConnections.closeStatement(ps);
        }
    }

    @Override
    public void update(Department obj) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(
                    "UPDATE department " +
                            "SET Name = ? " +
                            "WHERE Id = ? ",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, obj.getName());
            ps.setInt(2, obj.getId());

            Log.registerLog(Constants.logConstants.DB, "Attempting to update object " + obj);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
                DBConnections.closeResultSet(rs);
                Log.registerLog(Constants.logConstants.DB, "Successfully updated object, new object: " + obj);
            } else if (rowsAffected == 0) {
                Log.registerLog(Constants.logConstants.DB, "Could not find object with id " + obj.getId() + " to update");
            } else {
                Log.registerLog(Constants.logConstants.DB, "Failed to delete object " + obj.getId());
                Log.registerLog(Constants.logConstants.DB,
                        "Error code " + DBExceptionsCode.errorNumber.DB_GENERIC_ERROR +
                                ", error message: " + DBExceptionsCode.errorLogMessage.DB_GENERIC_ERROR_LOG);
            }

        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        } finally {
            DBConnections.closeStatement(ps);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                    "DELETE FROM department " +
                            "WHERE Id = ?"
            );

            ps.setInt(1, id);

            Log.registerLog(Constants.logConstants.DB, "Attempting to delete object with id " + id);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                Log.registerLog(Constants.logConstants.DB, "Successfully deleted object with id " + id);
            } else if (rowsAffected == 0) {
                Log.registerLog(Constants.logConstants.DB, "Could not find object with id " + id + " to delete");
            } else {
                Log.registerLog(Constants.logConstants.DB, "Failed to delete object " + id);
                Log.registerLog(Constants.logConstants.DB,
                        "Error code " + DBExceptionsCode.errorNumber.DB_GENERIC_ERROR +
                                ", error message: " + DBExceptionsCode.errorLogMessage.DB_GENERIC_ERROR_LOG);
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451){
                Log.registerLog(Constants.logConstants.DB,
                        "Error code " + DBExceptionsCode.errorNumber.DB_FAILED_FOREIGN_KEY_CONSTRAINT +
                                ", error message: " + DBExceptionsCode.errorLogMessage.DB_FAILED_FOREIGN_KEY_CONSTRAINT_LOG);
            } else {
                throw new DBException(e.getMessage());
            }
        } finally {
            DBConnections.closeStatement(ps);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(
                    "SELECT " +
                            "department.Id AS DepartmentId, " +
                            "department.Name AS DepartmentName " +
                            "FROM department " +
                            "WHERE department.Id = ?"
            );

            ps.setInt(1, id);
            rs = ps.executeQuery();
            Log.registerLog(Constants.logConstants.DB, "Executing findById with Id: " + id);

            if (rs.next()) {
                Department dep = instantiateDepartment(rs);
                Log.registerLog(Constants.logConstants.DB, "Found: " + dep);
                return dep;
            }
            Log.registerLog(Constants.logConstants.DB, "Did not find Id: " + id);
            return null;
        } catch (SQLException e) {
            Log.registerLog(Constants.logConstants.DB,
                    "Error code " + DBExceptionsCode.errorNumber.DB_GENERIC_ERROR +
                            ", error message: " + DBExceptionsCode.errorLogMessage.DB_GENERIC_ERROR_LOG);
            return null;
        } finally {
            DBConnections.closeStatement(ps);
            DBConnections.closeResultSet(rs);
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(
                    "SELECT " +
                            "department.Id AS DepartmentId, " +
                            "department.Name AS DepartmentName " +
                            "FROM department " +
                            "ORDER BY department.Id ASC"
            );
            rs = ps.executeQuery();
            Log.registerLog(Constants.logConstants.DB,
                    "Executing findAll on " + this.getClass().getSimpleName());

            List<Department> depList = new ArrayList<>();

            if (rs.isBeforeFirst()) {

                while (rs.next()) {
                    Department dep = instantiateDepartment(rs);
                    depList.add(dep);
                    Log.registerLog(Constants.logConstants.DB, "Found department: " + dep);
                }
                Log.registerLog(Constants.logConstants.DB, "Found " + depList.size() + " entries");

                return depList;
            } else {
                Log.registerLog(Constants.logConstants.DB, "Did not find entries");
                return null;
            }
        } catch (SQLException e) {
            Log.registerLog(Constants.logConstants.DB,
                    "Error code " + DBExceptionsCode.errorNumber.DB_GENERIC_ERROR +
                            ", error message: " + DBExceptionsCode.errorLogMessage.DB_GENERIC_ERROR_LOG);
            return null;
        } finally {
            DBConnections.closeStatement(ps);
            DBConnections.closeResultSet(rs);
        }
    }

    protected static Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("DepartmentId"));
        dep.setName(rs.getString("DepartmentName"));
        return dep;
    }
}
