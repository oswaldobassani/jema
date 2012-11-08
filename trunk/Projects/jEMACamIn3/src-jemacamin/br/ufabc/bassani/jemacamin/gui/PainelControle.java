package br.ufabc.bassani.jemacamin.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.media.Codec;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.bassani.jmf.gui.info.CodecPack;
import br.bassani.jmf.gui.info.UrlInfo;


public class PainelControle extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6318252341776813602L;

	JFrame frame;
	
	Component cc;
	Component vc;
	
	private JComboBox urlCombo, codecArrayCombo;
	
	private UrlInfo[] urls;
	private CodecPack[] codecsPacks;
	
	private UrlInfo urlSelecionada;
    private CodecPack codecPackSelecionado;

	public PainelControle(JFrame frame) {
		super();
		setLayout(new BorderLayout());
		
		this.frame = frame;
		
		urls = UrlInfo.getUrls();
		codecsPacks = CodecPack.getCodecsPacks(this.frame);

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
		
		urlSelecionada = (UrlInfo)urlCombo.getSelectedItem();
	    codecPackSelecionado = (CodecPack)codecArrayCombo.getSelectedItem();;
	}
	
	private void initGUI(){
		vc = new JLabel(" - - - - - ", JLabel.CENTER);
		add(vc, BorderLayout.CENTER);
		cc = new JLabel("<html><b>Nenhum Video Selecionado!</b><br/>Selecione um codec e abra um video.</html>", JLabel.CENTER);
		add(cc, BorderLayout.SOUTH);
	}
	
	public void actionPerformed(ActionEvent evento) {
		String comando = evento.getActionCommand();
		if(comando.equals("capturaFrame")){
			
		}else if(comando.equals("exportaVideo")){
			
		}else if(comando.equals("abrirVideo")){
			JFileChooser chooser = new JFileChooser(new File("."));
			int result = chooser.showOpenDialog(this);
			if(result==JFileChooser.APPROVE_OPTION){

			}
		}else if(comando.equals("usarURL")){
			if(urlCombo.getSelectedItem()!=null){
				urlSelecionada = (UrlInfo)urlCombo.getSelectedItem();
				System.out.println(urlSelecionada.nome);
			}
		}else if(comando.equals("usarCodec")){
			if(codecArrayCombo.getSelectedItem()!=null){
				codecPackSelecionado = (CodecPack)codecArrayCombo.getSelectedItem();
				System.out.println(codecPackSelecionado.nome);
			}
		}else if(comando.equals("resetGUI")){
			initGUI();
			repaint();
			frame.pack();
		}
	}

	/**
	 * @return the urlSelecionada
	 */
	public UrlInfo getUrlSelecionada() {
		return urlSelecionada;
	}

	/**
	 * @return the codecPackSelecionado
	 */
	public CodecPack getCodecPackSelecionado() {
		return codecPackSelecionado;
	}
	
}
