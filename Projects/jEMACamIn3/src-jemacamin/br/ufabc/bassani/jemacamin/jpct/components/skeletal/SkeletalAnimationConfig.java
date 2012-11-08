package br.ufabc.bassani.jemacamin.jpct.components.skeletal;

import java.util.HashMap;

public class SkeletalAnimationConfig {

	public int animationIndex = 0;
	public float animationTotalTime = 2.50f;
	public int keyframes_size = 10;

	public HashMap<String, SkeletalBoneAnimationConfig> mapa = new HashMap<String, SkeletalBoneAnimationConfig>();

	public void add(String boneName, SkeletalBoneAnimationConfig config){
		mapa.put(boneName, config);
	}

	public boolean hasConfig(String name){
		return mapa.containsKey(name);
	}

	public SkeletalBoneAnimationConfig getConfig(String name){
		if(mapa.containsKey(name)){
			return mapa.get(name);
		}
		return null;
	}

}
