import Tool.ByteArrayConvertor;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.LinkedList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class Application {

    public static void main(String[] args) {
        LinkedList<Byte> linkedList = new LinkedList<>();
        for (int i = 0; i < 128; i++) {
            linkedList.offer((byte) i);
            linkedList.offer((byte) (i + 1));
            linkedList.remove(0);

        }
    }
    @Test
    void run () {

    }
}
