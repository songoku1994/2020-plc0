package miniplc0java;

import miniplc0java.syn.Prototype;
import java.io.File;
import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        Prototype prototype = new Prototype(new File(args[0]), new File(args[1]));
    }
}