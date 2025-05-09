package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.entities.Board;
import il.cshaifasweng.OCSFMediatorExample.entities.Turn;

import java.io.IOException;

public class SimpleClient extends AbstractClient {

    public static SimpleClient client = null;
    public static int port = 3000;
    public static String ip = "localhost";
    public static String sign;
    public static String firstSign;

    private SimpleClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {

        if (msg.getClass().equals(Turn.class)) {
            EventBus.getDefault().post(new TurnEvent((Turn) msg));
        } else if (msg.getClass().equals(Board.class)) {
            EventBus.getDefault().post(new BoardEvent((Board) msg));
        } else if (msg.getClass().equals(Warning.class)) {
            EventBus.getDefault().post(new WarningEvent((Warning) msg));
        } else {
            String message = msg.toString();
            if (message.startsWith("Turn:")) {
                //For first time, since we are not subscribed yet with the event bus
                firstSign = message.split("Turn:")[1];
            } else if (message.equals("Two Players - Start!")) {
                Platform.runLater(() -> {
                    try {
                        App.setRoot("board");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else if (message.startsWith("Assign:")) {
                sign = message.split("Assign:")[1];
            }
        }
    }

    public static SimpleClient getClient() {
        if (client == null) {
            client = new SimpleClient(ip, port);
        }
        return client;
    }

}
