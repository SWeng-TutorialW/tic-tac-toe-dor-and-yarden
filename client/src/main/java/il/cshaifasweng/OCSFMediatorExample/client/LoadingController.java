package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import javafx.fxml.FXML;

public class LoadingController {


    @FXML
    void initialize(){
        try {
            SimpleClient.getClient().sendToServer("add client");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


