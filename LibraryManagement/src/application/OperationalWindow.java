package application;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import business.Address;
import business.Author;
import business.Book;
import business.BookCopy;
import business.CheckoutRecord;
import business.CheckoutRecordEntry;
import business.LibraryMember;
import business.LibrarySystemException;
import business.LoginException;
import business.MemberDisplay;
import business.RuleException;
import business.SystemController;
import dataaccess.Auth;
import dataaccess.DataAccess;
import dataaccess.DataAccessFacade;
import dataaccess.TestData;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Callback;


@SuppressWarnings("rawtypes")
public class OperationalWindow extends Application {
	private Auth auth;
	private BorderPane border;
	private GridPane grid;
	private String Ref;

	//Table View declaration
	TableView table;
	TableView table_Auth;
	TableView table_Book;
	TableView table_BookCopy;
	TableView table_Book_chekout;
	TableView table_MemberCheckout;
	TableView table_Checkout;
	TableView table_Overdue;

	OperationalWindow(Auth auth) {
		this.auth=auth;
	}

	public static void main(String[] args)
	{
		launch(args);
	}

	@SuppressWarnings("static-access")
	@Override
	public void start(Stage primaryStage) throws Exception {
		//TestData.main(null);
		primaryStage.setTitle("MUM Library Mangement System");
		border = new BorderPane();
		HBox hbox = addHBox();

		border.setAlignment(hbox, Pos.CENTER_RIGHT);
		border.setTop(hbox);
		//border.setStyle("-fx-border-color: blue");
		switch(auth){
		case LIBRARIAN:
			border.setLeft(addVBox("1"));//Menus for Librarian
			break;
		case ADMIN:
			border.setLeft(addVBox("2"));//Menus for Administrator	
			break;
		case BOTH:
			border.setLeft(addVBox("3"));//Menus for Both
			break;

		}

		Scene scene = new Scene(border, 900, 850);
		primaryStage.setScene(scene);
		scene.getStylesheets().add(getClass().getResource("MainUIStyle.css").toExternalForm());
		primaryStage.show();
	}

	private HBox addHBox() {

		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER_RIGHT);
		hbox.setPadding(new Insets(5, 15, 5, 5));
		hbox.setSpacing(10);   // Gap between nodes
		hbox.setStyle("-fx-background-color:  #800080; ");

		final ImageView imv = new ImageView();
		final Image image2 = new Image(OperationalWindow.class.getResourceAsStream("background.jpg"));
		imv.setImage(image2);

		final HBox pictureRegion = new HBox();
		pictureRegion.getChildren().add(imv);
		hbox.getChildren().addAll(pictureRegion);

