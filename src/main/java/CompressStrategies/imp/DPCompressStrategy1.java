package CompressStrategies.imp;

import CompressStrategies.CompressInfo;
import CompressStrategies.CompressStrategy;
import MyException.*;
import Tool.BitArrayList;
import Tool.ByteArrayConvertor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;

@Data
@AllArgsConstructor
public class DPCompressStrategy1 extends CompressStrategy {
    //！！！！！！！该策略完全按照书本上的算法，没有一丝丝的改变。
    // 算法中每段存储的是原本值。

    private int bitOfEachSectionLength = 8; // 每段长度 所占的位数
    private int bitOfEachSectionElemBit = 3; // 每段元素 所需位数 的位数
    private int bitOfEachSectionHeader = bitOfEachSectionLength + bitOfEachSectionElemBit; // 额外需要的位数
    private int maxSectionElemNum = 256; // 每段 中最多的元素个数。
    private ArrayList<Integer> ElemNumPerSection; // 每段元素个数
    private ArrayList<Integer> BitOfPerSectionElem; // 每段中 元素的位数

    public DPCompressStrategy1() {
        ElemNumPerSection = new ArrayList<Integer>(0);
        BitOfPerSectionElem = new ArrayList<Integer>(0);
    }

    @Override
    public byte[] compress(byte[] grayArr) throws ParaIllegalException {
        ArrayList<Byte> grayArrayList = ByteArrayConvertor.byteArrayToArrayList(grayArr);
        getBestSectionInfo(grayArrayList);
        BitArrayList bitArrayList = new BitArrayList();
        int sectionNum = ElemNumPerSection.size();
        int index = 0;
        for (int i = 0; i < sectionNum; i++) {
            int ElemNum = ElemNumPerSection.get(i);
            int bit = BitOfPerSectionElem.get(i);
            for (int j = 0; j < ElemNum; j++) {
                bitArrayList.add(grayArrayList.get(index + j), bit);
            }
            index += ElemNum;
        }
        byte[] result = bitArrayList.toByte();


        for (int i = 0; i < sectionNum; i++) { // 因为只用 8位（0~255） 和 3位（0~7） 其中 0 是不会用到的。 所以所有数值减1 (0~255)--(1~256)   (0~7)--(1~8)的映射
            ElemNumPerSection.set(i, ElemNumPerSection.get(i) - 1);
            BitOfPerSectionElem.set(i, BitOfPerSectionElem.get(i) - 1);
        }

        getCompressInfo().addIntListInfo("ElemNumPerSection", ElemNumPerSection, bitOfEachSectionLength);
        getCompressInfo().addIntListInfo("BitOfPerSectionElem", BitOfPerSectionElem, bitOfEachSectionElemBit);
        getCompressInfo().addIntInfo("bitOfEachSectionLength", bitOfEachSectionLength);
        getCompressInfo().addIntInfo("bitOfEachSectionElemBit", bitOfEachSectionElemBit);
        getCompressInfo().addIntInfo("bitOfEachSectionHeader", bitOfEachSectionHeader);
        getCompressInfo().addIntInfo("maxSectionElemNum", maxSectionElemNum);

        return result;
    }

