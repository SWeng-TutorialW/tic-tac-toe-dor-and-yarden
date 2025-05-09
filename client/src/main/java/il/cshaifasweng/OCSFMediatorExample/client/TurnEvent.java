package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Turn;
public class TurnEvent {
    private Turn turn;

    public Turn getTurn() {
        return turn;
    }

    public TurnEvent(Turn turn) {
        this.turn = turn;
    }
}
