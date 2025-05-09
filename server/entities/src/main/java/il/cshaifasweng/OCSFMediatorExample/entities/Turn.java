package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class Turn implements Serializable {


    private static final long serialVersionUID = -8224097662914849956L;

    private String turn="";

    public Turn(String turn) {
        this.turn = turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getTurn() {
        return turn;
    }
}
