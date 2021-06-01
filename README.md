# 文件压缩器（JAVA）

## 环境

​	Java8、maven、lombok

## 概述

​	目前对lena.bmp图片压缩效果最好的是**65.01%**。算法在4.3(3)

## 算法

### 1. DPCompressStrategy1

#### 1.1 算法描述

​	该算法是对 《算法设计与分析》王晓东版 中图像动态规划压缩算法的实现。值得注意的是书本上的算法是有bug的。我们的实现已修改了改bug，能够正常执行。

​	该算法是将文件数据分段存储，每段存该段长度（8位），所用位数（3位）、文件数据的存储方式不变。

#### 1.2 实现与效果

​	该算法已实现压缩和解压功能。

​	压缩文件：lena.bmp （8位bmp图  512*512）

​	压缩率：96.99%（压缩率=压缩文件大小/ 源文件大小）

#### 1.3 优化

​	（1）在压缩前，S型排列图像数据后。压缩率=96.81%（其中S型像素排列读取顺序如下图所示）

![S型读取](.\README.assets/S型读取.png)



---

### 2. DPCompressStrategy2

#### 2.1 算法描述

​	该算法是对算法1的优化，同样是将文件分段存储，但是每段存储的是最小值（8位）、差值所需位数（3）、该段长度（8）、文件数据的存储方式变为与所处分段最小值的差。

#### 2.2 实现与效果

​	该算法已实现压缩和解压功能

​	压缩文件：lena.bmp （8位bmp图  512*512）

​	压缩率：73.41%

#### 2.3 优化

​	（1）在压缩前，S型排列图像数据。压缩率=73.11%

---

### 3. HuffmanCompressStrategy

#### 3.1 算法描述

​	基于哈夫曼算法，将文件字节以频率作为权值构建哈夫曼树，根据哈夫曼树得出哈夫曼编码表。根据编码表对文件字节流进行编码。

#### 3.2 实现与效果

​	该算法已实现压缩和解压功能

​	压缩文件：lena.bmp （8位bmp图  512*512）

​	压缩率：94.00%

---

### 4. DifferenceDPCompressStrategy 

#### 4.1 算法描述

​	该算法为差分编码+动态规划。

​	差分编码：将每个像素修 改为 这个像素和上一个像素的差，

​	如此对文件进行编码后，再使用上述的DP算法进行分段存储。

​	注意：该策略需要嵌套其他压缩算法，对差分编码后的数据进行压缩。否则知识差分编码没有什么意义。

#### 4.2 实现与效果

​	（1）差分编码+DPCompressStrategy1

​				压缩率：68.62% （较直接DPCompressStrategy1压缩 有了非常大的改善）

​	（2）差分编码+DPCompressStrategy2

​				压缩率：71.97%

#### 4.3 优化

​	（1）差分编码 + DPCompressStrategy1 + 像素S型排列读取

​				压缩率：68.14%

​	（2）差分编码+DPCompressStrategy2 + 像素S型排列

​				压缩率：71.13%

​	（3）差分编码+HuffmanCompressStrategy + 像素S型排列

​				压缩率：65.01%

​				运行情况：该操作下，哈夫曼树的结点只有125个，即该图片的差分编码后，只存在125类字节。

​				测试代码位置：在 src/test\java\BmpCompressor\BmpCompressorWithDPCompressStrategy1Test.java 中

---

### 5. LZ77CompressStrategy

#### 5.1 算法描述

​	算法中有向前编码区（searchArea），和待编码区（lookAheadArea）。

​	向前编码区和待编码区的大小是固定的。这两个区域构成了滑动窗口。

​											![LZ77](.\README.assets\LZ77.png)

​	编码时，查找待编码区前面最长的一段字符串在 向前编码区 中的位置。如果找到，则用先前偏码区的偏移加上长度来表示。如果找不到则直接将当前字符写入文件中。

