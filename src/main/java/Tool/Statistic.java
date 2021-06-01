package Tool;

import java.io.File;
import java.text.DecimalFormat;

public class Statistic {
    public static Double fileSizeRate(String fileName1, String fileName2) { // fileName1文件大小/fileName2文件大小
        File file1 = new File(fileName1);
        File file2 = new File(fileName2);

        long file1Len = file1.length();
        long file2Len = file2.length();
        double rate = file1Len / (double) file2Len;
        return rate;
    }
}
