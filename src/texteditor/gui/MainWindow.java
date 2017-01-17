package texteditor.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import texteditor.model.DocumentModel;

public class MainWindow extends JFrame implements MyActionListener {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 2907743952161687854L;

	private boolean _initialized;
	private MainMenu _mainMenuBar;

	// Manager care se ocupa cu memorarea si aplicarea schimbarilor de genul undo/redo
	private UndoManager _undoManager;

	// Prezentarea este un textpane in care putem introduce text si putem citi documentul
	private JTextPane _view;
	
	// Modelul este reprezentarea interna si este bazat pe DefaultStyledDocument din Java
	private DocumentModel _model;

	public MainWindow() {
		this._mainMenuBar = null;
		this._undoManager = null;
		this._model = null;
		this._view = null;
		this._initialized = false;
	}

	public boolean initialize(MainMenu mainMenu) {

		if (mainMenu == null)
			return false;

		this._undoManager = new UndoManager();

		this._mainMenuBar = mainMenu;

		this.setTitle("TextEditor v0.1");
		this.setSize(800, 600);

		this.setJMenuBar(this._mainMenuBar);

		// Cream modelul si adaugam managerul undo/redo
		this._model = new DocumentModel();
		this._model.addUndoableEditListener(new UndoableEditListener() {

			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				_undoManager.addEdit(e.getEdit());

			}
		});

		// cream prezentarea si adaugam modelul la prezentare
		// acum prezentarea va reflecta modelul
		this._view = new JTextPane();
		// view este observer pentru model
		this._view.setStyledDocument(this._model);
		
		// adaugam binding-uri pentru prezentare pentru actiunile de undo
		this._view.getActionMap().put("Undo", new AbstractAction("Undo") {

			/**
			 * Generated UID
			 */
			private static final long serialVersionUID = 4223842289730829190L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (_undoManager.canUndo()) {
						_undoManager.undo();
					}
				} catch (CannotUndoException ex) {
				}
			}
		});
		this._view.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

		// adaugam binding-uri pentru prezentare pentru actiunile de redo
		this._view.getActionMap().put("Redo", new AbstractAction("Redo") {

			/**
			 * Generated UID
			 */
			private static final long serialVersionUID = 4212510461136618549L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (_undoManager.canRedo()) {
						_undoManager.redo();
					}
				} catch (CannotUndoException ex) {
				}
			}
		});
		this._view.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
		
		this.add(this._view);
		return (this._initialized = true);
	}

	static int wordcount(String s) {
		int c = 0;
		char ch[] = new char[s.length()]; // in string especially we have to
											// mention the () after length
		for (int i = 0; i < s.length(); i++) {
			ch[i] = s.charAt(i);
			if (((i > 0) && (ch[i] != ' ') && (ch[i - 1] == ' ')) || ((ch[0] != ' ') && (i == 0)))
				c++;
		}
		return c;
	}

	// functia care raspunde la schimbari din partea meniului principal
	@Override
	public void buttonPressed(MyAction action) {

		if (!this._initialized)
			return;

		switch (action) {
		case CENTER_TEXT:
			// schimbam direct in prezentare iar prezentarea va propaga schimbarea
			// si in model
			this._view.setLogicalStyle(DocumentModel.centerAligned);
			break;
		case LEFT_TEXT:
			this._view.setLogicalStyle(DocumentModel.leftAligned);
			break;
		case RIGHT_TEXT:
			this._view.setLogicalStyle(DocumentModel.rightAligned);
			break;
		case SAVE:
			// Afisam un dialog pentru selectarea unui fisier
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Save to JSON");
			fc.showSaveDialog(this);

			// daca a fost selectat un fisier
			if (fc.getSelectedFile() != null) {

				// salvam documentul la acea cale
				String path = fc.getSelectedFile().getAbsolutePath();
				DocumentModel.saveToFile(_model, path);
			}

			break;
		case LOAD:
			JFileChooser fc1 = new JFileChooser();
			fc1.setDialogTitle("Load from JSON");
			fc1.showDialog(this, "Load");

			if (fc1.getSelectedFile() != null) {

				String path = fc1.getSelectedFile().getAbsolutePath();

				this._model = DocumentModel.loadFromFile(path);
				this._view.setStyledDocument(this._model);
			}
			break;
		case STATS:
			// afisam un messagebox cu informatia legata de numarul de cuvinte
			JOptionPane.showMessageDialog(null, wordcount(this._view.getText()) + " words in this document");
			break;
		case UNDO:
			try {
				if (_undoManager.canUndo()) {
					_undoManager.undo();
				}
			} catch (CannotUndoException ex) {
			}
			break;
		case REDO:
			try {
				if (_undoManager.canRedo()) {
					_undoManager.redo();
				}
			} catch (CannotUndoException ex) {
			}
			break;
		case EXIT:
			System.exit(0);
			break;
		default:
			break;

		}
	}
}
