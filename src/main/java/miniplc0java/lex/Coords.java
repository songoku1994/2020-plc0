package miniplc0java.lex;

public class Coords {

    private int row = 0, col = 0;

    public Coords() {
    }

    public Coords(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void nextCol() {
        col = col + 1;
    }

    public void nextRow() {
        row = row + 1;
        col = 0;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
