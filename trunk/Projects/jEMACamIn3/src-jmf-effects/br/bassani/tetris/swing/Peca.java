package br.bassani.tetris.swing;

import java.awt.Point;

public class Peca {
	
	private boolean visivel;
	private int linha, coluna;
	private int tamanho;
	private int[][] composicao;
	
	public Peca(int[][] composicao){
		visivel = false;
		linha = -1;
		coluna = -1;
		this.composicao = composicao;
		tamanho = composicao.length;
	}

	public int getColuna() {
		return coluna;
	}

	public void setColuna(int coluna) {
		this.coluna = coluna;
	}

	public int getLinha() {
		return linha;
	}

	public void setLinha(int linha) {
		this.linha = linha;
	}
	
	public Point getPosicao() {
		return new Point(linha, coluna);
	}

	public void setPosicao(Point ponto) {
		this.linha = ponto.x;
		this.coluna = ponto.y;
	}
	
	public void setPosicao(int linha, int coluna) {
		this.linha = linha;
		this.coluna = coluna;
	}

	public boolean isVisivel() {
		return visivel;
	}

	public void setVisivel(boolean visivel) {
		this.visivel = visivel;
	}

	public int[][] getComposicao() {
		return composicao;
	}

	public int getTamanho() {
		return tamanho;
	}
	
	public void giraComposicaoSentidoHorario(){
		if(tamanho==3){
			giraComposicaoSentidoHorario_3x3();
			return;
		}
		if(tamanho==5){
			giraComposicaoSentidoHorario_5x5();
			return;
		}

		int[][] nova = new int[tamanho][tamanho];
		for(int l=0; l<tamanho; l++){
			for(int c=0; c<tamanho; c++){
				//FIXME:
				nova[l][c] = composicao[l][c];
			}
		}
		composicao = nova;
	}
	
	private void giraComposicaoSentidoHorario_3x3(){
		composicao = new int[][]{
				{composicao[2][0], composicao[1][0], composicao[0][0]},
				{composicao[2][1], composicao[1][1], composicao[0][1]},
				{composicao[2][2], composicao[1][2], composicao[0][2]}
		};
	}
	
	private void giraComposicaoSentidoHorario_5x5(){
		composicao = new int[][]{
				{composicao[4][0], composicao[3][0], composicao[2][0], composicao[1][0], composicao[0][0]},
				{composicao[4][1], composicao[3][1], composicao[2][1], composicao[1][1], composicao[0][1]},
				{composicao[4][2], composicao[3][2], composicao[2][2], composicao[1][2], composicao[0][2]},
				{composicao[4][3], composicao[3][3], composicao[2][3], composicao[1][3], composicao[0][3]},
				{composicao[4][4], composicao[3][4], composicao[2][4], composicao[1][4], composicao[0][4]}
		};
	}
}
