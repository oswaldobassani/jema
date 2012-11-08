package com.threed.jpct.skeleton;

public class Quaternion
{
	//Quaternion data
	float[] quatData = new float[4];
	
	public Quaternion(float[] angles)
	{
		fromAngles(angles);
	}
	public Quaternion(Quaternion q1,Quaternion q2,float interp)
	{
		slerp(q1,q2,interp);
	}
	public Quaternion(float ax,float ay,float az,float angle)
	{
		fromAxisAngle(ax,ay,az,angle);
	}
	
	public void inverse()
	{
		quatData[0] = -quatData[0];
		quatData[1] = -quatData[1];
		quatData[2] = -quatData[2];
		quatData[3] = -quatData[3];
	}
	public void fromAxisAngle(float ax,float ay,float az,float angle)
	{
		double w = Math.cos(angle / 2);
		double x = ax * Math.sin(angle / 2);
		double y = ay * Math.sin(angle / 2);
		double z = az * Math.sin(angle / 2);
		
		quatData[0] = (float)x;
		quatData[1] = (float)y;
		quatData[2] = (float)z;
		quatData[3] = (float)w;
	}
	public void fromAngles(float[] angles)
	{
		float  angle;
		double sr, sp, sy, cr, cp, cy;

		angle = angles[2] * 0.5f;
		sy = Math.sin(angle);
		cy = Math.cos(angle);
		angle = angles[1] * 0.5f;
		sp = Math.sin(angle);
		cp = Math.cos(angle);
		angle = angles[0] * 0.5f;
		sr = Math.sin(angle);
		cr = Math.cos(angle);

		double crcp = cr * cp;
		double srsp = sr * sp;

		quatData[0] = (float)(sr * cp * cy - cr * sp * sy);
		quatData[1] = (float)(cr * sp * cy + sr * cp * sy);
		quatData[2] = (float)(crcp * sy - srsp * cy);
		quatData[3] = (float)(crcp * cy + srsp * sy); 
	}
	public void slerp(Quaternion q1,Quaternion q2,float interp)
	{
		// Decide if one of the quaternions is backwards
		int   i;
		float a = 0,b = 0;
		
		for( i = 0; i < 4; i++ )
		{
			a += (q1.quatData[i] - q2.quatData[i]) * (q1.quatData[i] - q2.quatData[i]);
			b += (q1.quatData[i] + q2.quatData[i]) * (q1.quatData[i] + q2.quatData[i]);
		}
		if(a > b)
			q2.inverse();

		float  cosom = q1.quatData[0] * q2.quatData[0] + q1.quatData[1] * q2.quatData[1] + q1.quatData[2] * q2.quatData[2] + q1.quatData[3] * q2.quatData[3];
		double sclq1, sclq2;

		if ((1.0 + cosom) > 0.00000001)
		{
			if ((1.0 - cosom) > 0.00000001)
			{
				double omega = Math.acos(cosom);
				double sinom = Math.sin(omega);
				sclq1 = Math.sin((1.0 - interp) * omega ) / sinom;
				sclq2 = Math.sin(interp * omega) / sinom;
			}
			else
			{
				sclq1 = 1.0-interp;
				sclq2 = interp;
			}
			for (i = 0;i < 4;i++)
				quatData[i] = (float)(sclq1 * q1.quatData[i] + sclq2 * q2.quatData[i]);
		}
		else
		{
			quatData[0] = -q1.quatData[1];
			quatData[1] =  q1.quatData[0];
			quatData[2] = -q1.quatData[3];
			quatData[3] =  q1.quatData[2];

			sclq1 = Math.sin((1.0 - interp) * 0.5 * Math.PI);
			sclq2 = Math.sin(interp * 0.5 * Math.PI);
			
			for(i = 0;i < 3;i++)
				quatData[i] = (float)(sclq1 * q1.quatData[i] + sclq2 * quatData[i]);
		}
	}
}