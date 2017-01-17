package texteditor.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

public class DocumentModel extends DefaultStyledDocument {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 8873227802868749773L;
	
	public static Style rightAligned;
	public static Style leftAligned;
	public static Style centerAligned;
	
	public static void initialize() {
		// Facem un nou context de stiluri care ne permite sa adaugam si sa eliminam stiluri
		StyleContext sc = new StyleContext();
		// Definim un stil default si il preluam de la sistem (cel cu care vine sistemul cand porneste)
		Style defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
		
		// Definim cele trei stiluri (aliniere la dreapta, stanga si central)
		DocumentModel.centerAligned = sc.addStyle("Center", defaultStyle);
		StyleConstants.setAlignment(DocumentModel.centerAligned, StyleConstants.ALIGN_CENTER);
		
		DocumentModel.rightAligned = sc.addStyle("Right", defaultStyle);
		StyleConstants.setAlignment(DocumentModel.rightAligned, StyleConstants.ALIGN_RIGHT);
		
		DocumentModel.leftAligned = sc.addStyle("Left", defaultStyle);
		StyleConstants.setAlignment(DocumentModel.leftAligned, StyleConstants.ALIGN_LEFT);
	}
	
	// functie care converteste din reprezentarea interna a documentului din Java in propria reprezentare interna
	// care permite salvare in JSON. Este nevoie atat de document cat si de elementul curent pentru ca elementele
	// nu contin datele documentului ci doar datele despre stil. In document se gasesc inclusiv datele efective (adica textul).
	public static DocumentJSONElement parseElem(DocumentModel model, Element elem) throws BadLocationException {
		// Cream un nou element JSON intern
		DocumentJSONElement e = new DocumentJSONElement();
		
		// setam continutului elementului ca fiind textul aferent stilului curent
		e.content = model.getText(elem.getStartOffset(), elem.getEndOffset() - elem.getStartOffset());
		
		// cream o lista de elemente JSON copii
		e.children = new ArrayList<DocumentJSONElement>();
		
		// setam alinirea pe care o extragem din atributele elementului curent
		e.alignment = StyleConstants.getAlignment(elem.getAttributes());
		
		// setam indexii de start si de final ai elementului
		e.start = elem.getStartOffset();
		e.end = elem.getEndOffset();
		
		// daca elementul nu are copii atunci il intoarcem
		if (elem.isLeaf()) {
			return e;
		}
		
		// altfel, apelam functia si pentru copii lui
		for (int i = 0; i < elem.getElementCount(); i++) {
			e.children.add(parseElem(model, elem.getElement(i)));
		}
		
		return e;
	}
	
	// functie care trece prin structura JSON a unui document si populeaza documentul cu text si stil
	public static void parseJSONElement(DocumentModel model, DocumentJSONElement elem) throws BadLocationException {
		
		// Pentru fiecare element curent, cream un set de atribute care este identic cu cel pe care elemetul in avea
		// atunci cand l-am salvat.
		
		// Setul va contine doar informatii legate de aliniere pe care le extragem din elementul curent
		SimpleAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setAlignment(attr, elem.alignment);
		
		// Daca elementul este frunza atunci el contine toate stilurile necesare pentru a il putea introduce in document
		if (elem.children.size() == 0) {
			// in functie de aliniere, setam stilul logic al paragrafului
			if (elem.alignment == StyleConstants.ALIGN_CENTER)
				model.setLogicalStyle(elem.start, centerAligned);
			else if (elem.alignment == StyleConstants.ALIGN_RIGHT)
				model.setLogicalStyle(elem.start, rightAligned);
			else
				model.setLogicalStyle(elem.start, leftAligned);
			
			// intoducem in document continutul elementului la indexul din element
			model.insertString(elem.start, elem.content, attr);
		}
		
		// daca nu e frunza, atunci contiuam sa exploram arborele prin copii lui
		for (DocumentJSONElement el : elem.children) 
			parseJSONElement(model, el);
	}
	
	public static boolean saveToFile(DocumentModel model, String path) {
		// extrage elementul radacina din reprezentarea interna a documentului
		Element root = model.getDefaultRootElement();
		try {
			// gson este clasa care salveaza in JSON
			Gson g = new Gson();
			
			// converteste arborele intern intr-un alt arobore care poate fi salvat in JSON
			DocumentJSONElement newRoot = parseElem(model, root);
			
			// Deschid un fisier la calea aleasa
			FileWriter f = new FileWriter(new File(path));
			
			// Salvam structura interna JSON in fisier
			g.toJson(newRoot, f);
			f.close();
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return true;
	}
	
	public static DocumentModel loadFromFile(String path) {
		// Cream un nou document si o radacina interna
		DocumentModel model = new DocumentModel();
		DocumentJSONElement root;
		
		FileReader r;
		try {
			// Deschidem fisierul sursa
			r = new FileReader(new File(path));
			Gson g = new Gson();
			// citim din JSON radacina si structura arborescenta interna JSON
			root = g.fromJson(r, DocumentJSONElement.class);
			
			// initializam documentul cu o radacina interna al lui
			model.createDefaultRoot();
			
			// Populam documentul cu stiluri si text peste radacina creata mai sus
			parseJSONElement(model, root);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		// intoarcem documentul creat
		return model;
	}
}