​	[reference](https://www.techug.com/post/lz77-lossless-data-compression-algorithms.html)

​	我采用的存储策略是。每个单位用**两个字节**。如果没有找到匹配的就写入（0，待编码区第一个字符）。如果找到的写入（长度，偏移）

​	该算法适用于重复率比较大的文本文件，对于图片这些重复较少的文件压缩效率通常不太好。

#### 5.2 实现与效果

​	（1）文件按字节流读取后使用该策略压缩

​				压缩率：182.31% （ 笑死，反向压缩）

​	（2）bmp格式读取 + 像素S型排列 + LZ77压缩策略

​				压缩率：177.94%（一个鬼样）

​	（3）bmp格式读取 + 像素S型排列 + 差分编码 + LZ77压缩策略

​				压缩率：127.19%（好了一点，但是还是反向压缩）

---

### 6. LZ77CompressStrategy2

#### 6.1 算法描述

​	该算法和5算法一样，实现了可以自由调整搜索区和待编码区的大小。由6.2的结果可见，图片的重复性真的高，不适合使用LZ77进行压缩。

#### 6.2 实现与效果（在5.2(3)的基础上修改搜索区大小）

​	（1）搜索区大小为8B

​				**压缩率：115.48%**

​	（2）搜索区大小改为16B后

​				压缩率：116.78%

​	（3）搜索区大小改为32B后

​				压缩率：120.00%

​	（4）搜索区大小为64B

​				压缩率：123.41%

​	（5）搜索区大小为128B

​				压缩率：125.97%

​	（6）将搜索区大小改为1024Byte后，程序运行时间大幅度上升，且压缩率反而上升了

​				压缩率：127.63%

---

## 系统结构

​	使用了策略者模式，实现的算法分别对应一个策略类。要更新算法只需要编写新的CompressStrategy即可。

​	**CompressInfo类**可以把压缩后所必须的描述信息写入文件。

​	**FileHelper类**可以将文件以字节流的形式读入内存。

​	**BmpFileHelper类**是将bmp文件文件头文件体分离读入内存。

​	**BitArrayList类**是最重要的工具类。作用是充当位数组，能够紧凑的写入数据。例如将22（10110）以5位的形式写入位数组中。

​	**ByteArrayConvertor类**的作用是字符数组和各类型数据之间的相互转换。如果是字符数组和整型的转换，采用的是大端的方式

​	**BmpCompressor类** 的综合使用了上面的类，只需要设置压缩策略，输入文件名即可进行压缩。BmpCompressor只会对像素信息进行压缩，在**压缩前还会对像素信息的排列进行处理，采用上面的S型排列**

​	**FileCompressor类** 同样综合使用上面类，以字节形式读取整个文件，然后对所有字节信息进行压缩。

<img src=".\README.assets\系统结构.png" alt="系统结构" style="zoom:200%;" />

一下是一些简单的使用例子

```java
public class Compress {
    public static void main(String[] args) throws ByteArrayException, BitArrayListOutOfBoundException, CompressStrategyNotInitException, ParaIllegalException, IOException, WrongFileOffsetException {
        String fileName = "src/main/resources/lena512.bmp"; // 要压缩的文件
        String compressFileName = "src/main/resources/lena512(lz77)"; // 压缩后的文件名
        String decompressFileName = "src/main/resources/lena512(lz77).bmp";  // 解压后的文件名

        // 使用BmpCompressor类进行压缩, 该压缩器会见bmp文件的文件头和像素信息分开读取，
        // 只对像素信息进行压缩，而且在压缩前会将像素排列方式修改成S型排列。
        BmpCompressor bmpCompressor =new BmpCompressor();  
        
        //（1）差分 + LZ77CompressStrategy
        // DifferenceCompressStrategy strategy = new DifferenceCompressStrategy();  
        // strategy.setCompressStrategy(new LZ77CompressStrategy());
        
        //（2）差分 + 哈夫曼编码
        DifferenceCompressStrategy strategy = new DifferenceDPCompressStrategy();  
        strategy.setCompressStrategy(new HuffmanCompressStrategy());
        
        //（3）课本动态规划
        // DPCompressStrategy1 strategy = new DPCompressStrategy1();
        
        //（4）改进动态规划
        // DPCompressStrategy2 strategy = new DPCompressStrategy2();
        
        //（5）哈夫曼编码
        // HuffmanCompressStrategy strategy = new HuffmanCompressStrategy();
        
        //（6）差分编码+动态规划1
        // DifferenceCompressStrategy strategy = new DifferenceCompressStrategy();
        // strategy.setCompressStrategy(new DPCompressStrategy1());
        
        //（7）差分编码+动态规划2
        // DifferenceCompressStrategy strategy = new DifferenceCompressStrategy();
        // strategy.setCompressStrategy(new DPCompressStrategy2());
        
        bmpCompressor.setCompressStrategy(strategy); // 设置策略

        bmpCompressor.compress(fileName, compressFileName);
        bmpCompressor.decompress(compressFileName, decompressFileName);

        
        // 下面是计算压缩率（压缩率=压缩后大小/压缩前大小）
        File src = new File(fileName);
        File compressFile = new File(compressFileName);
        File decompressFile = new File(decompressFileName);
        long srcLen = src.length();
        long compressLen = compressFile.length();
        long decompressLen = decompressFile.length();
        double compressRate = (double)compressLen / (double)srcLen;
        System.out.println("源文件长度：" + srcLen);
        System.out.println("压缩文件长度：" + compressLen);
        System.out.println("解压文件长度：" + decompressLen);
        System.out.println("压缩率：" + new DecimalFormat("0.00%").format(compressRate));
    }
}
```



## 已知陷阱

### (1)BitArrayList具体位长度未存入文件中

​	 该问题导致有时，压缩后会有少于一字节的多余信息写入文件中。该多余信息可能会在后面解压的时候导致解压数据多出一字节。

​	 在该项目中，只有在差分编码+哈夫曼压缩+S型像素读取中出现该问题。

​     解决方法：应想办法检测最后一个字节的正确性，例如另外将具体长度存入文件，或者将最后一个字节所使用的位数存入文件等。



## 展望

​	（1）压缩文件夹

​	（2）将前的一些基本文件信息如文件名、后缀名写入压缩文件，这样能够解压的时候自动得到解压文件名，不需要自己去写。
