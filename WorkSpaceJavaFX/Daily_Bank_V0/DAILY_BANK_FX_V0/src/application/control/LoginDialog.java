package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.LoginDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;
import model.orm.AccessEmploye;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

/**
 * La classe LoginDialog permet de génerer la ressource FXML qui affiche la fenetre de login pour se connecter sur l'app en tant qu'employé.
 * Cette classe recherche aussi un employé à partir de son mot de passe et de son login.
 */
public class LoginDialog {

	private Stage primaryStage;
	private DailyBankState dbs;
	private LoginDialogController ldc;
	
	/**
	 * Permet de générer la ressource logindialog.fxml depuis son controller. Elle prend en parametre la fenetre(Stage) et l'état de l'agence bancaire(DailyBankState).
	 * @param _parentStage
	 * @param _dbstate
	 * @param client
	 */
	public LoginDialog(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(LoginDialogController.class.getResource("logindialog.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Identification");
			primaryStage.getIcons().add(new Image(DailyBankMainFrame.class.getResourceAsStream("../../DailyBankIcon.png")));
			this.primaryStage.setResizable(false);

			this.ldc = loader.getController();
			this.ldc.initContext(this.primaryStage, this, _dbstate);
			
			this.ldc.getTxtLogin().requestFocus();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Permet d'afficher le contenu de la fenetre pour se connecter sur l'application attends une interaction potentielle avec celle-ci
	 * @return le contenu à afficher dans la boites de dialogue
	 */
	public void doLoginDialog() {
		this.ldc.displayDialog();
	}
	
	/**
	 * Permet de rechercher un employé dans la base de données SQL depuis son login et mot de passe. Prend en paramtre le mot de passe et le login saisi
	 * @param login
	 * @param password
	 * @return l'employé recherché
	 */
	public Employe chercherParLogin(String login, String password) {
		Employe employe = null;
		try {
			AccessEmploye ae = new AccessEmploye();

			employe = ae.getEmploye(login, password);

			if (employe != null) {
				this.dbs.setEmpAct(employe);
			}
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.dbs.setEmpAct(null);
			this.primaryStage.close();
			employe = null;
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			this.dbs.setEmpAct(null);
			this.primaryStage.close();
			employe = null;
		}
		return employe;
	}
	
}
