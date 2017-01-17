package texteditor.main;

import texteditor.gui.MainMenu;
import texteditor.gui.MainWindow;
import texteditor.model.DocumentModel;

public class MainClass {

	public static void main(String[] args) {
		
		// Functie statica pentru initializarea stilurilor folosite in document
		// Stilurile pot fi: aliniere la dreapta, stanga si central
		DocumentModel.initialize();
		
		// Facem o bara de meniu si o initializam
		MainMenu mainMenuBar = new MainMenu();
		if (!mainMenuBar.initialize()) {
			System.out.println("Could not initialize main menu");
			System.exit(-1);
		}
		
		// Facem fereastra principala si o initializam, injectam dependinta de bara de meniu
		MainWindow mainWindow = new MainWindow();
		if (!mainWindow.initialize(mainMenuBar)) {
			System.out.println("Could not initialize main window");
			System.exit(-1);
		}
		
		// Adaugam pe mainWindow ca fiind action listener pentru meniu
		// Cu alte cuvinte, daca se intampla o actiune in meniu, mainWindow va
		// reactiona cu alta actiune
		mainMenuBar.addActionListener(mainWindow);
		
		// Setam fereastra principala vizibila
		mainWindow.setVisible(true);
	}
}
