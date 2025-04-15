package core;

import core.utils.Log;
import demo01.Program;

import java.util.List;

import static core.utils.Constants.logConstants.*;

public class Main {

    public static List<String> usingLogs;

    public static void main(String[] args) {
        usingLogs = List.of(
//                DEBUG,
//                INFO,
                DB
        );

        DBConnections.getConnection();

        Log.printAllLogs();
    }

    public static List<String> getUsingLogs() {
        return Program.usingLogs; // Colocar programa a ser executado com usingLogs aqui.
    }
}