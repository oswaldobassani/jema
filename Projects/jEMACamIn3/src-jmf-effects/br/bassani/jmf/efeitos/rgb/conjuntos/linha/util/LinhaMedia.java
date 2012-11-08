package br.bassani.jmf.efeitos.rgb.conjuntos.linha.util;

import java.awt.Point;

import br.bassani.jmf.efeitos.rgb.conjuntos.linha.objetos.ConjuntoLinha;
import br.bassani.jmf.efeitos.rgb.conjuntos.linha.objetos.ConjuntoPixeis;



public class LinhaMedia {

	private Point ponto1;
	private Point ponto2;
	
	public LinhaMedia(ConjuntoPixeis conjuntos) {
		int numCL = conjuntos.getNumeroConjuntos();
		int[] pontosX = new int[numCL];
		int[] pontosY = new int[numCL];
		ConjuntoLinha cl;
		int colMin, colMax;
		for (int i = 0; i < numCL; i++) {
			cl = conjuntos.getConjuntoLinha(i);
			colMin = cl.getColunaInicial();
			colMax = cl.getColunaFinal();
			pontosX[i] = colMin + ((int) ((colMax - colMin) / 2));
			pontosY[i] = cl.getLinha();
		}
		encontraMelhorReta(numCL, pontosX, pontosY);
	}

	
	private float cost, sint, rho;
	private void encontraMelhorReta(int npoints, int[] xpoints, int[] ypoints) {
		float x, y, xm, ym, x2, y2, xy, xm2, ym2, a, b, c, t;

		/* Compute mean of coordinates and sum of their products: */
		xm = ym = x2 = y2 = xy = (float) 0.0;
		for (int i = 0; i < npoints; i++) {
			xm += x = xpoints[i];
			ym += y = ypoints[i];
			x2 += x * x;
			y2 += y * y;
			xy += x * y;
		}
		xm /= npoints;
		ym /= npoints;

		/* Compute parameters of the fitted line: */
		xm2 = xm * xm;
		ym2 = ym * ym;
		a = (float) 2.0 * (xy - npoints * xm * ym);
		b = x2 - y2 - npoints * (xm2 - ym2);
		c = (float) Math.sqrt(a * a + b * b);
		cost = (float) Math.sqrt((b + c) / (2.0 * c));
		if (Math.abs(cost) < 0.001) {
			cost = (float) 0.0;
			sint = (float) 1.0;
		} else
			sint = a / ((float) 2.0 * c * cost);

		Point[] pontos = new Point[2];
		pontos[0] = new Point(0, 0);
		pontos[1] = new Point(0, 0);

		/* Project the endpoints onto the line: */
		rho = -xm * sint + ym * cost;
		t = cost * xpoints[0] + sint * ypoints[0];
		ponto1 = new Point(0, 0);
		ponto1.x = Math.round(t * cost - rho * sint);
		ponto1.y = Math.round(t * sint + rho * cost);
		t = cost * xpoints[npoints - 1] + sint * ypoints[npoints - 1];
		ponto2 = new Point(0, 0);
		ponto2.x = Math.round(t * cost - rho * sint);
		ponto2.y = Math.round(t * sint + rho * cost);
	}

	public Point getPonto1() {
		return ponto1;
	}

	public Point getPonto2() {
		return ponto2;
	}

	public int comprimento() {
		double soma = Math.pow((ponto1.x - ponto2.x), 2);
		soma += Math.pow((ponto1.y - ponto2.y), 2);
		int compri = Math.round((float) Math.pow(soma, .5));
		return compri;
	}
	
}
