package br.bassani.tetris.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JPanel;

import br.bassani.jmf.efeitos.rgb.conjuntos.linha.util.LinhaMedia;

public class PainelTetris extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5649099315905119609L;
	
	private int painelHeight;
	private int painelWidth;

	private int videoHeight = 120;
	private int videoWidth = 160;
	
	private int pecaHeight = 20;
	private int pecaWidth = 20;
	
	private int numLinhas = 35;
	private int numColunas = 20;
	
	private int numTipos = 3;

	//TODO
//	private int numMinPecasAlinhadas = 5;
	
	private HashMap<Integer, Color> cores;
	private int[] tipos;
	
	private int[][] tabuleiro;
	
	private ArrayList<Peca> pecas;
	
	public Vector<MarcaDedo> marcasAnteriores;
	public Vector<MarcaDedo> marcasAtuais;
	
	public PainelTetris(){
		painelHeight = numLinhas * pecaHeight;
		painelWidth = numColunas * pecaWidth;
		
		cores = new HashMap<Integer, Color>();
		
		tipos = new int[numTipos];
		for(int i=0; i<numTipos; i++){
			tipos[i] = i;
			cores.put(tipos[i], getRandomColor());
		}
		
		tabuleiro = new int[numLinhas][numColunas];
		pecas = new ArrayList<Peca>();

		initTabuleiroVazio();
		
		initPecasExemplo();

		this.addKeyListener(new java.awt.event.KeyAdapter() { 
			public void keyPressed(java.awt.event.KeyEvent e) {
				eventoTeclado(e);
			}
		});
		
		marcasAnteriores = null;
		marcasAtuais = new Vector<MarcaDedo>();

	}

	public void initTabuleiroVazio(){
		for(int l=0; l<numLinhas; l++){
			for(int c=0; c<numColunas; c++){
				tabuleiro[l][c] = -1;
			}
		}
		pecas.removeAll(pecas);
	}
	
	public void initPecasExemplo(){	
		pecas.add(PecaUtil.criaPecaBarraVertical3x3(0));
		pecas.add(PecaUtil.criaPecaI3x3(1));
		pecas.add(PecaUtil.criaPecaBarraVertical3x3(2));
		pecas.add(PecaUtil.criaPecaBarraHorizontal3x3(1));
		pecas.add(PecaUtil.criaPecaT3x3(1));
		pecas.add(PecaUtil.criaPecaI3x3(2));
		pecas.add(PecaUtil.criaPecaBarraVertical3x3(2));
		pecas.add(PecaUtil.criaPecaI3x3(0));
		pecas.add(PecaUtil.criaPecaBarraVertical3x3(1));
		pecas.add(PecaUtil.criaPecaBarraHorizontal3x3(0));
		pecas.add(PecaUtil.criaPecaT3x3(2));
		pecas.add(PecaUtil.criaPecaI3x3(0));
	}
	
	public void setVideoSize(int videoWidth, int videoHeight){
		this.videoWidth = videoWidth;
		this.videoHeight = videoHeight;
	}
	
	public void addMarcaDedo(int tipoConjunto, int tamanhoConjunto, LinhaMedia linhaDedo){
		
		MarcaDedo marca = new MarcaDedo(tipoConjunto, tamanhoConjunto, linhaDedo);
		marcasAtuais.add(marca);
		
		if(marcasAnteriores==null) return;
		if(atual==null) return;
		
		for(MarcaDedo antiga : marcasAnteriores){
			if(antiga.getTipoConjunto()==tipoConjunto && Math.abs(antiga.getTamanhoConjunto()-tamanhoConjunto)<50){
				int dx, dy;
				double distancia;
				
				Point p2 = linhaDedo.getPonto2(); // [0-videoWidth x 0-videoHeight]
				
				if(atual==null) return;
				int linhaPeca = atual.getLinha();
				int colunaPeca = atual.getColuna();
				
				int pColunaDedo = numColunas * (videoWidth-p2.x) / videoWidth;
				int pLinhaDedo = numLinhas * (videoHeight-p2.y) / videoHeight;
				
				if(Math.abs(colunaPeca-pColunaDedo)<2 && Math.abs(linhaPeca-pLinhaDedo)<2){
					dx = p2.x - antiga.getPonto2().x;
					dy = p2.y - antiga.getPonto2().y;
					distancia = Math.sqrt(dx*dx+dy*dy);
					if(distancia>1.0){
						if(dx>dy){
							if(dx>0){
								moveRight();
								repaint();
							}else{
								moveLeft();
								repaint();
							}
						}else{
							if(dy>0){
								rotate();
								repaint();
							}else{
								moveDown();
								repaint();
							}
						}
						//marcasAnteriores.remove(antiga);
					}
				}
			}
		}
	}
	
	//private Object waitSync = new Object();
	//private boolean stateTransitionOK = true;
	//synchronized (waitSync) {
	//TODO
	//}
	
	MarcaDedo[] todasAtuais;
	
	public void fimQuadro(){
		marcasAnteriores = marcasAtuais;
		
		todasAtuais = marcasAtuais.toArray(new MarcaDedo[0]);
		
		marcasAtuais = new Vector<MarcaDedo>();
	}
	
	private Image backgroundImage = null;
	
	public void setImagemFundo(Image base_tetris){
		backgroundImage = base_tetris;
	}
	
	public void eventoTeclado(KeyEvent e) {
		System.out.println(e);
		if (e.getKeyChar()=='r') {		
			initTabuleiroVazio();
			repaint();
		}
		if (e.getKeyChar()=='e') {
			initPecasExemplo();
			repaint();
		}
		if (e.getKeyChar()=='-') {		
			int index = (int)(Math.random()*numTipos);
			if(index>=numTipos) index = numTipos-1;
			if(index<0) index = 0;
			int tipo = tipos[index];
			pecas.add(PecaUtil.criaPecaBarraHorizontal3x3(tipo));
			repaint();
		}
		if (e.getKeyChar()=='|') {
			int index = (int)(Math.random()*numTipos);
			if(index>=numTipos) index = numTipos-1;
			if(index<0) index = 0;
			int tipo = tipos[index];
			pecas.add(PecaUtil.criaPecaBarraVertical3x3(tipo));
			repaint();
		}
		if (e.getKeyChar()=='i') {		
			int index = (int)(Math.random()*numTipos);
			if(index>=numTipos) index = numTipos-1;
			if(index<0) index = 0;
			int tipo = tipos[index];
			pecas.add(PecaUtil.criaPecaI3x3(tipo));
			repaint();
		}
		if (e.getKeyChar()=='t') {
			int index = (int)(Math.random()*numTipos);
			if(index>=numTipos) index = numTipos-1;
			if(index<0) index = 0;
			int tipo = tipos[index];
			pecas.add(PecaUtil.criaPecaT3x3(tipo));
			repaint();
		}
		if(atual==null) return;
		if (e.getKeyCode()==KeyEvent.VK_LEFT) {
			moveLeft();
			repaint();
		}
		if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
			moveRight();
			repaint();
		}
		if (e.getKeyCode()==KeyEvent.VK_UP) {
			rotate();
			repaint();
		}				
		if (e.getKeyCode()==KeyEvent.VK_DOWN) {
			moveDown();
			repaint();
		}
	}
	
	public void moveLeft() {
		int l, c;
		l = atual.getLinha();
		c = atual.getColuna();
		boolean naoMover = false;
		if(c-1>0-atual.getTamanho()/2){
			
			int[][] composicao;
			int tamanho;
			int linhaCentro, colunaCentro;
			
			composicao = atual.getComposicao();
			tamanho = atual.getTamanho();
			linhaCentro = atual.getLinha();
			colunaCentro = atual.getColuna();
			
			int lg, cg;
			for(/*int*/ l=0, lg=linhaCentro-(tamanho/2); lg<numLinhas && l<tamanho; l++, lg++){
				if(lg>=0){
					for(/*int*/ c=0, cg=colunaCentro-(tamanho/2); cg<numColunas && c<tamanho; c++, cg++){
						if(cg>=0){
							if(cg-1>=0 && composicao[l][c]!=-1 && tabuleiro[lg][cg-1]!=-1){
								naoMover = true;
							}
						}
					}
				}
			}
			if(!naoMover){
				l = atual.getLinha();
				c = atual.getColuna();
				atual.setPosicao(l, c-1);
			}
		}
	}
	public void moveRight() {
		int l, c;
		l = atual.getLinha();
		c = atual.getColuna();
		boolean naoMover = false;
		if(c+1<numColunas-atual.getTamanho()/2){
			
			int[][] composicao;
			int tamanho;
			int linhaCentro, colunaCentro;
			
			composicao = atual.getComposicao();
			tamanho = atual.getTamanho();
			linhaCentro = atual.getLinha();
			colunaCentro = atual.getColuna();
			
			int lg, cg;
			for(/*int*/ l=0, lg=linhaCentro-(tamanho/2); lg<numLinhas && l<tamanho; l++, lg++){
				if(lg>=0){
					for(/*int*/ c=0, cg=colunaCentro-(tamanho/2); cg<numColunas && c<tamanho; c++, cg++){
						if(cg>=0){
							if(cg+1<numColunas && composicao[l][c]!=-1 && tabuleiro[lg][cg+1]!=-1){
								naoMover = true;
							}
						}
					}
				}
			}
			if(!naoMover){
				l = atual.getLinha();
				c = atual.getColuna();
				atual.setPosicao(l, c+1);
			}
		}
	}
	public void moveDown() {
		int l, c;
		l = atual.getLinha();
		c = atual.getColuna();
		boolean naoMover = false;
		if(l+1<numLinhas-atual.getTamanho()/2){
			
			int[][] composicao;
			int tamanho;
			int linhaCentro, colunaCentro;
			
			composicao = atual.getComposicao();
			tamanho = atual.getTamanho();
			linhaCentro = atual.getLinha();
			colunaCentro = atual.getColuna();
			
			int lg, cg;
			for(/*int*/ l=0, lg=linhaCentro-(tamanho/2); lg<numLinhas && l<tamanho; l++, lg++){
				if(lg>=0){
					for(/*int*/ c=0, cg=colunaCentro-(tamanho/2); cg<numColunas && c<tamanho; c++, cg++){
						if(cg>=0){
							if(lg+1<numLinhas && composicao[l][c]!=-1 && tabuleiro[lg+1][cg]!=-1){
								naoMover = true;
							}
						}
					}
				}
			}
			if(!naoMover){
				l = atual.getLinha();
				c = atual.getColuna();
				atual.setPosicao(l+1, c);
			}
		}
	}
	public void rotate() {
		atual.giraComposicaoSentidoHorario();
	}
	

	@Override
	public int getHeight() {
		return painelHeight;
	}
	@Override
	public int getWidth() {
		return painelWidth;
	}
	@Override
	public Dimension getMaximumSize() {
		return getSize();
	}
	@Override
	public Dimension getMinimumSize() {
		return getSize();
	}
	@Override
	public Dimension getPreferredSize() {
		return getSize();
	}
	@Override
	public Dimension getSize(Dimension size) {
		size = getSize();
		return getSize();
	}
	@Override
	public Dimension getSize() {
		return new Dimension(painelWidth, painelHeight);
	}

	private Color backGroundColor = Color.black;
	private Color backLinesColor = Color.white;
	
	@Override
	public void paint(Graphics graphics) {
		if(backgroundImage==null){
			graphics.setColor(backGroundColor);
			graphics.fillRect(0, 0, painelWidth, painelHeight);
		}else{
			//Graphics2D g2d = (Graphics2D) graphics;
			//g2d.drawImage(backgroundImage, 0, 0, this);
			graphics.drawImage(backgroundImage, 0, 0, this);
		}
		
		for(int l=0; l<numLinhas; l++){
			for(int c=0; c<numColunas; c++){
				int tipo = tabuleiro[l][c];
				if(tipo!=-1){
					graphics.setColor(backLinesColor);
					graphics.fillRect(c*pecaWidth, l*pecaHeight, pecaWidth, pecaHeight);
					graphics.setColor(cores.get(tipo));
					graphics.fillRect(c*pecaWidth+1, l*pecaHeight+1, pecaWidth-2, pecaHeight-2);
				}else{
					if(backgroundImage==null){
						graphics.setColor(backLinesColor);
						graphics.fillRect(c*pecaWidth, l*pecaHeight, pecaWidth, pecaHeight);
						graphics.setColor(backGroundColor);
						graphics.fillRect(c*pecaWidth+1, l*pecaHeight+1, pecaWidth-2, pecaHeight-2);
					}else{
						graphics.setColor(backLinesColor);
						graphics.drawRect(c*pecaWidth, l*pecaHeight, pecaWidth, pecaHeight);
					}
				}
			}
		}
		
		int[][] composicao;
		int tamanho;
		int linhaCentro, colunaCentro;
		for(Peca p : pecas){
			if(p.isVisivel()){
				composicao = p.getComposicao();
				tamanho = p.getTamanho();
				linhaCentro = p.getLinha();
				colunaCentro = p.getColuna();
				
				for(int l=0, lg=linhaCentro-(tamanho/2); lg<numLinhas && l<tamanho; l++, lg++){
					if(lg>=0){
						for(int c=0, cg=colunaCentro-(tamanho/2); cg<numColunas && c<tamanho; c++, cg++){
							if(cg>=0){
								int tipo = composicao[l][c];
								if(tipo!=-1){
									graphics.setColor(cores.get(tipo));
									graphics.fillRect(cg*pecaWidth+1, lg*pecaHeight+1, pecaWidth-2, pecaHeight-2);
								}
							}
						}
					}
				}
			}
		}
		
		if(todasAtuais!=null){
			for(MarcaDedo marca : todasAtuais){
				graphics.setColor(Color.black);
				Point p1 = marca.getPonto1(); // [0-videoWidth x 0-videoHeight]
				Point p2 = marca.getPonto2(); // [0-videoWidth x 0-videoHeight]
				
				int p1xConv = numColunas * (videoWidth-p1.x) / videoWidth;
				int p1yConv = numLinhas * (videoHeight-p1.y) / videoHeight;
				p1.x = p1xConv * pecaWidth + pecaWidth/2;
				p1.y = p1yConv * pecaHeight + pecaHeight/2;
				
				int p2xConv = numColunas * (videoWidth-p2.x) / videoWidth;
				int p2yConv = numLinhas * (videoHeight-p2.y) / videoHeight;
				p2.x = p2xConv * pecaWidth + pecaWidth/2;
				p2.y = p2yConv * pecaHeight + pecaHeight/2;
				
				graphics.drawLine(p1.x, p1.y, p2.x, p2.y);
				graphics.drawOval(p1.x-2, p1.y-2, 4, 4);
				graphics.setColor(Color.red);
				graphics.fillOval(p2.x-2, p2.y-2, 4, 4);
			}
		}
		//fimQuadro();
		
	}
	
	private static Color getRandomColor() {
		return new Color((int) (10 + Math.random() * 235), (int) (10 + Math.random() * 235), (int) (10 + Math.random() * 235));
	}
	
	ControleJogo controle = null;
	public void iniciaJogo(){
		if(controle==null){
			controle = new ControleJogo();
			controle.start();
		}
	}
	
	public void terminaJogo(){
		controle.rodando = false;
	}
	
	Peca atual = null;
	class ControleJogo extends Thread{
		boolean rodando; 
		public void run(){
			rodando = true;
			while(rodando){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(atual==null){
					if(pecas.size()>0){
						atual = pecas.get(0);
						if(!atual.isVisivel()){
							atual.setPosicao(0 - atual.getTamanho()/2, numColunas/2);
							atual.setVisivel(true);
						}
					}
				}else{
					int l, c;
					l = atual.getLinha();
					c = atual.getColuna();
					boolean removerPeca = false;
					if(l+1<numLinhas-atual.getTamanho()/2){
						
						int[][] composicao;
						int tamanho;
						int linhaCentro, colunaCentro;
						
						composicao = atual.getComposicao();
						tamanho = atual.getTamanho();
						linhaCentro = atual.getLinha();
						colunaCentro = atual.getColuna();
						
						int lg, cg;
						for(/*int*/ l=0, lg=linhaCentro-(tamanho/2); lg<numLinhas && l<tamanho; l++, lg++){
							if(lg>=0){
								for(/*int*/ c=0, cg=colunaCentro-(tamanho/2); cg<numColunas && c<tamanho; c++, cg++){
									if(cg>=0){
										if(lg+1<numLinhas && composicao[l][c]!=-1 && tabuleiro[lg+1][cg]!=-1){
											removerPeca = true;
										}
									}
								}
							}
						}
						if(!removerPeca){
							l = atual.getLinha();
							c = atual.getColuna();
							atual.setPosicao(l+1, c);
						}
					}else{
						removerPeca = true;
					}
					if(removerPeca){
						int[][] composicao;
						int tamanho;
						int linhaCentro, colunaCentro;
						
						composicao = atual.getComposicao();
						tamanho = atual.getTamanho();
						linhaCentro = atual.getLinha();
						colunaCentro = atual.getColuna();
						
						int lg, cg;
						for(/*int*/ l=0, lg=linhaCentro-(tamanho/2); lg<numLinhas && l<tamanho; l++, lg++){
							if(lg>=0){
								for(/*int*/ c=0, cg=colunaCentro-(tamanho/2); cg<numColunas && c<tamanho; c++, cg++){
									if(cg>=0){
										if(composicao[l][c]!=-1){
											tabuleiro[lg][cg] = composicao[l][c];
										}
									}
								}
							}
						}
						atual.setVisivel(false);
						pecas.remove(atual);
						atual = null;
					}
				}
				repaint();
			}
		}
	}
}
