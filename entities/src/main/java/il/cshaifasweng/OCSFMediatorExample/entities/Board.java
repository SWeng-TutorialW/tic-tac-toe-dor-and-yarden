package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class Board implements Serializable {

    private static final long serialVersionUID = -8224097662914849956L;

    private String[][] board= new String [3][3];

    public Board() {
        this.board = new String[3][3];
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }

    public String getValue(int i, int j) {
        return board[i][j];
    }

    public String[][] getBoard() {
        return board;
    }
}
