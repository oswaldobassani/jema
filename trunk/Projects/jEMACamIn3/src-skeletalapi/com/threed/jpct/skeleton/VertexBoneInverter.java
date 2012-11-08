package com.threed.jpct.skeleton;

import com.threed.jpct.GenericVertexController;
import com.threed.jpct.SimpleVector;

/**
 * Inverts the vertices of an object, to "undo" the rest pose.
 * 
 * @author mcgreevyj
 */

public class VertexBoneInverter extends GenericVertexController
{
	protected SkeletalObject3D obj;
	
	VertexBoneInverter(SkeletalObject3D obj)
    {
		this.obj = obj;
    }
    public void apply()
    {
    	SimpleVector[] srcMesh = getSourceMesh();
     	SimpleVector[] dstMesh = getDestinationMesh();
     	SimpleVector[] srcNormal = getSourceNormals();
     	SimpleVector[] dstNormal = getDestinationNormals();
     	
     	int i = 0;
     	for (SimpleVector vertex : dstMesh)
		{
     		if(obj.vertexAssignments[i] != null && obj.vertexAssignments[i].hasAssignedBones())
			{
//				Matrix matrix = obj.vertexAssignments[i].bones[0].matrixAbsolute;
//				
//				Vector vector = new Vector();
//     			vector.set(srcMesh[i].toArray());
//     			
//     			matrix.inverseTranslateVect(vector.vectorData);
//     			matrix.inverseRotateVect(vector.vectorData);
//				
//     			dstMesh[i].x = vector.vectorData[0];
//     			dstMesh[i].y = vector.vectorData[1];
//     			dstMesh[i].z = vector.vectorData[2];
//     			
     			
     			dstMesh[i].set(srcMesh[i]);
     			
     			Vector vector = new Vector();
     			
     			int i2 = 0, num2 = 1;//obj.vertexAssignments[i].boneCtr;
     			while(i2<num2)
     			{
     				Matrix matrix = obj.vertexAssignments[i].bones[i2].matrixAbsolute;
    				
     				vector.set(dstMesh[i].toArray());
         			
         			matrix.inverseTranslateVect(vector.vectorData);
         			matrix.inverseRotateVect(vector.vectorData);
    				
         			dstMesh[i].x = vector.vectorData[0];
         			dstMesh[i].y = vector.vectorData[1];
         			dstMesh[i].z = vector.vectorData[2];
         			
     				i2++;
     			}
     			
     			dstNormal[i].set(srcNormal[i]);
     			
     			i2 = 0;num2 = 1;//obj.vertexAssignments[i].boneCtr;
     			while(i2<num2)
     			{
     				Matrix matrix = obj.vertexAssignments[i].bones[i2].matrixAbsolute;
    				
     				vector.set(dstNormal[i].toArray());
         			
         			matrix.inverseRotateVect(vector.vectorData);
    				
         			dstNormal[i].x = vector.vectorData[0];
         			dstNormal[i].y = vector.vectorData[1];
         			dstNormal[i].z = vector.vectorData[2];
         			
     				i2++;
     			}
     			
     			
				//dstMesh[i] = vertex.calcSub(obj.vertexAssignments[i].bones[0].matrixAbsolute);
		    }
			else
		    {
				vertex.set(srcMesh[i]);
		    }
			i++;
		}
    }
}