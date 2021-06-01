package ToolTest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Phone {
    int number;
}

abstract class Person  {
    Phone phone;
    public Person() {
        phone = new Phone();
    }
}

class Student extends Person {
    public Student(int num) {
        this.phone.setNumber(num);
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }
    public Phone getPhone() {
        return this.phone;
    }
}


public class JavaBaseTest {
    public static void main(String[] args) {
        Student student = new Student(12345);
        student.getPhone().setNumber(23456);
        System.out.println(student.getPhone().getNumber());
    }
}
