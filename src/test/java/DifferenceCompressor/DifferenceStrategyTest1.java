package DifferenceCompressor;

import CompressStrategies.imp.DifferenceCompressStrategy;
import MyException.BitArrayListOutOfBoundException;
import MyException.CompressStrategyNotInitException;
import MyException.ParaIllegalException;

public class DifferenceStrategyTest1 {
    public static void main(String[] args) throws ParaIllegalException, BitArrayListOutOfBoundException, CompressStrategyNotInitException {
        byte[] a = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
        DifferenceCompressStrategy strategy = new DifferenceCompressStrategy();
        byte[] compressData = strategy.compress(a);
        byte[] decompressData = strategy.decompress(compressData);
        System.out.print("压缩前:");
        for (byte b : a) {
            System.out.print(b + " ");
        }
        System.out.println();
        System.out.print("压缩后:");
        for (byte b : compressData) {
            System.out.print(b + " ");
        }
        System.out.println();
        System.out.print("解压后:");
        for (byte b : decompressData) {
            System.out.print(b + " ");
        }
        System.out.println();
    }
}
