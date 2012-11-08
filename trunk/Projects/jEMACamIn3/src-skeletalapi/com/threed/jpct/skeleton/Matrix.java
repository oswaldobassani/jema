package com.threed.jpct.skeleton;

import java.util.Arrays;

public class Matrix
{
	//Matrix data, stored in column-major order
	float[] matrixData = new float[16];
	
	public Matrix()
	{
		loadIdentity();
	}
	
	public void set(float[] matrix)
	{
		//memcpy( m_matrix, matrix, sizeof( float )*16 );
		int i = 0,num = matrix.length;
		while(i<num)
		{
			matrixData[i] = matrix[i];
			i++;
		}
	}
	public void loadIdentity()
	{
		//memset( m_matrix, 0, sizeof( float )*16 );
		Arrays.fill(matrixData, 0);
		matrixData[0] = matrixData[5] = matrixData[10] = matrixData[15] = 1;
	}
	public void inverseRotateVect(float[] pVect)
	{
		float[] vec = new float[3];

		vec[0] = pVect[0] * matrixData[0] + pVect[1] * matrixData[1] + pVect[2] * matrixData[2];
		vec[1] = pVect[0] * matrixData[4] + pVect[1] * matrixData[5] + pVect[2] * matrixData[6];
		vec[2] = pVect[0] * matrixData[8] + pVect[1] * matrixData[9] + pVect[2] * matrixData[10];

		//memcpy( pVect, vec, sizeof(float)*3 );
		pVect[0] = vec[0];
		pVect[1] = vec[1];
		pVect[2] = vec[2];
	}
	public void inverseTranslateVect(float[] pVect)
	{
		pVect[0] = pVect[0] - matrixData[12];
		pVect[1] = pVect[1] - matrixData[13];
		pVect[2] = pVect[2] - matrixData[14];
	}
	public float[] getMatrix()
	{
		return matrixData;
	}
	public void postMultiply(Matrix matrix)
	{
		float[] newMatrix = new float[16];
		float[] m1 = matrixData, m2 = matrix.matrixData;

		newMatrix[0]  = m1[0] * m2[0] + m1[4] * m2[1] + m1[8] * m2[2];
		newMatrix[1]  = m1[1] * m2[0] + m1[5] * m2[1] + m1[9] * m2[2];
		newMatrix[2]  = m1[2] * m2[0] + m1[6] * m2[1] + m1[10] * m2[2];
		newMatrix[3]  = 0;

		newMatrix[4]  = m1[0] * m2[4] + m1[4] * m2[5] + m1[8] * m2[6];
		newMatrix[5]  = m1[1] * m2[4] + m1[5] * m2[5] + m1[9] * m2[6];
		newMatrix[6]  = m1[2] * m2[4] + m1[6] * m2[5] + m1[10] * m2[6];
		newMatrix[7]  = 0;

		newMatrix[8]  = m1[0] * m2[8] + m1[4] * m2[9] + m1[8] * m2[10];
		newMatrix[9]  = m1[1] * m2[8] + m1[5] * m2[9] + m1[9] * m2[10];
		newMatrix[10] = m1[2] * m2[8] + m1[6] * m2[9] + m1[10] * m2[10];
		newMatrix[11] = 0;

		newMatrix[12] = m1[0] * m2[12] + m1[4] * m2[13] + m1[8] * m2[14] + m1[12];
		newMatrix[13] = m1[1] * m2[12] + m1[5] * m2[13] + m1[9] * m2[14] + m1[13];
		newMatrix[14] = m1[2] * m2[12] + m1[6] * m2[13] + m1[10] * m2[14] + m1[14];
		newMatrix[15] = 1;

		set(newMatrix);
	}
	public void setTranslation(float[] translation)
	{
		matrixData[12] = translation[0];
		matrixData[13] = translation[1];
		matrixData[14] = translation[2];
	}
	public void setInverseTranslation(float[] translation)
	{
		matrixData[12] = -translation[0];
		matrixData[13] = -translation[1];
		matrixData[14] = -translation[2];
	}
	public void setRotationDegrees(float[] angles)
	{
		float[] vec = new float[3];
		
		vec[0] = (float)(angles[0] * 180.0 / Math.PI);
		vec[1] = (float)(angles[1] * 180.0 / Math.PI);
		vec[2] = (float)(angles[2] * 180.0 / Math.PI);
		
		setRotationRadians(vec);
	}
	public void setInverseRotationDegrees(float[] angles)
	{
		float[] vec = new float[3];
		
		vec[0] = (float)(angles[0] * 180.0 / Math.PI);
		vec[1] = (float)(angles[1] * 180.0 / Math.PI);
		vec[2] = (float)(angles[2] * 180.0 / Math.PI);
		
		setInverseRotationRadians(vec);
	}
	public void setRotationRadians(float[] angles)
	{
		double cr = Math.cos(angles[0]);
		double sr = Math.sin(angles[0]);
		double cp = Math.cos(angles[1]);
		double sp = Math.sin(angles[1]);
		double cy = Math.cos(angles[2]);
		double sy = Math.sin(angles[2]);

		matrixData[0] = (float)(cp * cy);
		matrixData[1] = (float)(cp * sy);
		matrixData[2] = (float)(-sp);

		double srsp = sr * sp;
		double crsp = cr * sp;

		matrixData[4] = (float)(srsp * cy - cr * sy);
		matrixData[5] = (float)(srsp * sy + cr * cy);
		matrixData[6] = (float)(sr * cp );

		matrixData[8]  = (float)(crsp * cy + sr * sy);
		matrixData[9]  = (float)(crsp * sy - sr * cy);
		matrixData[10] = (float)(cr*cp);
	}
    public void setInverseRotationRadians(float[] angles)
	{
		double cr = Math.cos(angles[0]);
		double sr = Math.sin(angles[0]);
		double cp = Math.cos(angles[1]);
		double sp = Math.sin(angles[1]);
		double cy = Math.cos(angles[2]);
		double sy = Math.sin(angles[2]);

		matrixData[0] = (float)(cp * cy);
		matrixData[4] = (float)(cp * sy);
		matrixData[8] = (float)(-sp);

		double srsp = sr * sp;
		double crsp = cr * sp;

		matrixData[1] = (float)(srsp * cy - cr * sy);
		matrixData[5] = (float)(srsp * sy + cr * cy);
		matrixData[9] = (float)(sr * cp);

		matrixData[2]  = (float)(crsp * cy + sr * sy);
		matrixData[6]  = (float)(crsp * sy - sr * cy);
		matrixData[10] = (float)(cr * cp);
	}
	public void setRotationQuaternion(Quaternion quat)
	{
		matrixData[0] = (float)(1.0 - 2.0 * quat.quatData[1] * quat.quatData[1] - 2.0 * quat.quatData[2] * quat.quatData[2]);
		matrixData[1] = (float)(2.0 * quat.quatData[0] * quat.quatData[1] + 2.0 * quat.quatData[3] * quat.quatData[2]);
		matrixData[2] = (float)(2.0 * quat.quatData[0] * quat.quatData[2] - 2.0 * quat.quatData[3] * quat.quatData[1]);

		matrixData[4] = (float)(2.0 * quat.quatData[0] * quat.quatData[1] - 2.0 * quat.quatData[3] * quat.quatData[2]);
		matrixData[5] = (float)(1.0 - 2.0 * quat.quatData[0] * quat.quatData[0] - 2.0 * quat.quatData[2] * quat.quatData[2]);
		matrixData[6] = (float)(2.0 * quat.quatData[1] * quat.quatData[2] + 2.0 * quat.quatData[3] * quat.quatData[0]);

		matrixData[8] = (float)(2.0 * quat.quatData[0] * quat.quatData[2] + 2.0 * quat.quatData[3] * quat.quatData[1]);
		matrixData[9] = (float)(2.0 * quat.quatData[1] * quat.quatData[2] - 2.0 * quat.quatData[3] * quat.quatData[0]);
		matrixData[10] = (float)(1.0 - 2.0 * quat.quatData[0] * quat.quatData[0] - 2.0 * quat.quatData[1] * quat.quatData[1]);
	}
}