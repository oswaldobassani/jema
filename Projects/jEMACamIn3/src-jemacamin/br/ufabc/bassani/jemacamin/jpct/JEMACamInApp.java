package br.ufabc.bassani.jemacamin.jpct;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import br.ufabc.bassani.jemacamin.gui.PainelControle;
import br.ufabc.bassani.jemacamin.gui.info.SkeletalModelInfo;
import br.ufabc.bassani.jemacamin.jpct.components.Viewport;

public class JEMACamInApp extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4461344401070599383L;

	protected JMenuBar menuBar;
	protected JMenu menuFile;
	//Cube Models
	protected JMenuItem menuFileOpenCube, menuFileOpenEsfera, menuFileOpenGusExtra;
	//Exit MenuItem
	protected JMenuItem menuFileOpenExit;
	//Load Model Chooser
	protected JFileChooser fileChooser = new JFileChooser(".");
	
	// The 3D viewport swing component
	protected Viewport viewport;
	
	// Painel de Controle (2D - Swing)
	private PainelControle painelControle;
	
	private SkeletalModelInfo[] models;
	
	private Component controlComp;
	
	public JEMACamInApp(){
		super("jEMA - Esqueleto Modelo Animação");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout());
		
		painelControle = new PainelControle(this);
		add(painelControle, BorderLayout.NORTH);
		
		JPanel painel3d = new JPanel();
		painel3d.setLayout(new FlowLayout(FlowLayout.CENTER));
		viewport = new Viewport();
		painel3d.add(viewport);
		//add(painel3d, BorderLayout.CENTER);
		add(painel3d, BorderLayout.SOUTH);
		
		controlComp = new JPanel(new FlowLayout(FlowLayout.CENTER));
		((JPanel)controlComp).add(new JLabel(" - - - Barra de controle de Vídeo - - - "));
		//add(controlComp, BorderLayout.SOUTH);
		add(controlComp, BorderLayout.CENTER);
		
		// Load models
		models = SkeletalModelInfo.getModels();
		createMenuBar();
		
		pack();
        setResizable(false);
        setVisible(true);
	}
	
	public static void main(String[] args){
		new JEMACamInApp();
	}
	
	
	public void createMenuBar(){
		//Create the menu bar.
		menuBar = new JMenuBar();
		
		//JMenuBar menuBar;
		
		//Build the first menu.
		menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menuFile);
		
		for(SkeletalModelInfo model : models){
			JMenuItem menuItem = new JMenuItem(model.menuTitle);
			menuItem.setActionCommand(model.menuActionCommand);
			menuItem.addActionListener(this);
			menuFile.add(menuItem);
		}
		
		menuFile.addSeparator();
		
		menuFileOpenExit = new JMenuItem("Exit");
		menuFileOpenExit.addActionListener(this);
		menuFile.add(menuFileOpenExit);

		setJMenuBar(menuBar);
	}
	
	public void actionPerformed(ActionEvent e){
		
		String aCommand = e.getActionCommand();
		for(SkeletalModelInfo model : models){
			if(model.menuActionCommand.equals(aCommand)){
				boolean initVideo = false;
				if(model.apiType==SkeletalModelInfo.API.SkeletalAPI){
					//Mesh
		       		File file = new File(model.filesDir + model.meshFileName);
		       		//Skel
		       		File skel = new File(model.filesDir + model.skelFileName);
		       		setName(model.name);
		       		viewport.loadModel(model.modelObjectName, file, skel, model.scale, model.useDefaultLoader);
		       		// Ready!
		       		initVideo = true;
				}else if(model.apiType==SkeletalModelInfo.API.Bones){
					
					//Mesh
		       		// File file = new File(model.filesDir + model.meshFileName);
		       		//Skel
		       		// File skel = new File(model.filesDir + model.skelFileName);
		       		setName(model.name);
		       		viewport.loadModel_BonesAPI(model.modelObjectName, model.filesDir, model.meshFileName, "ninja", model.textureFileName);

					// Ready!
		       		initVideo = true;
				}
				if(initVideo){
					viewport.setMidiaInputAndCodecs(painelControle.getUrlSelecionada(), painelControle.getCodecPackSelecionado());
		       		viewport.abreMidia();
		       		//Auto init do video (Opcional, podemos comentar)
		       		//viewport.iniciaPlayer();
		       		
		       		remove(controlComp);
		       		controlComp = null;
		       		
		       		try {
						controlComp = viewport.getControlComponent();
						//add(controlComp, BorderLayout.SOUTH);
						add(controlComp, BorderLayout.CENTER);
						controlComp.repaint();
					} catch (Exception e1) {
						System.out.println("fail control video component -> " + e1);
					}
		    		
		    		getContentPane().repaint();
		    		this.pack();
				}
			}
		}
		
		if(e.getSource() == menuFileOpenExit){
			this.setVisible(false);
			this.dispose();
			System.exit(0);
		}
		repaint();
	}
}
