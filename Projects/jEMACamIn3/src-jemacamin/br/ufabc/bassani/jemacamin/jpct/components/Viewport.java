/**
 * 
 */
package br.ufabc.bassani.jemacamin.jpct.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.ufabc.bassani.jemacamin.jpct.components.skeletal.SkeletalAnimationConfig;
import br.ufabc.bassani.jemacamin.jpct.components.skeletal.SkeletalBoneAnimationConfig;
import br.ufabc.bassani.jpct.components.Texto2D;

import com.threed.jpct.Animation;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.World;
import com.threed.jpct.skeleton.Quaternion;
import com.threed.jpct.skeleton.RotationKeyframe;
import com.threed.jpct.skeleton.SimpleBone;
import com.threed.jpct.skeleton.SimpleSkeleton;
import com.threed.jpct.skeleton.SkeletalAnimation;
import com.threed.jpct.skeleton.SkeletalObject3D;
import com.threed.jpct.skeleton.TranslationKeyframe;

/**
 * @author bassani
 *
 */
public class Viewport extends VideoPlayerViewport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 494284231456035602L;
	
	/* - 3D - */
	
	protected Object3D model = null;
	
	private Texto2D fgText;
	private Texto2D bgText;

	/**
	 * 
	 */
	public Viewport() {
		super();
	}

	/* (non-Javadoc)
	 * @see br.ufabc.bassani.jemacamin.jpct.components.VideoPlayerViewport#init()
	 */
	@Override
	protected void init() {
		super.init();

		fgText = new Texto2D( "By Oswaldo Bassani @ UFABC" );
        bgText = new Texto2D( "Infos extras: texto no 'background'" );
        bgText.setColors( Color.GREEN, Color.DARK_GRAY );
	}
	
	@Deprecated
	public void loadModel(String modelTitle, File file, File skel){
		loadModel(modelTitle, file, skel, 1.0f, true);
	}
	
	public void loadModel(String modelTitle, File file, File skel, float scale, boolean originalJPCTloader)
	{
		fgText.setText("Model name: "+modelTitle);
		if(model!=null){
			//Limpa o word, removendo o modelo anterior.
			renderFactory.world.removeObject(model);
			model = null;
		}
		try{
    		//Load the model
			/*
    		model = renderFactory.loadModel(
    							file.getName(), 
    							file.toURI().toURL().openStream(),
    							skel.toURI().toURL().openStream(), 
    							scale, originalJPCTloader);
    							*/
			
			model = renderFactory.loadModel_Full(
					file.getName(), 
					file.toURI().toURL().openStream(),
					skel.toURI().toURL().openStream(), 
					scale, originalJPCTloader)[0];
			
    		//model.setCulling(false);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    	repaint();
	}
	
	public void loadModel_BonesAPI(String modelTitle, String directory, String fileName, String textureName, String textureFileName){
		fgText.setText("Model name: "+modelTitle);
		if(model!=null){
			//Limpa o word, removendo o modelo anterior.
			renderFactory.world.removeObject(model);
			model = null;
		}
		try{
    		//Load the model
			
			model = renderFactory.loadModel_BonesAPI_Ogre(directory, fileName, textureName, textureFileName)[0];	
			
    		//model.setCulling(false);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    	repaint();
	}

	/**
	 * Draws a background onto the framebuffer of a renderpane.
	 * 
	 * @param renderFactory
	 */
	public void drawBackground(Graphics g, int width, int height)
	{
		drawBackground_FillColor(g, width, height);
		
		drawBackground_VideoFrame(g, width, height);
		
		drawBackground_DrawGridLines(g, width, height);
	}
	
	public void render2DBackground(Graphics g, int width, int height)
    {
        // Draw a gradient background:
        Graphics2D g2d = (Graphics2D)g;
//        Composite oldcomp = g2d.getComposite();
//        g2d.setComposite( AlphaComposite.SrcOver.derive(1.0f) );
//        g2d.setPaint( new GradientPaint( 0, 0, new Color( 110, 110, 128 ), 0, getHeight(), Color.BLACK ) );
//        g2d.fillRect( 0, 0, getWidth(), getHeight() );
//        g2d.setComposite( oldcomp );
        
        // Render the background text
        bgText.render( g2d, width / 4, (int)(height / 2.3) );
    }
    
    public void render2DForeground(Graphics g, int width, int height)
    {
		Graphics2D g2d = (Graphics2D)g;
    	
        // Render the foreground text
        fgText.render( g2d, width / 2, height / 2 );
    }
	
	private boolean test = true;
	
	/** 
	 * Renders the model onto the framebuffer.
	 * 
	 * @see br.ufabc.bassani.jemacamin.jpct.components.AViewport#renderFrame()
	 */
	@Override
	public void renderFrame()
	{
		if(model != null)
		{
			// //model.rotateX(0.02f);
			// //model.rotateY(-0.02f);
			
			// //if(test) model.rotateZ(-0.02f);
			
			//if(test) model.rotateY(-0.02f);
			
			//((SkeletalObject3D)model).advanceAnimation();
			/* */
			if(model!=null){
				if(model instanceof SkeletalObject3D){
					// ((SkeletalObject3D)model).advanceAnimation();
					SkeletalObject3D mSkel = (SkeletalObject3D)model;
					SimpleSkeleton simpleSkel = mSkel.getSkeleton();
					if(!test && simpleSkel!=null){
						SkeletalAnimation[] animas = simpleSkel.getAnimations();
						// System.out.println("Seq:"+((SkeletalObject3D)model).getAnimationSequence().getSequenceCount());
						System.out.println("Animas: "+animas.length);
						for(SkeletalAnimation anim : animas){
							System.out.println(" - Name: "+anim.getName());
							System.out.println("   Length: "+anim.getLength());
							System.out.println("   Loop: "+anim.isLooping());
						}
						simpleSkel.setCurrentAnimation("Cabeca");//OK
						//simpleSkel.setCurrentAnimation("AnimaPernaD");//
						//simpleSkel.setCurrentAnimation("AnimaPernaE");//
						//simpleSkel.setCurrentAnimation("AnimaBracoE");//
						//simpleSkel.setCurrentAnimation("AnimaBracoD");//
						
						//simpleSkel.setCurrentAnimation(2);
						simpleSkel.restartAnimation();
						
						//model.rotateX((float)(+Math.PI/2.0f));
						
						System.out.println("______ rotate e translate ______");
						
						model.rotateX((float)(+Math.PI/2.0f));
						//model.translate(0f, -10.0f, 0.0f);
						//model.rotateZ((float)(+Math.PI/2.0f));
						
						test = true;
					}
					if(test){
						mSkel.advanceAnimation();
					}
				}
			}/* */
		}
		World       world       = renderFactory.world;
		FrameBuffer frameBuffer = renderFactory.frameBuffer;
		frameBuffer.clear();
		
		int width = frameBuffer.getOutputWidth();
		int height = frameBuffer.getOutputHeight();
		Graphics g = frameBuffer.getGraphics();
		
		drawBackground(g, width, height);
		render2DBackground(g, width, height);
		
		world.renderScene(frameBuffer);
		
		boolean comWireframe = true;
		boolean drawNormal = true;
		Color wireframeColor = Color.WHITE;
		if(drawNormal){
			world.draw(frameBuffer);
		}
		if(comWireframe){
			wireframeColor = Color.RED;
			world.drawWireframe(frameBuffer, wireframeColor);
		}
		
		render2DForeground(g, width, height);
		
		frameBuffer.update();
		
		countFrames++;
		long time = System.currentTimeMillis();
		if(lastRenderTime==-1){
			lastRenderTime = time;
		}else{
			if(time-lastRenderTime>10 * 1000){
				
				if(lastFPS<0){
					setMyAnim();
				}
				
				lastFPS = countFrames*1000/(time-lastRenderTime);
				countFrames = 0;
				lastRenderTime = time;
				bgText.setText("FPS: "+lastFPS);
			}
		}
    }
	
	private void setMyAnim__Old(){
		//if(true) return;
		System.out.println("Nois tentando aki!!");
		System.out.println("Nois tentando aki!!");
		
		if(model!=null){
			
			// Ideal para o Gus 
			model.rotateX((float)(+Math.PI/2.0f));
			
			if(model instanceof SkeletalObject3D){
				// ((SkeletalObject3D)model).advanceAnimation();
				SkeletalObject3D mSkel = (SkeletalObject3D)model;
				SimpleSkeleton simpleSkel = mSkel.getSkeleton();
				
				int animationIndex = 0;
				float animationTotalTime = 1.56f;
				
				//Cria animacao na mao
				//Para o GUS.
				SkeletalAnimation[] animationsArray = new SkeletalAnimation[1];
	    		SkeletalAnimation animation = new SkeletalAnimation();
	    		animation.setName("MyAnima");
	    		animation.setLength(animationTotalTime);
	    		animation.setLooping(true);
	    		animationsArray[animationIndex] = animation;
	    		
	    		SimpleBone[] bonesAll = simpleSkel.getBones();
	    		
	    		Vector<String> noAnimBones = new Vector<String>();
	    		
	    		Vector<String> inverseAnimBones = new Vector<String>();
	    		
	    		/*
	    		for(SimpleBone bone : bonesAll){
	    			System.out.println("Bone name: "+bone.getName());
	    			if(!bone.getName().equals("ArmR") &&
	    					!bone.getName().equals("BicepL") &&
	    					!bone.getName().equals("PointLA") &&
	    					!bone.getName().equals("PointLB") &&
	    					!bone.getName().equals("PointLC")){
	    				noAnimBones.add(bone.getName());
	    			}
	    		}
	    		
	    		for(SimpleBone bone : bonesAll){
	    			if(bone.getName().equals("ArmR") ||
	    					bone.getName().equals("BicepL") ||
	    					bone.getName().equals("PointLA") || 
	    					bone.getName().equals("PointLB") || 
	    					bone.getName().equals("PointLC")){
	    				inverseAnimBones.add(bone.getName());
	    			}
	    		}*/
	    		
	    		for(SimpleBone bone : bonesAll){
	    			System.out.println("Bone name: "+bone.getName());
	    			if(!bone.getName().equals("braco.d") &&
//	    					!bone.getName().equals("perna.d") &&
	    					!bone.getName().equals("perna.e") &&
//	    					!bone.getName().equals("braco.e") &&
	    					!bone.getName().equals("cabeca")){
	    				noAnimBones.add(bone.getName());
	    			}
	    		}
	    		
	    		for(SimpleBone bone : bonesAll){
	    			if(bone.getName().equals("braco.d") ||
//	    					bone.getName().equals("perna.d") ||
	    					bone.getName().equals("perna.e") || 
//	    					bone.getName().equals("braco.e") || 
	    					bone.getName().equals("cabeca")){
	    				inverseAnimBones.add(bone.getName());
	    			}
	    		}
	    		
	    		noAnimBones.add("tronco");
		    	noAnimBones.add("quadril.d");
		    	noAnimBones.add("quadril.e");
		    	noAnimBones.add("ombro.d");
		    	noAnimBones.add("ombro.e");
		    	noAnimBones.add("pescoco");
	    		inverseAnimBones.add("braco.d");
		    	inverseAnimBones.add("perna.d");
	    		
	    	//	noAnimBones.add("tronco");
	    	//	noAnimBones.add("quadril.d");
	    	//	noAnimBones.add("quadril.e");
	    	//	noAnimBones.add("ombro.d");
	    	//	noAnimBones.add("ombro.e");
	    	//	noAnimBones.add("pescoco");
	    		/*
	    		noAnimBones.add("Torax");
	    		noAnimBones.add("OmbroD");
	    		noAnimBones.add("OmbroE");
	    		noAnimBones.add("Pescoco");
	    		noAnimBones.add("Quadril");
	    		noAnimBones.add("QuadrilD");
	    		noAnimBones.add("QuadrilE");
	    		noAnimBones.add("AnteBracoD");
	    		noAnimBones.add("AnteBracoE");
	    		noAnimBones.add("Pescoco");
	    		noAnimBones.add("Cabeca");
	    		noAnimBones.add("CoxaD");
	    		//noAnimBones.add("CoxaE");
	    		//noAnimBones.add("BracoD");
	    		noAnimBones.add("BracoE");
	    		noAnimBones.add("PernaD");
	    		noAnimBones.add("PernaE");
	    		*/
	    		
	    	//	inverseAnimBones.add("braco.d");
	    	//	inverseAnimBones.add("perna.d");
	    	//	inverseAnimBones.add("BracoD");
		    //	inverseAnimBones.add("CoxaE");
	    		
	    	//	inverseAnimBones.add("calf.R");
	    	//	inverseAnimBones.add("handGEO.R");
				
	    		//Animation Tracks
	    		for(SimpleBone bone : simpleSkel.getBones())
	        	{
	        		//Load keyframe data into bone.
	        		bone.setMaxAnimations(1);
	        		String bone_name = bone.getName();
	        		
	        		int keyframes_size = 10;
	        		
	        		RotationKeyframe[]    rotationKeyframes    = new RotationKeyframe[keyframes_size];
	        		TranslationKeyframe[] translationKeyframes = new TranslationKeyframe[keyframes_size];
	        		for(int i = 0; i<keyframes_size; i++)
	            	{
	            		float timeAnima = i * (animationTotalTime/keyframes_size);
	            		float translateX = 0.0f;
	            		float translateY = 0.0f;
	            		float translateZ = 0.0f;
	            		float rotateAngle = (float)(i * ((Math.PI/4)/keyframes_size));
	            		if(noAnimBones.contains(bone_name)){
	            			rotateAngle = 0.0f;
	            		}
	            		if(inverseAnimBones.contains(bone_name)){
	            			rotateAngle = -rotateAngle;
	            		}
	            		float rotateX = 0.0f;
	            		float rotateY = 0.0f;
	            		float rotateZ = 1.0f;
	            		RotationKeyframe    rotationKeyframe    = new RotationKeyframe();
	            		TranslationKeyframe translationKeyframe = new TranslationKeyframe();
	            		rotationKeyframe.time = timeAnima;
	                	rotationKeyframe.quat = new Quaternion(rotateX,rotateY,rotateZ,rotateAngle);
	                	translationKeyframe.time = timeAnima;
	            		translationKeyframe.setTranslation(translateX,translateY,translateZ);
	            		rotationKeyframes[i]    = rotationKeyframe;
	            		translationKeyframes[i] = translationKeyframe;
	            	}
	            	bone.setKeyframes(animationIndex,rotationKeyframes, translationKeyframes);
	        	}
	    		
	    		simpleSkel.setAnimations(animationsArray);
			}
		}
	}
	
	private SkeletalObject3D mSkel;
	private SimpleSkeleton simpleSkel;
	private SimpleBone[] bonesAll;
	private Vector<String> allBonesNames;
	
	private Vector<String> bonesSelecionados = new Vector<String>();
	
	private void setMyAnim(){
		//if(true) return;
		System.out.println("setMyAnim!!");
		if(model!=null){
			// Ideal para o Gus 
			model.rotateX((float)(+Math.PI/2.0f));
			if(model instanceof SkeletalObject3D){
				mSkel = (SkeletalObject3D)model;
				simpleSkel = mSkel.getSkeleton();
				
				bonesAll = simpleSkel.getBones();
	    		
	    		allBonesNames = new Vector<String>();
	    		for(SimpleBone bone : bonesAll){
	    			System.out.println("Bone name: "+bone.getName());
	    			allBonesNames.add(bone.getName());
	    		}
	    		
				int animationIndex = 0;
				float animationTotalTime = 1.56f;
				int keyframes_size = 10;
				float rotMaxAngle = (float)(Math.PI/4);
				
				bonesSelecionados.clear();
				bonesSelecionados.add("braco.d");
	    		//bonesSelecionados.add("perna.d");
				bonesSelecionados.add("perna.e");
	    		//bonesSelecionados.add("braco.e");
				bonesSelecionados.add("cabeca");
				
				setMyAnim_Param(animationIndex, animationTotalTime, keyframes_size, rotMaxAngle, bonesSelecionados);
				
				openDialogConfiguracao(null);
			}
		}
	}
	
	private void setMyAnim_Param(int animationIndex, float animationTotalTime, int keyframes_size, float rotMaxAngle, Vector<String> bonesSelecionados){
		System.out.println("setMyAnim param!!");
				
		//Cria animacao na mao
		//Para o GUS.
		SkeletalAnimation[] animationsArray = new SkeletalAnimation[1];
		SkeletalAnimation animation = new SkeletalAnimation();
		animation.setName("MyAnima");
		animation.setLength(animationTotalTime);
		animation.setLooping(true);
		animationsArray[animationIndex] = animation;
		
		Vector<String> noAnimBones = new Vector<String>();
		Vector<String> inverseAnimBones = new Vector<String>();
		
		for(SimpleBone bone : bonesAll){
			System.out.println("Bone name: "+bone.getName());
			if(!bonesSelecionados.contains(bone.getName())){
				noAnimBones.add(bone.getName());
			}
		}
		for(SimpleBone bone : bonesAll){
			if(!noAnimBones.contains(bone.getName())){
				inverseAnimBones.add(bone.getName());
			}
		}
		
		//Animation Tracks
		for(SimpleBone bone : simpleSkel.getBones())
    	{
    		//Load keyframe data into bone.
    		bone.setMaxAnimations(1);
    		String bone_name = bone.getName();
    		
    		RotationKeyframe[]    rotationKeyframes    = new RotationKeyframe[keyframes_size];
    		TranslationKeyframe[] translationKeyframes = new TranslationKeyframe[keyframes_size];
    		for(int i = 0; i<keyframes_size; i++)
        	{
        		float timeAnima = i * (animationTotalTime/keyframes_size);
        		float translateX = 0.0f;
        		float translateY = 0.0f;
        		float translateZ = 0.0f;
        		float rotateAngle = (float)(i * ((rotMaxAngle)/keyframes_size));
        		if(noAnimBones.contains(bone_name)){
        			rotateAngle = 0.0f;
        		}
        		if(inverseAnimBones.contains(bone_name)){
        			rotateAngle = -rotateAngle;
        		}
        		float rotateX = 0.0f;
        		float rotateY = 0.0f;
        		float rotateZ = 1.0f;
        		RotationKeyframe    rotationKeyframe    = new RotationKeyframe();
        		TranslationKeyframe translationKeyframe = new TranslationKeyframe();
        		rotationKeyframe.time = timeAnima;
            	rotationKeyframe.quat = new Quaternion(rotateX,rotateY,rotateZ,rotateAngle);
            	translationKeyframe.time = timeAnima;
        		translationKeyframe.setTranslation(translateX,translateY,translateZ);
        		rotationKeyframes[i]    = rotationKeyframe;
        		translationKeyframes[i] = translationKeyframe;
        	}
        	bone.setKeyframes(animationIndex, rotationKeyframes, translationKeyframes);
    	}
		
		simpleSkel.setAnimations(animationsArray);
	}
	
	private void setMyAnim_Param(float newScale, float modelTranslX, float modelTranslY, float modelTranslZ, SkeletalAnimationConfig animaConf){
		System.out.println("setMyAnim param!!");

		int animationIndex = animaConf.animationIndex;
		float animationTotalTime = animaConf.animationTotalTime;
		int keyframes_size = animaConf.keyframes_size;
		
		//Cria animacao na mao
		//Para o GUS.
		SkeletalAnimation[] animationsArray = new SkeletalAnimation[1];
		SkeletalAnimation animation = new SkeletalAnimation();
		animation.setName("MyAnima");
		animation.setLength(animationTotalTime);
		animation.setLooping(true);
		animationsArray[animationIndex] = animation;
		
		Vector<String> noAnimBones = new Vector<String>();
		Vector<String> inverseAnimBones = new Vector<String>();
		
		for(SimpleBone bone : bonesAll){
			System.out.println("Bone name: "+bone.getName());
			if(!animaConf.hasConfig(bone.getName())){
				noAnimBones.add(bone.getName());
			}
		}
		for(SimpleBone bone : bonesAll){
			if(!noAnimBones.contains(bone.getName())){
				inverseAnimBones.add(bone.getName());
			}
		}
		
		//Animation Tracks
		for(SimpleBone bone : simpleSkel.getBones())
    	{
    		//Load keyframe data into bone.
    		bone.setMaxAnimations(1);
    		String bone_name = bone.getName();
    		
    		RotationKeyframe[]    rotationKeyframes    = new RotationKeyframe[keyframes_size];
    		TranslationKeyframe[] translationKeyframes = new TranslationKeyframe[keyframes_size];
    		for(int i = 0; i<keyframes_size; i++)
        	{
        		float timeAnima = i * (animationTotalTime/keyframes_size);
        		float translateX = 0.0f;
        		float translateY = 0.0f;
        		float translateZ = 0.0f;
        		float rotateAngle = 0.0f;
        		float rotateX = 0.0f;
        		float rotateY = 0.0f;
        		float rotateZ = 1.0f;
        		RotationKeyframe    rotationKeyframe    = new RotationKeyframe();
        		TranslationKeyframe translationKeyframe = new TranslationKeyframe();
        		if(inverseAnimBones.contains(bone_name)){
        			SkeletalBoneAnimationConfig boneConf = animaConf.getConfig(bone_name);
        			float kf_angle = (float)(i * ((boneConf.rotateAngle)/keyframes_size));
        			rotationKeyframe.time = timeAnima;
        			boneConf.setRotationKeyframe_Quaternion(rotationKeyframe, kf_angle);
	            	translationKeyframe.time = timeAnima;
	            	boneConf.setTranslationKeyframe(translationKeyframe);
	        		rotationKeyframes[i]    = rotationKeyframe;
	        		translationKeyframes[i] = translationKeyframe;
        		}else{
	        		rotationKeyframe.time = timeAnima;
	            	rotationKeyframe.quat = new Quaternion(rotateX,rotateY,rotateZ,rotateAngle);
	            	translationKeyframe.time = timeAnima;
	        		translationKeyframe.setTranslation(translateX,translateY,translateZ);
	        		rotationKeyframes[i]    = rotationKeyframe;
	        		translationKeyframes[i] = translationKeyframe;
        		}
        	}
        	bone.setKeyframes(animationIndex, rotationKeyframes, translationKeyframes);
    	}
		
		simpleSkel.setAnimations(animationsArray);
		model.setScale(newScale);
		Matrix m = new Matrix();
		m.translate(modelTranslX, modelTranslY, modelTranslZ);
		model.setTranslationMatrix(m);
	}

	//Objetos Graficos da Configuracao
	private JPanel painel;
	
	JLabel l_ossos, l_ossosSelec, l_totaltime, l_keyframes, l_maxAngle;
	JComboBox ossosDisponiveis;
	JList ossosSelecionados;
	JSlider totalTime, keyFrames, maxAngleRot;
	
	public JPanel openPanelConfiguracao(){
		if(painel==null){
			painel = new JPanel();
			painel.setLayout(new BorderLayout(5,5));

			painel.add(new JLabel(" -- Propriedades -- "), BorderLayout.NORTH);
			
			JPanel sliders = new JPanel(new GridLayout(-1,1,5,5));
			
			l_ossos = new JLabel("Ossos");
			l_ossosSelec = new JLabel("Selecionados");
			l_totaltime = new JLabel("Tempo Anim");
			l_keyframes = new JLabel("Numero Frames");
			l_maxAngle = new JLabel("Angulo Rot");

			ossosDisponiveis = new JComboBox(allBonesNames);
			
			ossosSelecionados = new JList(new DefaultListModel());
			totalTime = new JSlider(0, 300, 156);
			keyFrames = new JSlider(0, 20, 10);
			maxAngleRot = new JSlider(-180, 180, 45);
			
			JButton add = new JButton("Adiciona");
			add.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Object value = ossosDisponiveis.getSelectedItem();
					ossosDisponiveis.removeItem(value);
					ossosDisponiveis.setSelectedIndex(0);
					((DefaultListModel)ossosSelecionados.getModel()).insertElementAt(value, 0);
				}
			});
			
			JButton del = new JButton("Remove");
			del.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Object value = ossosSelecionados.getSelectedValue();
					ossosDisponiveis.addItem(value);
					((DefaultListModel)ossosSelecionados.getModel()).removeElement(value);
				}
			});
			
			JButton confir = new JButton("Executar");
			confir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					int animationIndex = 0;
					float animationTotalTime = totalTime.getValue()/100f;
					int keyframes_size = keyFrames.getValue();
					float rotMaxAngle = (float)(maxAngleRot.getValue() * 2*Math.PI / 360);
					
					bonesSelecionados.clear();
					Enumeration enum0 = ((DefaultListModel)ossosSelecionados.getModel()).elements();
					while(enum0.hasMoreElements()){
						Object v = enum0.nextElement();
						bonesSelecionados.add(v.toString());
					}
					
					setMyAnim_Param(animationIndex, animationTotalTime, keyframes_size, rotMaxAngle, bonesSelecionados);
					
				}
			});
			
			JButton teste = new JButton("Teste");
			teste.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SkeletalAnimationConfig animaConf;
					animaConf = new SkeletalAnimationConfig();
					
					animaConf.add("cabeca", SkeletalBoneAnimationConfig.getRotateZConfig((float)(Math.PI/8)));
					animaConf.add("perna.d", SkeletalBoneAnimationConfig.getRotateZConfig((float)(-Math.PI/14)));
					animaConf.add("braco.e", SkeletalBoneAnimationConfig.getRotateZConfig((float)(Math.PI/12)));
					
					float translateX = 10.0f;
					float translateY = 4.0f;
					float translateZ = -10.0f;
					
					setMyAnim_Param(1.2f, translateX, translateY, translateZ, animaConf);
				}
			});
			
			JButton testeRan = new JButton("Teste Random");
			testeRan.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SkeletalAnimationConfig animaConf;
					animaConf = new SkeletalAnimationConfig();
					
					animaConf.add("cabeca", SkeletalBoneAnimationConfig.getRotateZConfig((float)(Math.PI/8 - 2 * Math.random() * Math.PI/8)));
					animaConf.add("perna.e", SkeletalBoneAnimationConfig.getRotateZConfig((float)(Math.PI/6 - 2 * Math.random() * Math.PI/6)));
					animaConf.add("perna.d", SkeletalBoneAnimationConfig.getRotateZConfig((float)(Math.PI/6 - 2 * Math.random() * Math.PI/6)));
					animaConf.add("braco.e", SkeletalBoneAnimationConfig.getRotateZConfig((float)(Math.PI/4 - 2 * Math.random() * Math.PI/4)));
					animaConf.add("braco.d", SkeletalBoneAnimationConfig.getRotateZConfig((float)(Math.PI/4 - 2 * Math.random() * Math.PI/4)));
					
					float translateX = 5.0f * (float)(1 - 2 * Math.random());
					float translateY = 5.0f * (float)(1 - 2 * Math.random());
					float translateZ = 5.0f * (float)(1 - 2 * Math.random());
					
					setMyAnim_Param((float)(0.5f + 1.3f*Math.random()), translateX, translateY, translateZ, animaConf);
				}
			});
			
			sliders.add(l_ossos);
			sliders.add(ossosDisponiveis);
			sliders.add(add);
			sliders.add(l_ossosSelec);
			sliders.add(ossosSelecionados);
			sliders.add(del);
			sliders.add(l_totaltime);
			sliders.add(totalTime);
			sliders.add(l_keyframes);
			sliders.add(keyFrames);
			sliders.add(l_maxAngle);
			sliders.add(maxAngleRot);
			sliders.add(confir);
			sliders.add(teste);
			sliders.add(testeRan);

			// -------------------------------
			painel.add(sliders, BorderLayout.CENTER);

		}
		return painel;
	}
	
	private JDialog d;
	public void openDialogConfiguracao(JFrame parent) {
		if(d==null){
			d = new JDialog(parent);
			d.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			d.setTitle(getName());
			d.setLayout(new BorderLayout(0,0));
			d.add(openPanelConfiguracao(), BorderLayout.CENTER);
		}
		d.setVisible(true);
		d.pack();
	}
	
}
