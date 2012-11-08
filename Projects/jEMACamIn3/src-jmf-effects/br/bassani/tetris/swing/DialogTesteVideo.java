package br.bassani.tetris.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

public class DialogTesteVideo extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3285364550839606598L;
	
	PainelBackgroundVideo painelVideo;
	
	public DialogTesteVideo(){
		super();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.out.println("Fechando");
				painelVideo.terminaPlayer();
				setVisible(false);
				dispose();
			}
		});
		painelVideo = new PainelBackgroundVideo();
		add(painelVideo);
		painelVideo.setFocusable(true);
		painelVideo.setRequestFocusEnabled(true);
	}
	
	public void iniciaPlayer(){
		painelVideo.iniciaPlayer();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DialogTesteVideo d = new DialogTesteVideo();
		d.setVisible(true);
		d.pack();
		d.iniciaPlayer();
	}

}
