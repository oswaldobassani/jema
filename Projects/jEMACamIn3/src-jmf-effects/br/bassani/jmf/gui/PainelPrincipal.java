package br.bassani.jmf.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class PainelPrincipal extends JPanel implements IPainel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3890624911625438403L;

	private AFrame frame;
	
	private PainelVideo painelVideo;
	
	public PainelPrincipal(AFrame frame){
		super();
		setLayout(new BorderLayout());
		
		this.frame = frame;
		
		painelVideo = new PainelVideo(frame);
		add(painelVideo, BorderLayout.CENTER);
	}
	
	public void finalizaPainel(){
		painelVideo.finalizaPainel();
	}

	public void exit() {
		frame.exit();
	}
}
