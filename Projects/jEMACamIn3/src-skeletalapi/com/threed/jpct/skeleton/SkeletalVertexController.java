package com.threed.jpct.skeleton;

import com.threed.jpct.GenericVertexController;
import com.threed.jpct.SimpleVector;

/**
 * Applies the changes to the vertices of an object
 * that has had it's skeletal animation advanced.
 * 
 * @author mcgreevyj
 */

public class SkeletalVertexController extends GenericVertexController
{
	protected SkeletalObject3D obj;
	
    SkeletalVertexController(SkeletalObject3D obj)
    {
    	this.obj = obj;
    }
 
    public void apply()
    {
    	SimpleVector[] srcMesh = getSourceMesh();
     	SimpleVector[] dstMesh = getDestinationMesh();
     	SimpleVector[] srcNormal = getSourceNormals();
     	SimpleVector[] dstNormal = getDestinationNormals();
     	
     	//SimpleVector tmp = new SimpleVector(0,0,0);
     	//int size = getMeshSize();
     	VertexAssignment[] vertexAssignments = obj.vertexAssignments;
     	
     	int i = 0,num = dstMesh.length;
     	while(i<num)
     	{
     		
     		if(vertexAssignments[i] != null && vertexAssignments[i].hasAssignedBones())
     		{
     			Vector vector = new Vector();
     			
     			dstMesh[i].x = 0;
     			dstMesh[i].y = 0;
     			dstMesh[i].z = 0;
     			
     			int i2 = 0, num2 = 1;//vertexAssignments[i].boneCtr;
     			while(i2<num2)
     			{
     				vector.set(srcMesh[i].toArray());
         			vector.transform(vertexAssignments[i].bones[i2].matrixFinal);
         			
         			dstMesh[i].x += vector.vectorData[0] / vertexAssignments[i].weight[i2];
         			dstMesh[i].y += vector.vectorData[1] / vertexAssignments[i].weight[i2];
         			dstMesh[i].z += vector.vectorData[2] / vertexAssignments[i].weight[i2];
     				
         			
         			
         			//dstMesh[i].add(tmp);
         			
     				i2++;
     			}
     			
     			dstNormal[i].set(srcNormal[i]);
     			
     			i2 = 0;num2 = 1;//obj.vertexAssignments[i].boneCtr;
     			while(i2<num2)
     			{
     				Matrix matrix = vertexAssignments[i].bones[i2].matrixFinal;
    				
     				vector.set(dstNormal[i].toArray());
         			
         			matrix.inverseRotateVect(vector.vectorData);
    				
         			dstNormal[i].x = vector.vectorData[0];
         			dstNormal[i].y = vector.vectorData[1];
         			dstNormal[i].z = vector.vectorData[2];
         			
     				i2++;
     			}
     			
     			//Transform according to bone's final matrix
     			//dstMesh[i].matMul(vertexAssignments[i].bones[0].rotationFinal);
     			//dstMesh[i].add(vertexAssignments[i].bones[0].matrixFinal);
     			//TODO: Transform normals.
     		}
     		else
     		{
     			dstMesh[i].set(srcMesh[i]);
     		}
     		i++;
     	}
    }
} 