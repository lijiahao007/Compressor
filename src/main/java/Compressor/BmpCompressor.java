package Compressor;

import CompressStrategies.CompressStrategy;
import CompressStrategies.imp.DPCompressStrategy1;
import FileHelper.BmpFileHelper.BitMapData;
import FileHelper.BmpFileHelper.BmpFileHelper;
import FileHelper.BmpFileHelper.RGBQUAD;
import FileHelper.BmpFileHelper.RgbQuadInfo;
import MyException.*;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Data
public class BmpCompressor {
    CompressStrategy compressStrategy;

    public BmpCompressor() {
        compressStrategy = new DPCompressStrategy1();
    }

    public BmpCompressor(CompressStrategy compressStrategy) {
        this.compressStrategy = compressStrategy;
    }

    public void setCompressStrategy(CompressStrategy compressStrategy) {
        this.compressStrategy = compressStrategy;
    }

    public boolean compress(String fileName, String compressFileName) throws IOException, ByteArrayException, ParaIllegalException, BitArrayListOutOfBoundException, CompressStrategyNotInitException {
        if (!fileName.contains(".bmp"))
            return false;

        File file = new File(compressFileName);
        file.delete(); // 如果存在则先删除后创建
        file.createNewFile();

        BmpFileHelper bmpFileHelper =  BmpFileHelper.readBmpFile(fileName); // 读bmp文件数据
        byte[] compressData = null;   // 压缩后的数据

        int bitType = bmpFileHelper.getBitmapinfoheader().getBiBitCount();
        if (bitType == 8) { // 8位灰度图
            compressData = compress8BitBmpData(bmpFileHelper);
        } else if (bitType == 24) { // 24位彩图
            compressData = compress24BitBmpData(bmpFileHelper);
        }

        BitMapData bitMapData = new BitMapData();
        bitMapData.setPixelArrayList(compressData);
        BmpFileHelper.writeHeader(compressFileName, bmpFileHelper); // 头文件直接写进压缩文件
        compressStrategy.getCompressInfo().writeCompressInfo(compressFileName); // 压缩信息写进压缩文件
        BitMapData.writeBitMapData(compressFileName,bitMapData); // 压缩后得图像数据存入文件
        return true;
    }

    private byte[] compress8BitBmpData(BmpFileHelper bmpFileHelper) throws ParaIllegalException { // 此时bmpFilehelper存有bmp文件的所有信息
        BitMapData bitMapData =  bmpFileHelper.getBitMapData();
        byte[] pixels = bitMapData.getPixelByteArray();
        // 注意这里的图像数据是总左下到右上的，我们需要将其处理为S型的排列
        int height = bmpFileHelper.getBitmapinfoheader().getBiHeight();
        int width = bmpFileHelper.getBitmapinfoheader().getBiWidth();
        // 0~width-1 正序
        // width ~ 2width-1 逆序
        for (int i = 1; i < height; i = i + 2) { // 奇数行倒转
            int beginIndex = i * width;
            int begin = beginIndex;
            int end = beginIndex + width - 1;
            while (begin < end) {
                int tmp = pixels[begin] & 0xff;
                pixels[begin] = pixels[end];
                pixels[end] = (byte)tmp;
                begin++;
                end--;
            }
        }
        return compressStrategy.compress(pixels);
    }

    private BmpFileHelper decompress8BitBmpData(BmpFileHelper bmpFileHelper, byte[] decompressData ) {
        // 此时的bmpFileHelper存有除bmp像素信息之外的其他信息，decompressData为图像信息，该函数需要将图像信息存放到合适的位置
        // 方法返回处理好的BmpFileHelper

        // 上面compress8BitBmpData中奇数行倒转，这里需要恢复原来，即奇数行再次倒转
        int height = bmpFileHelper.getBitmapinfoheader().getBiHeight();
        int width = bmpFileHelper.getBitmapinfoheader().getBiWidth();
        for (int i = 1; i < height; i = i + 2) {
            int beginIndex = i * width;
            int begin = beginIndex;
            int end = beginIndex + width - 1;
            while (begin < end) {
                int tmp = decompressData[begin] & 0xff;
                decompressData[begin] = decompressData[end];
                decompressData[end] = (byte)tmp;
                begin++;
                end--;
            }
        }

        BitMapData decompressBitMapData = new BitMapData(decompressData); // 获取新的位图数据
        bmpFileHelper.setBitMapData(decompressBitMapData); // 将位图数据放入bmpFileHeader中
        return bmpFileHelper;
    }

