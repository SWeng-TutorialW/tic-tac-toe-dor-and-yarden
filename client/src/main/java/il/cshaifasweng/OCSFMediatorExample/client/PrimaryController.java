package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.Board;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PrimaryController {
	public static String message="";

	/**
	 * The board button values will be like this:
	 * | 1 | 2 | 3|
	 * | 4 | 5 | 6|
	 * | 7 | 8 | 9|
	 */
	@FXML
	private Button button1;

	@FXML
	private Button button2;

	@FXML
	private Button button3;

	@FXML
	private Button button4;

	@FXML
	private Button button5;

	@FXML
	private Button button6;

	@FXML
	private Button button7;

	@FXML
	private Button button8;

	@FXML
	private Button button9;

	@FXML
	private Label signLabel;

	@FXML
	private Label turnLabel;

	@FXML
	void cellSelect(ActionEvent event) {
		Button ClickedButton = (Button) event.getSource();
		String ButtonId= ClickedButton.getId().split("button")[1]; //If button is named button1, the value will be 1. (note this is 3 button for each row)
		if(turnLabel.getText().strip().equals(signLabel.getText().strip())){
			//Your turn
            try {
                SimpleClient.getClient().sendToServer("SelectID: "+ButtonId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
			//Not your turn - will show na alert
			Alert alert = new Alert(Alert.AlertType.INFORMATION, String.format("Message: %s\n", "Not your turn Please wait for your turn!"));
			alert.show();
		}
	}


//    @FXML
//    void sendWarning(ActionEvent event) {
//    	try {
//			SimpleClient.getClient().sendToServer("#warning");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }

	@Subscribe
	public void onTurnEvent(TurnEvent event) {

		Platform.runLater(() -> {
			String turn = event.getTurn().getTurn();
			if(turn.equals("END-DRAW")){
				message="Game Over, we got a draw";
				try {
					App.setRoot("finish");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}else if(turn.startsWith("END-")){
				//Someone won and its not a draw
				String winner = turn.split("END-")[1];
				message="The Winner is: " + winner;
				try {
					App.setRoot("finish");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}else{
				turnLabel.setText(turn);
			}
		});
	}


    @Subscribe
	public void onBoardEvent(BoardEvent event) {
		Platform.runLater(() -> {
			String[][] board = event.getBoard().getBoard();
			button1.setText(board[0][0] == null ? "" : board[0][0]);
			button2.setText(board[0][1] == null ? "" : board[0][1]);
			button3.setText(board[0][2] == null ? "" : board[0][2]);
			button4.setText(board[1][0] == null ? "" : board[1][0]);
			button5.setText(board[1][1] == null ? "" : board[1][1]);
			button6.setText(board[1][2] == null ? "" : board[1][2]);
			button7.setText(board[2][0] == null ? "" : board[2][0]);
			button8.setText(board[2][1] == null ? "" : board[2][1]);
			button9.setText(board[2][2] == null ? "" : board[2][2]);
		});
	}

	@FXML
	void initialize(){
		EventBus.getDefault().register(this);
		signLabel.setText(SimpleClient.sign);
		turnLabel.setText(SimpleClient.firstSign);
	}
}
