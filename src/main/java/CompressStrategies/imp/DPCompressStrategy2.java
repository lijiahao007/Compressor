package CompressStrategies.imp;

import CompressStrategies.CompressStrategy;
import MyException.BitArrayListOutOfBoundException;
import MyException.CompressStrategyNotInitException;
import MyException.ParaIllegalException;
import Tool.BitArrayList;
import Tool.ByteArrayConvertor;
import lombok.Data;

import java.util.ArrayList;

@Data
public class DPCompressStrategy2 extends CompressStrategy {
    // 该算法 来自 黄志豪同学。
    // 每段只存最小值 加上每个元素和这个最小值的差。

    private int bitOfEachElemSectionMinElem = 8; // 每段中最小值 的位数大小 = 8
    private int bitOfEachElemDifferBit = 3; // 每段中 差值所需要占用的位数 的位数 = 3 （1 ~ 8位）
    private int bitOfEachSectionElemLen = 8; // 表示长度所需要 的位数  (每段最大的数量为 256， 且没有为0的。所以要用8位。)(1~256)
    private int maxLengthPerSection = 256; // 每段对打数量
    private int bitOfEachSectionHeader = bitOfEachElemSectionMinElem + bitOfEachElemDifferBit + bitOfEachSectionElemLen; // 每段需要附加的信息所占位数
    private int minBit = 0; // 最小的位数 （在getBestSection函数中获取）
    private ArrayList<Integer> ElemNumPerSection; // 每段元素个数  (1~256)--(0~255)
    private ArrayList<Integer> BitOfPerSectionElem; // 每段中 和最小值的差的最大值 的位数 (1~8)--(0-7)
    private ArrayList<Integer> MinElemPerSection; // 每段中最小值
    private ArrayList<ArrayList<Byte>> DifferElemPerSection; // 每个元素都以和该段最小值的差

    public DPCompressStrategy2() {
        ElemNumPerSection = new ArrayList<>();
        BitOfPerSectionElem = new ArrayList<>();
        MinElemPerSection = new ArrayList<>();
        DifferElemPerSection = new ArrayList<>();
    }

    @Override
    public byte[] compress(byte[] grayArr) throws ParaIllegalException {
        ArrayList<Byte> grayArrayList = ByteArrayConvertor.byteArrayToArrayList(grayArr);
        getBestSection(grayArrayList); // 获取最佳分段信息
        BitArrayList bitArrayList = new BitArrayList();
        int sectionNum = ElemNumPerSection.size();
        for (int i = 0; i < sectionNum; i++) {
            int ElemNum = ElemNumPerSection.get(i);
            int bit = BitOfPerSectionElem.get(i);
            ArrayList<Byte> Differs = DifferElemPerSection.get(i);
            for (int j = 0; j < ElemNum; j++) {
                bitArrayList.add(Differs.get(j), bit);
            }
        }

        byte[] result = bitArrayList.toByte();

        // 因为只用 8位（0~255） 和 3位（0~7） 其中 0 是不会用到的。 所以所有数值减1 (0~255)--(1~256)   (0~7)--(1~8)的映射
        for (int i = 0; i < sectionNum; i++) {
            ElemNumPerSection.set(i,ElemNumPerSection.get(i) - 1);
            BitOfPerSectionElem.set(i,BitOfPerSectionElem.get(i) - 1);
        }

        getCompressInfo().addIntListInfo("ElemNumPerSection", ElemNumPerSection,bitOfEachSectionElemLen);
        getCompressInfo().addIntListInfo("BitOfPerSectionElem", BitOfPerSectionElem, bitOfEachElemDifferBit);
        getCompressInfo().addIntListInfo("MinElemPerSection", MinElemPerSection, bitOfEachElemSectionMinElem);

        getCompressInfo().addIntInfo("bitOfEachElemSectionMinElem", bitOfEachElemSectionMinElem);
        getCompressInfo().addIntInfo("bitOfEachElemDifferBit", bitOfEachElemDifferBit);
        getCompressInfo().addIntInfo("bitOfEachSectionElemLen", bitOfEachSectionElemLen);
        getCompressInfo().addIntInfo("maxLengthPerSection", maxLengthPerSection);
        getCompressInfo().addIntInfo("bitOfEachSectionHeader", bitOfEachSectionHeader);

        return result;
    }

    @Override
    public byte[] decompress(byte[] grayArr) throws CompressStrategyNotInitException, BitArrayListOutOfBoundException, ParaIllegalException {
        this.bitOfEachElemSectionMinElem = getCompressInfo().getIntInfo("bitOfEachElemSectionMinElem");
        this.bitOfEachElemDifferBit = getCompressInfo().getIntInfo("bitOfEachElemDifferBit");
        this.bitOfEachSectionElemLen = getCompressInfo().getIntInfo("bitOfEachSectionElemLen");
        this.maxLengthPerSection = getCompressInfo().getIntInfo("maxLengthPerSection");
        this.bitOfEachSectionHeader = getCompressInfo().getIntInfo("bitOfEachSectionHeader");

        this.ElemNumPerSection = getCompressInfo().getIntListInfo("ElemNumPerSection");
        this.BitOfPerSectionElem = getCompressInfo().getIntListInfo("BitOfPerSectionElem");
        this.MinElemPerSection = getCompressInfo().getIntListInfo("MinElemPerSection");

        int sectionNum = this.ElemNumPerSection.size();
        if (grayArr.length < sectionNum || (grayArr.length!=0 && sectionNum <= 0)) {
            throw new CompressStrategyNotInitException("BmpCompressStrategy类未能正确的初始化，sectionNum > " + grayArr.length + " && secctionNum <= 0 \n" +
                    "CompressInfo未正确读取, 试调用CompressStrategy.readCompressInfo(String fileName, int offset) 来获取正确的压缩信息 \n");
        }

        for (int i = 0; i < sectionNum; i++) { // (0~255)---(1~256)    |   (0~7)---(1~8)
            ElemNumPerSection.set(i,ElemNumPerSection.get(i) + 1);
            BitOfPerSectionElem.set(i,BitOfPerSectionElem.get(i) + 1);
        }

        BitArrayList bitArrayList = new BitArrayList(grayArr);
        ArrayList<Byte> result = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < sectionNum; i++) {
            int ElemNum = ElemNumPerSection.get(i);
            int bit = BitOfPerSectionElem.get(i);
            int min = MinElemPerSection.get(i); // 最小值
            for (int j = 0; j < ElemNum * bit; j += bit) {
                long elem = bitArrayList.at(j + index, bit); // 这是差值
                elem += min; // 加上最小值就是原来的值
                result.add((byte)elem);
            }
            index += ElemNum * bit;
        }

