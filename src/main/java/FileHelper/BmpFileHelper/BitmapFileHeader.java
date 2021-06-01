package FileHelper.BmpFileHelper;

import MyException.ByteArrayException;
import Tool.ByteArrayConvertor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BitmapFileHeader {
    private short bfType; // 文件类型 2bytes
    private int bfSize; // 文件大小 4bytes
    private short bfReserved1; //保留字1  2bytes
    private short bfReserved2; //保留字2  2bytes
    private int bfOffBits; // 实际图像数据的偏移  4bytes
    private final int offset = 14; // 该部分数据的字节数（单位bytes）（不是文件中的内容）
    public static BitmapFileHeader readBitmapFileHeader(String fileName, int offset) throws FileNotFoundException, IOException, ByteArrayException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        fileInputStream.skip(offset);

        byte[] bfType_b = new byte[2];
        byte[] bfSize_b = new byte[4];
        byte[] bfReserved1_b = new byte[2];
        byte[] bfReserved2_b = new byte[2];
        byte[] bfOffBits_b = new byte[4];

        fileInputStream.read(bfType_b,0,bfType_b.length);
        fileInputStream.read(bfSize_b,0,bfSize_b.length);
        fileInputStream.read(bfReserved1_b,0,bfReserved1_b.length);
        fileInputStream.read(bfReserved2_b,0,bfReserved2_b.length);
        fileInputStream.read(bfOffBits_b,0,bfOffBits_b.length);

        short bfType = ByteArrayConvertor.byteArrayToShort(bfType_b);
        int bfSize = ByteArrayConvertor.byteArrayToInt(bfSize_b);
        short bfReserved1 = ByteArrayConvertor.byteArrayToShort(bfReserved1_b);
        short bfReserved2 = ByteArrayConvertor.byteArrayToShort(bfReserved1_b);
        int bfOffBits = ByteArrayConvertor.byteArrayToInt(bfOffBits_b);

        BitmapFileHeader bitmapFileHeader = new BitmapFileHeader(bfType,bfSize,bfReserved1,bfReserved2,bfOffBits);

        fileInputStream.close();
        return bitmapFileHeader;
    }

    public static void writeBitmapFileHeader(String fileName, BitmapFileHeader bitmapFileHeader) throws  FileNotFoundException, IOException{ // 写在文件末尾
        File file = new File(fileName);
        file.createNewFile();


        FileOutputStream fileOutputStream = new FileOutputStream(file, true);

        byte[] bfType_b = ByteArrayConvertor.shortToByteArray(bitmapFileHeader.getBfType());
        byte[] bfSize_b = ByteArrayConvertor.intToByteArray(bitmapFileHeader.getBfSize());
        byte[] bfReserved1_b = ByteArrayConvertor.shortToByteArray(bitmapFileHeader.getBfReserved1());
        byte[] bfReserved2_b = ByteArrayConvertor.shortToByteArray(bitmapFileHeader.getBfReserved2());
        byte[] bfOffBits_b = ByteArrayConvertor.intToByteArray(bitmapFileHeader.getBfOffBits());

        fileOutputStream.write(bfType_b);
        fileOutputStream.write(bfSize_b);
        fileOutputStream.write(bfReserved1_b);
        fileOutputStream.write(bfReserved2_b);
        fileOutputStream.write(bfOffBits_b);

        fileOutputStream.close();
    }

    public int getSize() {
        return offset;
    }

}
