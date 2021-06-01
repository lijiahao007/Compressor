package MyException;

public class ParaIllegalException extends Exception{
    String msg = "ParaIllegalException: ";

    public ParaIllegalException() {

    }

    public ParaIllegalException(String msg) {
        this.msg += msg;
    }

    @Override
    public String toString() {
        return "ParaIllegalException{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
