package MyException;

public class WrongFileOffsetException extends Exception{
    String msg = "WrongFileOffsetException ";

    public WrongFileOffsetException() {

    }

    public  WrongFileOffsetException(String msg) {
        this.msg += msg;
    }

    @Override
    public String toString() {
        return "WrongFileOffsetException{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
