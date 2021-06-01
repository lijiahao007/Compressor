package HuffmanCompressor;

import FileHelper.FileHelper.FileHelper;
import HuffmanTree.HuffmanTree;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HuffmanTest1 {

    public static void main(String[] args) throws IOException {
        String fileName = "src/main/resources/lena512.bmp";
        FileHelper fileHelper =  FileHelper.readFileInBytes(fileName,0);
        ArrayList<Byte> fileInfo =  fileHelper.getFileInfo();

        int[] count = new int[256];
        for (Byte b : fileInfo) {
            int tmp = b & 0xFF;
            count[tmp]++;
        }




        HuffmanTree huffmanTree = new HuffmanTree();
        Map<Byte, Integer> frequenceForByte = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            frequenceForByte.put((byte)i, count[i]);
        }
        Map<Byte, String> encodeTable =  huffmanTree.encode(frequenceForByte);


        int[] byteNum = new int[256];
        for (int i = 0; i < 256; i++) {
            byteNum[i] = i;
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


        System.out.println(" 字节              哈夫曼编码 个数");
        int sum = 0;
        int sumByte = 0;
        for (int i = 0; i < 255; i++) {
            byte b = (byte)byteNum[i];
            String s = encodeTable.get(b);
            int num = count[i];
            System.out.printf("%4d : %18s  %d \n",(b&0xff) , s, num);
            sum += num * s.length();
            sumByte += num;
        }

        System.out.println("原来编码长度:" + sumByte * 8);
        System.out.println("现在编码长度:" + sum);
        double rate = (double) sum / (double) (sumByte * 8);
        System.out.println("     压缩率:" + new DecimalFormat("0.00%").format(rate));
        System.out.println("       数量:" + sumByte);

    }

}
