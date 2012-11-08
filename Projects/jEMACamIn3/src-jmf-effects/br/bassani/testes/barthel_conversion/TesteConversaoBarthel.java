package br.bassani.testes.barthel_conversion;

import color.util.de.berlin.fhtw.barthel.BarthelColorSpaceConversionUtil;

/**
 * Conhecimento previo:
 * Metodo de Deteccao de Pele em RGB
 * : (r > g) && (g > b) && ( b > 64 )
 * 
 * @author OswaldoPoli
 */
public class TesteConversaoBarthel {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int[] county, countu, countv;
		county = new int[256];
		countu = new int[256];
		countv = new int[256];
		
		BarthelColorSpaceConversionUtil conversor = new BarthelColorSpaceConversionUtil();
		
		int r,g,b;
		int[] yuv = new int[3];
		for(r=0;r<256;r++){
			for(g=0;g<256;g++){
				for(b=0;b<256;b++){
					if((r > g) && (g > b) && ( b > 64 )){
						conversor.rgb2yuv(r, g, b, yuv);
						
						/*System.out.println(yuv[0]+" "+yuv[1]+" "+yuv[2])*/;
						
						
						verificaValores(yuv);
						
						county[yuv[0]] += 1;
						countu[yuv[1]] += 1;
						countv[yuv[2]] += 1;
					}
				}
			}
		}
		
		int i;
		for(i=0;i<256;i++){
			if(county[i]!=0){
				System.out.println("county["+i+"]: "+county[i]);
			}
		}
		for(i=0;i<256;i++){
			if(countu[i]!=0){
				System.out.println("countu["+i+"]: "+countu[i]);
			}
		}
		for(i=0;i<256;i++){
			if(countv[i]!=0){
				System.out.println("countv["+i+"]: "+countv[i]);
			}
		}
		
		System.out.println("RED RGB >> YUV");
		r=255;
		g=0;
		b=0;
		conversor.rgb2yuv(r, g, b, yuv);
		System.out.println(yuv[0]+" "+yuv[1]+" "+yuv[2]);
		verificaValores(yuv);
		System.out.println(yuv[0]+" "+yuv[1]+" "+yuv[2]);
	}
	
	private static void verificaValores(int[] yuv){
		if(yuv[0]>255) { yuv[0] -= 256; /*System.out.println("."); */}
		if(yuv[1]>255) { yuv[1] -= 256; /*System.out.println("."); */}
		if(yuv[2]>255) { yuv[2] -= 256; /*System.out.println("."); */}
		if(yuv[0]<0) { yuv[0] += 256; /*System.out.println("."); */}
		if(yuv[1]<0) { yuv[1] += 256; /*System.out.println("."); */}
		if(yuv[2]<0) { yuv[2] += 256; /*System.out.println("."); */}
	}

}
