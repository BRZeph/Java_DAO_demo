package core.utils;

import java.util.HashMap;

import static core.Main.getUsingLogs;
import static core.utils.Constants.logConstants.*;

public class Log {

    private static HashMap<String, HashMap<Integer, String>> logs = new HashMap<>(); // logT | num | message
    private static HashMap<String, Integer> logCount = new HashMap<>(); // logT | num
    private static HashMap<String, String> logName = new HashMap<>(); // logT | log name
    private static boolean initialized = false;

    public static int registerLog(String logT, String message){
        initializeAll();
        if (!logs.containsKey(logT)) {
            System.out.println("Log type Not Found: " + logT);
            return -1; // Unregistered log
        } else {
            logCount.putIfAbsent(logT, -1);
            int logPos = logCount.get(logT) + 1;
            logs.get(logT).put(logPos, (logName.get(logT) + message));
            logCount.put(logT, logCount.get(logT) + 1);
            return logPos;
        }
    }

    public static void registerAndPrint(String logT, String message){
        int logPosition = registerLog(logT, message);
        System.out.println(logs.get(logT).get(logPosition));
    }

    public static void printLogByType(String logT){
        System.out.println("\n*******************************");
        if (logs.get(logT).isEmpty()){
            System.out.println("No logs found for " + getLogName(logT));
        } else {
            System.out.println("Printing logs for " + getLogName(logT));
            for (String s : logs.get(logT).values()) {
                System.out.println(s);
            }
        }
        System.out.println("*******************************");
    }

    public static void printAllLogs(){
        for (String logT : logs.keySet()){
            if (getUsingLogs().contains(logT)) {
                printLogByType(logT);
            }
        }
    }



    private static void initializeAll(){
        if (!initialized) {
            addLogToShow(DEBUG);
            addLogToShow(INFO);
            addLogToShow(DB);
            logName.put(DEBUG, DEBUG_PREFIX);
            logName.put(INFO, INFO_PREFIX);
            logName.put(DB, DB_PREFIX);
            initialized = true;
        }
    }

    private static void addLogToShow(String logT) {
        if (!logs.containsKey(logT)) {
            logs.put(logT, new HashMap<>());
        }
    }

    private static String getLogName(String logT){
        /*
        currently using logName as `[logT]` without the last 2 digits (remove `: `).
         */
        return logName.get(logT).substring(0, logName.get(logT).length() - 2);
    }
}
