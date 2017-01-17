package texteditor.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainMenu extends JMenuBar {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 8346195673578148977L;
	
	// Interfata care asculta la schimbari (e.g. apasari de butoane din interfata)
	private MyActionListener _listener;
	
	private boolean _initialized;
	private JMenu _fileMenu;
	private JMenu _editMenu;
	private JMenu _aboutMenu;
	
	private JMenuItem _exitButton;
	
	private JMenuItem _centerTextButton;
	private JMenuItem _leftTextButton;
	private JMenuItem _rightTextButton;
	
	private JMenuItem _statisticsButton;
	
	private JMenuItem _saveButton;
	private JMenuItem _loadButton;
	
	private JMenuItem _undoButton;
	private JMenuItem _redoButton;
	
	public MainMenu() {
		this._listener = null;
		this._fileMenu = null;
		this._editMenu = null;
		this._exitButton = null;
		this._saveButton = null;
		this._loadButton = null;
		this._undoButton = null;
		this._redoButton = null;
		this._statisticsButton = null;
		this._aboutMenu = null;
		this._initialized = false;
	}

	public boolean initialize() {
		this._exitButton = new JMenuItem("Exit");
		this._exitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_listener.buttonPressed(MyAction.EXIT);
			}
		});
		
		this._fileMenu = new JMenu("File");
		this._fileMenu.setMnemonic('F');
		
		this._editMenu = new JMenu("Edit");
		this._editMenu.setMnemonic('E');
		
		this._undoButton = new JMenuItem("Undo");
		this._undoButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_listener.buttonPressed(MyAction.UNDO);
			}
		});
		
		this._redoButton = new JMenuItem("Redo");
		this._redoButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_listener.buttonPressed(MyAction.REDO);
			}
		});
		
		this._centerTextButton = new JMenuItem("Center align");
		this._centerTextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_listener.buttonPressed(MyAction.CENTER_TEXT);
			}
		});
		
		this._rightTextButton = new JMenuItem("Right align");
		this._rightTextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_listener.buttonPressed(MyAction.RIGHT_TEXT);
			}
		});
		
		this._leftTextButton = new JMenuItem("Left align");
		this._leftTextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_listener.buttonPressed(MyAction.LEFT_TEXT);
			}
		});
		
		this._statisticsButton = new JMenuItem("Statistics");
		this._statisticsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_listener.buttonPressed(MyAction.STATS);
			}
		});
		
		this._editMenu.add(this._undoButton);
		this._editMenu.add(this._redoButton);
		this._editMenu.addSeparator();
		this._editMenu.add(this._centerTextButton);
		this._editMenu.add(this._rightTextButton);
		this._editMenu.add(this._leftTextButton);
		this._editMenu.addSeparator();
		this._editMenu.add(this._statisticsButton);
		
		
		this._saveButton = new JMenuItem("Save");
		this._saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_listener.buttonPressed(MyAction.SAVE);
			}
		});
		
		this._loadButton = new JMenuItem("Load");
		this._loadButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_listener.buttonPressed(MyAction.LOAD);
			}
		});
		
		this._fileMenu.add(this._saveButton);
		this._fileMenu.add(this._loadButton);
		this._fileMenu.addSeparator();
		this._fileMenu.add(this._exitButton);
		
		this._aboutMenu = new JMenu("About");
		this._aboutMenu.setMnemonic('A');
		
		this.add(this._fileMenu);
		this.add(this._editMenu);
		this.add(this._aboutMenu);
		
		return (this._initialized = true);
	}
	
	public boolean addActionListener(MyActionListener listener) {
		if (!this._initialized)
			return false;
		
		this._listener = listener;
		
		return true;
	}
}
