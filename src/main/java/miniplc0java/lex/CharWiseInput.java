package miniplc0java.lex;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CharWiseInput {

    private Coords coords = new Coords();
    private FileReader fileReader;
    private char thisChar, nextChar;

    public CharWiseInput(File file) throws IOException {
        fileReader = new FileReader(file);
        nextChar = (char) fileReader.read();
        System.out.print(nextChar);
    }

    public char peekChar() {
        return nextChar;
    }

    public char getChar() {
        return thisChar;
    }

    public char nextChar() throws IOException {
        if (thisChar == '\n') {
            coords.nextRow();
        } else {
            coords.nextCol();
        }
        thisChar = nextChar;
        nextChar = (char) fileReader.read();
        System.out.print(nextChar);
        return thisChar;
    }

    public char expectChar(char a) throws IOException {
        if (!ifNextChar(a)) {
            throw new Error("expect \'" + a + "\'at: row" + coords.getRow() + " col" + coords.getCol());
        }
        return thisChar;
    }

    public char expectChar(char a, char b) throws IOException {
        if (!ifNextChar(a, b)) {
            throw new Error(
                "expect [" + a + "-" + b + " at: row" + coords.getRow() + " col" + coords.getCol());
        }
        return thisChar;
    }

    public boolean ifNextChar(char a) throws IOException {
        if (nextChar == a) {
            nextChar();
            return true;
        } else {
            return false;
        }
    }

    public boolean ifNextChar(char a, char b) throws IOException {
        if (a <= nextChar && nextChar <= b) {
            nextChar();
            return true;
        } else {
            return false;
        }
    }

    public Coords getCoords() {
        return coords;
    }
}
