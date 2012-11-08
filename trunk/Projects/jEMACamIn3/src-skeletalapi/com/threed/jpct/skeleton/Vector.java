package com.threed.jpct.skeleton;

public class Vector
{
	//Vector data
	float[] vectorData = new float[4];
	
	public Vector()
	{
		reset();
	}
	
	public void reset()
	{
		vectorData[0] = vectorData[1] = vectorData[2] = 0;
		vectorData[3] = 1;
	}

	public void set(float[] values)
	{
		vectorData[0] = values[0];
		vectorData[1] = values[1];
		vectorData[2] = values[2];
	}

	public void add(Vector v)
	{
		vectorData[0] += v.vectorData[0];
		vectorData[1] += v.vectorData[1];
		vectorData[2] += v.vectorData[2];
		vectorData[3] += v.vectorData[3];
	}

	public void normalize()
	{
		float len = length();

		vectorData[0] /= len;
		vectorData[1] /= len;
		vectorData[2] /= len;
	}

	public float length()
	{
		return (float)Math.sqrt(vectorData[0] * vectorData[0] + vectorData[1] * vectorData[1] + vectorData[2] * vectorData[2]);
	}
	
	public void transform(Matrix m)
	{
		double[] vector = new double[4];
		float[] matrix = m.getMatrix();

		vector[0] = vectorData[0] * matrix[0] + vectorData[1] * matrix[4] + vectorData[2] * matrix[8] + matrix[12];
		vector[1] = vectorData[0] * matrix[1] + vectorData[1] * matrix[5] + vectorData[2] * matrix[9] + matrix[13];
		vector[2] = vectorData[0] * matrix[2] + vectorData[1] * matrix[6] + vectorData[2] * matrix[10] + matrix[14];
		vector[3] = vectorData[0] * matrix[3] + vectorData[1] * matrix[7] + vectorData[2] * matrix[11] + matrix[15];

		vectorData[0] = (float)(vector[0]);
		vectorData[1] = (float)(vector[1]);
		vectorData[2] = (float)(vector[2]);
		vectorData[3] = (float)(vector[3]);
	}

	public void transform3(Matrix m)
	{
		double[] vector = new double[3];
		float[]  matrix = m.getMatrix();

		vector[0] = vectorData[0] * matrix[0] + vectorData[1] * matrix[4] + vectorData[2] * matrix[8];
		vector[1] = vectorData[0] * matrix[1] + vectorData[1] * matrix[5] + vectorData[2] * matrix[9];
		vector[2] = vectorData[0] * matrix[2] + vectorData[1] * matrix[6] + vectorData[2] * matrix[10];

		vectorData[0] = (float)(vector[0]);
		vectorData[1] = (float)(vector[1]);
		vectorData[2] = (float)(vector[2]);
		vectorData[3] = 1;
	}
}