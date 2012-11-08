package br.bassani.tetris.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

public class DialogTeste extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3285364550839606598L;
	
	PainelTetris tetris;
	
	public DialogTeste(){
		super();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.out.println("Fechando");
				tetris.terminaJogo();
				setVisible(false);
				dispose();
			}
		});
		tetris = new PainelTetris();
		add(tetris);
		tetris.setFocusable(true);
		tetris.setRequestFocusEnabled(true);
	}
	
	public void iniciaJogo(){
		tetris.iniciaJogo();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DialogTeste d = new DialogTeste();
		d.setVisible(true);
		d.pack();
		d.iniciaJogo();
	}

}