    private byte[] compress24BitBmpData(BmpFileHelper bmpFileHelper) throws ParaIllegalException {
        // 此时bmpFilehelper存有bmp文件的所有信息
        // 对于24位彩图，没有调色板，图像数据是以RGBA 4个字节的形式存储。
        RgbQuadInfo rgbQuadInfo = bmpFileHelper.getRgbQuadInfo(); // rgb信息存储
        ArrayList<RGBQUAD> rgbaList =  rgbQuadInfo.getRgbquadArrayList(); //
        // 将信息全部存到一个字节数组中。
        byte[] rgbByteArray = new byte[rgbaList.size() * 4];
        int index = 0;
        for (int i = 0; i < rgbaList.size(); i++) {
            RGBQUAD rgba = rgbaList.get(i);
            rgbByteArray[index++] = rgba.getRed();
            rgbByteArray[index++] = rgba.getGreen();
            rgbByteArray[index++] = rgba.getBlue();
            rgbByteArray[index++] = rgba.getAlpha();
        }
        return compressStrategy.compress(rgbByteArray);
    }

    private BmpFileHelper decompress24BitBmpData(BmpFileHelper bmpFileHelper, byte[] decompressData) {
        // 此时的bmpFileHelper存有除bmp像素信息之外的其他信息，decompressData为图像信息，该函数需要将图像信息存放到合适的位置
        ArrayList<RGBQUAD> rgbaList = new ArrayList<>();
        for (int i = 0; i < decompressData.length; i += 4) {
            RGBQUAD rgbquad = new RGBQUAD();
            rgbquad.setRed(decompressData[i]);
            rgbquad.setGreen(decompressData[i + 1]);
            rgbquad.setBlue(decompressData[i + 2]);
            rgbquad.setAlpha(decompressData[i + 3]);
            rgbaList.add(rgbquad);
        }
        RgbQuadInfo rgbQuadInfo = new RgbQuadInfo();
        rgbQuadInfo.setRgbquadArrayList(rgbaList);
        bmpFileHelper.setRgbQuadInfo(rgbQuadInfo);
        bmpFileHelper.setBitMapData(null);
        return bmpFileHelper;
    }

    public boolean decompress(String fileName, String decompressFileName) throws IOException, ByteArrayException, WrongFileOffsetException, CompressStrategyNotInitException, BitArrayListOutOfBoundException, ParaIllegalException {
        File file = new File(decompressFileName);
        file.delete(); // 如果存在则先删除后创建
        file.createNewFile();

        int offset = 0;
        BmpFileHelper bmpFileHeader = BmpFileHelper.readHeader(fileName); // 读头文件
        offset += bmpFileHeader.getSize();

        compressStrategy.readCompressInfo(fileName,offset); // 读压缩信息
        offset += compressStrategy.getCompressInfo().getOffset();

        BitMapData bitMapData =  BitMapData.readBitMapData(fileName,offset); // 读压缩后的位图数据
        byte[] compressData = bitMapData.getPixelByteArray();
        byte[] decompressData = compressStrategy.decompress(compressData); // 解压

        if (bmpFileHeader.getBitmapinfoheader().getBiBitCount() == 8) {
            bmpFileHeader = decompress8BitBmpData(bmpFileHeader, decompressData);
        } else if (bmpFileHeader.getBitmapinfoheader().getBiBitCount() == 24) {
            bmpFileHeader = decompress24BitBmpData(bmpFileHeader, decompressData);
        }

        BmpFileHelper.writeBmpFile(decompressFileName, bmpFileHeader); // 写入新的图片
        return true;
    }

}
