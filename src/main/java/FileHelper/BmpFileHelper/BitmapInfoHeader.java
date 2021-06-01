package FileHelper.BmpFileHelper;

import MyException.ByteArrayException;
import Tool.ByteArrayConvertor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BitmapInfoHeader {
    int biSize; // 该结构体所需字节数 4bytes
    int biWidth; // 图像宽度（单位：像素） 4bytes
    int biHeight; // 像素高度（单位：像素） 4bytes
    short biPlanes; // 目标颜色平面数（总是1） 2bytes
    short biBitCount; //比特/像素 （其值为1、4、8、16、24、32） 2bytes
    int biCompression;// 4bytes
    // 图像压缩类型: 0~5
    // 0 : 不压缩
    // 1 : 8bit游程编码， 用于8位图
    // 2 : 4bit游程编码， 用于4位图
    // 3 : 比特域，用于16/32位图
    // 4 : 位图含jpeg图像 （用于打印机）
    // 5 : 位图含png图像（用于打印机）

    int biSizeImage; //图像数据的大小(单位bytes) 4bytes
    int biXPelsPerMeter; // 水平分辨率（像素/m）有符号整数 4bytes
    int biYPelsPerMeter; // 垂直分辨率（像素/m）4bytes
    int biClrUsed; //位图实际使用的颜色索引数 4bytes
    int biClrImportant; // 对图像有重要影响的颜色索引数 （如果是0，则是都重要） 4bytes
    final int offset = 40; //该部分数据的字节数（单位bytes）（不是文件中的内容）

    public static BitmapInfoHeader readBitmapInfoHeader(String fileName, int offset) throws FileNotFoundException, IOException, ByteArrayException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        fileInputStream.skip(offset);


        byte[] biSize_b = new byte[4];
        byte[] biWidth_b = new byte[4];
        byte[] biHeight_b = new byte[4];
        byte[] biPlanes_b = new byte[2];
        byte[] biBitCount_b = new byte[2];
        byte[] biCompression_b = new byte[4];
        byte[] biSizeImage_b = new byte[4];
        byte[] biXPelsPerMeter_b = new byte[4];
        byte[] biYPelsPerMeter_b = new byte[4];
        byte[] biClrUsed_b = new byte[4];
        byte[] biClrImportant_b = new byte[4];

        fileInputStream.read(biSize_b,0,biSize_b.length);
        fileInputStream.read(biWidth_b,0,biWidth_b.length);
        fileInputStream.read(biHeight_b,0,biHeight_b.length);
        fileInputStream.read(biPlanes_b,0,biPlanes_b.length);
        fileInputStream.read(biBitCount_b,0,biBitCount_b.length);
        fileInputStream.read(biCompression_b,0,biCompression_b.length);
        fileInputStream.read(biSizeImage_b,0,biSizeImage_b.length);
        fileInputStream.read(biXPelsPerMeter_b,0,biXPelsPerMeter_b.length);
        fileInputStream.read(biYPelsPerMeter_b,0,biYPelsPerMeter_b.length);
        fileInputStream.read(biClrUsed_b,0,biClrUsed_b.length);
        fileInputStream.read(biClrImportant_b,0,biClrImportant_b.length);

        int biSize = ByteArrayConvertor.byteArrayToInt(biSize_b);
        int biWidth = ByteArrayConvertor.byteArrayToInt(biWidth_b);
        int biHeight = ByteArrayConvertor.byteArrayToInt(biHeight_b);
        short biPlanes = ByteArrayConvertor.byteArrayToShort(biPlanes_b);
        short biBitCount = ByteArrayConvertor.byteArrayToShort(biBitCount_b);
        int biCompression = ByteArrayConvertor.byteArrayToInt(biCompression_b);
        int biSizeImage = ByteArrayConvertor.byteArrayToInt(biSizeImage_b);
        int biXPelsPerMeter = ByteArrayConvertor.byteArrayToInt(biXPelsPerMeter_b);
        int biYPelsPerMeter = ByteArrayConvertor.byteArrayToInt(biYPelsPerMeter_b);
        int biClrUsed = ByteArrayConvertor.byteArrayToInt(biClrUsed_b);
        int biClrImportant = ByteArrayConvertor.byteArrayToInt(biClrImportant_b);

        BitmapInfoHeader bitmapInfoHeader = new BitmapInfoHeader(biSize, biWidth, biHeight, biPlanes, biBitCount, biCompression, biSizeImage, biXPelsPerMeter, biYPelsPerMeter, biClrUsed, biClrImportant);
        return bitmapInfoHeader;
    }

    public static void writeBitmapInfoHeader(String fileName, BitmapInfoHeader bitmapInfoHeader) throws FileNotFoundException,IOException{
        File file = new File(fileName);
        file.createNewFile();

        FileOutputStream fileOutputStream = new FileOutputStream(file, true);

        byte[] biSize_b = ByteArrayConvertor.intToByteArray(bitmapInfoHeader.getBiSize());
        byte[] biWidth_b = ByteArrayConvertor.intToByteArray(bitmapInfoHeader.getBiWidth());
        byte[] biHeight_b = ByteArrayConvertor.intToByteArray(bitmapInfoHeader.getBiHeight());
        byte[] biPlanes_b = ByteArrayConvertor.shortToByteArray(bitmapInfoHeader.getBiPlanes());
        byte[] biBitCount_b = ByteArrayConvertor.shortToByteArray(bitmapInfoHeader.getBiBitCount());
        byte[] biCompression_b = ByteArrayConvertor.intToByteArray(bitmapInfoHeader.getBiCompression());
        byte[] biSizeImage_b = ByteArrayConvertor.intToByteArray(bitmapInfoHeader.getBiSizeImage());
        byte[] biXPelsPerMeter_b = ByteArrayConvertor.intToByteArray(bitmapInfoHeader.getBiXPelsPerMeter());
        byte[] biYPelsPerMeter_b = ByteArrayConvertor.intToByteArray(bitmapInfoHeader.getBiYPelsPerMeter());
        byte[] biClrUsed_b = ByteArrayConvertor.intToByteArray(bitmapInfoHeader.getBiClrUsed());
        byte[] biClrImportant_b = ByteArrayConvertor.intToByteArray(bitmapInfoHeader.getBiClrImportant());

        fileOutputStream.write(biSize_b);
        fileOutputStream.write(biWidth_b);
        fileOutputStream.write(biHeight_b);
        fileOutputStream.write(biPlanes_b);
        fileOutputStream.write(biBitCount_b);
        fileOutputStream.write(biCompression_b);
        fileOutputStream.write(biSizeImage_b);
        fileOutputStream.write(biXPelsPerMeter_b);
        fileOutputStream.write(biYPelsPerMeter_b);
        fileOutputStream.write(biClrUsed_b);
        fileOutputStream.write(biClrImportant_b);

        fileOutputStream.close();
    }

    public int getSize() {
        return offset;
    } // 返回该“结构体”的

}
