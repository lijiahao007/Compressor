package Compressor;

import CompressStrategies.CompressStrategy;
import CompressStrategies.imp.DPCompressStrategy2;
import FileHelper.FileHelper.FileHelper;
import MyException.*;

import java.io.File;
import java.io.IOException;

public class FileCompressor {
    CompressStrategy compressStrategy;
    public FileCompressor() {
        this.compressStrategy = new DPCompressStrategy2();
    }

    public void setCompressStrategy(CompressStrategy strategy) {
        this.compressStrategy = strategy;
    }

    public void compress(String fileName, String compressFileName) throws IOException, ParaIllegalException {

        File file = new File(compressFileName);
        file.delete(); // 如果存在则先删除后创建
        file.createNewFile();

        FileHelper fileHelper =  FileHelper.readFileInBytes(fileName, 0);

        byte[] fileData = fileHelper.getFileInfoInBytes();
        byte[] compressData =  this.compressStrategy.compress(fileData);
        fileHelper.setFileInfoInBytes(compressData);

        compressStrategy.getCompressInfo().writeCompressInfo(compressFileName); // 写入压缩信息
        FileHelper.writeFileInBytes(compressFileName, fileHelper); // 写入文件信息
    }

    public void decompress(String fileName, String decompressFileName) throws ByteArrayException, BitArrayListOutOfBoundException, WrongFileOffsetException, ParaIllegalException, IOException, CompressStrategyNotInitException {
        int offset = 0;
        compressStrategy.getCompressInfo().readCompressInfo(fileName, offset); // 读压缩信息
        offset += compressStrategy.getCompressInfo().getOffset();

        FileHelper fileHelper =  FileHelper.readFileInBytes(fileName, offset); // 读文件信息。

        byte[] compressData = fileHelper.getFileInfoInBytes();
        byte[] decompressData = compressStrategy.decompress(compressData); // 解压

        fileHelper.setFileInfoInBytes(decompressData);
        FileHelper.writeFileInBytes(decompressFileName, fileHelper); // 解压信息写入文件
        return;
    }
}
