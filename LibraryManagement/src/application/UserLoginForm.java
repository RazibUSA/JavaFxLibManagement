package application;

import javax.swing.JOptionPane;

import business.LoginException;
import business.SystemController;
import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.*;

public class UserLoginForm extends Application {

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.initStyle(StageStyle.UNDECORATED);
		GridPane grid = new GridPane();

		grid.setAlignment(Pos.CENTER);
		grid.setHgap(5);
		grid.setVgap(10);
		grid.setPadding(new Insets(25,5,25,25));//top, right, bottom, left
		

		
		Label lblogo = new Label("");
		lblogo.setMaxSize(160,87);
		lblogo.setId("Logo");
		grid.add(lblogo, 0, 0, 2,10);//node, col, row, colspan, rowspan
		
		Text sceneTitle = new Text("      Welcome at MUM Library");
		
		sceneTitle.setFont(Font.font("Tahoma",FontWeight.NORMAL,16) );
		grid.add(sceneTitle, 0, 12, 2,1);//node, col, row, colspan, rowspan
		
		Label lblUserName = new Label("User Name: ");
		lblUserName.setAlignment(Pos.CENTER_RIGHT);
		grid.add(lblUserName, 0,14);
		
		TextField txtUserName = new TextField();
		txtUserName.setPromptText("Enter User Name");
		grid.add(txtUserName, 1, 14);
		
		Label lblPassWord = new Label("   Password: ");
		lblPassWord.setAlignment(Pos.CENTER_RIGHT);
		grid.add(lblPassWord, 0, 16);
		
		PasswordField  txtPassword = new PasswordField ();
		txtPassword.setPromptText("Enter Password");
		grid.add(txtPassword,1,16);
		
		Button btnSignIn = new Button("Sign In");	
		Button btnCancel= new Button("Cancel");	
		HBox hbButn = new HBox(10);//FlowLayOut
		hbButn.setAlignment(Pos.BOTTOM_CENTER);
		hbButn.getChildren().addAll(btnSignIn,btnCancel);
		grid.add(hbButn, 1, 18,1,1);
		
	
		final Text copyright = new Text("Copyright©2015 reserved by AKM.Ujjal.SM ");
		copyright.setFont(Font.font("Tahoma",FontWeight.NORMAL,9) );
		copyright.setTextAlignment(TextAlignment.RIGHT);  
		grid.add(copyright,1,20,2,1);
		  
		  
		btnSignIn.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
	           	SystemController sysCtl= new SystemController();
            	try{
            	sysCtl.login(txtUserName.getText(), txtPassword.getText());
            //	System.out.println(SystemController.currentAuth);
            	}catch(LoginException lgE){
            		JOptionPane.showMessageDialog(null,lgE.getMessage(), "Login Failed",
    				        JOptionPane.ERROR_MESSAGE);
            		
            		}
            	OperationalWindow op= new OperationalWindow(SystemController.currentAuth);
            	try{
            	 Stage st= new Stage();
            	 op.start(st);
            	}catch(Exception e){
            		System.out.println(e);
            	}
            	((Node)(arg0.getSource())).getScene().getWindow().hide();
 			}
			
		});
	
		
		btnCancel.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				Platform.exit();
				System.exit(0);
				
			}
		});
		
		Scene scene = new Scene(grid, 400, 320);
		primaryStage.setScene(scene);
		scene.getStylesheets().add(getClass().getResource("Login1.css").toExternalForm());
		primaryStage.show();
		
	}

}
