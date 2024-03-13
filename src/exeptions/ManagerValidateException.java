package exeptions;

public class ManagerValidateException extends RuntimeException {
    public ManagerValidateException(String message) {
        super(message);
    }
}