package Tool;

import MyException.BitArrayListOutOfBoundException;
import MyException.ParaIllegalException;
import lombok.Data;

import java.util.ArrayList;

@Data
public class BitArrayList {
    // 这个位数组有一个坑：如果将位数组转换成字符数组写入文件，然后再读出来，具体的位数量信息会丢失。即最后一个字节的正确性不能够确定。
    // 解决方法：使用的时候最好想办法验证一下最后一个字节的正确性。

    ArrayList<Byte> bitArrayList;
    int tailBit; // 最后的一位 在位数组中的位置  at(tailBit)

    public BitArrayList() {
        bitArrayList = new ArrayList<Byte>(0);
        tailBit = -1;
    }

    public BitArrayList(BitArrayList bitArrayList) {
        this.bitArrayList = bitArrayList.bitArrayList;
        this.tailBit = bitArrayList.tailBit;
    }

    public BitArrayList(byte[] byteArr) {
        this.bitArrayList = ByteArrayConvertor.byteArrayToArrayList(byteArr);
        this.tailBit = byteArr.length * 8 - 1;
    }

    public BitArrayList(byte[] byteArr, int tailBit) {
        this.bitArrayList = ByteArrayConvertor.byteArrayToArrayList(byteArr);
        this.tailBit = tailBit;
    }

    public void add(byte val) throws ParaIllegalException {
        this.add(val, 8);
    }

    public void add(byte val, int bit) throws ParaIllegalException { // 添加将val添加到bitArrayList尾部，且只占用bit位
        if (bit < length_b(val))
            throw new ParaIllegalException("BitArray.add(" + val + "," + bit + ") 中" + bit + "<" + "length(val):" + length_b(val));
        int bitCapacity = bitArrayList.size() * 8; //
        int left = bitCapacity - getBitSize(); // 剩余的位数
        if (left == 0) {
            bitArrayList.add((byte) 0);
            left = 8;
        }
        if (left < bit) {
            byte tmp = bitArrayList.get(bitArrayList.size() - 1);
            byte valHignLeft =(byte)((val&0XFF) >> (bit - left)) ;// val的高left位 (在bit位的二进制串中)
            byte result =(byte) (tmp | valHignLeft);
            bitArrayList.set(bitArrayList.size() - 1, result);
            bitArrayList.add((byte) 0);
            byte valLowRest = (byte)((val&0XFF) << (8 - (bit - left)));
            result = (byte)(0x00 | valLowRest);
            bitArrayList.set(bitArrayList.size() - 1, result);
        } else if (left >= bit) {
            byte tmp = (byte)(val << (left - bit));
            byte result = (byte)(bitArrayList.get(bitArrayList.size() - 1) | tmp);
            bitArrayList.set(bitArrayList.size() - 1, result);
        }
        this.bitArrayList.get(0);
        tailBit += bit;
    }

    public void add(int val, int bit) throws ParaIllegalException {
        if (val < 0) {
            val = val & 0XFFFF;
        }

        if (bit <= 8) {
            byte val_byte = (byte) val;
            this.add(val_byte, bit);
        } else {
            byte[] val_byteArr = ByteArrayConvertor.intToByteArray(val); // b[0] 是低位
            int pos = (bit - 1) / 8; // 最高位所在字节
            // b[0]     b[1]     b[2]     b[3]
            // 01101101 10100011 11010011 00001111
            // 87654321 87654321 87654321 87654321
            // 7 / 8 = 0
            // 8 / 8 = 1
            // 14 位
            // pos = 1  --》最高位在b[1]
            // 14 % 8 = 6
            // 9 % 8 = 1
            // 16 % 8 = 0
            // 15 % 8 = 7
            // 1 2 3 4 5 6 7 0
            // bitArray是高位先存
            int mod = bit % 8 == 0 ? 8 : bit % 8; // 最高位所在字节要存入数组的个数。
            this.add(val_byteArr[pos], mod);
            for (int i = pos - 1; i >= 0; i--) {
                this.add(val_byteArr[i]);
            }
        }
    }

    public Byte at(int index) throws BitArrayListOutOfBoundException { // 返回bitArrayList中第 index 位的二进制数
        int pos = index / 8;
        if (pos > tailBit || pos < 0)
            throw new BitArrayListOutOfBoundException();

        int mod = index % 8;
        int temp = bitArrayList.get(pos) & 0xFF;
        byte result = (byte) ((temp >> (8 - (mod + 1))) % 2);
        return result;
    }

    public long at(int index, int bit) throws BitArrayListOutOfBoundException, ParaIllegalException{
        if (bit > Long.SIZE * 8) // 超过整型的范围
            throw new ParaIllegalException(" BitArrayList.at(int index, int bit) 中bit数量过大。 最大数量=" + Long.SIZE * 8 );

        long result = 0;
        for (int i = index; i < index + bit; i++) {
            byte temp = at(i);
            result = result * 2 + temp;
        }
        return result;
    }

    public void set(int index, int bit) throws BitArrayListOutOfBoundException, ParaIllegalException {
        if (bit != 0 && bit != 1) {
            throw new ParaIllegalException("BitArray 位数组中 set(int index, int bit) 中的 bit = " + bit + " 不合法。在位数组中只可以是 0 或者 1");
        }
        if (at(index) != bit) {
            int pos = index / 8;
            if (pos > tailBit || pos < 0)
                throw new BitArrayListOutOfBoundException();

            byte tmp = bitArrayList.get(pos);
            int mod = index % 8;
            int adder = 1 << (8 - (mod + 1));
            byte result = 0;
            if (bit == 1) {
                result = (byte) (tmp + adder);
            } else  {
                result = (byte) (tmp - adder);
            }
            bitArrayList.set(pos,result);
        }
    }

    public int getByteSize() {
        return bitArrayList.size();
    }

    public int getBitSize() {
        return tailBit == -1 ? 0 : tailBit + 1;
    }

    public byte[] toByte() {
        byte[] result = new byte[bitArrayList.size()];
        for (int i = 0; i < bitArrayList.size(); i++) {
            result[i] = bitArrayList.get(i);
        }
        return result;
    }

    public static int length_b(byte val) {
        int tmp = val & 0XFF ;
        return tmp == 0 ? 1 : (int)Math.floor(Math.log(tmp) / Math.log(2)) + 1; // 0 也是需要占一位的。
    }

    public static int length_i(int val) { // 返回 整型val所需要的最少位数(val >= 0)
        if (val < 0) return 32;

        int bitCount = 0;
        while (val !=0 ) {
            bitCount++;
            val = val >> 1;
        }
        return bitCount;
    }

}
