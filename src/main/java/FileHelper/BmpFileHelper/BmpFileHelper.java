package FileHelper.BmpFileHelper;

import MyException.ByteArrayException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class BmpFileHelper {
    // 该类主要处理8位bmp图片和24位bmp图片。
    // 两者的不同点是：bmp24位图片没有调色板，每位像素都是由4个字节组成 RGBA
    // 或者说 24位bmp图像的调色板就是所有像素的信息。
    private BitmapFileHeader bitmapfileheader;
    private BitmapInfoHeader bitmapinfoheader;
    private RgbQuadInfo rgbQuadInfo;
    private BitMapData bitMapData;
    private int size;

    public static BmpFileHelper readBmpFile(String fileName) throws FileNotFoundException,IOException, ByteArrayException{
        int offset = 0;
        BitmapFileHeader bitmapFileHeader = BitmapFileHeader.readBitmapFileHeader(fileName, offset); // 读 文件头
        offset += bitmapFileHeader.getOffset();

        BitmapInfoHeader bitmapInfoHeader = BitmapInfoHeader.readBitmapInfoHeader(fileName, offset); // 读 信息头
        offset += bitmapInfoHeader.getOffset();

        BmpFileHelper bmpFileHelper = null;
        int picBitNum = bitmapInfoHeader.getBiBitCount();
        if (picBitNum == 8) { // 8位灰度图
            int numOfRgbQuad = bitmapInfoHeader.getBiClrUsed();
            RgbQuadInfo rgbQuadInfo =  RgbQuadInfo.readRgbQuadInfo(fileName,offset, numOfRgbQuad); // 读 调色板

            offset += rgbQuadInfo.getOffset();
            int numOfPixels = bitmapInfoHeader.getBiHeight() * bitmapInfoHeader.getBiWidth();
            BitMapData bitMapData = BitMapData.readBitMapData(fileName,offset, numOfPixels); // 读 位图数据

            int fileSize = bitmapFileHeader.getSize() + bitmapInfoHeader.getSize() + rgbQuadInfo.getOffset() + bitMapData.getSize();
            bmpFileHelper = new BmpFileHelper(bitmapFileHeader, bitmapInfoHeader, rgbQuadInfo, bitMapData, fileSize);
            if (fileSize != bitmapFileHeader.getBfSize()) {
                System.out.println("文件读取出错");
            }
        } else if (picBitNum == 24) { // 24位彩图
            int numOfRgbQuad = bitmapInfoHeader.getBiHeight() * bitmapInfoHeader.getBiHeight();
            RgbQuadInfo rgbQuadInfo = RgbQuadInfo.readRgbQuadInfo(fileName, offset, numOfRgbQuad);// 读rgb信息 (24位图没有调色板，每一位像素都是由rgb+保留字共4个字节表示)

            int fileSize = bitmapFileHeader.getSize() + bitmapInfoHeader.getSize() + rgbQuadInfo.getOffset();
            bmpFileHelper = new BmpFileHelper(bitmapFileHeader, bitmapInfoHeader, rgbQuadInfo, null, fileSize);
        }

        return bmpFileHelper;
    }



    public static void writeBmpFile(String fileName, BmpFileHelper bmpFileHelper) throws FileNotFoundException, IOException {
        BitmapFileHeader.writeBitmapFileHeader(fileName, bmpFileHelper.getBitmapfileheader()); // 写文件头
        BitmapInfoHeader.writeBitmapInfoHeader(fileName, bmpFileHelper.getBitmapinfoheader()); // 写信息头
        RgbQuadInfo.writeRgbQuadInfo(fileName, bmpFileHelper.getRgbQuadInfo()); // 写调色板
        if (bmpFileHelper.getBitmapinfoheader().getBiBitCount() == 8)
            BitMapData.writeBitMapData(fileName, bmpFileHelper.getBitMapData()); // 写位图数据
    }

    public static void writeHeader(String fileName, BmpFileHelper bmpFileHelper) throws IOException { // 不写图像数据
        BitmapFileHeader.writeBitmapFileHeader(fileName, bmpFileHelper.getBitmapfileheader());
        BitmapInfoHeader.writeBitmapInfoHeader(fileName, bmpFileHelper.getBitmapinfoheader()); // 写信息头
        if (bmpFileHelper.getBitmapinfoheader().getBiBitCount() == 8)
            RgbQuadInfo.writeRgbQuadInfo(fileName, bmpFileHelper.getRgbQuadInfo()); // 写调色板
    }

    public static BmpFileHelper readHeader(String fileName) throws IOException, ByteArrayException {
        int offset = 0;
        BitmapFileHeader bitmapFileHeader = BitmapFileHeader.readBitmapFileHeader(fileName, offset); // 读 文件头

        offset += bitmapFileHeader.getOffset();
        BitmapInfoHeader bitmapInfoHeader = BitmapInfoHeader.readBitmapInfoHeader(fileName, offset); // 读 信息头

        BmpFileHelper bmpFileHelper = null;
        if (bitmapInfoHeader.getBiBitCount() == 8) {
            offset += bitmapInfoHeader.getOffset();
            int numOfRgbQuad = bitmapInfoHeader.getBiClrUsed();
            RgbQuadInfo rgbQuadInfo =  RgbQuadInfo.readRgbQuadInfo(fileName,offset, numOfRgbQuad); // 读 调色板

            int size = bitmapFileHeader.getSize() + bitmapInfoHeader.getSize() + rgbQuadInfo.getOffset();
            bmpFileHelper = new BmpFileHelper(bitmapFileHeader, bitmapInfoHeader, rgbQuadInfo, null, size );

        } else if (bitmapInfoHeader.getBiBitCount() == 24) {
            int size = bitmapFileHeader.getSize() + bitmapInfoHeader.getSize();
            bmpFileHelper = new BmpFileHelper(bitmapFileHeader, bitmapInfoHeader, null , null, size);
        }
        return bmpFileHelper;
    }
}
