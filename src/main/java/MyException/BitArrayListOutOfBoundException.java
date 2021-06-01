package MyException;

public class BitArrayListOutOfBoundException  extends  Exception{
    String msg = "BitArrayListOutOfBoundException";
    public BitArrayListOutOfBoundException() {

    }

    public BitArrayListOutOfBoundException(String append) {
        msg += ":" + append;
    }

    @Override
    public String toString() {
        return "BitArrayListOutOfBoundException{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
