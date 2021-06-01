package CompressStrategies.imp;

import CompressStrategies.CompressStrategy;
import MyException.BitArrayListOutOfBoundException;
import MyException.ParaIllegalException;
import Tool.BitArrayList;
import HuffmanTree.*;
import Tool.ByteArrayConvertor;
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;

import java.util.*;

public class HuffmanCompressStrategy extends CompressStrategy {
// 采用哈夫曼编码进行压缩(未实现)

    @Override
    public byte[] compress(byte[] data) throws ParaIllegalException {
        Map<Byte, Integer> frequencyForByte = new HashMap<>();
        for (byte b : data) {
            if (frequencyForByte.containsKey(b)) {
                int freq = frequencyForByte.get(b);
                freq++;
                frequencyForByte.put(b, freq);
            } else {
                frequencyForByte.put(b,1);
            }
        }

        HuffmanTree huffmanTree = new HuffmanTree();
        Map<Byte, String> encodeForByte = huffmanTree.encode(frequencyForByte); // 哈夫曼编码表
        ArrayList<Integer> lengthOfcoding = new ArrayList<>(); // 每个byte的编码长度
        ArrayList<Integer> intCodeForBytes = new ArrayList<>(); // 将String类型的哈夫曼编码 转换成整数类型的哈夫曼编码

        // 将String 类型的哈夫曼编码 转化成 int类型的哈夫曼编码
        for (int i = 0; i < 256; i++) {
            String code = encodeForByte.get((byte) i) ;

            if (code == null) { // 使用差分编码的时候，有的字节可能没有出现
                lengthOfcoding.add(-1);
                intCodeForBytes.add(-1);
                continue;
            }

            int length = code.length();
            lengthOfcoding.add(length);

            int code_int = 0;
            for (int j = 0; j < code.length(); j++) {
                char ch = code.charAt(j);
                int bitNum = ch - '0';
                code_int = code_int * 2 + bitNum;
            }
            intCodeForBytes.add(code_int);
        }

        // 压缩byte[]
        BitArrayList bitArrayList = new BitArrayList();
        for (byte b : data) {
            int codeInt = intCodeForBytes.get(b & 0xff); // 获取整型编码
            int codeLength = lengthOfcoding.get(b & 0xff); // 获取编码的长度
             bitArrayList.add(codeInt, codeLength); // 压入位数组中
        }

        // 将元素和频率写进文件中，到时候取出来，重新构建哈夫曼树
        ArrayList<Integer> elem = new ArrayList<>();
        ArrayList<Integer> frequency = new ArrayList<>();
        for(Byte b : frequencyForByte.keySet()) {
            Integer freq = frequencyForByte.get(b);
            elem.add(b & 0xff);
            frequency.add(freq);
        }
        getCompressInfo().addIntListInfo("ElemInfo", elem, 8);
        getCompressInfo().addIntListInfo("ElemFrequency", frequency);


        return bitArrayList.toByte() ;
    }

    @Override
    public byte[] decompress(byte[] data) throws ParaIllegalException, BitArrayListOutOfBoundException {

        // 获取元素及其频率
        ArrayList<Integer> elemInfo = getCompressInfo().getIntListInfo("ElemInfo");
        ArrayList<Integer> frequency = getCompressInfo().getIntListInfo("ElemFrequency");

        // 重新构造哈夫曼树
        HashMap<Byte, Integer> frequencyForByte = new HashMap<>();
        for (int i = 0; i < elemInfo.size(); i++) {
            byte b = (byte)(int)elemInfo.get(i);
            int freq = frequency.get(i);
            frequencyForByte.put(b,freq);
        }
        HuffmanTree huffmanTree = new HuffmanTree();
        huffmanTree.encode(frequencyForByte);
        Node root = huffmanTree.getHuffmanTree();

        // 解码 byte[]
        BitArrayList bitArrayList = new BitArrayList(data);
        // 这里可能存在的问题是：如果压缩前数据的位数不能被8整除，位数组最后一个字节剩余的位全位0。将位数组按字节写入文件后，会丢失真实的位数。
        // 后面哈夫曼译码时，可能最后一个字节会有问题。
        // 解决方案是，位数组写入文件时，将真实的位数也写进去。（有点麻烦，先不管了，反正问题不大）
        int index = 0;
        int bitSize = bitArrayList.getBitSize();

        ArrayList<Byte> result = new ArrayList<>();
        Node pres = root;
        while (index < bitSize) {
            int code = bitArrayList.at(index++);
            if (code == 0) { // 左子树
                pres = pres.getLeft();
            } else { // 右子树
                pres = pres.getRight();
            }
            if (pres.isLeaf()) { // 如果是右子树就输出info
                result.add(pres.getInfo());
                pres = root; // 指针重新只会根节点。
            }
        }
        return ByteArrayConvertor.arrayListToByteArray(result);
    }

    private int length(int val) {
        return BitArrayList.length_i(val);
    }


}
