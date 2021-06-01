package Tool;

import MyException.ByteArrayException;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ByteArrayConvertor { // 统一采用大端的方式转换字节文件
    public static short byteArrayToShort(byte[] arr) throws ByteArrayException{ // byte[0]是低位   byte[1] 是高位
        if (arr.length != 2 ) {
            throw new ByteArrayException("字符数组长度=" + arr.length + "short大小=2bytes" );
        }
        int highByte = (0XFF & arr[0]) << 0;
        int lowByte = (0XFF & arr[1]) << 8;
        short result = (short) (highByte | lowByte);
        return result;
    }

    public static int byteArrayToInt(byte[] arr) throws ByteArrayException{ // byte[0] 是低位  byte[3]是高位（在bmp文件数据中采取的是大端：先读进来的是低位）
        if (arr.length != 4 ) {
            throw new ByteArrayException("字符数组长度=" + arr.length + "int大小=4bytes" );
        }
        int byte0 = ( 0XFF & arr[0]) << 0;
        int byte1 = ( 0XFF & arr[1]) << 8;
        int byte2 = ( 0XFF & arr[2]) << 16;
        int byte3 = ( 0XFF & arr[3]) << 24 ;
        int result = byte0 | byte1 | byte2 | byte3;

        return result;
    }

    public static byte[] shortToByteArray(short val) {  // 采用大端的方式，
        byte[] result = ByteBuffer.allocate(2).putShort(val).array(); //ByteBuffer采用小端的方式 result[0] 是高位  result[1] 是低位 （ByteBuffer 采取的是小端，先都进来的是高位）
        byte tmp = result[0];
        result[0] = result[1];
        result[1] = tmp;
        return result;
    }

    public static byte[] intToByteArray(int val) {// 采用大端的方式
        byte[] tmp = ByteBuffer.allocate(4).putInt(val).array(); // ByteBuffer采用小端的方式
        byte[] result = new byte[4];
        result[0] = tmp[3];
        result[1] = tmp[2];
        result[2] = tmp[1];
        result[3] = tmp[0];
        return result;
    }

    public static ArrayList<Byte> byteArrayToArrayList(byte[] byteArray) {
        ArrayList<Byte> result = new ArrayList<Byte>();
        for (byte b : byteArray) {
            result.add(b);
        }
        return result;
    }

    public static byte[] arrayListToByteArray(ArrayList<Byte> byteArrayList) {
        byte[] result = new byte[byteArrayList.size()];
        int index = 0;
        for (byte b: byteArrayList) {
            result[index++] = b;
        }
        return result;
    }
}
