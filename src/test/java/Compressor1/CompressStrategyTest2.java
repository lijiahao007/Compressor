package Compressor1;

import CompressStrategies.imp.DPCompressStrategy1;
import MyException.BitArrayListOutOfBoundException;
import MyException.ByteArrayException;
import MyException.CompressStrategyNotInitException;
import MyException.ParaIllegalException;

public class CompressStrategyTest2 {
    public static void main(String[] args) throws ByteArrayException, ParaIllegalException, BitArrayListOutOfBoundException, CompressStrategyNotInitException {
        DPCompressStrategy1 dpCompressStrategy1 = new DPCompressStrategy1();
        byte[] test = {1,2,3,4,5,6,7,8,8,9,1,2,3,4,5,6,7,5,4,3,2,2};
        byte[] compress = dpCompressStrategy1.compress(test);
        byte[] decompress = dpCompressStrategy1.decompress(compress);



    }
}
