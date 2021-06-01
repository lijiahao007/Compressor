package BmpCompressor;

import FileHelper.BmpFileHelper.*;
import MyException.ByteArrayException;

import java.io.IOException;

public class BmpFileHelperTest {
    public static void main(String[] args) throws IOException, ByteArrayException {
        String fileName =  "src/main/resources/lena512color.bmp";
//        int offset = 0;
//        BitmapFileHeader bitmapFileHeader =  BitmapFileHeader.readBitmapFileHeader(fileName, 0);
//        offset += bitmapFileHeader.getOffset();
//        BitmapInfoHeader bitmapInfoHeader = BitmapInfoHeader.readBitmapInfoHeader(fileName, offset); // 24位彩图没有调色板
//        offset += bitmapInfoHeader.getOffset();
//        int numOfPixels = bitmapInfoHeader.getBiHeight() * bitmapInfoHeader.getBiWidth();
//        RgbQuadInfo rgbQuadInfo =  RgbQuadInfo.readRgbQuadInfo(fileName,offset,numOfPixels);
        BmpFileHelper bmpFileHelper = BmpFileHelper.readBmpFile(fileName);

    }
}
