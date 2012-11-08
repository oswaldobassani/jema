package br.bassani.tetris.swing;

public class PecaUtil {
	
	public static Peca criaPecaBarraVertical3x3(int tipo){
		return new Peca(new int[][]{
				{-1, tipo, -1},
				{-1, tipo, -1},
				{-1, tipo, -1}
		});
	}
	
	public static Peca criaPecaBarraVertical5x5(int tipo){
		return new Peca(new int[][]{
				{-1, -1, tipo, -1, -1},
				{-1, -1, tipo, -1, -1},
				{-1, -1, tipo, -1, -1},
				{-1, -1, tipo, -1, -1},
				{-1, -1, tipo, -1, -1}
		});
	}
	
	public static Peca criaPecaBarraHorizontal3x3(int tipo){
		return new Peca(new int[][]{
				{-1, -1, -1},
				{tipo, tipo, tipo},
				{-1, -1, -1}
		});
	}
	
	public static Peca criaPecaBarraHorizontal5x5(int tipo){
		return new Peca(new int[][]{
				{-1, -1, -1, -1, -1},
				{-1, -1, -1, -1, -1},
				{tipo, tipo, tipo, tipo, tipo},
				{-1, -1, -1, -1, -1},
				{-1, -1, -1, -1, -1}
		});
	}
	
	public static Peca criaPecaT3x3(int tipo){
		return new Peca(new int[][]{
				{tipo, tipo, tipo},
				{-1, tipo, -1},
				{-1, tipo, -1}
		});
	}
	
	public static Peca criaPecaT5x5(int tipo){
		return new Peca(new int[][]{
				{-1, tipo, tipo, tipo, -1},
				{-1, -1, tipo, -1, -1},
				{-1, -1, tipo, -1, -1},
				{-1, -1, tipo, -1, -1},
				{-1, -1, tipo, -1, -1}
		});
	}
	
	public static Peca criaPecaI3x3(int tipo){
		return new Peca(new int[][]{
				{tipo, tipo, tipo},
				{-1, tipo, -1},
				{tipo, tipo, tipo}
		});
	}
	
	public static Peca criaPecaI5x5(int tipo){
		return new Peca(new int[][]{
				{-1, tipo, tipo, tipo, -1},
				{-1, -1, tipo, -1, -1},
				{-1, -1, tipo, -1, -1},
				{-1, -1, tipo, -1, -1},
				{-1, tipo, tipo, tipo, -1}
		});
	}
}
