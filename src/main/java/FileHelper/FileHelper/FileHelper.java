package FileHelper.FileHelper;

import Tool.ByteArrayConvertor;
import lombok.Data;

import java.io.*;
import java.util.ArrayList;

@Data
public class FileHelper {
    ArrayList<Byte> fileInfo;

    public static FileHelper readFileInBytes(String fileName, int offset) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        fileInputStream.skip(offset);

        ArrayList<Byte> fileInfo = new ArrayList<>();
        int tmp = 0;
        do {
            tmp = fileInputStream.read();
            if (tmp != -1) {
                fileInfo.add((byte)tmp);
            }
        } while (tmp != -1);

        FileHelper fileHelper = new FileHelper();
        fileHelper.setFileInfo(fileInfo);

        fileInputStream.close();
        return fileHelper;
    }

    public static void writeFileInBytes(String fileName, FileHelper fileHelper) throws IOException {
        File file = new File(fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        byte[] fileInfo = ByteArrayConvertor.arrayListToByteArray(fileHelper.getFileInfo());
        fileOutputStream.write(fileInfo);

        fileOutputStream.close();
        return ;
    }

    public byte[] getFileInfoInBytes() {
        return ByteArrayConvertor.arrayListToByteArray(this.fileInfo);
    }

    public void setFileInfoInBytes(byte[] fileInfo) {
        this.fileInfo = ByteArrayConvertor.byteArrayToArrayList(fileInfo);
    }
}
