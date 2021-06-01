package CompressStrategies;

import MyException.BitArrayListOutOfBoundException;
import MyException.ByteArrayException;
import MyException.ParaIllegalException;
import MyException.WrongFileOffsetException;
import Tool.BitArrayList;
import Tool.ByteArrayConvertor;
import lombok.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@Data
public class CompressInfo {
    private HashMap<String,Integer> intInfoHashMap;
    private HashMap<String,ArrayList<Integer>> intListInfoHashMap;
    private HashMap<String,Integer> intListCompressBit; // 整型列表中String对应的列表中每个元素使用Integer位来存储

    int infoNum = 0;
    int offset = 0; // 偏移（即写入文件中的数据字节大小）

    public CompressInfo() {
        intListInfoHashMap = new HashMap<String, ArrayList<Integer>>(0);
        intInfoHashMap = new HashMap<String, Integer>(0);
        intListCompressBit = new HashMap<String, Integer>(0);
    }

    public void addIntListInfo(String descript, ArrayList<Integer> intInfoList, Integer compressBit) throws ParaIllegalException {
        boolean legal = true;
        for(Integer i : intInfoList) {
            if (BitArrayList.length_i(i) > compressBit) {
                legal = false;
                String msg = "\naddIntListInfo(String descript, ArrayList<Integer> intInfoList, Integer compressBit)中" +
                        "\n intInfoList中有的元素所需要的位数比compressBit要大。因此compressBit这只不合理\n"  +  "其中compressBit = " + compressBit + ";" +
                        " intInfoList中存在整型：i=" + i + "\n";
                throw new ParaIllegalException(msg);
            }
        }
        intListInfoHashMap.put(descript, intInfoList);
        intListCompressBit.put(descript, compressBit);

        infoNum++;
    }

    public void addIntListInfo(String descript, ArrayList<Integer> intInfoList) {
        intListInfoHashMap.put(descript, intInfoList);
        intListCompressBit.put(descript, 32);
        infoNum++;
    }

    public void addIntInfo(String descript, Integer intInfo) {
        intInfoHashMap.put(descript, intInfo);
        infoNum++;
    }

    public void writeCompressInfo(String fileName) throws IOException, ParaIllegalException {
        File file = new File(fileName);
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        offset = 0;

        //  文件结构如下
        //  | 整型属性数目(int 4bytes) | 整型列表属性数目(4bytes) |
        //  | 每个属性所占的字节数(int 4bytes) |

        //  | 整型属性名称的字节数(int 4bytes) |
        //  | 整型属性名称(String) |
        //  | 整型属性(int 4bytes) |

        //  | 整型列表属性名称的字节数(int 4bytes) |
        //  | 整型列表属性名称(String) |
        //  | 整型列表的压缩位数(int 4bytes) |
        //  | 整型列表元素个数(int 4bytes) |
        //  | 整型列表属性 (n bytes) |


        // 属性数量
        byte[] intInfoNumByte = ByteArrayConvertor.intToByteArray(intInfoHashMap.size());
        byte[] intListInfoNumByte = ByteArrayConvertor.intToByteArray(intListInfoHashMap.size());
        fileOutputStream.write(intInfoNumByte); // 整型属性数目
        fileOutputStream.write(intListInfoNumByte); // 整型列表属性数目
        offset += 8;

        Set<String> intInfoKeySet = intInfoHashMap.keySet();
        Set<String> intListInfoKeySet = intListInfoHashMap.keySet();

        HashMap<String, BitArrayList> intListInfoBitArrayHashMap = new HashMap<>(); // 将每个ArrayList<Integer>中每个整型按bit位压缩进BitArray中。
        for (String key : intListInfoKeySet) {
            int bit = intListCompressBit.get(key);
            BitArrayList  bitArrayList = new BitArrayList();
            ArrayList<Integer> intList = intListInfoHashMap.get(key);
            for (Integer i : intList) { // 把其中的每个元素压入位数组中
                bitArrayList.add(i, bit);
            }
            intListInfoBitArrayHashMap.put(key, bitArrayList);
        }

        // 写入每个属性所占的字节数 (都是整型)
        for (String key : intInfoKeySet) {
            int byteOfEachAttribute = 0;
            byteOfEachAttribute += 4; // 属性名称的字节数(int 4bytes)
            byteOfEachAttribute += key.getBytes().length; // 属性名称(String)
            byteOfEachAttribute += 4; // 属性(int 4bytes)
            fileOutputStream.write(ByteArrayConvertor.intToByteArray(byteOfEachAttribute));
            offset += 4;
        }
        for (String key : intListInfoKeySet) {
            int byteOfEachAttribute = 0;
            byteOfEachAttribute += 4; // 属性名称的字节数(int 4bytes)
            byteOfEachAttribute += key.getBytes().length; // 属性名称(String)
            byteOfEachAttribute += 4; // 压缩的位数
            byteOfEachAttribute += 4; // 列表元素个数
            BitArrayList attribute = intListInfoBitArrayHashMap.get(key);
            byteOfEachAttribute += attribute.getByteSize(); // 属性字节数
            fileOutputStream.write(ByteArrayConvertor.intToByteArray(byteOfEachAttribute));
            offset += 4;
        }

        // 每个属性
        for (String key : intInfoKeySet) {
            Integer attribute = intInfoHashMap.get(key);
            int byteLenOfStringName = key.getBytes().length;
            fileOutputStream.write(ByteArrayConvertor.intToByteArray(byteLenOfStringName)); //属性名称的字节数
            fileOutputStream.write(key.getBytes()); // 属性的名称
            fileOutputStream.write(ByteArrayConvertor.intToByteArray(attribute)); // 写入属性
            offset += 4 + byteLenOfStringName + 4;
        }
        for (String key : intListInfoKeySet) {
            BitArrayList attributes = intListInfoBitArrayHashMap.get(key);

            int byteLenOfStringName = key.getBytes().length;
            fileOutputStream.write(ByteArrayConvertor.intToByteArray(byteLenOfStringName)); //属性名称的字节数

            fileOutputStream.write(key.getBytes()); // 属性的名称

            int compressBit = intListCompressBit.get(key);
            fileOutputStream.write(ByteArrayConvertor.intToByteArray(compressBit)); // 整型列表的压缩位数(int 4bytes)

            int attributeNum = intListInfoHashMap.get(key).size();// 列表元素个数
            fileOutputStream.write(ByteArrayConvertor.intToByteArray(attributeNum));

            ArrayList<Byte> attributesByteArrayList =  attributes.getBitArrayList();
            byte[] attributesByteArray = ByteArrayConvertor.arrayListToByteArray(attributesByteArrayList);
            fileOutputStream.write(attributesByteArray); // 写入压缩后的属性

            offset += 4 + byteLenOfStringName + 4 + 4 +attributes.getByteSize();
        }

        fileOutputStream.close();
    }

