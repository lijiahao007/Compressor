package Compressor;

import CompressStrategies.CompressInfo;
import MyException.BitArrayListOutOfBoundException;
import MyException.ByteArrayException;
import MyException.ParaIllegalException;
import MyException.WrongFileOffsetException;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class CompressInfoTest {
    public static void main(String[] args) throws IOException, ByteArrayException, WrongFileOffsetException, ParaIllegalException, BitArrayListOutOfBoundException {
        CompressInfo compressInfo = new CompressInfo();
        compressInfo.addIntInfo("first", 12345);
        compressInfo.addIntInfo("second", 23456);
        ArrayList<Integer> a =  new ArrayList(Arrays.asList(1,2,3,4,5));
        ArrayList<Integer> b = new ArrayList(Arrays.asList(2,3,4,5,6));
        compressInfo.addIntListInfo("firstList", a, 5);
        compressInfo.addIntListInfo("secondList", b, 5);


        compressInfo.writeCompressInfo("src/main/resources/compressinfo.txt");

        CompressInfo info = new CompressInfo();
        info.readCompressInfo("src/main/resources/compressinfo.txt",0);

        HashMap<String, Integer> intInfoHashMap = info.getIntInfoHashMap();
        HashMap<String, ArrayList<Integer>> intListInfoHashMap = info.getIntListInfoHashMap();
        for (String key : intInfoHashMap.keySet()) {
            System.out.println(key + " : " +intInfoHashMap.get(key));
        }
        System.out.println();

        System.out.println("list");
        for (String key: intListInfoHashMap.keySet()){
            System.out.print(key + " : ");
            ArrayList<Integer> attribute = intListInfoHashMap.get(key);
            for (Integer i : attribute) {
                System.out.print(i + " ");
            }
            System.out.println();
        }

        System.out.println("source :" + compressInfo.getOffset());
        System.out.println("src    :" + info.getOffset());

    }
}
