package br.bassani.jmf.gui.info;

import java.awt.Color;
import java.util.Vector;

import javax.media.Codec;
import javax.swing.JFrame;

import br.bassani.jmf.efeitos.argb.ARGBPele;
import br.bassani.jmf.efeitos.argb.ARGBSubtracaoMediaDesvio;
import br.bassani.jmf.efeitos.argb.conjuntos.linha.ARGBConjuntosObjMemoComLinhas;
import br.bassani.jmf.efeitos.bayer.RGBBayer2TrueRGB;
import br.bassani.jmf.efeitos.bayer.RGBBayer2TrueRGBFase2;
import br.bassani.jmf.efeitos.bayer.RGBBayer2TrueRGBFase3;
import br.bassani.jmf.efeitos.bayer.RGBBayerPele;
import br.bassani.jmf.efeitos.rgb.CoresRGBPredefinidas;
import br.bassani.jmf.efeitos.rgb.HistogramaRGB;
import br.bassani.jmf.efeitos.rgb.NyARToolkitEffect;
import br.bassani.jmf.efeitos.rgb.RGBOCVCalibCorrecaoEstatica;
import br.bassani.jmf.efeitos.rgb.RGBOCVCalibTabuleiro;
import br.bassani.jmf.efeitos.rgb.RGBPele;
import br.bassani.jmf.efeitos.rgb.Rotacao90;
import br.bassani.jmf.efeitos.rgb.RotationEffect;
import br.bassani.jmf.efeitos.rgb.SubtracaoMediaDesvio;
import br.bassani.jmf.efeitos.rgb.Zoom2x;
import br.bassani.jmf.efeitos.rgb.conjuntos.linha.ConjuntosObjComLinhas;
import br.bassani.jmf.efeitos.rgb.conjuntos.linha.ConjuntosObjMemoComLinhas;
import br.bassani.jmf.efeitos.rgb.conjuntos.linha.ConjuntosOtimSemLinhas;
import br.bassani.jmf.efeitos.rgb.convolucao.MediaKernel3x3;
import br.bassani.jmf.efeitos.rgb.convolucao.MediaKernel5x5;
import br.bassani.jmf.efeitos.stereo.RGBStereoAnaglyph;
import br.bassani.jmf.efeitos.yuv.YUVPele;

public class CodecPack {

	private static CodecPack[] codecsPacks = {};
	
		public String nome;
		public Codec[] codecs;

		public CodecPack(String nome, Codec[] codecs) {
			super();
			this.nome = nome;
			this.codecs = codecs;
		}

		@Override
		public String toString() {
			return nome;
		}
		
		
	
	public static CodecPack[] getCodecsPacks(JFrame parent) {
		if(codecsPacks.length==0) initCodecsPacks(parent);
			return codecsPacks;
		}

