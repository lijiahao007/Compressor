package FileCompressor;

import Compressor.FileCompressor;
import MyException.*;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class FileCompressorTest1 {
    public static void main(String[] args) throws IOException, ParaIllegalException, ByteArrayException, BitArrayListOutOfBoundException, WrongFileOffsetException, CompressStrategyNotInitException {

//        String fileName = "src/main/resources/lena512color.bmp";
//        String compressFileName = "src/main/resources/lena512color(compress3)";
//        String decompressFileName = "src/main/resources/lena512color(decompress3).bmp";

        String fileName = "src/main/resources/lena512.bmp";
        String compressFileName = "src/main/resources/lena512(compress3)";
        String decompressFileName = "src/main/resources/lena512(decompress3).bmp";

        FileCompressor compressor = new FileCompressor();
        compressor.compress(fileName, compressFileName);

        FileCompressor decompressor = new FileCompressor();
        decompressor.decompress(compressFileName, decompressFileName);

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
