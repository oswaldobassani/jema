package br.bassani.jmf.efeitos.rgb.conjuntos.linha.objetos;

import java.awt.Point;

public class ConjuntoLinha {
	
	public static final int TipoContato_FORTE = 0;
	public static final int TipoContato_SUAVE = 1;
	private static int tipoContato = TipoContato_FORTE;

	private int tipo;
	private int numColunaInicial, numColunaFinal;
	private int numLinha;

	public ConjuntoLinha(int numL, int numCol0, int numCol1, int type) {
		tipo = type;
		numColunaInicial = numCol0;
		numColunaFinal = numCol1;
		numLinha = numL;
	}

	public static void setTipoContato(int _tipoContato) {
		switch (_tipoContato) {
		case TipoContato_FORTE:
			tipoContato = _tipoContato;
			break;
		case TipoContato_SUAVE:
			tipoContato = _tipoContato;
			break;
		default:
			System.out
					.println("Tipo de contato desconhecido - Usando default : TipoContato_FORTE ("
							+ TipoContato_FORTE + ")");
			tipoContato = TipoContato_FORTE;
			break;
		}
	}

	public static int getTipoContato() {
		return tipoContato;
	}

	public boolean fazContato(ConjuntoLinha cl) {
		if (tipo == cl.tipo) {
			if (Math.abs(numLinha - cl.numLinha) == 1) {
				if (tipoContato == TipoContato_SUAVE) {
					/**
					 * Condicao ideal para detectar linhas
					 */
					if (!(numColunaInicial > cl.numColunaFinal + 1 || numColunaFinal < cl.numColunaInicial - 1)) {
						return true;
					}
				} else if (tipoContato == TipoContato_FORTE) {
					/**
					 * Condicao ideal para detectar conjuntos bem definidos
					 */
					if (!(numColunaInicial > cl.numColunaFinal || numColunaFinal < cl.numColunaInicial)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean fazContatoTipoIndiferente(ConjuntoLinha cl) {
		if (Math.abs(numLinha - cl.numLinha) == 1) {
			if (tipoContato == TipoContato_SUAVE) {
				/**
				 * Condicao ideal para detectar linhas
				 */
				if (!(numColunaInicial > cl.numColunaFinal + 1 || numColunaFinal < cl.numColunaInicial - 1)) {
					return true;
				}
			} else if (tipoContato == TipoContato_FORTE) {
				/**
				 * Condicao ideal para detectar conjuntos bem definidos
				 */
				if (!(numColunaInicial > cl.numColunaFinal || numColunaFinal < cl.numColunaInicial)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean estaContido(ConjuntoLinha cl) {
		if (numLinha == cl.numLinha) {
			if (!(numColunaInicial > cl.numColunaFinal || numColunaFinal < cl.numColunaInicial)) {
				return true;
			}
		}
		return false;
	}

	public int getLinha() {
		return numLinha;
	}

	public int getColunaInicial() {
		return numColunaInicial;
	}

	public int getColunaFinal() {
		return numColunaFinal;
	}

	public void setTipo(int _tipo) {
		tipo = _tipo;
	}
	
	public int getTipo() {
		return tipo;
	}

	public int getArea() {
		return numColunaFinal - numColunaInicial + 1;
	}

	public Point getCentro() {
		Point c = new Point();
		c.setLocation(((double) numColunaInicial)
				+ ((numColunaFinal - numColunaInicial) / 2.0), numLinha);
		return c;
	}

	public String toString() {
		return "ConjuntoLinha ( " + numLinha + ", " + numColunaInicial + " - "
				+ numColunaFinal + ") : " + tipo;
	}
}
