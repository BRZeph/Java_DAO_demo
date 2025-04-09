package Core;

import Core.utils.Log;

import java.util.List;

import static Core.utils.Constants.logConstants.*;

public class Main {

    public static List<String> usingLogs;

    public static void main(String[] args) {
        usingLogs = List.of(
//                DEBUG,
//                INFO,
                DB
        );

        Log.printAllLogs();
    }

    public static List<String> getUsingLogs() {
        return usingLogs;
    }
}