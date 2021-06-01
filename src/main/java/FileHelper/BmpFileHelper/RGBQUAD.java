package FileHelper.BmpFileHelper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RGBQUAD {
    byte Red;
    byte Green;
    byte Blue;
    byte Alpha; // 透明量（0为不透明）
    final int offset = 4; // 该部分数据的字节数（单位bytes）（不是文件中的内容）


    public static RGBQUAD readRGBQUAD(String fileName, int offset) throws FileNotFoundException, IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        fileInputStream.skip(offset);

        byte red = (byte) fileInputStream.read();
        byte green = (byte)fileInputStream.read();
        byte blue = (byte) fileInputStream.read();
        byte alpha = (byte) fileInputStream.read();

        fileInputStream.close();
        return new RGBQUAD(red,green,blue,alpha);
    }

    public static ArrayList<RGBQUAD> readRGBQUADArrayList(String fileName, int offset, int numOfRGBQUAD) throws FileNotFoundException, IOException{
        if (numOfRGBQUAD <= 0) {
            return null;
        }

        FileInputStream fileInputStream = new FileInputStream(fileName);
        fileInputStream.skip(offset);

        ArrayList<RGBQUAD> rgbquads = new ArrayList<RGBQUAD>();
        for (int i = 0; i < numOfRGBQUAD; i++) {
            byte red = (byte) fileInputStream.read();
            byte green = (byte)fileInputStream.read();
            byte blue = (byte) fileInputStream.read();
            byte alpha = (byte) fileInputStream.read();
            rgbquads.add(new RGBQUAD(red,green,blue,alpha));
        }

        fileInputStream.close();
        return rgbquads;
    }

    public static void writeRGBQUAD(String fileName, RGBQUAD rgbquad) throws FileNotFoundException, IOException{
        File file = new File(fileName);
        file.createNewFile();

        FileOutputStream fileOutputStream = new FileOutputStream(file, true);

        fileOutputStream.write(rgbquad.getRed());
        fileOutputStream.write(rgbquad.getGreen());
        fileOutputStream.write(rgbquad.getBlue());
        fileOutputStream.write(rgbquad.getAlpha());

        fileOutputStream.close();
    }

    public static void writeRGBQUAD(String fileName, List<RGBQUAD> rgbquadList) throws FileNotFoundException, IOException{
        File file = new File(fileName);
        file.createNewFile();

        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        for (RGBQUAD rgbquad : rgbquadList) {
            fileOutputStream.write(rgbquad.getRed());
            fileOutputStream.write(rgbquad.getGreen());
            fileOutputStream.write(rgbquad.getBlue());
            fileOutputStream.write(rgbquad.getAlpha());
        }
        fileOutputStream.close();
    }

    public int getSize() {
        return offset;
    }

}
