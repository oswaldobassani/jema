package br.ufabc.bassani.jemacamin.jpct.components.skeletal;

import com.threed.jpct.skeleton.Quaternion;
import com.threed.jpct.skeleton.RotationKeyframe;
import com.threed.jpct.skeleton.TranslationKeyframe;

public class SkeletalBoneAnimationConfig {

	public float translateX = 0.0f;
	public float translateY = 0.0f;
	public float translateZ = 0.0f;
	public float rotateX = 0.0f;
	public float rotateY = 0.0f;
	public float rotateZ = 1.0f;
	public float rotateAngle = 0.0f;
	
	public void setTranslate(float translateX, float translateY, float translateZ) {
		this.translateX = translateX;
		this.translateY = translateY;
		this.translateZ = translateZ;
	}
	
	public void setRotate(float rotateX, float rotateY, float rotateZ, float rotateAngle) {
		setRotate(rotateX, rotateY, rotateZ);
		setRotateAngle(rotateAngle);
	}

	public void setRotate(float rotateX, float rotateY, float rotateZ) {
		this.rotateX = rotateX;
		this.rotateY = rotateY;
		this.rotateZ = rotateZ;
	}

	public void setRotateAngle(float rotateAngle) {
		this.rotateAngle = rotateAngle;
	}

	public void setRotationKeyframe_Quaternion(RotationKeyframe rotationKeyframe, float kf_rotateAngle){
		rotationKeyframe.quat = new Quaternion(rotateX, rotateY, rotateZ, kf_rotateAngle);
	}

	public void setTranslationKeyframe(TranslationKeyframe translationKeyframe){
		translationKeyframe.setTranslation(translateX, translateY, translateZ);
	}
	
	public static final SkeletalBoneAnimationConfig getRotateZConfig(float rotateA){
		SkeletalBoneAnimationConfig cfg = new SkeletalBoneAnimationConfig();
		cfg.rotateAngle = rotateA;
		return cfg;
	}
	
}
