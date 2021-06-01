package LZ77CompressorTest;

import CompressStrategies.imp.DifferenceCompressStrategy;
import CompressStrategies.imp.LZ77CompressStrategy;
import Compressor.BmpCompressor;
import MyException.*;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class LZ77CompressorTest2 {
    public static void main(String[] args) throws ByteArrayException, BitArrayListOutOfBoundException, CompressStrategyNotInitException, ParaIllegalException, IOException, WrongFileOffsetException {
        String fileName = "src/main/resources/lena512.bmp";
        String compressFileName = "src/main/resources/lena512(lz77)";
        String decompressFileName = "src/main/resources/lena512(lz77).bmp";

        BmpCompressor bmpCompressor =new BmpCompressor();
        DifferenceCompressStrategy strategy = new DifferenceCompressStrategy();
        strategy.setCompressStrategy(new LZ77CompressStrategy());
        bmpCompressor.setCompressStrategy(strategy);

        bmpCompressor.compress(fileName, compressFileName);
        bmpCompressor.decompress(compressFileName, decompressFileName);

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