	private static void initCodecsPacks(JFrame frame){
		Vector<CodecPack> packs = new Vector<CodecPack>();
		
		Codec[] codecVazio = { };
		packs.add(new CodecPack("Vazio", codecVazio));
		
		Codec[] codecRotationEffect = { new RotationEffect() };
		packs.add(new CodecPack("RotationEffect", codecRotationEffect));
		
		Codec[] codecRGBPele = { new RGBPele() };
		packs.add(new CodecPack("RGBPele", codecRGBPele));
		
		Codec[] codecRGBOCVCalibTabuleiro = { new RGBOCVCalibTabuleiro() };
		packs.add(new CodecPack("RGBOCVCalibTabuleiro", codecRGBOCVCalibTabuleiro));
		
		Codec[] codecRGBOCVCalibCorrecaoEstatica = { new RGBOCVCalibCorrecaoEstatica() };
		packs.add(new CodecPack("RGBOCVCalibCorrecaoEstatica", codecRGBOCVCalibCorrecaoEstatica));
		
		Codec[] codecNyARToolkitEffect = { new NyARToolkitEffect() };
		packs.add(new CodecPack("NyARToolkitEffect", codecNyARToolkitEffect));
		
		Codec[] codecYUVPele = { new YUVPele() };
		packs.add(new CodecPack("YUVPele", codecYUVPele));
		Codec[] codecRGBBayerPele = { new RGBBayerPele() };
		packs.add(new CodecPack("RGBBayerPele", codecRGBBayerPele));
		
		Codec[] codecRGBBayer2TrueRGB = { new RGBBayer2TrueRGB() };
		packs.add(new CodecPack("RGBBayer2TrueRGB", codecRGBBayer2TrueRGB));
		Codec[] codecRGBBayer2TrueRGBPele = { new RGBBayer2TrueRGB(), new RGBBayerPele() };
		packs.add(new CodecPack("RGBBayer2TrueRGBPele", codecRGBBayer2TrueRGBPele));
		Codec[] codecRGBBayer2TrueRGBFase2 = { new RGBBayer2TrueRGB(), new RGBBayer2TrueRGBFase2() };
		packs.add(new CodecPack("RGBBayer2TrueRGBFase2", codecRGBBayer2TrueRGBFase2));
		Codec[] codecRGBBayer2TrueRGBFase23 = { new RGBBayer2TrueRGB(), new RGBBayer2TrueRGBFase2(), new RGBBayer2TrueRGBFase3() };
		packs.add(new CodecPack("RGBBayer2TrueRGBFase23", codecRGBBayer2TrueRGBFase23));
		
		RGBPele tabuleiro = new RGBPele();
		tabuleiro.setRMinimo(30);
		tabuleiro.setRMaximo(255);
		tabuleiro.setGMinimo(30);
		tabuleiro.setGMaximo(255);
		tabuleiro.setBMinimo(30);
		tabuleiro.setBMaximo(255);
		tabuleiro.setFuncaoRmaiorGmaiorBmaiorMin(false);
		tabuleiro.setIntervaloCor(true);
		
		Codec[] codecSubMediaDesvioTabuleiro = { new SubtracaoMediaDesvio(), tabuleiro };
		packs.add(new CodecPack("SubMediaDesvioTabuleiro", codecSubMediaDesvioTabuleiro));
		
		Codec[] codecMediaKernelPele = { new MediaKernel3x3(), new RGBPele() };
		packs.add(new CodecPack("MediaKernelPele", codecMediaKernelPele));
		
		Codec[] codecMediaKernelHist = { new MediaKernel3x3(), new HistogramaRGB() };
		packs.add(new CodecPack("MediaKernelHist", codecMediaKernelHist));
		Codec[] codecMediaKernel5Hist = { new MediaKernel5x5(), new HistogramaRGB() };
		packs.add(new CodecPack("MediaKernel5Hist", codecMediaKernel5Hist));
		Codec[] codecMediaKernel5 = { new MediaKernel5x5() };
		packs.add(new CodecPack("MediaKernel5", codecMediaKernel5));
		
		Codec[] codecHistogramaRGB = { new HistogramaRGB() };
		packs.add(new CodecPack("HistogramaRGB", codecHistogramaRGB));
		
		Codec[] codecCoresRGBPredefinidas = { new CoresRGBPredefinidas() };
		packs.add(new CodecPack("CoresRGBPredefinidas", codecCoresRGBPredefinidas));
		
		Codec[] codecCoresRGBPredefinidasConjuntos = { new CoresRGBPredefinidas(), new ConjuntosOtimSemLinhas() };
		packs.add(new CodecPack("CoresRGBPredefinidasConjuntos", codecCoresRGBPredefinidasConjuntos));
		
		Codec[] codecCoresRGBPredefinidasConjuntosComLinhas = { new CoresRGBPredefinidas(), new ConjuntosObjComLinhas() };
		packs.add(new CodecPack("CoresRGBPredefinidasConjuntosComLinhas", codecCoresRGBPredefinidasConjuntosComLinhas));
		
		Codec[] codecSubtracaoMediaDesvio = { new SubtracaoMediaDesvio() };
		packs.add(new CodecPack("SubtracaoMediaDesvio", codecSubtracaoMediaDesvio));
		
		Codec[] codecARGBSubtracaoMediaDesvio = { new ARGBSubtracaoMediaDesvio() };
		packs.add(new CodecPack("RGBSubtracaoMediaDesvio", codecARGBSubtracaoMediaDesvio));
		
		Codec[] codecSubMediaDesvioPele = { new SubtracaoMediaDesvio(), new RGBPele() };
		packs.add(new CodecPack("SubMediaDesvioPele", codecSubMediaDesvioPele));
		
		Codec[] codecSubMediaDesvioHist = { new SubtracaoMediaDesvio(), new HistogramaRGB() };
		packs.add(new CodecPack("SubMediaDesvioHist", codecSubMediaDesvioHist));
		
		Color[] binaria1 = {new Color(0,0,0), new Color(100,1,1)};
		Codec[] codecSubMediaDesvioCoresBin = { new SubtracaoMediaDesvio(), new CoresRGBPredefinidas(binaria1) };
		packs.add(new CodecPack("SubMediaDesvioCoresBin", codecSubMediaDesvioCoresBin));
		
		Color[] binaria2 = {new Color(0,0,0), new Color(100,1,1)};
		Codec[] codecSubMediaDesvioCoresBinConjuntos = { new SubtracaoMediaDesvio(), new CoresRGBPredefinidas(binaria2), new ConjuntosObjComLinhas() };
		packs.add(new CodecPack("SubMediaDesvioCoresBinConjuntos", codecSubMediaDesvioCoresBinConjuntos));
		Codec[] codecSubMediaDesvioCoresBinConjuntosMemo = { new SubtracaoMediaDesvio(), new CoresRGBPredefinidas(binaria2), new ConjuntosObjMemoComLinhas() };
		packs.add(new CodecPack("SubMediaDesvioCoresBinConjuntosMemo", codecSubMediaDesvioCoresBinConjuntosMemo));
		
		Codec[] codecARGBSubMediaDesvioConjuntos = { new ARGBSubtracaoMediaDesvio(), new ARGBConjuntosObjMemoComLinhas(frame, false) };
		packs.add(new CodecPack("ARGBSubMediaDesvioConjuntos", codecARGBSubMediaDesvioConjuntos));
		
		Codec[] codecARGBPeleConjuntos = { new ARGBPele(), new ARGBConjuntosObjMemoComLinhas(frame, false) };
		packs.add(new CodecPack("ARGBPeleConjuntos", codecARGBPeleConjuntos));
		
		Codec[] codecRot90ARGBPeleConjuntos = { new Rotacao90(), new ARGBPele(), new ARGBConjuntosObjMemoComLinhas(frame, false) };
		packs.add(new CodecPack("Rot90ARGBPeleConjuntos", codecRot90ARGBPeleConjuntos));
		
		//Para WEBCAM !!
		boolean jogoTetris = false;
		if(jogoTetris){
			//Jogo Tetris aberto
		}
		Codec[] codecJogoTetris = { new Rotacao90(), new ARGBSubtracaoMediaDesvio(), new ARGBPele(), new ARGBConjuntosObjMemoComLinhas(frame, jogoTetris) };
		packs.add(new CodecPack("JogoTetris", codecJogoTetris));
		
		Codec[] codecARGBSubMediaDesvioPele = { new ARGBSubtracaoMediaDesvio(), new ARGBPele() };
		packs.add(new CodecPack("ARGBSubMediaDesvioPele", codecARGBSubMediaDesvioPele));
		Codec[] codecARGBSubMediaDesvioPeleConj = { new ARGBSubtracaoMediaDesvio(), new ARGBPele(), new ConjuntosObjMemoComLinhas() };
		packs.add(new CodecPack("ARGBSubMediaDesvioPeleConj", codecARGBSubMediaDesvioPeleConj));
		
		Codec[] codecRotacao90 = { new Rotacao90() };
		packs.add(new CodecPack("Rotacao90", codecRotacao90));
		Codec[] codecZoom2x = { new Zoom2x() };
		packs.add(new CodecPack("Zoom2x", codecZoom2x));
		
		Color[] binaria = {new Color(0,0,0), new Color(100,1,1)};
		Codec[] codecSubCoresConj = { new SubtracaoMediaDesvio(), new CoresRGBPredefinidas(binaria), new ConjuntosOtimSemLinhas() };
		packs.add(new CodecPack("SubCoresConj", codecSubCoresConj));
		
		Codec[] codecConjuntosOtimSemLinhas = { new ConjuntosOtimSemLinhas() };
		packs.add(new CodecPack("ConjuntosOtimSemLinhas", codecConjuntosOtimSemLinhas));
		
		Codec[] codecStereoAnaglyph = { new RGBStereoAnaglyph() };
		packs.add(new CodecPack("Stereo Anaglyph", codecStereoAnaglyph));
		
		codecsPacks = packs.toArray(codecsPacks);
	}
}
