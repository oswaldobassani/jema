package br.ufabc.bassani.jemacamin.gui.info;

import java.util.Vector;

public class SkeletalModelInfo {
	
	public enum API {
		SkeletalAPI,
		Bones
	}
	
	private static SkeletalModelInfo[] models = {};

	public String menuTitle;
	public String menuActionCommand;
	
	public String filesDir;
	public String meshFileName;
	public String skelFileName;
	
	public String modelObjectName;
	public float scale;
	
	public boolean useDefaultLoader;
	
	public String name;
	
	public API apiType;
	public String textureFileName;
	
	public SkeletalModelInfo(String menuTitle, String menuActionCommand, String filesDir, String meshFileName, String skelFileName, String modelObjectName, float scale, boolean useDefaultLoader) {
		this(menuTitle, menuActionCommand, filesDir, meshFileName, skelFileName, modelObjectName, scale, useDefaultLoader, API.SkeletalAPI, null);
	}
		
	public SkeletalModelInfo(String menuTitle, String menuActionCommand, String filesDir, String meshFileName, String skelFileName, String modelObjectName, float scale, boolean useDefaultLoader, API apiType, String textureFileName) {
		super();
		this.menuTitle = menuTitle;
		this.menuActionCommand = menuActionCommand;
		this.filesDir = filesDir;
		this.meshFileName = meshFileName;
		this.skelFileName = skelFileName;
		this.modelObjectName = modelObjectName;
		this.scale = scale;
		this.useDefaultLoader = useDefaultLoader;
		
		this.name = "Skeletal API (" + meshFileName + " / " + skelFileName + ")";
		
		this.apiType = apiType;
		this.textureFileName = textureFileName;
		
		if(apiType==API.Bones) this.name = "Bones API (" + meshFileName + " / " + skelFileName + ")";
	}

	@Override
	public String toString() {
		return name;
	}
	
	public static SkeletalModelInfo[] getModels() {
		if(models.length==0) initModels();
		return models;
	}