    public void readCompressInfo(String fileName, int offset) throws IOException, ByteArrayException, WrongFileOffsetException, ParaIllegalException, BitArrayListOutOfBoundException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        fileInputStream.skip(offset);

        int compressInfoOffset = 0;

        //  文件结构如下
        //  文件结构如下
        //  | 整型属性数目(int 4bytes) | 整型列表属性数目(4bytes) |
        //  | 每个属性所占的字节数(int 4bytes) |

        //  | 整型属性名称的字节数(int 4bytes) |
        //  | 整型属性名称(String) |
        //  | 整型属性(int 4bytes) |

        //  | 整型列表属性名称的字节数(int 4bytes) |
        //  | 整型列表属性名称(String) |
        //  | 整型列表的压缩位数(int 4bytes) |
        //  | 整型列表元素个数(int 4bytes) |
        //  | 整型列表属性 (n bytes) |


        // 整型属性数目(int 4bytes) | 整型列表属性数目(4bytes)
        byte[] intInfoNumByte = new byte[4];
        byte[] intListInfoNumByte = new byte[4];
        fileInputStream.read(intInfoNumByte);
        fileInputStream.read(intListInfoNumByte);
        int intInfoNum = ByteArrayConvertor.byteArrayToInt(intInfoNumByte);
        int intListInfoNum = ByteArrayConvertor.byteArrayToInt(intListInfoNumByte);
        compressInfoOffset += 8;

        // 读每个属性所占字节数
        ArrayList<Integer> bytesOfEachIntAttribute = new ArrayList<>();
        ArrayList<Integer> bytesOfEachIntListAttribute = new ArrayList<>();
        for (int i = 0; i < intInfoNum; i++) {
            byte[] attributeByteNum = new byte[4];
            fileInputStream.read(attributeByteNum);
            bytesOfEachIntAttribute.add(ByteArrayConvertor.byteArrayToInt(attributeByteNum));
        }
        for (int i = 0; i < intListInfoNum; i++) {
            byte[] attributeByteNum = new byte[4];
            fileInputStream.read(attributeByteNum);
            bytesOfEachIntListAttribute.add(ByteArrayConvertor.byteArrayToInt(attributeByteNum));
        }
        compressInfoOffset += bytesOfEachIntAttribute.size() * 4 + bytesOfEachIntListAttribute.size() * 4;


        //读整型属性
        HashMap<String, Integer> intInfoHashMap = new HashMap<>();
        for (int i = 0; i < bytesOfEachIntAttribute.size(); i++) {
            int byteLen = bytesOfEachIntAttribute.get(i); // 该属性的字节长度
            compressInfoOffset += byteLen;

            byte[] attributeNameByteLen_ByteArray = new byte[4];
            fileInputStream.read(attributeNameByteLen_ByteArray);
            int attributeNameByteLen = ByteArrayConvertor.byteArrayToInt(attributeNameByteLen_ByteArray); // 属性名所用的字节长度

            byte[] attributeName_ByteArray = new byte[attributeNameByteLen];
            fileInputStream.read(attributeName_ByteArray);
            String attributeName = new String(attributeName_ByteArray); // 属性名

            int attributeByteLen = byteLen - 4 - attributeNameByteLen; // 属性数据的字节数
            if (attributeByteLen != 4)
                throw new WrongFileOffsetException("readCompressInfo(String fileName, int offset)中，整型数据格式不合预设规则，可能是偏移offset有误");
            byte[] attribute_byteArr = new byte[4];
            fileInputStream.read(attribute_byteArr);
            Integer attribute = ByteArrayConvertor.byteArrayToInt(attribute_byteArr);
            intInfoHashMap.put(attributeName, attribute);

        }


