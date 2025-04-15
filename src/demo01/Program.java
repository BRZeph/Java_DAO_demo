package demo01;

import core.DBConnections;
import core.utils.Log;
import demo01.model.dao.DaoFactory;
import demo01.model.dao.SellerDao;
import demo01.model.entities.Department;
import demo01.model.entities.Seller;

import java.util.List;
import java.util.Date;

import static core.utils.Constants.logConstants.*;

public class Program {

    public static List<String> usingLogs;
    private static SellerDao sellerDao;

    public static void main(String[] args) {
        usingLogs = List.of(
//                DEBUG,
//                INFO,
                DB
        );

        sellerDao = DaoFactory.createSellerDao();
        Seller seller = sellerDao.findById(3);
        sellerDao.findByDepartment(new Department(3, ""));
        sellerDao.findAll();
        sellerDao.insert(new Seller(
                null, "Ricardo", "ricardocardosodecampos@Hotmail.com", new Date(), 1000d,
                new Department(2, null)
                ));
        seller.setName("Updated Name");
        sellerDao.update(seller);
        sellerDao.deleteById(25); // Remember to change this upon each execution.

        DBConnections.closeConnection();

        Log.printAllLogs();
    }
}

/*
    TODO:
    1) Improve on log class.
    1.1- add a possibility to show logs by entrance order (pilha).
    2) Create Department classes like the Seller classes (DepartmentDao etc).
 */
