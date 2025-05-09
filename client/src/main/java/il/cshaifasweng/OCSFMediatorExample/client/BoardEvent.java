package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Board;

public class BoardEvent {
    private Board board;

    public Board getBoard() {
        return board;
    }

    public BoardEvent(Board board) {
        this.board = board;
    }
}


