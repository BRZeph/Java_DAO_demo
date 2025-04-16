package demo01.model.dao;

import core.DBConnections;
import demo01.model.impl.DepartmentDaoJDBC;
import demo01.model.impl.SellerDaoJDBC;

public class DaoFactory {

    public static SellerDao createSellerDao() { // Esconder implementação
        return new SellerDaoJDBC(DBConnections.getConnection());
    }

    public static DepartmentDao createDepartmentDao() {
        return new DepartmentDaoJDBC(DBConnections.getConnection());
    }
    /*
    SellerDao sellerDao = createSellerDao(); // traz a implementação para a classe que instancia a interface
     */
}
