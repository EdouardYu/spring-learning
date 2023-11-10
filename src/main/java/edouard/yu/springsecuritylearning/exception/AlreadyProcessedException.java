package edouard.yu.springsecuritylearning.exception;

public class AlreadyProcessedException extends RuntimeException{
    public AlreadyProcessedException(String msg) { super(msg); }
}
