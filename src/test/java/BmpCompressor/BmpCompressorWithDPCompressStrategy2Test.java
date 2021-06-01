package BmpCompressor;

import Compressor.BmpCompressor;
import CompressStrategies.imp.DPCompressStrategy2;
import MyException.*;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class BmpCompressorWithDPCompressStrategy2Test {
    public static void main(String[] args) throws ByteArrayException, BitArrayListOutOfBoundException, CompressStrategyNotInitException, ParaIllegalException, IOException, WrongFileOffsetException {
         BmpCompressor bmpCompressor = new BmpCompressor();
        bmpCompressor.setCompressStrategy(new DPCompressStrategy2());
        String fileName = "src/main/resources/lena512.bmp";
        String compressFileName = "src/main/resources/lena512(DP2compress)";
        String decompressFileName = "src/main/resources/lena512(DP2decompress).bmp";
        bmpCompressor.compress(fileName,compressFileName);

        BmpCompressor bmpDecompressor = new BmpCompressor();
        bmpDecompressor.setCompressStrategy(new DPCompressStrategy2()); //设置策略
        bmpDecompressor.decompress(compressFileName, decompressFileName);

        File src = new File(fileName);
        File compressFile = new File(compressFileName);
        File decompressFile = new File(decompressFileName);
        long srcLen = src.length();
        long compressLen = compressFile.length();
        long decompressLen = decompressFile.length();
        double compressRate = (double)compressLen / (double)srcLen;
        System.out.println("源文件长度：" + srcLen);
        System.out.println("压缩文件长度：" + compressLen);
        System.out.println("解压文件长度：" + decompressLen);
        System.out.println("压缩率：" + new DecimalFormat("0.00%").format(compressRate));
    }
}
