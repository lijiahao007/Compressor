package FileHelper.BmpFileHelper;

import Tool.ByteArrayConvertor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BitMapData {
    ArrayList<Byte> pixelArrayList;
    int size;

    public BitMapData(byte[] pixelByteArray) {
        ArrayList<Byte> pixelArrayList =  ByteArrayConvertor.byteArrayToArrayList(pixelByteArray);
        this.pixelArrayList = pixelArrayList;
        size = pixelArrayList.size();
    }

    public static BitMapData readBitMapData(String fileName, int offset, int numOfPixel) throws FileNotFoundException, IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        fileInputStream.skip(offset);

        ArrayList<Byte> pixelArrayList = new ArrayList<Byte>();
        for (int i = 0; i < numOfPixel; i++) {
            byte pixel = (byte) fileInputStream.read();
            pixelArrayList.add(pixel);
        }
        BitMapData bitMapData = new BitMapData(pixelArrayList, pixelArrayList.size());
        fileInputStream.close();
        return bitMapData;
    }

    public static BitMapData readBitMapData(String fileName, int offset) throws IOException { // 直接读到文件末尾
        FileInputStream fileInputStream = new FileInputStream(fileName);
        fileInputStream.skip(offset);


        ArrayList<Byte> pixelArrayList = new ArrayList<Byte>();
        int tmp = 0;
        do{
            tmp = fileInputStream.read();
            if (tmp != -1) {
                byte pixel = (byte) tmp;
                pixelArrayList.add(pixel);
            }
        }while (tmp != -1);
        BitMapData bitMapData = new BitMapData(pixelArrayList, pixelArrayList.size());
        fileInputStream.close();
        return bitMapData;
    }

    public static void writeBitMapData(String fileName, BitMapData bitMapData) throws FileNotFoundException,IOException {
        File file = new File(fileName);
        file.createNewFile();

        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        for (Byte pixel : bitMapData.getPixelArrayList()) {
            fileOutputStream.write(pixel);
        }
        fileOutputStream.close();
    }

    public byte[] getPixelByteArray() {
        return ByteArrayConvertor.arrayListToByteArray(this.pixelArrayList);
    }

    public void setPixelArrayList(byte[] pixelByteArray) {
        this.pixelArrayList =  ByteArrayConvertor.byteArrayToArrayList(pixelByteArray);
        size = pixelByteArray.length;
    }

    public int getOffset() {
        return size;
    }
}