		return hbox;
	}

	private VBox addVBox(String role) {
		String roleName="";
		if(role == "1")
		{
			roleName="         Librarian        ";
		}
		else if(role == "2")
		{
			roleName="       Administrator      ";
		}
		else if(role == "3")
		{
			roleName="Administrator & Librarian";
		}

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10)); 
		vbox.setSpacing(8);
		vbox.setStyle("-fx-background-color:  #D0D0D0; ");

		Text title = new Text(roleName.toString());
		title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		vbox.getChildren().add(title);

		ArrayList<Hyperlink> options = new ArrayList<Hyperlink>();
		Hyperlink hpExit = new Hyperlink("logout                                ");
		if(role == "1")
		{
			librarianMenu(  options);     
			options.add(hpExit);
		}
		else if(role == "2")
		{
			administrationMenu( options);
			options.add(hpExit);
		}
		else if(role == "3")
		{
			librarianMenu(  options);
			administrationMenu( options);
			options.add(hpExit);
		}
		hpExit.setOnAction(evt->{
			UserLoginForm uf= new UserLoginForm();
			try{
				Stage st= new Stage();
				uf.start(st);
			}catch(Exception e){
				System.out.println(e);
			}
			((Node)(evt.getSource())).getScene().getWindow().hide();

		});

		for (int i=0; i<options.size(); i++) {
			VBox.setMargin(options.get(i), new Insets(0, 0, 0, 8));
			vbox.getChildren().add(options.get(i));
		}

		return vbox;
	}

	public void librarianMenu( ArrayList<Hyperlink> options)
	{
		Hyperlink hp1=new Hyperlink("Checkout Book                  ");
		options.add(hp1);
		hp1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				border.setCenter( addBookCheckoutGridPane());
			}
		});

		Hyperlink hp2=new Hyperlink("Checkout Record               ");
		options.add(hp2);
		hp2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				border.setCenter( addBookCheckoutRecordGridPane());
			}
		});

		Hyperlink hp3=new Hyperlink("Overdue Search                 ");
		options.add(hp3);
		hp3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				border.setCenter( overDueGridPane());
			}
		});

	}



	public void administrationMenu( ArrayList<Hyperlink> options)
	{

		Hyperlink hp1=new Hyperlink("Book                                  ");
		options.add(hp1);	
		hp1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				border.setCenter(addBookGridPane());
			}
		});


		Hyperlink hp2=new Hyperlink("Library Member                 ");
		options.add(hp2);	
		hp2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				border.setCenter( addMemberGridPane());
			}
		});

		Hyperlink hp3= new Hyperlink("Add Book copy                  ");
		options.add(hp3); 
		hp3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				border.setCenter( addBookCopyGridPane());
			}
		});
	}

	/*************Member Grid********************/

	@SuppressWarnings("unchecked")
	private GridPane addMemberGridPane() {

		grid = new GridPane();
		//grid.setAlignment(Pos.CENTER);
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setPadding(new Insets(10, 10, 10, 10));

		Text sceneTitle = new Text("Member Entry Form...");
		sceneTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneTitle, 0, 1,1,2); 

		Label lblMemberNum = new Label("Member Number:");	       
		grid.add(lblMemberNum, 0, 4); 	        


		TextField txtMemberNum = new TextField();	      
		grid.add(txtMemberNum, 1, 4);

		Label lblfirstName = new Label("First Name:");	       
		grid.add(lblfirstName, 0, 5); 	        


		TextField txtfirstName = new TextField();	      
		grid.add(txtfirstName, 1, 5);

		Label lbllastName = new Label("Last Name:");	       
		grid.add(lbllastName, 0, 6); 	        


		TextField txtlasttName = new TextField();	      
		grid.add(txtlasttName, 1, 6);

		Text sceneAddress = new Text("Address :: ");
		sceneAddress.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneAddress, 0, 8,2,1); 

		Label lblstreet = new Label("Street :");	       
		grid.add(lblstreet, 0, 11); 	        


		TextField txtstreet = new TextField();	      
		grid.add(txtstreet, 1, 11);

		Label lblcity = new Label("City :");	       
		grid.add(lblcity, 0, 12); 	        


		TextField txtcity = new TextField();	      
		grid.add(txtcity, 1, 12);

		Label lblstate= new Label("State :");	       
		grid.add(lblstate, 0, 13); 	        


		TextField txtState = new TextField();	      
		grid.add(txtState, 1, 13);

		Label lblzip = new Label("Zip :");	       
		grid.add(lblzip, 0, 14); 	        


		TextField txtzip = new TextField();	      
		grid.add(txtzip, 1, 14);

		Label lblphoneNum = new Label("Phone Number :");	       
		grid.add(lblphoneNum, 0, 15); 	        


		TextField txtphoneNum = new TextField();	      
		grid.add(txtphoneNum, 1, 15);

		Button btnSave = new Button("Save");
		Button btnUpdate= new Button("Update");
		Button btnDelete=new Button("Delete");
		btnUpdate.setDisable(true);
		btnDelete.setDisable(true);
		Button btnReset= new Button("Reset");
		HBox hbButn = new HBox(10);//FlowLayOut
		hbButn.setAlignment(Pos.BOTTOM_CENTER);
		hbButn.getChildren().addAll(btnSave,btnUpdate,btnDelete,btnReset);
		grid.add(hbButn, 1, 19,2,1);

		btnSave.setOnAction(evt->{

			SystemController sysCtl1=new SystemController();
			try{
				sysCtl1.memberCheckValidity(txtMemberNum.getText(), txtfirstName.getText(), txtlasttName.getText(), txtphoneNum.getText());
				sysCtl1.addressCheckValidity(txtstreet.getText(), txtcity.getText(),txtState.getText(), txtzip.getText());
				Address addr=new Address(txtstreet.getText(), txtcity.getText(),txtState.getText(), txtzip.getText());
				sysCtl1.addNewMember(txtMemberNum.getText(), txtfirstName.getText(), txtlasttName.getText(), txtphoneNum.getText(), addr);
				clearMemnerGrid();
			}catch(LibrarySystemException e1){
				JOptionPane.showMessageDialog(null,e1.getMessage(), "Transaction Failed",
						JOptionPane.ERROR_MESSAGE);		
			}
			catch(RuleException e1){
				JOptionPane.showMessageDialog(null,e1.getMessage(), "Transaction Failed",
						JOptionPane.ERROR_MESSAGE);		
			}



			table= this.displayMember();
			final VBox vbox = new VBox(5);
			vbox.setPadding(new Insets(10,0,10,0));
			vbox.getChildren().add(table);
			grid.add(vbox, 0, 27,5,3);

			table.setRowFactory( tv -> {
				TableRow<MemberDisplay> row = new TableRow<>();
				row.setOnMouseClicked(event -> {
					if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
						MemberDisplay mem = row.getItem();                   
						txtMemberNum.setText(mem.getMemberId());
						txtfirstName.setText(mem.getFirstName());
						txtlasttName.setText(mem.getLastName());
						txtstreet.setText(mem.getStreet());
						txtcity.setText(mem.getCity());
						txtState.setText(mem.getState());
						txtzip.setText(mem.getZip());
						txtphoneNum.setText(mem.getTelephone());
						Ref=mem.getMemberId();
					}
					btnSave.setDisable(true);
					btnUpdate.setDisable(false);
					btnDelete.setDisable(false);
				});
				return row ;
			});
		});

		btnReset.setOnAction(evt->{
			clearMemnerGrid();
		});

		btnDelete.setOnAction(evt->{
			SystemController sysCtl=new SystemController();
			try{
				sysCtl.deletMember(txtMemberNum.getText());
			}catch(LibrarySystemException e1){
				JOptionPane.showMessageDialog(null,e1.getMessage(), "Failed Action",
						JOptionPane.ERROR_MESSAGE);		
			}
			clearMemnerGrid();
			border.setCenter(addMemberGridPane());

		});

		btnUpdate.setOnAction(etv->{
			SystemController sysCtl=new SystemController();
			try{
				sysCtl.memberCheckValidity(txtMemberNum.getText(), txtfirstName.getText(), txtlasttName.getText(), txtphoneNum.getText());
				sysCtl.addressCheckValidity(txtstreet.getText(), txtcity.getText(),txtState.getText(), txtzip.getText());
				Address addr=new Address(txtstreet.getText(), txtcity.getText(),txtState.getText(), txtzip.getText());
				LibraryMember libMem=sysCtl.searchMember(Ref);

				sysCtl.updateMemberInfo(txtMemberNum.getText(), txtfirstName.getText(), txtlasttName.getText(), txtphoneNum.getText(), addr, libMem);
				btnSave.setDisable(false);
				btnUpdate.setDisable(true);
				btnDelete.setDisable(true);
				clearMemnerGrid();
			}catch(LibrarySystemException e1){
				JOptionPane.showMessageDialog(null,e1.getMessage(), "Failed Action",
						JOptionPane.ERROR_MESSAGE);		
			}
			catch(RuleException e1){
				JOptionPane.showMessageDialog(null,e1.getMessage(), "Transaction Failed",
						JOptionPane.ERROR_MESSAGE);		
			}

			table= this.displayMember();
			final VBox vbox = new VBox(5);
			vbox.setPadding(new Insets(10,0,10,0));
			vbox.getChildren().add(table);
			grid.add(vbox, 0, 27,5,3);

			table.setRowFactory( tv -> {
				TableRow<MemberDisplay> row = new TableRow<>();
				row.setOnMouseClicked(event -> {
					if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
						MemberDisplay mem = row.getItem();                   
						txtMemberNum.setText(mem.getMemberId());
						txtfirstName.setText(mem.getFirstName());
						txtlasttName.setText(mem.getLastName());
						txtstreet.setText(mem.getStreet());
						txtcity.setText(mem.getCity());
						txtState.setText(mem.getState());
						txtzip.setText(mem.getZip());
						txtphoneNum.setText(mem.getTelephone());
						Ref=mem.getMemberId();
					}

					btnSave.setDisable(true);
					btnUpdate.setDisable(false);
					btnDelete.setDisable(false);

				});
				return row ;
			});

		});


		Text sceneSearch = new Text("Search Member ...");
		sceneSearch.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneSearch, 0, 23,1,2); 

		Label lblMemberNumSearch = new Label("Member Number:");	       
		grid.add(lblMemberNumSearch, 0, 25); 	        


		TextField txtMemberNumSearch = new TextField();	      
		grid.add(txtMemberNumSearch, 1, 25);

		Button btnSeaarch = new Button("Search");
		HBox hbox1 = new HBox(10);
		hbox1.setAlignment(Pos.BOTTOM_CENTER);
		hbox1.getChildren().add(btnSeaarch);
		grid.add(hbox1, 2, 25);

		btnSeaarch.setOnAction(evt->{
			SystemController sysCtl=new SystemController();
			LibraryMember lib= sysCtl.searchMember(txtMemberNumSearch.getText());
			if(lib==null){
				JOptionPane.showMessageDialog(null,"No member found", "Failed",
						JOptionPane.ERROR_MESSAGE);	
			}
			else{
				txtMemberNum.setText(lib.getMemberId());
				txtfirstName.setText(lib.getFirstName());
				txtlasttName.setText(lib.getLastName());
				txtstreet.setText(lib.getAddress().getStreet());
				txtcity.setText(lib.getAddress().getCity());
				txtState.setText(lib.getAddress().getState());
				txtzip.setText(lib.getAddress().getZip());
				txtphoneNum.setText(lib.getTelephone());
				btnUpdate.setDisable(false);
				btnDelete.setDisable(false);
				btnSave.setDisable(true);
				Ref= lib.getMemberId();
			}			
		});



		table=displayMember();

		final VBox vbox = new VBox(5);
		vbox.setPadding(new Insets(10,0,10,0));
		vbox.getChildren().add(table);
		grid.add(vbox, 0, 27,5,3);

		table.setRowFactory( tv -> {
			TableRow<MemberDisplay> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
					MemberDisplay mem = row.getItem();                   
					txtMemberNum.setText(mem.getMemberId());
					txtfirstName.setText(mem.getFirstName());
					txtlasttName.setText(mem.getLastName());
					txtstreet.setText(mem.getStreet());
					txtcity.setText(mem.getCity());
					txtState.setText(mem.getState());
					txtzip.setText(mem.getZip());
					txtphoneNum.setText(mem.getTelephone());
					Ref=mem.getMemberId();
				}
				btnSave.setDisable(true);
				btnUpdate.setDisable(false);
				btnDelete.setDisable(false);

			});
			return row ;
		});

		return grid;
	}

	/***************************Book Grid**********************/

	@SuppressWarnings("unchecked")
	private GridPane addBookGridPane() {

		grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setPadding(new Insets(10, 10, 10, 10));

		Text sceneTitle = new Text("Book Entry Form...");
		sceneTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneTitle, 0, 1,1,2); 

		Label lblISBNNum = new Label("ISBN Number:");	       
		grid.add(lblISBNNum, 0, 4); 	        

		TextField txtISBNNum = new TextField();	      
		grid.add(txtISBNNum, 1, 4);

		Label lbltitle = new Label("Title:");	       
		grid.add(lbltitle, 0, 5); 	        

		TextField txttitle = new TextField();	      
		grid.add(txttitle, 1, 5);

		Label lbldayLength = new Label("Max Day Length :");	       
		grid.add(lbldayLength, 0, 6); 	        


		ComboBox cmbMaxdayLength = new ComboBox<>();
		cmbMaxdayLength.setMaxWidth(180);
		cmbMaxdayLength.getItems().addAll("7","21");
		cmbMaxdayLength.setValue("7");
		grid.add(cmbMaxdayLength, 1, 6);

		Text sceneAuthors = new Text("List of Authors :: ");
		sceneAuthors.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneAuthors, 0, 7,2,1); 


		Label lblfirstName = new Label("First Name:");	       
		grid.add(lblfirstName, 1, 8); 	        


		TextField txtfirstName = new TextField();	      
		grid.add(txtfirstName, 2, 8);

		Label lbllastName = new Label("Last Name:");	       
		grid.add(lbllastName, 1, 9); 	        


		TextField txtlasttName = new TextField();	      
		grid.add(txtlasttName, 2, 9);

		Text sceneAddress = new Text("Address :: ");
		sceneAddress.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneAddress, 1, 10,1,1); 

		Label lblstreet = new Label("Street :");	       
		grid.add(lblstreet, 1, 11); 	        

		TextField txtstreet = new TextField();	      
		grid.add(txtstreet, 2, 11);

		Label lblcity = new Label("City :");	       
		grid.add(lblcity, 1, 12); 	        


		TextField txtcity = new TextField();	      
		grid.add(txtcity, 2, 12);

		Label lblstate= new Label("State :");	       
		grid.add(lblstate, 1, 13); 	        

		TextField txtState = new TextField();	      
		grid.add(txtState, 2, 13);

		Label lblzip = new Label("Zip :");	       
		grid.add(lblzip, 1, 14); 	        


		TextField txtzip = new TextField();	      
		grid.add(txtzip, 2, 14);

		Label lblphoneNum = new Label("Phone Number :");	       
		grid.add(lblphoneNum, 1, 15); 	        


		TextField txtphoneNum = new TextField();	      
		grid.add(txtphoneNum, 2, 15);

		Label lblcredentials = new Label("Credential :");	       
		grid.add(lblcredentials, 1, 16); 	        


		TextField txtcredentials = new TextField();	      
		grid.add(txtcredentials, 2, 16);

		Label lblbio = new Label("Bibliography :");	       
		grid.add(lblbio, 1, 17); 	        


		TextField txtbio = new TextField();	      
		grid.add(txtbio, 2, 17);

		Button btnNext = new Button("Add Author");
		HBox hbButn1 = new HBox(10);//FlowLayOut
		hbButn1.setAlignment(Pos.BOTTOM_CENTER);
		hbButn1.getChildren().add(btnNext);
		grid.add(hbButn1, 3, 17);

		List<Author> authors= new ArrayList<Author>();

		table_Auth= new TableView();


		btnNext.setOnAction(evt->{
			SystemController sysCtl=new SystemController();
			try{
				sysCtl.addressCheckValidity(txtstreet.getText(), txtcity.getText(),txtState.getText(), txtzip.getText());
				sysCtl.authorCheckValidity(txtfirstName.getText(), txtlasttName.getText(), txttitle.getText(), txtcredentials.getText(), txtbio.getText());
				
				Address addr=new Address(txtstreet.getText(), txtcity.getText(),txtState.getText(), txtzip.getText());
				authors.add(new Author(txtfirstName.getText(), txtlasttName.getText(), txttitle.getText(), addr,txtcredentials.getText(), txtbio.getText()));
			
				table_Auth = bindAuthorDetails(authors,0);
				txtfirstName.setText("");
				txtlasttName.setText("");
				txtState.setText("");
				txtstreet.setText("");
				txtcredentials.setText("");
				txtzip.setText("");
				txtcity.setText("");
				txtphoneNum.setText("");
				txtbio.setText("");

				grid.add(table_Auth, 1, 18,3,1);

			}catch(RuleException e1){
				JOptionPane.showMessageDialog(null,e1.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}catch(Exception e1){
				System.out.println(e1);

			}
		});



		Button btnSave = new Button("Save");	
		Button btnUpdate= new Button("Update");
		Button btnDelete= new Button("Delete");
		Button btnReset= new Button("Reset");
		HBox hbButn = new HBox(10);//FlowLayOut
		hbButn.setAlignment(Pos.BOTTOM_CENTER);
		hbButn.getChildren().addAll(btnSave,btnDelete,btnReset);
		grid.add(hbButn, 1, 21);


		btnSave.setOnAction(evt->{
			try{
				SystemController sysCtl=new SystemController();
				sysCtl.bookCheckValidity(txtISBNNum.getText(), txttitle.getText());
				sysCtl.addBook(txtISBNNum.getText(), txttitle.getText(),Integer.parseInt(cmbMaxdayLength.getValue().toString()), authors);
				border.setCenter( addBookGridPane());

			}catch(LibrarySystemException e1){
				JOptionPane.showMessageDialog(null,e1.getMessage(), "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);
			}catch(RuleException e1){
				JOptionPane.showMessageDialog(null,e1.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}catch(Exception e2){
				JOptionPane.showMessageDialog(null,e2.getMessage(), "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);
				border.setCenter( addBookGridPane());
			}

		});

		btnDelete.setOnAction(evt->{
			try{
				SystemController sysCtl=new SystemController();
				sysCtl.deletBook(txtISBNNum.getText());

				List<Book> allBook = sysCtl.getAllBookList();
				table_Book = bindBookList(allBook);
				grid.add(table_Book, 0, 27,5,1);
				border.setCenter( addBookGridPane());
			}catch(LibrarySystemException e1){
				JOptionPane.showMessageDialog(null,e1.getMessage(), "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);
			}catch(Exception e2){
				JOptionPane.showMessageDialog(null,e2.getMessage(), "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);
				border.setCenter( addBookGridPane());
			}
		});

		btnReset.setOnAction(evt->{
			border.setCenter( addBookGridPane());

		});


		Text sceneSearch = new Text("Search Book ...");
		sceneSearch.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneSearch, 0, 23,1,1); 

		Label lblBookSearch = new Label("Book ISBN:");	       
		grid.add(lblBookSearch, 0, 25); 	        


		TextField txtBookSearch = new TextField();	      
		grid.add(txtBookSearch, 1, 25);

		Button btnSeaarch = new Button("Search");
		HBox hbox1 = new HBox(5);
		hbox1.setAlignment(Pos.CENTER_LEFT);
		hbox1.getChildren().add(btnSeaarch);
		grid.add(hbox1, 2, 25);

		btnSeaarch.setOnAction(evt->{
			SystemController sysCtl=new SystemController();
			Book book= sysCtl.searchBook(txtBookSearch.getText());
			if(book==null){
				JOptionPane.showMessageDialog(null,"No Book found", "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);	
			}
			else{
				txtISBNNum.setText(book.getIsbn());
				txttitle.setText(book.getTitle());
				cmbMaxdayLength.setValue(book.getMaxCheckoutLength()+"");
				txtfirstName.setText(book.getAuthors().get(0).getFirstName());
				txtlasttName.setText(book.getAuthors().get(0).getLastName());
				txtstreet.setText(book.getAuthors().get(0).getAddress().getStreet());
				txtState.setText(book.getAuthors().get(0).getAddress().getState());
				txtcity.setText(book.getAuthors().get(0).getAddress().getCity());
				txtzip.setText(book.getAuthors().get(0).getAddress().getZip());
				txtcredentials.setText(book.getAuthors().get(0).getCredential());
				txtbio.setText(book.getAuthors().get(0).getBio());
				txtphoneNum.setText(book.getAuthors().get(0).getTelephone());

			}
			btnSave.setDisable(true);
			btnNext.setDisable(true);
			btnUpdate.setDisable(false);
			btnDelete.setDisable(false);

		});


		SystemController sysCtl=new SystemController();
		try{
			List<Book> allBook1 = sysCtl.getAllBookList();
			table_Book= bindBookList(allBook1);
			table_Book.setEditable(true);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,e.getMessage(), "Failed Transaction",
					JOptionPane.ERROR_MESSAGE);	
		}

		table_Book.setRowFactory( tv -> {
			TableRow<Book> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
					Book book = row.getItem();
					txtISBNNum.setText(book.getIsbn());
					txttitle.setText(book.getTitle());
					txtfirstName.setText(book.getAuthors().get(0).getFirstName());
					txtlasttName.setText(book.getAuthors().get(0).getLastName());
					txtcity.setText(book.getAuthors().get(0).getAddress().getCity());
					txtstreet.setText(book.getAuthors().get(0).getAddress().getStreet());
					txtState.setText(book.getAuthors().get(0).getAddress().getState());
					txtzip.setText(book.getAuthors().get(0).getAddress().getZip());
					txtbio.setText(book.getAuthors().get(0).getBio());
					txtcredentials.setText(book.getAuthors().get(0).getCredential());
					txtphoneNum.setText(book.getAuthors().get(0).getTelephone());
					
				}
				btnSave.setDisable(true);
				btnNext.setDisable(true);
				btnUpdate.setDisable(false);
				btnDelete.setDisable(false);

			});
			return row ;
		});
		
		final VBox vbox = new VBox(5);
		vbox.setPadding(new Insets(10,0,10,0));
		vbox.getChildren().add(table_Book);
		grid.add(vbox, 0, 27,5,1);


		return grid;
	}


	/******************Book Copy*******************************/


	@SuppressWarnings("unchecked")
	private GridPane  addBookCopyGridPane() {
		GridPane grid = new GridPane();
		SystemController sysCtl1=new SystemController();
		//grid.setAlignment(Pos.CENTER);
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setPadding(new Insets(10, 10, 10, 10));

		Text sceneTitle = new Text("Book Copy Entry Form.....");
		sceneTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneTitle, 0, 1,1,2); 

		Label lblISBNNum = new Label("ISBN Number:");        
		grid.add(lblISBNNum, 0, 4);          

		
		ComboBox cmbISBN = new ComboBox<>();
		cmbISBN.setMaxWidth(180);
		
		SystemController sys=new SystemController();
		try{
		List<Book> bookList=sys.getAllBookList();
		for(Book book: bookList)
			cmbISBN.getItems().addAll(book.getIsbn());
		grid.add(cmbISBN, 1, 4);
		}catch(Exception e ){
			System.out.println(e);
		}
		
//		TextField txtISBNNum = new TextField();       
//		grid.add(txtISBNNum, 1, 4);

		Button btnSearch = new Button("Search");
		HBox hbButn1 = new HBox(10);//FlowLayOut
		hbButn1.setAlignment(Pos.BOTTOM_CENTER);
		hbButn1.getChildren().add(btnSearch);
		grid.add(hbButn1, 2, 4);

		Label lbltitle = new Label("Title:");        
		grid.add(lbltitle, 0, 5);          
		TextField txttitle = new TextField();
		txttitle.setDisable(true);
		grid.add(txttitle, 1, 5);

		Label lblcheckoutLenght = new Label("Checkout Lenght:");        
		grid.add(lblcheckoutLenght, 0, 7);          
		TextField txtcheckoutLenght = new TextField(); 
		txtcheckoutLenght.setDisable(true);
		grid.add(txtcheckoutLenght, 1, 7);

		Label lblNumberOfCopy = new Label("Number of Copy (Currently):");        
		grid.add(lblNumberOfCopy, 0, 8);          
		TextField txtNumberOfCopy = new TextField();
		txtNumberOfCopy.setDisable(true);
		grid.add(txtNumberOfCopy, 1, 8);

		Label lbladdCopyNumber = new Label("Add Copy Number");        
		grid.add(lbladdCopyNumber, 0, 9);          

		ComboBox cmbaddCopyNumber = new ComboBox<>();
		cmbaddCopyNumber.setMaxWidth(180);
		cmbaddCopyNumber.getItems().addAll("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15");
		cmbaddCopyNumber.setValue("1");
		grid.add(cmbaddCopyNumber, 1, 9);

		

		Button btnAddCopy = new Button("Add Copy");
		btnAddCopy.setDisable(true);
		HBox hbButn = new HBox(10);//FlowLayOut
		hbButn.setAlignment(Pos.BOTTOM_RIGHT);
		hbButn.getChildren().add(btnAddCopy);
		grid.add(hbButn, 2, 9);
		
		table_BookCopy = new TableView();
		btnSearch.setOnAction(evt->{

			try
			{
				Book book=sysCtl1.searchBook(cmbISBN.getValue().toString());
				if(book==null){
					throw new LibrarySystemException("Book ISBN : " + cmbISBN.getValue().toString()
							+ " not found in Book Database!");
				}
				txttitle.setText(book.getTitle());
				txtcheckoutLenght.setText(book.getMaxCheckoutLength()+"");
				txtNumberOfCopy.setText(book.getNumCopies()+"");
				BookCopy [] bc = book.getCopies();
				List<BookCopy> bcList = new ArrayList<BookCopy>(Arrays.asList(bc));
				table_BookCopy = bindBookCopyDetails(bcList);
				grid.add(table_BookCopy, 0, 14,2,1);
				btnAddCopy.setDisable(false);
				//System.out.println(book);
			}catch(Exception e2){
				JOptionPane.showMessageDialog(null,e2.getMessage(), "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);
				border.setCenter( addBookCopyGridPane());
			}
		});
		//table_BookCopy = new TableView<>();

		btnAddCopy.setOnAction(evt->{

			try{
				for(int i=0; i<Integer.parseInt(cmbaddCopyNumber.getValue().toString()); i++)
					sysCtl1.addBookCopy(cmbISBN.getValue().toString());
				Book book=sysCtl1.searchBook(cmbISBN.getValue().toString());
				txtNumberOfCopy.setText(book.getNumCopies()+"");
				BookCopy [] bc = book.getCopies();
				List<BookCopy> bcList = new ArrayList<BookCopy>(Arrays.asList(bc));
				table_BookCopy = bindBookCopyDetails(bcList);
				grid.add(table_BookCopy, 0, 14,2,1);
			}catch(LibrarySystemException e1){
				JOptionPane.showMessageDialog(null,e1.getMessage(), "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);
			}catch(Exception e2){
				JOptionPane.showMessageDialog(null,e2.getMessage(), "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);
				border.setCenter( addBookCopyGridPane());
			}
		});

		Text sceneAddress = new Text("Book Copy List... ");
		sceneAddress.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneAddress, 0, 12,2,1);    

		table_BookCopy= new TableView();
		table_BookCopy.setEditable(true);
		TableColumn colCopyNum = new TableColumn("Copy Number");
		TableColumn colAvailability = new TableColumn("Availability"); 
		table_BookCopy.getColumns().addAll(colCopyNum,colAvailability);
		final VBox vbox = new VBox(5);
		vbox.setPadding(new Insets(10,0,10,0));
		vbox.getChildren().add(table_BookCopy);
		grid.add(vbox, 0, 14,2,1);
		return grid;
	}



	/*****************Checkout********************************/

	@SuppressWarnings("unchecked")
	private GridPane  addBookCheckoutGridPane() {
		GridPane grid = new GridPane();
		//grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		Text sceneTitle = new Text("Book Checkout Entry Form...");
		sceneTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneTitle, 0, 1,1,2); 

		Label lblISBNNum = new Label("ISBN Number:");        
		grid.add(lblISBNNum, 0, 3);
		
		TextField txtISBNNum = new TextField();       
		grid.add(txtISBNNum, 1, 3);

		Label lbltitle = new Label("Member ID:");        
		grid.add(lbltitle, 0, 4);          

		TextField txtmemberID = new TextField();  
		grid.add(txtmemberID, 1, 4);

		Button btnCheckout = new Button("Checkout");
		HBox hbButn1 = new HBox(10);//FlowLayOut
		hbButn1.setAlignment(Pos.BOTTOM_CENTER);
		hbButn1.getChildren().add(btnCheckout);
		grid.add(hbButn1, 2, 4);  

		Text sceneMember = new Text("Member List... ");
		sceneMember.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneMember, 0, 7,2,1);  


		btnCheckout.setOnAction(evt->{
			SystemController sysCtl2=new SystemController();
			try{

				sysCtl2.checkoutBook( txtmemberID.getText(),txtISBNNum.getText());
				LibraryMember member = sysCtl2.searchMember(txtmemberID.getText());
				CheckoutRecord chkRecord = member.getCheckoutRecord();
				List<CheckoutRecordEntry> ckEntry = chkRecord.getCheckoutEntries();
				table_Checkout = bindCheckOutEntry(ckEntry);
				grid.add(table_Checkout, 0, 9,5,1);

				List<Book> allBook1 = sysCtl2.getAllBookList();
				table_Book_chekout= bindBookListforCheckout(allBook1);
				table_Book_chekout.setEditable(true);

				final VBox vbox1 = new VBox(5);
				vbox1.setPadding(new Insets(10,0,10,0));
				vbox1.getChildren().add(table_Book_chekout);
				grid.add(vbox1, 0, 13,5,1);
				btnCheckout.setVisible(false);
				txtISBNNum.setDisable(true);
				txtmemberID.setDisable(true);
				sceneMember.setText("Checkout Details");

			}catch(LibrarySystemException e1){
				JOptionPane.showMessageDialog(null,e1.getMessage(), "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);
			}catch(Exception e2){
				JOptionPane.showMessageDialog(null,e2.getMessage(), "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);
				border.setCenter( addBookCheckoutGridPane());
			}

		});


		table_MemberCheckout=displayMember();

		final VBox vbox = new VBox(5);
		vbox.setPadding(new Insets(10,10,10,10));
		vbox.getChildren().add(table_MemberCheckout);
		grid.add(vbox, 0, 9,5,1);

		table_MemberCheckout.setRowFactory( tv -> {
			TableRow<MemberDisplay> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
					MemberDisplay mem = row.getItem();                   
					txtmemberID.setText(mem.getMemberId());
				}
			});
			return row ;
		});

		Text sceneBook = new Text("Book List... ");
		sceneBook.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneBook, 0, 11,2,1);    

		SystemController sysCtl=new SystemController();
		try{
			List<Book> allBook1 = sysCtl.getAllBookList();
			table_Book_chekout= bindBookListforCheckout(allBook1);
			table_Book_chekout.setEditable(true);
			table_Book_chekout.setRowFactory( tv -> {
				TableRow<Book> row = new TableRow<>();
				row.setOnMouseClicked(event -> {
					if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
						Book book = row.getItem();                   
						txtISBNNum.setText(book.getIsbn());
				
					}
				});
				return row ;
			});
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,e.getMessage(), "Failed Transaction",
					JOptionPane.ERROR_MESSAGE);	
		}

		final VBox vbox1 = new VBox(5);
		vbox1.setPadding(new Insets(10,10,10,10));
		vbox1.getChildren().add(table_Book_chekout);
		grid.add(vbox1, 0, 13,5,1);


		return grid;
	}


	/***************************CheckoutRecord********************************/


	@SuppressWarnings("unchecked")
	private GridPane  addBookCheckoutRecordGridPane() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));

		Text sceneTitle = new Text("Checkout Record Form...");
		sceneTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneTitle, 0, 1,1,2); 

		Label lbltitle = new Label("Member ID:");        
		grid.add(lbltitle, 0, 4);          

		TextField txtmemberID = new TextField();  
		grid.add(txtmemberID, 1, 4);

		Button btnSearch = new Button("Search");
		HBox hbButn1 = new HBox(10);//FlowLayOut
		hbButn1.setAlignment(Pos.BOTTOM_CENTER);
		hbButn1.getChildren().add(btnSearch);
		grid.add(hbButn1, 2, 4);  

		btnSearch.setOnAction(evt->{
			SystemController sysCtl2=new SystemController();
			try{
				LibraryMember member = sysCtl2.searchMember(txtmemberID.getText());

				if(member == null){
					throw new LibrarySystemException("Member ID : " + txtmemberID.getText()
							+ " is in the Member Database!");
				}

				CheckoutRecord chkRecord = member.getCheckoutRecord();
				List<CheckoutRecordEntry> ckEntry = chkRecord.getCheckoutEntries();

				Text sceneMember = new Text("Checkout List... ");
				sceneMember.setFont(Font.font("Arial", FontWeight.BOLD, 12));
				grid.add(sceneMember, 0,9,2,1);    

				table_Checkout = bindCheckOutEntry(ckEntry);
				grid.add(table_Checkout, 0, 11,5,1);

			}catch(LibrarySystemException e1){
				JOptionPane.showMessageDialog(null,e1.getMessage(), "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);
			}catch(Exception e2){
				JOptionPane.showMessageDialog(null,e2.getMessage(), "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);
				border.setCenter( addBookCheckoutRecordGridPane());
			}

		});

		Text sceneMember = new Text("Member List... ");
		sceneMember.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneMember, 0, 6,2,1);    

		table_MemberCheckout=displayMember();

		final VBox vbox = new VBox(5);
		vbox.setPadding(new Insets(0,0,0,0));
		vbox.getChildren().add(table_MemberCheckout);
		grid.add(vbox, 0, 7,5,1);

		table_MemberCheckout.setRowFactory( tv -> {
			TableRow<MemberDisplay> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
					MemberDisplay mem = row.getItem();                   
					txtmemberID.setText(mem.getMemberId());
				}
			});
			return row ;
		});

		return grid;
	}


	/*******************Overdue**************************/



	@SuppressWarnings("unchecked")
	private GridPane  overDueGridPane() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 30, 10, 10));

		Text sceneTitle = new Text("Overdue Form...");
		sceneTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(sceneTitle, 0, 1,1,2); 

		Label lbltitle = new Label("Book ISBN:");        
		grid.add(lbltitle, 0, 4);          

		TextField txtISBN = new TextField();  
		grid.add(txtISBN, 1, 4);

		Button btnSearch = new Button("Search");
		HBox hbButn1 = new HBox(10);
		hbButn1.setAlignment(Pos.BOTTOM_CENTER);
		hbButn1.getChildren().add(btnSearch);
		grid.add(hbButn1, 2, 4);  


		btnSearch.setOnAction(evt->{
			SystemController sysCtl=new SystemController();
			try{
				System.out.println(sysCtl.checkOverdue(txtISBN.getText()));
			}catch(Exception e){

			}
			try{
				List<LibraryMember> memberList=sysCtl.checkOverdue(txtISBN.getText());
				table_Overdue = bindOverdue(memberList, txtISBN.getText());

				Label lblOverdue = new Label("Over Due List..."); 
				lblOverdue.setFont(Font.font("Arial", FontWeight.BOLD, 12));
				grid.add(lblOverdue, 0, 9);          

				grid.add(table_Overdue, 0, 10,5,1);

			}catch(LibrarySystemException e1){
				JOptionPane.showMessageDialog(null,e1.getMessage(), "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);
			}catch(Exception e2){
				JOptionPane.showMessageDialog(null,e2.getMessage(), "Failed Transaction",
						JOptionPane.ERROR_MESSAGE);
				border.setCenter( overDueGridPane());
			}
		});

		Label lblBookList = new Label("Book List...");
		lblBookList.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		grid.add(lblBookList, 0, 5);   

		SystemController sysCtl=new SystemController();
		try{
			List<Book> allBook1 = sysCtl.getAllBookList();
			table_Book= bindBookList(allBook1);
			table_Book.setEditable(true);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,e.getMessage(), "Failed Transaction",
					JOptionPane.ERROR_MESSAGE);	
		}

		grid.add(table_Book, 0, 6,5,1);

		table_Book.setRowFactory( tv -> {
			TableRow<Book> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
					Book book = row.getItem();                   
					txtISBN.setText(book.getIsbn());
				}
			});
			return row ;
		});

		return grid;
	}


	/******************Additional Method*************************/

	// Methods for Member viewing table view

	public TableView displayMember(){
		table= new TableView();
		table.setEditable(true);
		DataAccess da = new DataAccessFacade();
		HashMap<String, LibraryMember> hs= da.readMemberMap();
		ObservableList<MemberDisplay> data= FXCollections.observableArrayList();

		Iterator iterator = hs.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) iterator.next();
			LibraryMember lib = (LibraryMember) mapEntry.getValue();
			MemberDisplay mem= new MemberDisplay(lib.getMemberId(),lib.getFirstName(),lib.getLastName(), (lib.getAddress()).getStreet(), (lib.getAddress()).getCity(), (lib.getAddress()).getState(), (lib.getAddress()).getZip(), lib.getTelephone());
			data.add(mem);
		}

		TableColumn<LibraryMember, String> memberId = new TableColumn<>(String.format("MemberID"));
		memberId.setCellValueFactory( new PropertyValueFactory<LibraryMember, String>("memberId"));

		TableColumn<LibraryMember, String> fname = new TableColumn<>(String.format("First Name"));
		fname.setCellValueFactory( new PropertyValueFactory<LibraryMember, String>("firstName"));

		TableColumn<LibraryMember, String> lname = new TableColumn<>(String.format("Last Name"));
		lname.setCellValueFactory( new PropertyValueFactory<LibraryMember, String>("lastName"));

		TableColumn<LibraryMember, String> street = new TableColumn<>(String.format("Street"));
		street.setCellValueFactory( new PropertyValueFactory<LibraryMember, String>("street"));

		TableColumn<LibraryMember, String> city = new TableColumn<>(String.format("City"));
		city.setCellValueFactory( new PropertyValueFactory<LibraryMember, String>("city"));

		TableColumn<LibraryMember, String> state = new TableColumn<>(String.format("State"));
		state.setCellValueFactory( new PropertyValueFactory<LibraryMember, String>("state"));

		TableColumn<LibraryMember, String> zip = new TableColumn<>(String.format("Zip"));
		zip.setCellValueFactory( new PropertyValueFactory<LibraryMember, String>("zip"));

		TableColumn<LibraryMember, String> pnumber = new TableColumn<>(String.format("Phone Number"));
		pnumber.setCellValueFactory( new PropertyValueFactory<LibraryMember, String>("telephone"));

		memberId.setEditable(false);
		table.setItems(data);
		table.getColumns().addAll(memberId,fname,lname,street, city,state,zip,pnumber);
		return table;


	}

	public void clearMemnerGrid(){
		for (Node node : grid.getChildren()) {
			if (node instanceof TextField) {
				// clear
				((TextField)node).setText("");
			}
		}
	}

	//Bind Author details

	@SuppressWarnings("rawtypes")
	public TableView bindAuthorDetails(List<Author> authors, int count)
	{
		ObservableList<Author> author = FXCollections.observableArrayList(authors);
		TableView<Author> tableView = new TableView<Author>(author);
		tableView.setItems(author);


		TableColumn<Author,String> colSLNext = new TableColumn<Author,String>("Si.NO");
		colSLNext.setCellValueFactory(new Callback<CellDataFeatures<Author, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Author, String> p) {
				return new ReadOnlyStringWrapper(table_Auth.getItems().indexOf(p.getValue()) + 1 + "");

			}
		});
		TableColumn<Author,String> colFirstNameNext = new TableColumn<Author,String>("F.Name");
		colFirstNameNext.setCellValueFactory(new Callback<CellDataFeatures<Author, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Author, String> p) {
				return new ReadOnlyStringWrapper(p.getValue().getFirstName());

			}
		});
		TableColumn<Author,String> colLastNameNext = new TableColumn<Author,String>("L.Name");
		colLastNameNext.setCellValueFactory(new Callback<CellDataFeatures<Author, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Author, String> p) {
				return new ReadOnlyStringWrapper(p.getValue().getLastName());

			}
		});
		TableColumn<Author,String> colBioNext = new TableColumn<Author,String>("Bio");
		colBioNext.setCellValueFactory(new Callback<CellDataFeatures<Author, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Author, String> p) {
				return new ReadOnlyStringWrapper(p.getValue().getBio());

			}
		});
		tableView.getColumns().add(colSLNext);
		tableView.getColumns().add(colFirstNameNext);
		tableView.getColumns().add(colLastNameNext);
		tableView.getColumns().add(colBioNext);

		return tableView;

	}


	//Bind Book details

	@SuppressWarnings("rawtypes")
	public TableView bindBookList(List<Book> books)
	{
		ObservableList<Book> book = FXCollections.observableArrayList(books);
		TableView<Book> BooktableView = new TableView<Book>(book);
		BooktableView.setItems(book);

		TableColumn<Book,String> colSLNext = new TableColumn<Book,String>("Si.NO");
		colSLNext.setCellValueFactory(new Callback<CellDataFeatures<Book, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Book, String> p) {
				return new ReadOnlyStringWrapper(table_Book.getItems().indexOf(p.getValue()) + 1 + "");

			}
		});

		TableColumn<Book,String> colISBN = new TableColumn<Book,String>("ISBN");
		colISBN.setCellValueFactory(new Callback<CellDataFeatures<Book, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Book, String> p) {
				return new ReadOnlyStringWrapper(p.getValue().getIsbn());

			}
		});

		TableColumn<Book,String> colNewTitle = new TableColumn<Book,String>("Title");
		colNewTitle.setCellValueFactory(new Callback<CellDataFeatures<Book, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Book, String> p) {
				return new ReadOnlyStringWrapper(p.getValue().getTitle());

			}
		});

		TableColumn<Book,String> colBookCopyNumber = new TableColumn<Book,String>("NumberOfBookCopy");
		colBookCopyNumber.setCellValueFactory(new Callback<CellDataFeatures<Book, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Book, String> p) {

				BookCopy[] listBookCopy =  p.getValue().getCopies();
				return new ReadOnlyStringWrapper(""+listBookCopy.length+"");

			}
		});

		TableColumn<Book,String> colAvailabeCopyNumber = new TableColumn<Book,String>("NumberOfAvailable");
		colAvailabeCopyNumber.setCellValueFactory(new Callback<CellDataFeatures<Book, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Book, String> p) {

				BookCopy[] listBookCopy =  p.getValue().getCopies();
				int count = 0;
				for(BookCopy bk : listBookCopy)
				{
					if(bk.isAvailable() == true)
						count++;
				}
				return new ReadOnlyStringWrapper(""+count+"");

			}
		});

		TableColumn<Book,String> colAuthors = new TableColumn<Book,String>("Authors");
		colAuthors.setCellValueFactory(new Callback<CellDataFeatures<Book, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Book, String> p) {

				List<Author> authorList =  p.getValue().getAuthors();
				String authors="";
				for(int i = 0;i<authorList.size();i++){
					authors += authorList.get(i).getFirstName()+" "+authorList.get(i).getLastName();
					if(i<authorList.size()-1)
					{
						authors +=" , ";
					}
				}   
				return new ReadOnlyStringWrapper(authors);

			}
		});

		BooktableView.getColumns().add(colSLNext);
		BooktableView.getColumns().add(colISBN);
		BooktableView.getColumns().add(colNewTitle);
		BooktableView.getColumns().add(colAuthors);
		BooktableView.getColumns().add(colBookCopyNumber);
		BooktableView.getColumns().add(colAvailabeCopyNumber);

		return BooktableView;

	}

	//Book copy Bind

	
	public TableView bindBookCopyDetails(List<BookCopy> bookCopys)
	{
		ObservableList<BookCopy> bookCopy = FXCollections.observableArrayList(bookCopys);
		TableView<BookCopy> tableView = new TableView<BookCopy>(bookCopy);
		tableView.setItems(bookCopy);
		TableColumn<BookCopy,String> colSLNext = new TableColumn<BookCopy,String>("Si.NO");
		colSLNext.setCellValueFactory(new Callback<CellDataFeatures<BookCopy, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<BookCopy, String> p) {
				return new ReadOnlyStringWrapper(table_BookCopy.getItems().indexOf(p.getValue()) + 1 + "");
			}
		});
		TableColumn<BookCopy,String> colCopyNumber = new TableColumn<BookCopy,String>("Copy Number");
		colCopyNumber.setCellValueFactory(new Callback<CellDataFeatures<BookCopy, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<BookCopy, String> p) {
				return new ReadOnlyStringWrapper(p.getValue().getCopyNum()+"");
			}
		});
		TableColumn<BookCopy,String> colAvailability = new TableColumn<BookCopy,String>("Availability");
		colAvailability.setCellValueFactory(new Callback<CellDataFeatures<BookCopy, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<BookCopy, String> p) {
				return new ReadOnlyStringWrapper(p.getValue().isAvailable()+"");
			}
		});
		
		tableView.getColumns().add(colSLNext);
		tableView.getColumns().add(colCopyNumber);
		tableView.getColumns().add(colAvailability);
		
	   return tableView;
	}


	//Bind Book details

	@SuppressWarnings("rawtypes")
	public TableView bindBookListforCheckout(List<Book> books)
	{
		ObservableList<Book> book = FXCollections.observableArrayList(books);
		TableView<Book> BooktableView = new TableView<Book>(book);
		BooktableView.setItems(book);

		TableColumn<Book,String> colSLNext = new TableColumn<Book,String>("Si.NO");
		colSLNext.setCellValueFactory(new Callback<CellDataFeatures<Book, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Book, String> p) {
				return new ReadOnlyStringWrapper(table_Book_chekout.getItems().indexOf(p.getValue()) + 1 + "");

			}
		});

		TableColumn<Book,String> colISBN = new TableColumn<Book,String>("ISBN");
		colISBN.setCellValueFactory(new Callback<CellDataFeatures<Book, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Book, String> p) {
				return new ReadOnlyStringWrapper(p.getValue().getIsbn());

			}
		});

		TableColumn<Book,String> colNewTitle = new TableColumn<Book,String>("Title");
		colNewTitle.setCellValueFactory(new Callback<CellDataFeatures<Book, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Book, String> p) {
				return new ReadOnlyStringWrapper(p.getValue().getTitle());

			}
		});

		TableColumn<Book,String> colBookCopyNumber = new TableColumn<Book,String>("NumberOfBookCopy");
		colBookCopyNumber.setCellValueFactory(new Callback<CellDataFeatures<Book, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Book, String> p) {

				BookCopy[] listBookCopy =  p.getValue().getCopies();
				return new ReadOnlyStringWrapper(""+listBookCopy.length+"");

			}
		});

		TableColumn<Book,String> colAvailabeCopyNumber = new TableColumn<Book,String>("NumberOfAvailable");
		colAvailabeCopyNumber.setCellValueFactory(new Callback<CellDataFeatures<Book, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Book, String> p) {

				BookCopy[] listBookCopy =  p.getValue().getCopies();
				int count = 0;
				for(BookCopy bk : listBookCopy)
				{
					if(bk.isAvailable() == true)
						count++;
				}
				return new ReadOnlyStringWrapper(""+count+"");

			}
		});

		TableColumn<Book,String> colAuthors = new TableColumn<Book,String>("Authors");
		colAuthors.setCellValueFactory(new Callback<CellDataFeatures<Book, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Book, String> p) {

				List<Author> authorList =  p.getValue().getAuthors();
				String authors="";
				for(int i = 0;i<authorList.size();i++){
					authors += authorList.get(i).getFirstName()+" "+authorList.get(i).getLastName();
					if(i<authorList.size()-1)
					{
						authors +=" , ";
					}
				}   
				return new ReadOnlyStringWrapper(authors);

			}
		});

		BooktableView.getColumns().add(colSLNext);
		BooktableView.getColumns().add(colISBN);
		BooktableView.getColumns().add(colNewTitle);
		BooktableView.getColumns().add(colAuthors);
		BooktableView.getColumns().add(colBookCopyNumber);
		BooktableView.getColumns().add(colAvailabeCopyNumber);

		return BooktableView;

	}

	// checkout binding
	public TableView bindCheckOutEntry(List<CheckoutRecordEntry> chkEntry)
	{
		ObservableList<CheckoutRecordEntry> chkEty = FXCollections.observableArrayList(chkEntry);
		TableView<CheckoutRecordEntry> chkOutEntrytableView = new TableView<CheckoutRecordEntry>();
		chkOutEntrytableView.setItems(chkEty);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		TableColumn<CheckoutRecordEntry,String> colSLNext = new TableColumn<CheckoutRecordEntry,String>("Si.NO");
		colSLNext.setCellValueFactory(new Callback<CellDataFeatures<CheckoutRecordEntry, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<CheckoutRecordEntry, String> p) {
				return new ReadOnlyStringWrapper(table_Checkout.getItems().indexOf(p.getValue()) + 1 + "");

			}
		});

		TableColumn<CheckoutRecordEntry,String> colchkOutDate = new TableColumn<CheckoutRecordEntry,String>("CheckoutDate");
		colchkOutDate.setCellValueFactory(new Callback<CellDataFeatures<CheckoutRecordEntry, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<CheckoutRecordEntry, String> p) {
				return new ReadOnlyStringWrapper(formatter.format(p.getValue().getCheckoutDate()));

			}
		});
		TableColumn<CheckoutRecordEntry,String> colDueDate = new TableColumn<CheckoutRecordEntry,String>("DueDate");
		colDueDate.setCellValueFactory(new Callback<CellDataFeatures<CheckoutRecordEntry, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<CheckoutRecordEntry, String> p) {
				return new ReadOnlyStringWrapper(formatter.format(p.getValue().getDueDate()));

			}
		});
		TableColumn<CheckoutRecordEntry,String> colBookTitle = new TableColumn<CheckoutRecordEntry,String>("BookTitle");
		colBookTitle.setCellValueFactory(new Callback<CellDataFeatures<CheckoutRecordEntry, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<CheckoutRecordEntry, String> p) {
				return new ReadOnlyStringWrapper(p.getValue().getBookcopy().getBook().getTitle());

			}
		});

		TableColumn<CheckoutRecordEntry,String> colBookCopyNumber = new TableColumn<CheckoutRecordEntry,String>("BookCopyNumber");
		colBookCopyNumber.setCellValueFactory(new Callback<CellDataFeatures<CheckoutRecordEntry, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<CheckoutRecordEntry, String> p) {
				return new ReadOnlyStringWrapper(""+p.getValue().getBookcopy().getCopyNum()+"");

			}
		});

		chkOutEntrytableView.getColumns().add(colSLNext);
		chkOutEntrytableView.getColumns().add(colchkOutDate);
		chkOutEntrytableView.getColumns().add(colDueDate);
		chkOutEntrytableView.getColumns().add(colBookTitle);
		chkOutEntrytableView.getColumns().add(colBookCopyNumber);
		return chkOutEntrytableView;

	}


	public TableView bindOverdue(List<LibraryMember> member, String isbn)
	{
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		ObservableList<LibraryMember> mem = FXCollections.observableArrayList(member);
		TableView<LibraryMember> overDuetableView = new TableView<LibraryMember>();
		overDuetableView.setItems(mem);

//		TableColumn<LibraryMember,String> colSLNext = new TableColumn<LibraryMember,String>("Si.NO");
//		colSLNext.setCellValueFactory(new Callback<CellDataFeatures<LibraryMember, String>, ObservableValue<String>>() {
//			@Override
//			public ObservableValue<String> call(CellDataFeatures<LibraryMember, String> p) {
//				return new ReadOnlyStringWrapper(table_Overdue.getItems().indexOf(p.getValue()) + 1 + "");
//
//			}
//		});

		TableColumn<LibraryMember,String> colMemberId = new TableColumn<LibraryMember,String>("Member ID");
		colMemberId.setCellValueFactory(new Callback<CellDataFeatures<LibraryMember, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LibraryMember, String> p) {
				return new ReadOnlyStringWrapper(p.getValue().getMemberId());

			}
		});

		TableColumn<LibraryMember,String> colMemberName = new TableColumn<LibraryMember,String>("Member Name");
		colMemberName.setCellValueFactory(new Callback<CellDataFeatures<LibraryMember, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LibraryMember, String> p) {
				return new ReadOnlyStringWrapper(p.getValue().getFirstName() +" "+ p.getValue().getLastName());

			}
		});

		TableColumn<LibraryMember,String> colBookIsbn = new TableColumn<LibraryMember,String>("Book ISBN");
		colBookIsbn.setCellValueFactory(new Callback<CellDataFeatures<LibraryMember, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LibraryMember, String> p) {
				return new ReadOnlyStringWrapper(isbn);
			}
		});

		TableColumn<LibraryMember,String> colBookTitle = new TableColumn<LibraryMember,String>("Book Title");
		colBookTitle.setCellValueFactory(new Callback<CellDataFeatures<LibraryMember, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LibraryMember, String> p) {
				CheckoutRecord cr= p.getValue().getCheckoutRecord();
				String bookTitle="";
				List<CheckoutRecordEntry> chkEntryList=cr.getCheckoutEntries();
				for(CheckoutRecordEntry chkE: chkEntryList){
					if(chkE.getDueDate().before(new Date()) && chkE.getBookcopy().getBook().getIsbn().equals(isbn))
						bookTitle=chkE.getBookcopy().getBook().getTitle();
					break;
				}
				return new ReadOnlyStringWrapper(bookTitle);
			}
		});

		TableColumn<LibraryMember,String> colBookCopyNumber = new TableColumn<LibraryMember,String>("Copy Number");
		colBookCopyNumber.setCellValueFactory(new Callback<CellDataFeatures<LibraryMember, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LibraryMember, String> p) {
				CheckoutRecord cr= p.getValue().getCheckoutRecord();
				int bookCopyNumber=0;
				List<CheckoutRecordEntry> chkEntryList=cr.getCheckoutEntries();
				for(CheckoutRecordEntry chkE: chkEntryList){
					if(chkE.getDueDate().before(new Date()) && chkE.getBookcopy().getBook().getIsbn().equals(isbn))
						bookCopyNumber=chkE.getBookcopy().getCopyNum();
					break;
				}

				return new ReadOnlyStringWrapper(""+bookCopyNumber+"");
			}
		});

		TableColumn<LibraryMember,String> colCheckoutDate = new TableColumn<LibraryMember,String>("Checkout Date");
		colCheckoutDate.setCellValueFactory(new Callback<CellDataFeatures<LibraryMember, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LibraryMember, String> p) {
				CheckoutRecord cr= p.getValue().getCheckoutRecord();
				Date checkoutDate=new Date();
				List<CheckoutRecordEntry> chkEntryList=cr.getCheckoutEntries();
				for(CheckoutRecordEntry chkE: chkEntryList){
					if(chkE.getDueDate().before(new Date()) && chkE.getBookcopy().getBook().getIsbn().equals(isbn))
						checkoutDate=chkE.getCheckoutDate();
					break;
				}
				return new ReadOnlyStringWrapper(formatter.format(checkoutDate));
			}
		});

		TableColumn<LibraryMember,String> colCheckoutDueDate = new TableColumn<LibraryMember,String>("Due Date");
		colCheckoutDueDate.setCellValueFactory(new Callback<CellDataFeatures<LibraryMember, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LibraryMember, String> p) {
				CheckoutRecord cr= p.getValue().getCheckoutRecord();
				Date checkoutDueDate=new Date();
				List<CheckoutRecordEntry> chkEntryList=cr.getCheckoutEntries();
				for(CheckoutRecordEntry chkE: chkEntryList){
					if(chkE.getDueDate().before(new Date()) && chkE.getBookcopy().getBook().getIsbn().equals(isbn))
						checkoutDueDate=chkE.getDueDate();
					break;
				}
				return new ReadOnlyStringWrapper(formatter.format(checkoutDueDate));
			}
		});

	//	overDuetableView.getColumns().add(colSLNext);
		overDuetableView.getColumns().add(colBookIsbn);
		overDuetableView.getColumns().add(colBookTitle);
		overDuetableView.getColumns().add(colBookCopyNumber);
		overDuetableView.getColumns().add(colMemberId);
		overDuetableView.getColumns().add(colMemberName);
		overDuetableView.getColumns().add(colCheckoutDate);
		overDuetableView.getColumns().add(colCheckoutDueDate);

		return overDuetableView;

	}



}