        // 读整型列表属性
        HashMap<String, ArrayList<Integer>> intListInfoHashMap = new HashMap<>();
        for (int i = 0; i < bytesOfEachIntListAttribute.size(); i++) {
            int byteLen = bytesOfEachIntListAttribute.get(i); // 该属性的字节长度
            compressInfoOffset += byteLen;

            byte[] attributeNameByteLen_ByteArray = new byte[4];
            fileInputStream.read(attributeNameByteLen_ByteArray);
            int attributeNameByteLen = ByteArrayConvertor.byteArrayToInt(attributeNameByteLen_ByteArray); // 属性名所用的字节长度

            byte[] attributeName_ByteArray = new byte[attributeNameByteLen];
            fileInputStream.read(attributeName_ByteArray);
            String attributeName = new String(attributeName_ByteArray); // 属性名

            byte[] compressBit_ByteArray = new byte[4];
            fileInputStream.read(compressBit_ByteArray);
            int compressBit = ByteArrayConvertor.byteArrayToInt(compressBit_ByteArray); // 压缩位数

            byte[] attributeNum_ByteArray = new byte[4];
            fileInputStream.read(attributeNum_ByteArray);
            int attributeNum = ByteArrayConvertor.byteArrayToInt(attributeNum_ByteArray); // 元素个数

            int attributeByteLen = byteLen - 4 - attributeNameByteLen - 4 - 4; // 属性数据的字节数
            byte[] attributeBitArray = new byte[attributeByteLen];
            fileInputStream.read(attributeBitArray);
            BitArrayList bitArrayList = new BitArrayList(attributeBitArray);
            ArrayList<Integer> attribute = new ArrayList<>();
            for (int j = 0; j < attributeNum * compressBit; j += compressBit) {
                int attributeItem = (int) bitArrayList.at(j, compressBit);
                attribute.add(attributeItem);
            }
            intListInfoHashMap.put(attributeName, attribute);
        }

        this.intInfoHashMap = intInfoHashMap;
        this.intListInfoHashMap = intListInfoHashMap;
        this.offset = compressInfoOffset;

        fileInputStream.close();
    }

    public ArrayList<Integer> getIntListInfo(String key) {
        if (intListInfoHashMap.containsKey(key)) {
            return intListInfoHashMap.get(key);
        } else {
            return null;
        }
    }

    public Integer getIntInfo(String key) {
        if (intInfoHashMap.containsKey(key)) {
            return intInfoHashMap.get(key);
        } else {
            return null;
        }
    }

    public int getSize() {
        return getOffset();
    }

    public void addCompressInfo(CompressInfo compressInfo) throws ParaIllegalException { // 将其他CompressInfo中的信息赋值到这个CompressInfo中。
        HashMap<String, Integer> intInfoHashMap = compressInfo.getIntInfoHashMap();
        HashMap<String, Integer> intListCompressBit = compressInfo.getIntListCompressBit();
        HashMap<String, ArrayList<Integer>> intListInfoHashMap = compressInfo.getIntListInfoHashMap();

        for (String key : intInfoHashMap.keySet()) {
            Integer value = intInfoHashMap.get(key);
            this.addIntInfo(key, value);
        }

        for (String key : intListInfoHashMap.keySet()) {
            ArrayList<Integer> value = intListInfoHashMap.get(key);
            Integer bit = intListCompressBit.get(key);
            this.addIntListInfo(key, value, bit);
        }
    }

    public void addCompressInfoExcept(CompressInfo compressInfo, String ... exceptions) throws ParaIllegalException {
        HashMap<String, Integer> intInfoHashMap = compressInfo.getIntInfoHashMap();
        HashMap<String, Integer> intListCompressBit = compressInfo.getIntListCompressBit();
        HashMap<String, ArrayList<Integer>> intListInfoHashMap = compressInfo.getIntListInfoHashMap();

        for (String key : intInfoHashMap.keySet()) {
            if (!contains(exceptions, key)) {
                Integer value = intInfoHashMap.get(key);
                this.addIntInfo(key, value);
            }
        }

        for (String key : intListInfoHashMap.keySet()) {
            if (!contains(exceptions, key)) {
                ArrayList<Integer> value = intListInfoHashMap.get(key);
                Integer bit = intListCompressBit.get(key);
                this.addIntListInfo(key, value, bit);
            }
        }
    }

    private boolean contains(String[] keys, String target) {
        if (keys == null || target == null)
            return false;

        for (String key : keys) {
            if (key.equals(target)) {
                return true;
            }
        }
        return false;
    }

}
