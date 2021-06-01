package CompressStrategies.imp;

import CompressStrategies.CompressInfo;
import CompressStrategies.CompressStrategy;
import MyException.BitArrayListOutOfBoundException;
import MyException.CompressStrategyNotInitException;
import MyException.ParaIllegalException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
public class DifferenceCompressStrategy extends CompressStrategy {

    public DifferenceCompressStrategy(){
        this.setCompressStrategy(new DPCompressStrategy1());
    }

    public byte[] compress(byte[] data) throws ParaIllegalException {
        // 先将这个数据转换成差分的形式
        if (data == null || data.length == 0) {
            return null;
        }
        ArrayList<Integer> isMinus = new ArrayList<>();
        isMinus.add(0); // 第一个无关紧要
        byte[] res = new byte[data.length];
        res[0] = data[0];
        for (int i = 1; i < data.length; i++) {
            int dif = (data[i] & 0xff) - (data[i - 1] & 0xff);
            if (dif < 0) {
                isMinus.add(1);
                dif = -dif;
            } else
                isMinus.add(0);
            res[i] = (byte)dif;
        }

        // 将这个数据使用DPCompressStrategy2进行压缩
        CompressStrategy strategy = this.getCompressStrategy();
        byte[] compressData = strategy.compress(res);

        // 将strategy2的压缩信息以及这个策略所需要的strategy写入该策略的CompressInfo中
        CompressInfo thisCompressInfo = this.getCompressInfo();
        CompressInfo strategy2CompressInfo = strategy.getCompressInfo();
        thisCompressInfo.addCompressInfo(strategy2CompressInfo);
        thisCompressInfo.addIntListInfo("isMinus", isMinus, 1);

        return compressData;
    }

    public byte[] decompress(byte[] data) throws ParaIllegalException, BitArrayListOutOfBoundException, CompressStrategyNotInitException {

        // 这时compressInfo中已经存在所有的压缩信息，需要将strategy2的压缩信息分出去
        ArrayList<Integer> isMinus = this.getCompressInfo().getIntListInfo("isMinus");
        CompressInfo strategyCompressInfo = new CompressInfo();
        strategyCompressInfo.addCompressInfoExcept(this.getCompressInfo(), "isMinus"); // 除去“isMinus”其他都是宋体strategy2的压缩信息

        CompressStrategy strategy = this.getCompressStrategy();
        strategy.setCompressInfo(strategyCompressInfo);
        byte[] decompressData = strategy.decompress(data); // 该数据是差分编码后的文件数据

        if (isMinus.size() == decompressData.length - 1) { // 哈夫曼压缩然后再解压 有时会多出一个字节。可能与BitArrayList后面自动补0有关。验证最后一个字节的正确性。
            isMinus.add(0);
        }

        // 现在将差分数据还原成原始数据
        byte[] srcData = new byte[decompressData.length];
        srcData[0] = decompressData[0];
        for (int i = 1; i < decompressData.length; i++) {
            int dif = 0;
            if (isMinus.get(i) == 0) { // 与前一个的差值为正数或者零
                dif = decompressData[i] & 0xff;
            } else { // 与前一个的差值为负数
                dif = decompressData[i] & 0xff;
                dif = -dif;
            }
            srcData[i] = (byte)((srcData[i - 1] & 0xff) + dif);
        }

        return srcData;
    }
}