    @Override
    public byte[] decompress(byte[] grayArr) throws CompressStrategyNotInitException, ParaIllegalException, BitArrayListOutOfBoundException {
        this.bitOfEachSectionLength = getCompressInfo().getIntInfo("bitOfEachSectionLength");
        this.bitOfEachSectionElemBit = getCompressInfo().getIntInfo("bitOfEachSectionElemBit");
        this.bitOfEachSectionHeader = getCompressInfo().getIntInfo("bitOfEachSectionHeader");
        this.maxSectionElemNum = getCompressInfo().getIntInfo("maxSectionElemNum");

        this.ElemNumPerSection = getCompressInfo().getIntListInfo("ElemNumPerSection");
        this.BitOfPerSectionElem = getCompressInfo().getIntListInfo("BitOfPerSectionElem");

        for (int i = 0; i < ElemNumPerSection.size(); i++) { // 加1恢复 （对应上面compress映射的反映射）
            ElemNumPerSection.set(i,ElemNumPerSection.get(i) + 1);
            BitOfPerSectionElem.set(i,BitOfPerSectionElem.get(i) + 1);
        }

        // 在解压之前需要先获取正确的CompressInfo
        int sectionNum = ElemNumPerSection.size();
        if (grayArr.length < sectionNum || (grayArr.length!=0 && sectionNum <= 0)) {
            throw new CompressStrategyNotInitException("BmpCompressStrategy类未能正确的初始化，sectionNum > " + grayArr.length + " && secctionNum <= 0 \n" +
                                                       "CompressInfo未正确读取, 试调用CompressStrategy.readCompressInfo(String fileName, int offset) 来获取正确的压缩信息 \n");
        }
        BitArrayList bitArrayList  = new BitArrayList(grayArr);
        ArrayList<Byte> result = new ArrayList<Byte>(0);
        int index = 0;
        for (int i = 0; i < sectionNum; i++) {
            int elemNum = ElemNumPerSection.get(i);
            int bit = BitOfPerSectionElem.get(i);
            for(int j = 0; j < elemNum; j++) {
                 byte tmp = (byte)bitArrayList.at(index, bit);
                 index += bit;
                 result.add(tmp);
            }
        }

        return ByteArrayConvertor.arrayListToByteArray(result);
    }

    private void getBestSectionInfo(ArrayList<Byte> grayArr) { // 获取最佳分段信息
        int capacity = grayArr.size();
        int[] s = new int[capacity];
        int[] b = new int[capacity];
        int[] l = new int[capacity];

        b[0] = length(grayArr.get(0));
        s[0] = b[0] + bitOfEachSectionHeader;
        l[0] = 1;

        for (int i = 1; i < capacity; ++i) {
            b[i] = length(grayArr.get(i));
            int bmax = b[i];
            s[i] = s[i - 1] + bmax; // 分成两段：{0 ~ i-1} {i}  其中{0 ~ i-1}是最优分段。
            l[i] = 1; // l[i] 是 {0~i}最佳划分后，i所属分段的元素个数
            int j = 2;
            for (j = 2; j <= i && j <= maxSectionElemNum; ++j) {  // 分成两段： {0 ~ i-j} {i-j+1 ~ i} 其中 {0 ~ i-j}是最优分段
                // j ∈ [2,i]
                // j==i => 分成两段 {0} {1 ~ i}
                // 还需要讨论 全部分成一段时的情况
                if (bmax < b[i - j + 1])  // bmax 是 {i-j+1 ~ i} 中的最大位数
                    bmax = b[i - j + 1];

                if (s[i] > s[i - j] + bmax * j) {
                    s[i] = s[i - j] + bmax * j;
                    l[i] = j;  // {0 ~ i} 中的最优分段中，{i-j+1 ~ i} 的数量是j
                }
            }
            // 直接分成1段 {0 ~ i} (只有在j <= maxSectionElemNum的情况下才检查这个东西,否则抵达不了)
            if (j <= maxSectionElemNum) {
                if (bmax < b[0]) bmax = b[0];
                if (s[i] > bmax * (i + 1)) {
                    s[i] = bmax * (i + 1);
                    l[i] = i + 1;
                }
            }
            s[i] += bitOfEachSectionHeader;
        }



        ArrayList<Integer> ElemNumPerSection = new ArrayList<Integer>();
        ArrayList<Integer> BitOfPerSectionElem = new ArrayList<Integer>();

        int i = l.length - 1;
        while (i >= 0) {
            ElemNumPerSection.add(0, l[i]);
            int bmax = 0;
            if (i - l[i] < 0)
                bmax = (s[i] - bitOfEachSectionHeader) / l[i]; // 这一段的位数
            else
                bmax = (s[i] - s[i - l[i]] - bitOfEachSectionHeader) / l[i]; //这一段的位数
            BitOfPerSectionElem.add(0, bmax);

            i = i - l[i];
        }

        this.ElemNumPerSection = ElemNumPerSection;
        this.BitOfPerSectionElem = BitOfPerSectionElem;
    }

    private int length(byte val) { // 获取最少位数
        return BitArrayList.length_b(val);
    }


}
