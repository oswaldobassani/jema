package br.bassani.jmf.efeitos.rgb.conjuntos.linha.objetos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;


public class ConjuntoPixeis {


	private Vector<ConjuntoLinha> conjuntosLinha = null;

	private int numLinhaMax;

	private int numLinhaMin;

	private int numColunaMax;

	private int numColunaMin;

	private int tipo;

	private Color cor;

	private Point centro = null;

	private int area = -1;

	private static boolean debug = false;

	public ConjuntoPixeis(ConjuntoLinha cl0, Color color) {
		conjuntosLinha = new Vector<ConjuntoLinha>();
		conjuntosLinha.add(cl0);
		numLinhaMax = cl0.getLinha();
		numLinhaMin = cl0.getLinha();
		numColunaMax = cl0.getColunaFinal();
		numColunaMin = cl0.getColunaInicial();
		tipo = cl0.getTipo();
		cor = color;

		if (debug)
			System.out.println("Conjunto Criado");
		if (debug)
			System.out.println(cl0);
	}

	public int getNumeroConjuntos() {
		return conjuntosLinha.size();
	}

	public Vector<ConjuntoLinha> getConjuntos() {
		return conjuntosLinha;
	}

	public ConjuntoLinha getConjuntoLinha(int i) {
		return conjuntosLinha.get(i);
	}

