package MyException;

public class ByteArrayException extends Exception{

    String msg = "字符数组长度错误 ;";

    public ByteArrayException() {

    }

    public ByteArrayException(String msg) {
        this.msg += msg;
    }

    @Override
    public String toString() {
        return "ByteArrayException{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
