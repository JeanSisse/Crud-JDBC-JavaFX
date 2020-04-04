package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable{

	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}
	
	@FXML
	public void onMenuItemDepartemtnAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}

	// synchronized -> para que todo o processo seja executado sem interrupção durante multi-thread
	// loadView é um método genérico com uma interface funcional (Consumer<T>) para trabalhar com expressões lambda
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializinAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			// Pega referência do painel principal (referência para cena)
			Scene mainScene = Main.getMainScene();
			
			// Pega referência do VBox do painel principal
			/*
			 ** getRoot() --> pega o primeiro elemnto na referência do painel principal que é um scrollPane
			 ** ((ScrollPane)mainScene.getRoot()) é uma referência para o ScrollPane
			 ** getContent() --> pega o content dentro do ScrollPane que já é uma referência para o VBox
			 ** dentro do ScrollPane
			 */
			VBox mainVBox = (VBox)((ScrollPane)mainScene.getRoot()).getContent();
			
			/*
			 * getChildren().get(0) --> pega o filho do VBox na posição 0 (zero) (a barra de menu)
			*/
			Node mainMenu = mainVBox.getChildren().get(0);
			
			/*Limpa todos os filhos do mainVBox*/
			mainVBox.getChildren().clear();
			
			/*Adicionando filhos ao mainVBox*/
			mainVBox.getChildren().add(mainMenu);
			
			/*Adicionando uma coleção de filhos ao mainVBox*/
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			// Ativando a função recebido no paramêtro Consumer<T>
			T controller = loader.getController();
			initializinAction.accept(controller);
		}
		catch(IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
