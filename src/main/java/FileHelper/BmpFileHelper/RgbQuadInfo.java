package FileHelper.BmpFileHelper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RgbQuadInfo {
    ArrayList<RGBQUAD> rgbquadArrayList;
    int offset; // 这部分的字节数 （不是文件中的内容）

    public static RgbQuadInfo readRgbQuadInfo(String fileName, int offset, int numOfRgbQuad) throws FileNotFoundException, IOException {
        ArrayList<RGBQUAD> rgbquadArrayList = RGBQUAD.readRGBQUADArrayList(fileName,offset,numOfRgbQuad);
        int size = rgbquadArrayList.size() * 4;
        RgbQuadInfo rgbQuadInfo = new RgbQuadInfo(rgbquadArrayList, size);
        return rgbQuadInfo;
    }

    public static void writeRgbQuadInfo(String fileName, RgbQuadInfo rgbQuadInfo) throws IOException{
        RGBQUAD.writeRGBQUAD(fileName,rgbQuadInfo.getRgbquadArrayList());
    }



}
