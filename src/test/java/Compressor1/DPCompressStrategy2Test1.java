package Compressor1;

import CompressStrategies.imp.DPCompressStrategy2;
import Tool.ByteArrayConvertor;

import java.util.ArrayList;

public class DPCompressStrategy2Test1 {
    public static void main(String[] args) {
        DPCompressStrategy2 strategy2 = new DPCompressStrategy2(); //
        byte[] test = {33,25,19,17,55,32,22,14, -1,-2,-65, 1,2,3,4,5,6,7,8,9,10,11,12, -1,-2,-3,-4,-15,-16};
        ArrayList<Byte> bytes = ByteArrayConvertor.byteArrayToArrayList(test);
        strategy2.getBestSection(bytes);

        System.out.print("       原序列:");
        for (byte i : test) {
            System.out.print((i & 0xFF) + " ");
        }
        System.out.println();

        System.out.print("  每段元素个数:");
        for (Integer i : strategy2.getElemNumPerSection()) {
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.print("每段差值的位数:");
        for (Integer i : strategy2.getBitOfPerSectionElem()) {
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.print("每段中的最小值:");
        for (Integer i : strategy2.getMinElemPerSection()) {
            System.out.print(i + " ");
        }
        System.out.println("\n");

        int i = 1;
        for(ArrayList<Byte> diffBytes : strategy2.getDifferElemPerSection()) {
            System.out.print("第" + i++ +"段 差值:");
            for(Byte b : diffBytes) {
                System.out.print(b + " ");
            }
            System.out.println();
        }

        System.out.println("压缩后的位数:" + strategy2.getMinBit());
        System.out.println("   原来位数:" + bytes.size() * 8);


    }
}
