package core.exceptions;

public class DBExceptionsCode {

    public static class errorNumber {
        public static final int DB_GENERIC_ERROR = 500;
        public static final int DB_FAILED_FOREIGN_KEY_CONSTRAINT = 1451;
    }

    public static class errorLogMessage {
        public static final String DB_GENERIC_ERROR_LOG = "DB generic error";
        public static final String DB_FAILED_FOREIGN_KEY_CONSTRAINT_LOG = "DB failed foreign key constraint";
    }
}
