package CompressStrategies.imp;

import CompressStrategies.CompressStrategy;
import MyException.BitArrayListOutOfBoundException;
import MyException.ParaIllegalException;
import Tool.BitArrayList;
import Tool.ByteArrayConvertor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class LZ77CompressStrategy2 extends CompressStrategy {
    LinkedList<Byte> searchArea;
    LinkedList<Byte> lookAheadArea;
    int searchAreaMaxSize = 31; // 占用11位
    int lookAheadAreaMaxSize = searchAreaMaxSize; // 占用11位
    int bit = (int)(Math.log(searchAreaMaxSize + 1) / Math.log(2));

    int offset = 0;
    int length = 0;
    int nextByte = 0;

    public byte[] compress(byte data[]) throws ParaIllegalException {
        int index = 0; // index 永远指向下一个准备写入lookAheadArea的字节，或者指向data.length

        BitArrayList bitArrayList = new BitArrayList(); // 存放结果
        searchArea = new LinkedList<>();
        lookAheadArea = new LinkedList<>();

        // 将数据放入lookAheadArea中
        while (index < data.length && index < lookAheadAreaMaxSize) { // 不超过最大长度，不超过lookAheadArea的最大长度
            lookAheadArea.offer(data[index++]);
        }

        // 找lookAhaedArea 在 searchArea中匹配的最长字节串。
        while (lookAheadArea.size() > 0) {
            match(searchArea, lookAheadArea);
            bitArrayList.add(length, bit);
            if (length == 0) { // 未匹配,直接将该元素加入到结果中
                bitArrayList.add((byte)nextByte);
                // 搜索区加上待搜索区第一个元素，（需要先判断是否超过searchArea的最大长度, 如果是则将第一个元素删掉。）
                searchArea.offer(lookAheadArea.getFirst());
                if (searchArea.size() > searchAreaMaxSize) {
                    searchArea.remove(0);
                }

                // 待搜索区后移一个元素，需要先判断data是否还有数据
                // 如果还有则整体后移一个元素
                // 如果没有了，则只需要将链表头元素删除即可
                if (index >= data.length) { // 待搜索区外没有新的元素了。
                    lookAheadArea.remove(0);
                } else {
                    lookAheadArea.remove(0);
                    lookAheadArea.offer(data[index++]);
                }

            } else { // 匹配
                bitArrayList.add(offset, bit);

                // 假设匹配长度为length
                // 搜索区需要将这待搜索区前length个元素加入。（需要判断是否会超过SearchArea的最大长度）
                // 待搜索区需要后移length个元素（需要先判断后移时，是否还有新的元素）
                Iterator<Byte> iterator = lookAheadArea.listIterator();
                for (int i = 0; i < length; i++) { // 搜索区处理
                    searchArea.offer(iterator.next());
                    if (searchArea.size() > searchAreaMaxSize) {
                        searchArea.remove(0);
                    }
                }

                for (int i = 0; i < length; i++) { // 处理待搜索区
                    if (index >= data.length){
                        lookAheadArea.remove(0);
                    } else {
                        lookAheadArea.remove(0);
                        lookAheadArea.offer(data[index++]);
                    }
                }
            }
        }

        getCompressInfo().addIntInfo("bit", bit); // 长度和偏移需要使用的位数
        getCompressInfo().addIntInfo("bitArrayLength", bitArrayList.getBitSize()); // 真实的位大小

        return ByteArrayConvertor.arrayListToByteArray(bitArrayList.getBitArrayList());
    }

    public void match(LinkedList<Byte> first, LinkedList<Byte> second) { // second中以第一个字节为头的字节链表，在first中匹配的最长字节链表
        int maxLength = 0;

        for (int i = 0; i < first.size(); i++) {
            ListIterator<Byte> firstIterator = first.listIterator(i);
            ListIterator<Byte> secondIterator = second.listIterator(0);
            int preLength = 0;
            while (firstIterator.hasNext() && secondIterator.hasNext()) {
                if (firstIterator.next() == secondIterator.next()) {
                    preLength++;
                } else {
                    break;
                }
            }
            if (preLength > maxLength) {
                maxLength = preLength;
                offset = i;
            }
        }

        if (maxLength == 0) {
            nextByte = second.getFirst();
            this.length = 0;
        } else {
            nextByte = 0;
            this.length = maxLength;
        }
    }

    public byte[] decompress(byte data[]) throws ParaIllegalException, BitArrayListOutOfBoundException {

        this.bit = getCompressInfo().getIntInfo("bit");
        int dataBitLength = getCompressInfo().getIntInfo("bitArrayLength");
        BitArrayList bitArrayList = new BitArrayList(data);
        bitArrayList.setTailBit(dataBitLength - 1);

        searchArea = new LinkedList<>();
        ArrayList<Byte> res = new ArrayList<>();

        int index = 0; // index指向下一个元素的开始位数或者 等于data.length
        while (index < dataBitLength) {
            int length = (int)bitArrayList.at(index, bit);
            index += bit;
            if (length == 0) {
                byte nextByte = (byte) bitArrayList.at(index, 8);
                index += 8;

                // 将该元素写入加过中
                res.add(nextByte);
                // 将该元素写入到搜索区中 (需要判断搜索区是否超过最大限定长度255,是则将搜索区头删除)
                searchArea.offer(nextByte);
                if (searchArea.size() > searchAreaMaxSize) {
                    searchArea.remove(0);
                }
            } else { // searchArea中有匹配的内容
                int offset = (int) bitArrayList.at(index, bit);
                index += bit;

                // 将offset后length个元素加入到加结果中
                Iterator<Byte> iterator = searchArea.listIterator(offset);
                ArrayList<Byte> elems = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    byte tmp = iterator.next();
                    elems.add(tmp);
                    res.add(tmp);
                }

                // 然后再将这length个元素加入到搜索区中。 (需要判断搜索区大小是否超过最大限度)
                for (Byte b : elems) {
                    searchArea.offer(b);
                    if (searchArea.size() > searchAreaMaxSize) {
                        searchArea.remove(0);
                    }
                }
            }
        }

        return ByteArrayConvertor.arrayListToByteArray(res);
    }
}
