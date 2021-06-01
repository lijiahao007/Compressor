package CompressStrategies;

import MyException.*;
import lombok.Data;

import java.io.IOException;

@Data
public abstract class CompressStrategy {
    CompressStrategy compressStrategy; // 允许嵌套使用压缩策略
    CompressInfo compressInfo;

    public CompressStrategy() {
        compressInfo = new CompressInfo();
        compressStrategy = null;
    }

    public byte[] compress(byte[] grayArr) throws ParaIllegalException {
        System.out.println("该策略 byte[] compress(byte[] grayArr) 未重载");
        return null;
    }

    public byte[][] compress(byte[][] grayArr) {
        System.out.println("该策略 byte[][] compress(byte[][] grayArr) 未重载");
        return null;
    }

    public byte[] decompress(byte[] grayArr) throws CompressStrategyNotInitException, ParaIllegalException, BitArrayListOutOfBoundException {
        System.out.println("该策略 byte[] decompress(byte[] grayArr) 未重载");
        return null;
    }

    public byte[][] decompress(byte[][] grayArr) {
        System.out.println("该策略 byte[][] decompress(byte[][] grayArr) 未重载");
        return null;
    }

    public void readCompressInfo(String fileName, int offset) throws ByteArrayException, BitArrayListOutOfBoundException, WrongFileOffsetException, ParaIllegalException, IOException {
        compressInfo.readCompressInfo(fileName, offset);
    }

    public void writeCompressInfo(String fileName) throws IOException, ParaIllegalException {
        compressInfo.writeCompressInfo(fileName);
    }

}