	public boolean verificaSePertenceAoConjunto(ConjuntoLinha cl) {
		for (int i = 0; i < conjuntosLinha.size(); i++) {
			if (conjuntosLinha.get(i).fazContato(cl)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean verificaSePertenceAoConjuntoTipoIndiferente(ConjuntoLinha cl) {
		for (int i = 0; i < conjuntosLinha.size(); i++) {
			if (conjuntosLinha.get(i).fazContatoTipoIndiferente(cl)) {
				return true;
			}
		}
		return false;
	}

	public void setTipo(int _tipo) {
		for (int i = 0; i < conjuntosLinha.size(); i++){
			conjuntosLinha.get(i).setTipo(_tipo);
		}
		tipo = _tipo;
	}
	
	public boolean mesmoTipo(ConjuntoLinha cl) {
		return tipo == cl.getTipo();
	}

	public boolean estaContido(ConjuntoLinha cl) {
		for (int i = 0; i < conjuntosLinha.size(); i++) {
			if (conjuntosLinha.get(i).estaContido(cl)) {
				return true;
			}
		}
		return false;
	}


	public boolean contem(int x, int y) {
		ConjuntoLinha cl = new ConjuntoLinha(y, x, x, tipo);
		return estaContido(cl);
	}

	public void adicionaAoConjunto(ConjuntoLinha cl) {
		conjuntosLinha.add(cl);
		int linha = cl.getLinha();
		if (linha > numLinhaMax)
			numLinhaMax = linha;
		if (linha < numLinhaMin)
			numLinhaMin = linha;
		if (cl.getColunaFinal() > numColunaMax)
			numColunaMax = cl.getColunaFinal();
		if (cl.getColunaInicial() < numColunaMin)
			numColunaMin = cl.getColunaInicial();
		if (debug)
			System.out.println(cl);
	}

	public boolean adicionaSePertenceAoConjunto(ConjuntoLinha cl) {
		if (verificaSePertenceAoConjunto(cl)) {
			conjuntosLinha.add(cl);
			int linha = cl.getLinha();
			if (linha > numLinhaMax)
				numLinhaMax = linha;
			if (linha < numLinhaMin)
				numLinhaMin = linha;
			if (cl.getColunaFinal() > numColunaMax)
				numColunaMax = cl.getColunaFinal();
			if (cl.getColunaInicial() < numColunaMin)
				numColunaMin = cl.getColunaInicial();
			if (debug)
				System.out.println(cl);
			return true;
		} else
			return false;
	}


	public void adicionaConjunto(ConjuntoPixeis c) {
		for (int idC = 0; idC < c.getNumeroConjuntos(); idC++) {
				conjuntosLinha.add(c.getConjuntoLinha(idC));
		}
		if (c.numLinhaMax > numLinhaMax)
			numLinhaMax = c.numLinhaMax;
		if (c.numLinhaMin < numLinhaMin)
			numLinhaMin = c.numLinhaMin;
		if (c.numColunaMax > numColunaMax)
			numColunaMax = c.numColunaMax;
		if (c.numColunaMin < numColunaMin)
			numColunaMin = c.numColunaMin;
	}

	public int getArea() {
		if (area == -1) {
			int SomaArea = 0;
			for (int idCj = 0; idCj < getNumeroConjuntos(); idCj++) {
				SomaArea += conjuntosLinha.get(idCj).getArea();
			}
			area = SomaArea;
			return SomaArea;
		} else
			return area;
	}

	public Point getCentro() {
		if (centro == null) {
			centro = new Point(0, 0);
			int SomaArea = 0;
			Point tempP;
			int tempA;
			for (int idCj = 0; idCj < getNumeroConjuntos(); idCj++) {
				tempP = conjuntosLinha.get(idCj).getCentro();
				tempA = conjuntosLinha.get(idCj).getArea();
				centro.x = centro.x * SomaArea + tempA * tempP.x;
				centro.y = centro.y * SomaArea + tempA * tempP.y;
				SomaArea += tempA;
				centro.x = centro.x / SomaArea;
				centro.y = centro.y / SomaArea;
			}
			if (area == -1)
				area = SomaArea;
			return centro;
		} else
			return centro;
	}

	public int getTipo() {
		return tipo;
	}

	public int getLinhaMax() {
		return numLinhaMax;
	}

	public int getLinhaMin() {
		return numLinhaMin;
	}
	
	public int getNumeroLinhas() {
		return numLinhaMax-numLinhaMin;
	}

	public int getColunaMax() {
		return numColunaMax;
	}

	public int getColunaMin() {
		return numColunaMin;
	}

	public Rectangle getContorno() {
		return new Rectangle(numColunaMin, numLinhaMin, numColunaMax
				- numColunaMin + 1, numLinhaMax - numLinhaMin + 1);
	}

	public void setCor(Color _cor) {
		cor = _cor;
	}
	
	public Color getCor() {
		return cor;
	}

	public String toString() {
		if (centro == null)
			centro = getCentro();
		if (area == -1)
			area = getArea();
		String info = "C (" + centro.x + ", " + centro.y + ")";
		info += " A: " + area;
		info += " LxC: (" + (numLinhaMax - numLinhaMin + 1) + ", "
				+ (numColunaMax - numColunaMin + 1) + ")";
		info += " NConjs: " + getNumeroConjuntos();
		info += " Tipo: " + tipo;
		info += " Cor: (" + cor.getRed() + ", " + cor.getGreen() + ", "
				+ cor.getBlue() + ")";
		info += " Formacao:Linha";
		return info;
	}

	public void paint(Graphics g) {
			paint_Linhas(g);
	}

	public void paint_Linhas(Graphics g) {
		Color corCnj = getRandomColor();
		for (int j = 0; j < conjuntosLinha.size(); j++) {
			ConjuntoLinha cl = ((ConjuntoLinha) conjuntosLinha.get(j));
			g.setColor(corCnj);
			int largura = cl.getColunaFinal() - cl.getColunaInicial() + 1;
			g.fillRect(cl.getColunaInicial(), cl.getLinha(), largura, 1);
		}
	}

	public static Color getRandomColor() {
		return new Color((int) (10 + Math.random() * 235), (int) (10 + Math
				.random() * 235), (int) (10 + Math.random() * 235));
	}

	public int getAreaCobertaPor(ConjuntoPixeis cj) {
		return getAreaCobertaPor_Linha(cj);
	}

	private int getAreaCobertaPor_Linha(ConjuntoPixeis cj) {
		int areaCoberta = 0;
		for (int j = 0; j < conjuntosLinha.size(); j++) {
			ConjuntoLinha cl = ((ConjuntoLinha) conjuntosLinha.get(j));
			int y = cl.getLinha();
			for (int x = cl.getColunaInicial(); x <= cl.getColunaFinal(); x++) {
				if (cj.contem(x, y))
					areaCoberta++;
			}
		}
		return areaCoberta;
	}

}