        return ByteArrayConvertor.arrayListToByteArray(result);
    }

    public void getBestSection(ArrayList<Byte> grayArr) {
        int capacity = grayArr.size();
        int[] s = new int[capacity]; // 所需要的最少位数
        int[] l = new int[capacity]; // 最后一段的长度
        int[] m = new int[capacity]; // 最小元素

        s[0] = bitOfEachSectionHeader + 1;
        l[0] = 1;
        m[0] = grayArr.get(0) & 0xFF;

        for (int i = 1; i < capacity; i++) {
            // 分成两段 {0~i-1} {i} 计算所需位数
            int minElem = grayArr.get(i) & 0xFF; // 该段最小值
            m[i] = minElem;
            int maxDiffer = 0; // 最大差值
            s[i] = s[i - 1] + 1; //最后一段 因为只有一个，最小值是自己，差值就是一个0(1位)
            l[i] = 1;
            int j = 2;
            for (j = 2; j <= i && j <= maxLengthPerSection; j++) { // 分成 {0 ~ i-j} {i-j+1 ~ i}
                // j ∈ [2,i]
                // j==i => 分成两段 {0} {1 ~ i}
                // 还需要讨论 全部分成一段时的情况
                if (minElem > (grayArr.get(i - j + 1) & 0xFF)){
                    int dif = minElem - (grayArr.get(i - j + 1) & 0xFF);
                    minElem = grayArr.get(i - j + 1) & 0xFF;
                    maxDiffer += dif; //最小值换了，那个最大差值也会跟着变
                } else { // grayArr.get(i - j + 1) 不是最小值，计算一下他和最小值的差是不是最大的
                    int dif = (grayArr.get(i - j + 1) & 0xFF) - minElem;
                    if (dif > maxDiffer) {
                        maxDiffer = dif;
                    }
                }

                if (s[i] > s[i - j] + length((byte)maxDiffer) * j) {
                    s[i] = s[i - j] + length((byte)maxDiffer) * j;
                    l[i] = j;
                    m[i] = minElem; // 该段最小值
                }
            }
            // 直接分成1段 {0 ~ i}
            if (j <= maxLengthPerSection) {
                if (minElem > (grayArr.get(0) & 0xFF)) {
                    int dif = minElem - (grayArr.get(i - j + 1) & 0xFF);
                    minElem = (grayArr.get(0) & 0xFF);
                    maxDiffer += dif;
                } else {
                    int dif = (grayArr.get(0) & 0xFF) - minElem;
                    if (dif > maxDiffer) {
                        maxDiffer = dif;
                    }
                }
                if (s[i] > length((byte)maxDiffer) * (i + 1)){
                    s[i] = length((byte)maxDiffer) * (i + 1);
                    l[i] = i + 1;
                    m[i] = minElem;
                }
            }

            s[i] += bitOfEachSectionHeader;
        }

        ArrayList<Integer> ElemNumPerSection = new ArrayList<>(); // 每段元素个数
        ArrayList<Integer> BitOfPerSectionElem = new ArrayList<>(); // 每段中 和最小值的差的最大值 的位数
        ArrayList<Integer> MinElemPerSection = new ArrayList<>(); // 每段中最小值
        ArrayList<ArrayList<Byte>> DifferElemPerSection = new ArrayList<>(); // 每个元素都以和该段最小值的差

        int i = l.length - 1;
        while (i >= 0) {
            ElemNumPerSection.add(0,l[i]);
            MinElemPerSection.add(0,m[i]);
            int bitOfDiffer = 0;
            if (i - l[i] < 0) // 第一段
                bitOfDiffer = (s[i] - bitOfEachSectionHeader) / l[i] ;// 在这一段的位数
            else
                bitOfDiffer = (s[i] - s[i - l[i]] - bitOfEachSectionHeader) / l[i];
            BitOfPerSectionElem.add(0,bitOfDiffer);
            i = i - l[i];
        }

        int offset = 0;
        for (int j = 0; j < ElemNumPerSection.size(); j++) {
            int elemNum = ElemNumPerSection.get(j);
            int minElem = MinElemPerSection.get(j);
            DifferElemPerSection.add(new ArrayList<Byte>());
            for (int k = 0; k < elemNum; k++) {
                int diff = (grayArr.get(k + offset) & 0xFF) - minElem;
                DifferElemPerSection.get(j).add((byte)diff);
            }
            offset += elemNum;
        }

        this.ElemNumPerSection = ElemNumPerSection;
        this.BitOfPerSectionElem = BitOfPerSectionElem;
        this.MinElemPerSection = MinElemPerSection;
        this.DifferElemPerSection = DifferElemPerSection;
        this.minBit = s[s.length - 1];
    }

    private int length(byte val) {
        return BitArrayList.length_b(val);
    }

}
