package LZ77CompressorTest;

import CompressStrategies.imp.LZ77CompressStrategy2;
import MyException.BitArrayListOutOfBoundException;
import MyException.ParaIllegalException;

public class LZ77CompressTest3 {
    public static void main(String[] args) throws ParaIllegalException, BitArrayListOutOfBoundException {
        byte[] data = {1,2,3,4,5,6,7,8,90,12,32,53,12,3,4,5,6,7,8};
        LZ77CompressStrategy2 strategy2 = new LZ77CompressStrategy2();
        byte[] compressData = strategy2.compress(data);
        byte[] decompressData = strategy2.decompress(compressData);
        for (byte b: decompressData) {
            System.out.print(b + " ");
        }
    }
}
