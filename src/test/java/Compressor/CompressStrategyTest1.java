package Compressor;

import CompressStrategies.CompressInfo;
import CompressStrategies.imp.DPCompressStrategy1;

import java.util.ArrayList;

public class CompressStrategyTest1 {
    public static void main(String[] args) {

//        byte[] grayArr = {6, 5, 7,5, (byte)245, (byte)180, 28, 28, 19, 22, 25,20};
        byte[] grayArr = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};


        System.out.print("grayArr(" + grayArr.length + " bytes): ");
        for (byte b : grayArr) {
            System.out.print((b & 0XFF) + " ");
        }
        System.out.println();

        DPCompressStrategy1 compressStrategy = new DPCompressStrategy1();
        try {
            byte[] compress = compressStrategy.compress(grayArr);
            byte[] decompress = compressStrategy.decompress(compress);

            System.out.print("compress(" + compress.length + " bytes): ");
            for (byte b : compress) {
                System.out.print((b & 0XFF) + " ");
            }
            System.out.println();

            System.out.print("decompress(" + decompress.length + " bytes): ");
            for (byte b : decompress) {
                System.out.print((b & 0XFF) + " ");
            }
            System.out.println();

            boolean flag = true;

            for (int i = 0; i < grayArr.length; i++) {
                if (grayArr[i] != decompress[i]){
                    flag = false;
                    break;
                }
            }

            if (flag) {
                System.out.println("grayArr == decompressArr");
            } else {
                System.out.println("grayArr != decompressArr");
            }
            System.out.println();
            CompressInfo info =  compressStrategy.getCompressInfo();

            for (String key : info.getIntInfoHashMap().keySet()) {
                System.out.println(key + " " + info.getIntInfoHashMap().get(key));
            }
            System.out.println();
            for (String key : info.getIntListInfoHashMap().keySet()) {
                System.out.print(key + " : ");
                ArrayList<Integer> list = info.getIntListInfoHashMap().get(key);
                for (Integer i : list) {
                    System.out.print(i + " ");
                }
                System.out.println();
            }

        } catch (Exception e) {
            e.toString();
            e.printStackTrace();
        }
    }
}
