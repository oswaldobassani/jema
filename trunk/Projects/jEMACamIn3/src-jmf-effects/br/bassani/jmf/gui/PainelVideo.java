package br.bassani.jmf.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.Codec;
import javax.media.ConfigureCompleteEvent;
import javax.media.Control;
import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.UnsupportedPlugInException;
import javax.media.control.FrameGrabbingControl;
import javax.media.control.FramePositioningControl;
import javax.media.control.TrackControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.bassani.jmf.efeitos.CodecsDialog;
import br.bassani.jmf.gui.info.CodecPack;
import br.bassani.jmf.gui.info.UrlInfo;

import com.sun.media.BasicJMD;
import com.sun.media.BasicTrackControl;

public class PainelVideo extends JPanel implements IPainel, ControllerListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2679248499563184207L;
	
	private AFrame frame;
	
	private Object waitSync = new Object();
	private boolean stateTransitionOK = true;
	private Processor p;
	
	Component cc;
	Component vc;
	
	private TrackControl videoTrack = null;
	
	private FrameGrabbingControl frameGraberControl;
	private FramePositioningControl framePositionControl;
	
	private JComboBox urlCombo, codecArrayCombo;
	private JDialog codecDialog;
	
	private UrlInfo[] urls;
	private CodecPack[] codecsPacks;
	
	public PainelVideo(AFrame frame){
		setLayout(new BorderLayout());
		
		this.frame = frame;
		
		urls = UrlInfo.getUrls();
		codecsPacks = CodecPack.getCodecsPacks(frame);
		
		JPanel botoes = new JPanel(new GridLayout(3, 1));
		
		JPanel botoesSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton abrirVideoPredefinido, usarCodecSelecionado;
		botoesSuperior.add(new JLabel("Selecione o URL: "));
		urlCombo = new JComboBox(urls);
		botoesSuperior.add(urlCombo);
		abrirVideoPredefinido = new JButton("Usar url");
		abrirVideoPredefinido.setActionCommand("usarURL");
		abrirVideoPredefinido.addActionListener(this);
		botoesSuperior.add(abrirVideoPredefinido);
		
		JPanel botoesCentro = new JPanel(new FlowLayout(FlowLayout.CENTER));
		botoesCentro.add(new JLabel("Selecione o Codec[]: "));
		codecArrayCombo = new JComboBox(codecsPacks);
		botoesCentro.add(codecArrayCombo);
		usarCodecSelecionado = new JButton("Usar Codec Pack");
		usarCodecSelecionado.setActionCommand("usarCodec");
		usarCodecSelecionado.addActionListener(this);
		botoesCentro.add(usarCodecSelecionado);
		
		botoes.add(botoesCentro);
		botoes.add(botoesSuperior);
		
		JPanel botoesInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton abrirVideo, capturaFrame, exportaVideo, resetGUI;
		abrirVideo = new JButton("Selecionar Arquivo Video");
		abrirVideo.setActionCommand("abrirVideo");
		abrirVideo.addActionListener(this);
		botoesInferior.add(abrirVideo);
		botoesInferior.add(new JLabel("  "));
		capturaFrame = new JButton("SnapShot");
		capturaFrame.setActionCommand("capturaFrame");
		capturaFrame.addActionListener(this);
		//FIXME: Nao esta funcionando.
		capturaFrame.setEnabled(false);
		botoesInferior.add(capturaFrame);
		exportaVideo = new JButton("Exportar");
		exportaVideo.setActionCommand("exportaVideo");
		exportaVideo.addActionListener(this);
		exportaVideo.setEnabled(false);
		botoesInferior.add(exportaVideo);
		resetGUI = new JButton("Reset GUI");
		resetGUI.setActionCommand("resetGUI");
		resetGUI.addActionListener(this);
		botoesInferior.add(resetGUI);
		
		botoes.add(botoesInferior);
		
		add(botoes, BorderLayout.NORTH);
		
		initGUI();
		
		//setVisible(true);
	}
	
	private void initGUI(){
		ImageIcon iconHand = new ImageIcon("imagens/Hand.png");
		vc = new JLabel(iconHand, JLabel.CENTER);
		add(vc, BorderLayout.CENTER);
		cc = new JLabel("<html><b>Nenhum Video Selecionado!</b><br/>Selecione um codec e abra um video.</html>", JLabel.CENTER);
		add(cc, BorderLayout.SOUTH);
	}
	
	
	private boolean abrirCodecConfig = true;
    private Codec[] codec = {};
	

	/**
     * Given a media locator, create a processor and use that processor
     * as a player to playback the media.
     *
     * During the processor's Configured state, the RotationEffect is
     * inserted into the video track.
     *
     * Much of the code is just standard code to present media in JMF.
     */
    public boolean open(MediaLocator ml) {
		try {
		    p = Manager.createProcessor(ml);
		} catch (Exception e) {
		    System.err.println("Failed to create a processor from the given url: " + e);
		    return false;
		}
	
		p.addControllerListener(this);
	
		// Put the Processor into configured state.
		p.configure();
		if (!waitForState(Processor.Configured)) {
		    System.err.println("Failed to configure the processor.");
		    return false;
		}
	
		// So I can use it as a player.
		p.setContentDescriptor(null);
	
		// Obtain the track controls.
		TrackControl tc[] = p.getTrackControls();
	
		if (tc == null) {
		    System.err.println("Failed to obtain track controls from the processor.");
		    return false;
		}
	
		// Search for the track control for the video track.
		for (int i = 0; i < tc.length; i++) {
			System.out.println("TrackControl["+i+"]: "+tc[i].getClass().toString()+" ("+tc[i].getClass().getSuperclass().toString()+")");
			if (tc[i] instanceof BasicTrackControl){
				//((BasicTrackControl)tc[i]).stopTrack();
			}
		    if (tc[i].getFormat() instanceof VideoFormat) {
		    	videoTrack = tc[i];
		    	//break;
		    }
		}
		if (videoTrack == null) {
		    System.err.println("The input media does not contain a video track.");
		    return false;
		}
		System.err.println("Video format: " + videoTrack.getFormat());
	
		adicionaCodecs(videoTrack);
		
		Control c[] = p.getControls();
		for (int i = 0; i < c.length; i++) {
			System.out.println("Control["+i+"]: "+c[i].getClass().toString()+" ("+c[i].getClass().getSuperclass().toString()+")");
			//FramePositioningControl
			//FrameRateControl
			//BitRateControl
			if (c[i] instanceof BasicJMD){
				//Exibe e monitora os PlugIns
				// >> BasicJMD extends Panel implements JMD, WindowListener
				BasicJMD basicJMD = (BasicJMD)c[i];
				aplicaListenerBotaoPlugin(basicJMD);
				//Component[] componentes = basicJMD.getComponents();
			}
			//BasicTrackControl
			if (c[i] instanceof FramePositioningControl){
				framePositionControl = (FramePositioningControl)c[i];
			}
		}
		
		// Realize the processor.
		p.prefetch();
		if (!waitForState(Controller.Prefetched)) {
		    System.err.println("Failed to realize the processor.");
		    return false;
		}
	
		// Display the visual & control component if there's one.
	
		if ((vc = p.getVisualComponent()) != null) {
		    add(vc, BorderLayout.CENTER);
		    setOpaque(false);
		    setBackground(Color.yellow);
		    vc.setBackground(Color.blue);
		    
		    vc.repaint();
		}
	
		if ((cc = p.getControlPanelComponent()) != null) {
		    add(cc, BorderLayout.SOUTH);
		    System.out.println(cc + " " + cc.getClass());
		    if(cc instanceof Container){
		    	Container cont = (Container)cc;
		    	aplicaListenerBotaoControlPanel(cont);
		    }
		    
		    cc.repaint();
		}
	
		repaint();
		frame.pack();
		
		/*Control */
		c = p.getControls();
		System.out.println(" -- Prefetched -- ");
		for (int i = 0; i < c.length; i++) {
			System.out.println("Control["+i+"]: "+c[i].getClass().toString()+" ("+c[i].getClass().getSuperclass().toString()+")");
			//com.sun.media.renderer.video.GDIRenderer ( **FrameGrabbingControl )
			//FramePositioningControl
			//FrameRateControl
			//BitRateControl
			//BasicTrackControl
			if(c[i] instanceof FrameGrabbingControl){
				System.out.println("Control["+i+"]: **FrameGrabbingControl");
				if(frameGraberControl==null){
					frameGraberControl = (FrameGrabbingControl)c[i];
				}
			}
		}
		
		// Start the processor.
		p.start();

		return true;
    }

    private void aplicaListenerBotaoPlugin(Container painel){
    	System.out.println(painel);
    	Component[] componentes = painel.getComponents();
    	for(int i=0; i<componentes.length; i++){
    		System.out.println(componentes[i]);
    		if(componentes[i] instanceof Button){
    			//Botao com o Nome de Algum Plugin
    			//Button
    			Button b = (Button)componentes[i];
    			String nome = b.getLabel();
    			System.out.println("Nomes Botoes: "+nome);
    		}else if(componentes[i] instanceof Container){
    			aplicaListenerBotaoPlugin((Container)componentes[i]);
    		}
    	}
    }
    
    private void aplicaListenerBotaoControlPanel(Container painel){
    	System.out.println(painel);
    	Component[] componentes = painel.getComponents();
    	for(int i=0; i<componentes.length; i++){
    		System.out.println(componentes[i]);
    		if(componentes[i] instanceof Button){
    			//Botao com o Nome de Algum Plugin
    			//Button
    			Button b = (Button)componentes[i];
    			String nome = b.getLabel();
    			System.out.println("Nomes Botoes: "+nome);
    		}else if(componentes[i] instanceof Container){
    			aplicaListenerBotaoPlugin((Container)componentes[i]);
    		}
    	}
    }

    //Image Grabbing
    BufferToImage conversorBuff2Img;
    int count = 0;
    //-
    public void actionPerformed(ActionEvent evento) {
		String comando = evento.getActionCommand();
		if(comando.equals("capturaFrame")){
			if(frameGraberControl!=null){
				if(conversorBuff2Img==null){
					conversorBuff2Img = new BufferToImage((VideoFormat)videoTrack.getFormat());
				}
				if(conversorBuff2Img!=null){
					Buffer buff = frameGraberControl.grabFrame();
					if(buff!=null){
						BufferedImage buffImg = null;
						int modo = 1;
						if(modo == 0){
							Image i = conversorBuff2Img.createImage(buff);
							Dimension size = ((VideoFormat)videoTrack.getFormat()).getSize();
							buffImg = new BufferedImage(size.width, size.height, BufferedImage.TYPE_3BYTE_BGR);
							buffImg.getGraphics().drawImage(i, 0, 0, this);
						}else if(modo == 1){
							buffImg = (BufferedImage)conversorBuff2Img.createImage(buff);
						}
						try {
							ImageIO.write(buffImg, "png", new File("snapshot_"+count+".png"));
							count++;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			//Teste
			if(p.getDataOutput()==null){
				System.out.println("EXTRA INFO: ** getDataOutput() == NULL");
			}else{
				System.out.println("EXTRA INFO: ** getDataOutput() != NULL");
			}
			
		}else if(comando.equals("exportaVideo")){
			
		}else if(comando.equals("abrirVideo")){
			JFileChooser chooser = new JFileChooser(new File("."));
			int result = chooser.showOpenDialog(this);
			if(result==JFileChooser.APPROVE_OPTION){
				try{
					File file = chooser.getSelectedFile();
					
					if(p!=null){
						p.stop();
						this.remove(vc);
						this.remove(cc);
						p.close();
					}
					p = null;
					if(codecDialog!=null){
						codecDialog.dispose();
					}
					codecDialog = null;
					
					waitSync = new Object();
					stateTransitionOK = true;
					cc = null;
					vc = null;
					videoTrack = null;
					
					open(new MediaLocator(file.toURI().toURL()));
					repaint();
					
				}catch(MalformedURLException e){
					System.out.println(e);
				}
			}
		}else if(comando.equals("usarURL")){
			if(urlCombo.getSelectedItem()!=null){
				UrlInfo info = (UrlInfo)urlCombo.getSelectedItem();
				
				if(p!=null){
					p.stop();
					this.remove(vc);
					this.remove(cc);
					p.close();
				}
				p = null;
				if(codecDialog!=null){
					codecDialog.dispose();
				}
				codecDialog = null;
				
				waitSync = new Object();
				stateTransitionOK = true;
				cc = null;
				vc = null;
				videoTrack = null;
				
				open(new MediaLocator(info.url));
				repaint();
			}
		}else if(comando.equals("usarCodec")){
			if(codecArrayCombo.getSelectedItem()!=null){
				CodecPack pack = (CodecPack)codecArrayCombo.getSelectedItem();
				codec = pack.codecs;
			}
		}else if(comando.equals("resetGUI")){
			if(p!=null){
				p.stop();
				this.remove(vc);
				this.remove(cc);
				p.close();
			}
			p = null;
			if(codecDialog!=null){
				codecDialog.dispose();
			}
			codecDialog = null;
			
			waitSync = new Object();
			stateTransitionOK = true;
			cc = null;
			vc = null;
			videoTrack = null;
			
			initGUI();
			repaint();
			frame.pack();
		}
	}

	/**
     * Block until the processor has transitioned to the given state.
     * Return false if the transition failed.
     */
    boolean waitForState(int state) {
		synchronized (waitSync) {
		    try {
			while (p.getState() != state && stateTransitionOK)
			    waitSync.wait();
		    } catch (Exception e) {}
		}
		return stateTransitionOK;
    }


    /**
     * Controller Listener.
     */
    public void controllerUpdate(ControllerEvent evt) {
		if (evt instanceof ConfigureCompleteEvent ||
		    evt instanceof RealizeCompleteEvent ||
		    evt instanceof PrefetchCompleteEvent) {
		    synchronized (waitSync) {
			stateTransitionOK = true;
			waitSync.notifyAll();
		    }
		} else if (evt instanceof ResourceUnavailableEvent) {
		    synchronized (waitSync) {
			stateTransitionOK = false;
			waitSync.notifyAll();
		    }
		} else if (evt instanceof EndOfMediaEvent) {
			framePositionControl.seek(0);
			//p.stop();
			p.start();
		    //p.close();
		    //System.exit(0);
		}
		if (evt instanceof RealizeCompleteEvent) {
			//Procura Novos Controles
			Control c[] = p.getControls();
			System.out.println(" -- RealizeCompleteEvent -- ");
			for (int i = 0; i < c.length; i++) {
				System.out.println("Control["+i+"]: "+c[i].getClass().toString()+" ("+c[i].getClass().getSuperclass().toString()+")");
				//com.sun.media.renderer.video.GDIRenderer ( **FrameGrabbingControl )
				//FramePositioningControl
				//FrameRateControl
				//BitRateControl
				//BasicTrackControl
				if(c[i] instanceof FrameGrabbingControl){
					System.out.println("Control["+i+"]: **FrameGrabbingControl");
					if(frameGraberControl==null){
						frameGraberControl = (FrameGrabbingControl)c[i];
					}
				}
			}
		}
    }

    private void adicionaCodecs(TrackControl videoTrack){
    	//Instantiate and set the frame access codec to the data flow path.
    	try {
    		if(abrirCodecConfig){
    			codecDialog = new CodecsDialog(codec);
    		}
    	    videoTrack.setCodecChain(codec);
    	} catch (UnsupportedPlugInException e) {
    	    System.err.println("The processor does not support effects.");
    	}
    }

    public void finalizaPainel(){
    	if(p!=null){
    		System.out.println("Stopping the processor ...");
			p.stop();
			p.close();
			//New: deallocate
			p.deallocate();
    	}
	}
	
	public void exit(){
		frame.exit();
	}
	
}
