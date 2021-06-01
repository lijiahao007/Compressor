package DifferenceCompressor;

import CompressStrategies.imp.DifferenceCompressStrategy;
import Compressor.FileCompressor;
import MyException.*;
import Tool.Statistic;

import java.io.File;
import java.io.IOException;

public class DifferenceCompressorTest1 {
    public static void main(String[] args) throws IOException, ParaIllegalException, ByteArrayException, BitArrayListOutOfBoundException, WrongFileOffsetException, CompressStrategyNotInitException {
        String fileName = "src/main/resources/lena512.bmp";
        String compressFileName = "src/main/resources/lena512(differenceCompress)";
        String decompressFileName = "src/main/resources/lena512(differenceDeCompress).bmp";

        File compressFile = new File(compressFileName);
        compressFile.delete();
        File decompressFile = new File(decompressFileName);
        decompressFile.delete();

        FileCompressor fileCompressor = new FileCompressor();
        fileCompressor.setCompressStrategy(new DifferenceCompressStrategy());

        fileCompressor.compress(fileName, compressFileName);
        fileCompressor.decompress(compressFileName, decompressFileName);
        double compressRate = Statistic.fileSizeRate(compressFileName, fileName);
        System.out.println("压缩率:" + compressRate * 100 + "%");

    }
}
