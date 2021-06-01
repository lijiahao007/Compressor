package MyException;


public class CompressStrategyNotInitException extends Exception{
    String msg = "CompressStrategyNotInitException ";
    public CompressStrategyNotInitException() {

    }

    public CompressStrategyNotInitException(String msg) {
        this.msg += msg;
    }

    @Override
    public String toString() {
        return "CompressStrategyNotInitException{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
