package il.cshaifasweng.OCSFMediatorExample.server;


import il.cshaifasweng.OCSFMediatorExample.entities.Turn;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.entities.Board;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;


public class SimpleServer extends AbstractServer {
    private String[][] board = new String[3][3];
    public String turn;
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();


    public SimpleServer(int port) {
        super(port);
        //Board Reset
//        resetBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                board[i][j] = "";
        }
        //Setting the first turn (random between X/O)
        turn = getRandomXO();
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        String msgString = msg.toString();
        if (msgString.startsWith("#warning")) {
            Warning warning = new Warning("Warning from server!");
            try {
                client.sendToClient(warning);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (msgString.startsWith("add client")) {
            SubscribedClient connection = new SubscribedClient(client);
            SubscribersList.add(connection);
            try {
//                client.sendToClient("client added successfully");
                if (getNumberOfClients() < 3) {
                    if (getNumberOfClients() > 1) {
                        //The second player joined, we can start the game!
                        //Giving him the reverse rule of the first player

                        String choosenString = Objects.equals(turn, "X") ? "O" : "X";
                        client.sendToClient("Assign: " + choosenString);

                        //Sending the current turn (the first player) - the eventbus is not subscribed yet, so we will use this at the first time
                        sendToAllClients("Turn: " + turn);
                        //Start the game
                        sendToAllClients("Two Players - Start!");
                    } else {
                        //First player just joined
                        String choosenString = turn;
                        client.sendToClient("Assign: " + choosenString);
                    }
                } else {
                    //A third or more players tried to join, they will be stuck on the loading screen, but we should send an warning
                    Warning warning = new Warning("We already have 2 players!");
                    try {
                        client.sendToClient(warning);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else if (msgString.startsWith("SelectID")) {
            String selectedID = msgString.split("SelectID:")[1].trim();
            int buttonNum = Integer.parseInt(selectedID);
            int i = (buttonNum - 1) / 3;
            int j = (buttonNum - 1) % 3;
            /**
             * The board button location of values will be like this: , we convert it for i and j
             * | 1 | 2 | 3|
             * | 4 | 5 | 6|
             * | 7 | 8 | 9|
             */

            if (!isEmptyCell(i, j)) {
                Warning warning = new Warning("This cell is taken! choose a different cell");
                try {
                    client.sendToClient(warning);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                board[i][j] = turn;
                if(isWin()){
                    //We got a winner after this turn, meaning whoever was in this turn, won
                    Turn turnUpdate = new Turn("END-"+turn);
                    sendToAllClients(turnUpdate);
                }
                else if (isFullBoard()) {
                    //We got a draw after this turn, so we finish
                    Turn turnUpdate = new Turn("END-DRAW");
                    sendToAllClients(turnUpdate);
                }else{
                    //Sending the updated board
                    Board boardUpdated = new Board();
                    boardUpdated.setBoard(board);
                    sendToAllClients(boardUpdated);
                    //Sending an updated turn (switching the sign).
                    String choosenString = Objects.equals(turn, "X") ? "O" : "X";
                    turn = choosenString;
                    Turn turnUpdate = new Turn(choosenString);
                    sendToAllClients(turnUpdate);
                }

            }


        } else if (msgString.startsWith("remove client")) {
            if (!SubscribersList.isEmpty()) {
                for (SubscribedClient subscribedClient : SubscribersList) {
                    if (subscribedClient.getClient().equals(client)) {
                        SubscribersList.remove(subscribedClient);
                        break;
                    }
                }
            }
        }

    }

    public void sendToAllClients(String message) {
        try {
            for (SubscribedClient subscribedClient : SubscribersList) {
                subscribedClient.getClient().sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    /**
     * Will assign a random String, either X or O
     *
     * @return the random symbol.
     */
    public static String getRandomXO() {
        Random random = new Random();
        return random.nextBoolean() ? "X" : "O"; // Randomly returns "X" or "O"
    }

    /**
     * This function will return if the board is false or not, based if any cell is empty or not, if there is an empty cell, it will return false, else true
     *
     * @return true/false based if the board is full or not
     */
    public boolean isFullBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (isEmptyCell(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isWin() {
        // Check rows, columns, and diagonals for a win
        for (int i = 0; i < 3; i++) {
            if ((!Objects.equals(board[i][0], "") && Objects.equals(board[i][0], board[i][1]) && Objects.equals(board[i][1], board[i][2])) ||
                    (!Objects.equals(board[0][i], "") && Objects.equals(board[0][i], board[1][i]) && Objects.equals(board[0][i], board[2][i]))) {
                return true;
            }
        }
        return (!Objects.equals(board[0][0], "") && Objects.equals(board[0][0], board[1][1]) && Objects.equals(board[0][0], board[2][2])) ||
                (!Objects.equals(board[0][2], "") && board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0]));
    }

    /**
     * Checks if the cell is empty in the board (not assigned (-1)), and return true or false based on that
     *
     * @param i - the i index of the cell
     * @param j - the j index of the cell
     * @return if the cell is empty or not
     */
    public boolean isEmptyCell(int i, int j) {
        return board[i][j].isEmpty();
    }
}
