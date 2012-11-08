package br.bassani.jmf.efeitos;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;

import javax.media.Codec;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class CodecsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2109433583109851633L;

	public CodecsDialog(Codec[] codecs){
		super();
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.setTitle(getName());
		this.setLayout(new GridBagLayout());
		
		if(codecs.length==0){
			add(adicionaPanelVazio());
		}
		
		for(int i=0; i<codecs.length; i++){
			add(adicionaPanelConfiguracao(codecs[i]));
		}
		
		this.setVisible(true);
		this.pack();
	}
	
	public JPanel adicionaPanelConfiguracao(Codec c){
		JPanel p = new JPanel(new BorderLayout(5,5));
		p.setBorder(new TitledBorder(c.getName()));
		if(c instanceof IConfiguracao){
			IConfiguracao configCodec = (IConfiguracao)c;
			p.add(configCodec.openPanelConfiguracao(), BorderLayout.CENTER);
		}else{
			p.add(new JLabel("Sem opcoes de configuracao."), BorderLayout.CENTER);
		}
		return p;
	}
	
	private JPanel adicionaPanelVazio(){
		JPanel p = new JPanel(new BorderLayout(5,5));
		p.setBorder(new TitledBorder("Nenhum codec"));
		p.add(new JLabel("Não foi adicionado nenhum codec."), BorderLayout.CENTER);
		return p;
	}
	
}
