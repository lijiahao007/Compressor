package basic;

import CompressStrategies.imp.LZ77CompressStrategy;
import HuffmanTree.Node;

import java.util.Iterator;
import java.util.LinkedList;


public class ObjectTest {

    public static void main(String[] args){
        LinkedList<Byte> linkedList1 = new LinkedList<>();
        linkedList1.offer((byte) 1);
        linkedList1.offer((byte) 2);
        linkedList1.offer((byte) 3);

        Iterator<Byte> iterator = linkedList1.listIterator(0);

        for (int i = 0; i < 3; i++) {
            linkedList1.offer(iterator.next());
        }

        iterator = linkedList1.listIterator(0);
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        System.out.println();
    }


}
