package br.bassani.tetris.swing;

import java.awt.Point;

import br.bassani.jmf.efeitos.rgb.conjuntos.linha.util.LinhaMedia;

public class MarcaDedo {
	
	private int tipoConjunto;
	private int tamanhoConjunto;
	private LinhaMedia linhaDedo;
	
	public MarcaDedo(int tipoConjunto, int tamanhoConjunto, LinhaMedia linhaDedo){
		this.tipoConjunto = tipoConjunto;
		this.tamanhoConjunto = tamanhoConjunto;
		this.linhaDedo = linhaDedo;
	}

	public LinhaMedia getLinhaDedo() {
		return linhaDedo;
	}
	
	public Point getPonto1() {
		return linhaDedo.getPonto1();
	}
	
	public Point getPonto2() {
		return linhaDedo.getPonto2();
	}

	public int getTamanhoConjunto() {
		return tamanhoConjunto;
	}

	public int getTipoConjunto() {
		return tipoConjunto;
	}
	
	
	
}
