package br.bassani.jmf.gui;

import java.awt.event.WindowEvent;

public class FramePrincipal extends AFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8005958105994699442L;

	private IPainel painel;
	
	public FramePrincipal(){
		super("JMF CodecsEffects");
		
		addWindowListener(this);
		
		PainelPrincipal painelPrinc = new PainelPrincipal(this);
		painel = painelPrinc;
		
		setContentPane(painelPrinc);
	}
	
	public void addNotify(){
		super.addNotify();
		pack();
	}
	
	@Override
	public void windowClosing(WindowEvent evento) {
    	painel.finalizaPainel();
    	dispose();
    	System.exit(0);
    }

	@Override
	public void exit() {
		painel.finalizaPainel();
		System.exit(0);
	}

}
