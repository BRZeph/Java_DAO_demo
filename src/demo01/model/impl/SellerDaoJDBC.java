package demo01.model.impl;

import core.DBConnections;
import core.exceptions.DBException;
import core.exceptions.DBExceptionsCode;
import core.utils.Constants;
import core.utils.Log;
import demo01.model.dao.SellerDao;
import demo01.model.entities.Department;
import demo01.model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {

    private Connection conn;

    public SellerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Seller obj) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(
                    "INSERT INTO seller " +
                            "(Name, Email, BirthDay, BaseSalary, DepartmentId) " +
                            "VALUES " +
                            "(?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, obj.getName());
            ps.setString(2, obj.getEmail());
            ps.setDate(3, new Date(obj.getBirthday().getTime()));
            ps.setDouble(4, obj.getBaseSalary());
            ps.setInt(5, obj.getDepartment().getId());

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
            } else {
                Log.registerLog(Constants.logConstants.DB, "Failed to insert object " + obj);
                throw new DBException("Error code " + DBExceptionsCode.GENERIC_DATABASE_ERROR);
            }

        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        } finally {
            DBConnections.closeStatement(ps);
        }
    }

    @Override
    public void update(Seller obj) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(
                    "UPDATE seller " +
                            "SET Name = ?, Email = ?, BirthDay = ?, BaseSalary = ?, DepartmentId = ? " +
                            "WHERE Id = ? ",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, obj.getName());
            ps.setString(2, obj.getEmail());
            ps.setDate(3, new Date(obj.getBirthday().getTime()));
            ps.setDouble(4, obj.getBaseSalary());
            ps.setInt(5, obj.getDepartment().getId());
            ps.setInt(6, obj.getId());

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
                throw new DBException("Error code " + DBExceptionsCode.GENERIC_DATABASE_ERROR);
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
                    "DELETE FROM seller " +
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
                throw new DBException("Error code " + DBExceptionsCode.GENERIC_DATABASE_ERROR);
            }

        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        } finally {
            DBConnections.closeStatement(ps);
        }
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(
                    "SELECT " +
                            "seller.Id AS SellerId, " +
                            "seller.Name AS SellerName, " +
                            "seller.Email AS SellerEmail, " +
                            "seller.Birthday AS SellerBirthday, " +
                            "seller.BaseSalary AS SellerBaseSalary, " +
                            "seller.DepartmentId AS SellerDepartmentId, " +
                            "department.Id AS DepartmentId, " +
                            "department.Name AS DepartmentName " +
                            "FROM seller " +
                            "INNER JOIN department ON seller.DepartmentId = department.Id " +
                            "WHERE seller.Id = ?"
            );

            ps.setInt(1, id);
            rs = ps.executeQuery();
            Log.registerLog(Constants.logConstants.DB, "Executing findById with Id: " + id);

            if (rs.next()) {
                Department dep = instantiateDepartment(rs);
                Seller seller = instantiateSeller(rs, dep);
                Log.registerLog(Constants.logConstants.DB, "Found: " + seller);
                return seller;
            }
            Log.registerLog(Constants.logConstants.DB, "Did not find Id: " + id);
            return null;
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        } finally {
            DBConnections.closeStatement(ps);
            DBConnections.closeResultSet(rs);
        }
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(
                    "SELECT " +
                            "seller.Id AS SellerId, " +
                            "seller.Name AS SellerName, " +
                            "seller.Email AS SellerEmail, " +
                            "seller.Birthday AS SellerBirthday, " +
                            "seller.BaseSalary AS SellerBaseSalary, " +
                            "seller.DepartmentId AS SellerDepartmentId, " +
                            "department.Id AS DepartmentId, " +
                            "department.Name AS DepartmentName " +
                            "FROM seller " +
                            "INNER JOIN department " +
                            "ON seller.DepartmentId = department.Id " +
                            "ORDER BY seller.Name"
            );
            rs = ps.executeQuery();
            Log.registerLog(Constants.logConstants.DB,
                    "Executing findAll");

            List<Seller> depList = new ArrayList<>();

            if (rs.isBeforeFirst()) {
                Department dep = null;
                int count = 0;

                while (rs.next()) {
                    if (dep == null) {
                        dep = instantiateDepartment(rs);
                    } else if (dep.getId() != instantiateDepartment(rs).getId()) {
                        Log.registerLog(Constants.logConstants.DB, "Found " + count + " entries for department: " + dep);
                        dep = instantiateDepartment(rs);
                        count = 0;
                    }
                    count++;
                    Seller seller = instantiateSeller(rs, dep);
                    depList.add(seller);
                    Log.registerLog(Constants.logConstants.DB, "Found seller: " + seller);
                }

                Log.registerLog(Constants.logConstants.DB, "Found " + count + " entries for department: " + dep);
                Log.registerLog(Constants.logConstants.DB, "Found " + depList.size() + " entries");

                return depList;
            } else {
                Log.registerLog(Constants.logConstants.DB, "Did not find entries");
                return null;
            }
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        } finally {
            DBConnections.closeStatement(ps);
            DBConnections.closeResultSet(rs);
        }
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(
                    "SELECT " +
                            "seller.Id AS SellerId, " +
                            "seller.Name AS SellerName, " +
                            "seller.Email AS SellerEmail, " +
                            "seller.Birthday AS SellerBirthday, " +
                            "seller.BaseSalary AS SellerBaseSalary, " +
                            "seller.DepartmentId AS SellerDepartmentId, " +
                            "department.Id AS DepartmentId, " +
                            "department.Name AS DepartmentName " +
                            "FROM seller " +
                            "INNER JOIN department " +
                            "ON seller.DepartmentId = department.Id " +
                            "WHERE seller.DepartmentId = ? " +
                            "ORDER BY seller.Name"
            );
            ps.setInt(1, department.getId());
            rs = ps.executeQuery();
            Log.registerLog(Constants.logConstants.DB,
                    "Executing findByDepartment with department: " + department.getId());

            List<Seller> depList = new ArrayList<>();

            if (rs.isBeforeFirst()) {
                Department dep = null;

                while (rs.next()) {
                    if (dep == null) {
                        dep = instantiateDepartment(rs);
                    }
                    Seller seller = instantiateSeller(rs, dep);
                    depList.add(seller);
                    Log.registerLog(Constants.logConstants.DB, "Found seller: " + seller);
                }

                Log.registerLog(Constants.logConstants.DB, "Found " + depList.size() + " entries");

                return depList;
            } else {
                Log.registerLog(Constants.logConstants.DB, "Did not find entries");
                return null;
            }
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        } finally {
            DBConnections.closeStatement(ps);
            DBConnections.closeResultSet(rs);
        }
    }

    private static Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
        Seller seller = new Seller();
        seller.setId(rs.getInt("SellerId"));
        seller.setName(rs.getString("SellerName"));
        seller.setEmail(rs.getString("SellerEmail"));
        seller.setBirthday(rs.getDate("SellerBirthday"));
        seller.setBaseSalary(rs.getDouble("SellerBaseSalary"));
        seller.setDepartment(dep);
        return seller;
    }

    private static Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("DepartmentId"));
        dep.setName(rs.getString("DepartmentName"));
        return dep;
    }
}
