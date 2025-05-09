package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;

public class FinishController {

    @FXML
    private Label messageLabel;

    @FXML
    void initialize(){
        messageLabel.setText(PrimaryController.message);
    }
}
