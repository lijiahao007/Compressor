import java.util.LinkedList;
import java.util.Random;

public class Application {

    public static void main(String[] args) {
        LinkedList<Byte> linkedList = new LinkedList<>();
        for (int i = 0; i < 128; i++) {
            linkedList.offer((byte) i);
            linkedList.offer((byte) (i + 1));
            linkedList.remove(0);

        }



        // TODO: 2021/6/4  在这里添加编写和UI相关的应用程序




    }

}
