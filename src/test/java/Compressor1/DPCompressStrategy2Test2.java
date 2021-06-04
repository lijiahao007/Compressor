package Compressor1;

import CompressStrategies.imp.DPCompressStrategy2;
import MyException.BitArrayListOutOfBoundException;
import MyException.CompressStrategyNotInitException;
import MyException.ParaIllegalException;

public class DPCompressStrategy2Test2 {
    public static void main(String[] args) throws ParaIllegalException, BitArrayListOutOfBoundException, CompressStrategyNotInitException {
        DPCompressStrategy2 strategy2 = new DPCompressStrategy2();
        byte[] test = {33,25,19,17,55,32,22,14, -1,-2,-65, 1,2,3,4,5,6,7,8,9,10,11,12, -1,-2,-3,-4,-15,-16};
        byte[] compress = strategy2.compress(test);
        for (byte b : compress) {
            System.out.print((b & 0xFF) + " ");
        }
        System.out.println();

        byte[] decompress = strategy2.decompress(compress);

        for (byte b : decompress) {
            System.out.print(b  + " ");
        }
    }
}
