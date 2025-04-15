package demo01.model.dao;

import core.DBConnections;
import demo01.model.impl.SellerDaoJDBC;

public class DaoFactory {

    public static SellerDao createSellerDao() { // Esconder implementação
        return new SellerDaoJDBC(DBConnections.getConnection());
        /*
        SellerDao sellerDao = createSellerDao(); // traz a implementação para a classe que instancia a interface
         */
    }
}
