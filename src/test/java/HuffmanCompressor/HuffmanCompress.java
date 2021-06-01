package HuffmanCompressor;

import FileHelper.FileHelper.FileHelper;

import java.io.IOException;
import java.util.ArrayList;

public class HuffmanCompress {
    public static void main(String[] args) throws IOException {
        // 计数
        String fileName = "src/main/resources/lena512.bmp";
        FileHelper fileHelper =  FileHelper.readFileInBytes(fileName,0);
        ArrayList<Byte> fileInfo =  fileHelper.getFileInfo();
        int[] count = new int[256];
        int[] byteNum = new int[256];

        for (int i = 0; i < 256; i++) {
            byteNum[i] = i;
        }

        for (Byte b : fileInfo) {
            int tmp = b & 0xFF;
            count[tmp]++;
        }

        for (int i = 0; i < count.length - 1; i++) {
            for (int j = 0; j < count.length - 1 - i; j++) {
                if (count[j] < count[j + 1]) {
                    int tmp = count[j];
                    count[j] = count[j + 1];
                    count[j + 1] = tmp;
                    tmp = byteNum[j];
                    byteNum[j] = byteNum[j + 1];
                    byteNum[j + 1] = tmp;
                }
            }
        }

        for (int i = 0; i < 256; i++) {
            System.out.printf("%4d : %5d\n",byteNum[i],count[i]);
        }




    }
}
