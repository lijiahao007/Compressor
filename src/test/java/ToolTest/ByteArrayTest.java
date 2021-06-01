package ToolTest;

import FileHelper.BmpFileHelper.BmpFileHelper;
import MyException.BitArrayListOutOfBoundException;
import MyException.ByteArrayException;
import MyException.ParaIllegalException;
import Tool.BitArrayList;
import Tool.ByteArrayConvertor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ByteArrayTest {
    public static void main(String[] args) throws IOException, ByteArrayException, ParaIllegalException, BitArrayListOutOfBoundException {
//        File file = new File("src/hello.txt");
//        file.createNewFile();
//        FileOutputStream fileOutputStream = new FileOutputStream(file,true);
//        byte[] a = {1,2,3,4};
//        fileOutputStream.write(a);
//        fileOutputStream.close();
//
//        FileInputStream fileInputStream = new FileInputStream(file);
//        byte[] b = new byte[4];
//        fileInputStream.read(b);
//        for (byte by : b) {
//            System.out.print( by + " ");
//        }
//        fileInputStream.close();
        // 结论：b[0]是在高位的。（怎么写，读出来就是怎么样的。）

//        int res = 2;
//        byte[] b = ByteArrayConvertor.intToByteArray(res);
//        for (int i = 0; i < 4; i++) {
//            System.out.print(b[i] + " ");
//        }
//        int res_1 = ByteArrayConvertor.byteArrayToInt(b);
//
//        System.out.println();
//        System.out.println(res_1);


        BitArrayList bitArrayList = new BitArrayList();
        bitArrayList.add(-10086, 32);
        System.out.println(bitArrayList.at(0,20));
        int x = -10086 & 0xFFFF;
        System.out.println(-10086&0xffff);
        System.out.println(x);
    }
}
