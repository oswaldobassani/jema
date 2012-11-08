package br.bassani.jmf.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public abstract class AFrame extends JFrame implements IFrame, WindowListener {

	public AFrame(){
		super();
	}
	
	public AFrame(String titulo){
		super(titulo);
	}

	public void windowActivated(WindowEvent evento) {
		// TODO Auto-generated method stub
	}

	public void windowClosed(WindowEvent evento) {
		// TODO Auto-generated method stub
	}

	public void windowClosing(WindowEvent evento) {
		// TODO Auto-generated method stub
	}

	public void windowDeactivated(WindowEvent evento) {
		// TODO Auto-generated method stub
	}

	public void windowDeiconified(WindowEvent evento) {
		// TODO Auto-generated method stub
	}

	public void windowIconified(WindowEvent evento) {
		// TODO Auto-generated method stub
	}

	public void windowOpened(WindowEvent evento) {
		// TODO Auto-generated method stub
	}

	public void exit() {
		// TODO Auto-generated method stub
	}

}