	private static void initModels(){
		Vector<SkeletalModelInfo> array = new Vector<SkeletalModelInfo>();
		
		/* -- */

		array.add(new SkeletalModelInfo("Open Gus - Extra with Video Player", "gus-armature-cabeca",
					"./models/OgreXML_Export_03/", "Gus.mesh.xml", "Gus-Armature.Extra.xml",
					"Gus - Anima 'Cabeca'", 1.0f, true));

		array.add(new SkeletalModelInfo("Open Cube with Video Player", "cube-armature",
					"./models/Cube/", "Cube.mesh.xml", "Cube-Armature.skeleton.xml",
					"Cube", 2.0f, false));

		array.add(new SkeletalModelInfo("Open Esfera with Video Player", "dado-redendo",
					"./models/Esfera/", "DadoRedondoMesh.mesh.xml", "DadoRedondo-Armature.skeleton.xml",
					"DadoRedondo", 6.0f, true));

		/* -- */
		
		array.add(new SkeletalModelInfo("Open Gus", "gus",
					"./models/OgreXML_Export_03/", "Gus.mesh.xml", "Gus-Armature.skeleton.xml",
					"Gus", 1.0f, true));

		array.add(new SkeletalModelInfo("Open Gus - Anima 'PernaD'", "gus-pernad",
					"./models/OgreXML_Export_03/", "Gus.mesh.xml", "Gus-Armature.AnimaPernaD.xml",
					"Gus - Anima 'PernaD'", 1.0f, true));

		array.add(new SkeletalModelInfo("Open Gus - Anima 'PernaE'", "gus-pernae",
					"./models/OgreXML_Export_03/", "Gus.mesh.xml", "Gus-Armature.AnimaPernaE.xml",
					"Gus - Anima 'PernaE'", 1.0f, true));

		array.add(new SkeletalModelInfo("Open Gus - Anima 'BracoD'", "gus-bracod",
					"./models/OgreXML_Export_03/", "Gus.mesh.xml", "Gus-Armature.AnimaBracoD.xml",
					"Gus - Anima 'BracoD'", 1.0f, true));

		array.add(new SkeletalModelInfo("Open Gus - Anima 'BracoE'", "gus-bracoe",
					"./models/OgreXML_Export_03/", "Gus.mesh.xml", "Gus-Armature.AnimaBracoE.xml",
					"Gus - Anima 'BracoE'", 1.0f, true));

		array.add(new SkeletalModelInfo("Open Gus - Anima 'Cabeca'", "gus-cabeca",
					"./models/OgreXML_Export_03/", "Gus.mesh.xml", "Gus-Armature.AnimaCabeca.xml",
					"Gus - Anima 'Cabeca'", 1.0f, true));

		/*
		array.add(new SkeletalModelInfo("Open Gus - Extra", "gus-extra",
					"./models/OgreXML_Export_03/", "Gus.mesh.xml", "Gus-Armature.Extra.xml",
					"Gus - Anima 'Cabeca'", 1.0f, true));
					*/

		/*
		array.add(new SkeletalModelInfo("Open Flor", "flor",
					"./models/Flor_OgreXML_Export/", "Sphere.003.mesh.xml", "Flor-Armature.001.skeleton.xml",
					"Sphere.003", 1.0f, false));

		array.add(new SkeletalModelInfo("Open Humanoid", "humanoid",
					"./models/humanoid/", "Cube.001.mesh.xml", "Body-Armature.skeleton.xml",
					"Humanoid", 1.0f, true));

		array.add(new SkeletalModelInfo("Open Snake Thingy", "snake",
					"./models/snake thingy/", "Cube.mesh.xml", "Cube-Armature.skeleton.xml",
					"Snake Thingy", 1.0f, true));*/
		
		// -------------------------------------
		/*
		array.add(new SkeletalModelInfo("Open Navi3 2Beta", "Plane",
				"./models/Navi3_2beta_Export_01/", "Plane.mesh.xml", "Plane.skeleton.xml",
				"Plane", 1.0f, false));
		
		array.add(new SkeletalModelInfo("Open Navi3 2Final", "Plane",
				"./models/Navi3_versao2_projeto_oswaldo_Final2/", "Plane.mesh.xml", "Plane.skeleton.xml",
				"Plane", 2.0f, false));*/
		
		// -------------------------------------
		/*
		array.add(new SkeletalModelInfo("Cube 00", "Cube",
				"./models/cube_00/", "Cube.mesh.xml", "Cube.skeleton.xml",
				"Cube", 5.0f, false));
		
		array.add(new SkeletalModelInfo("ManCandy XX", "ManMeshXX",
				"./models/mancandy_2.48_export_test/", "ManMesh.mesh.xml", "ManCandy.skeleton.xml",
				"ManMesh", 10.0f, false));
		array.add(new SkeletalModelInfo("ManCandy (OLD 00)", "ManMesh00",
				"./models/mancandy_2.48_export_test.00/", "ManMesh.mesh.xml", "ManCandy.skeleton.xml",
				"ManMesh", 6.0f, false));
		array.add(new SkeletalModelInfo("ManCandy (OLD 01)", "ManMesh01",
				"./models/mancandy_2.48_export_test.01/", "ManMesh.mesh.xml", "ManCandy.skeleton.xml",
				"ManMesh", 6.0f, false));
		
		array.add(new SkeletalModelInfo("ManCandy RE0", "ManMeshR0",
				"./models/mancandy_2.48_export_test/reteste0/", "ManMesh.mesh.xml", "ManCandy.skeleton.xml",
				"ManMesh", 10.0f, false));
		array.add(new SkeletalModelInfo("ManCandy RE1", "ManMeshR1",
				"./models/mancandy_2.48_export_test/reteste1/", "ManMesh.mesh.xml", "ManCandy.skeleton.xml",
				"ManMesh", 10.0f, false));
		
		array.add(new SkeletalModelInfo("ManCandy REF", "ManMeshRF",
				"./models/mancandy_2.48_export_test/retestef/", "ManMesh.mesh.xml", "ManCandy.skeleton.xml",
				"ManMesh", 10.0f, false));
		
		array.add(new SkeletalModelInfo("ManCandy REF2", "ManMeshRF2",
				"./models/mancandy_2.48_export_test/retestef2/", "ManMesh.mesh.xml", "ManCandy.skeleton.xml",
				"ManMesh", 10.0f, false));
		
		array.add(new SkeletalModelInfo("ManCandy RR T1", "ManMeshRRT1",
				"./models/mancandy_2.48_export_test/rereT1/", "ManMesh.mesh.xml", "ManCandy.skeleton.xml",
				"ManMesh", 5.0f, false));
		
		array.add(new SkeletalModelInfo("ManCandy V2", "ManMeshV2",
				"./models/mancandy_2.48_export_test/v2/", "ManMesh.mesh.xml", "ManCandy.skeleton.xml",
				"ManMesh", 3.0f, false));
		
		array.add(new SkeletalModelInfo("Patrick 01", "Patrick01",
				"./models/patrik/01/", "Patrick_obj.mesh.xml", "Patrick_obj.skeleton.xml",
				"Patrick", 0.2f, false));
		
		array.add(new SkeletalModelInfo("Open Flor 2", "flor2",
				"./models/FlorFinal_OgreXML_Export/", "Sphere.003.mesh.xml", "Flor.skeleton.xml",
				"Sphere.003", 1.0f, false));
		
		array.add(new SkeletalModelInfo("Patrick 02", "Patrick02",
				"./models/patrik/02/", "Patrick_obj.mesh.xml", "Patrick_obj.skeleton.xml",
				"Patrick", 0.2f, false));
		
		array.add(new SkeletalModelInfo("ManCandy XX 00", "ManMeshXX00",
				"./models/mancandy_2.48_export_test/00/", "ManMesh.mesh.xml", "ManCandy.skeleton.xml",
				"ManMesh", 10.0f, false));
		
		array.add(new SkeletalModelInfo("ManCandy XX x1", "ManMeshXXx1",
				"./models/mancandy_2.48_export_test/x1/", "ManMesh.mesh.xml", "ManCandy.skeleton.xml",
				"ManMesh", 10.0f, false));
		
		array.add(new SkeletalModelInfo("Flor 3", "flor3",
				"./models/FlorFinal_OgreXML_Export/3/", "Sphere.003.mesh.xml", "Flor.skeleton.xml",
				"Sphere.003", 1.0f, false));
		
		array.add(new SkeletalModelInfo("Flor 4", "flor4",
				"./models/FlorFinal_OgreXML_Export/4/", "Sphere.003.mesh.xml", "Flor.skeleton.xml",
				"Sphere.003", .50f, false));
		
		array.add(new SkeletalModelInfo("Flor 5", "flor5",
				"./models/FlorFinal_OgreXML_Export/5/", "Sphere.003.mesh.xml", "Flor.skeleton.xml",
				"Sphere.003", .50f, false));
		
		array.add(new SkeletalModelInfo("Flor 6", "flor6",
				"./models/FlorFinal_OgreXML_Export/6/", "Sphere.003.mesh.xml", "Flor.skeleton.xml",
				"Sphere.003", 1.0f, false));
		
		array.add(new SkeletalModelInfo("BlenderBoy 01", "BlenderBoy01",
				"./models/blenderboy/01/", "BlenderBoy.mesh.xml", "BlenderBoy.skeleton.xml",
				"BlenderBoy", 5.0f, false));
		
		array.add(new SkeletalModelInfo("BlenderBoy 02", "BlenderBoy02",
				"./models/blenderboy/02/", "BlenderBoy.mesh.xml", "BlenderBoy.skeleton.xml",
				"BlenderBoy", 5.0f, false));
		
		array.add(new SkeletalModelInfo("BlenderBoy 03", "BlenderBoy03",
				"./models/blenderboy/03/", "BlenderBoy.mesh.xml", "BlenderBoy.skeleton.xml",
				"BlenderBoy", 3.0f, false));
		
		array.add(new SkeletalModelInfo("Open Navi3 2Final v2", "navi3_bassani_v2",
				"./models/Navi3_versao2_projeto_oswaldo_Final2/t2/", "Plane.mesh.xml", "Plane.skeleton.xml",
				"Plane", 2.0f, false));
		
		array.add(new SkeletalModelInfo("ManCandy XX v4", "ManMeshXXv4",
				"./models/mancandy_2.48_export_test/v4/", "ManMesh.mesh.xml", "ManCandy.skeleton.xml",
				"ManMesh", 10.0f, false));
		
		array.add(new SkeletalModelInfo("DummyGirl 4", "DummyGirl4_00",
				"./models/dummy_female_model4/00/", "DummyGirl.mesh.xml", "DummyGirl.skeleton.xml",
				"DummyGirl", 1.0f, false));
		*/
		array.add(new SkeletalModelInfo("Gus 00 01", "gus-00-01",
				"./models/gus/00/01/", "Cube.mesh.xml", "Gus.skeleton.xml",
				"Gus", 1.0f, true));
		
		// -------------------------------------
		
		array.add(new SkeletalModelInfo("Ninja 00 01", "ninja-00-01",
				"./models/NinjaBones", "ninja.mesh.xml", "ninja.skeleton.xml",
				"Ninja", 1.0f, true, API.Bones, "nskingr.jpg"));

		// -------------------------------------
		
		models = array.toArray(models);
	}
	
}
