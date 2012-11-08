package com.threed.jpct.skeleton;

import com.threed.jpct.Logger;

/**
 * Encapsulates a vertex assignment.
 * This class, by default, permits 4 bones, and
 * vertex weights.
 * 
 * @author mcgreevyj
 */

public class VertexAssignment
{
	protected SimpleBone[] bones   = new SimpleBone[4];
	protected float[]      weight = new float[4];
	protected int boneCtr = 0;
	
	/**
	 * Adds a bone to the assignment array.
	 * Maximum of 4 bones by default.
	 * 
	 * @param bone		Bone to add to list.
	 * @param weight	Weight of assignment.
	 */
	public void addBone(SimpleBone bone,float weight)
	{
		if(boneCtr >= 4)
		{
			Logger.log("You cannot have more then 4 bones per vertex!",
					   Logger.WARNING);
			return;
		}
			
		this.bones[boneCtr]  = bone;
		this.weight[boneCtr] = weight;
		
		boneCtr++;
	}
	
	/**
	 * Adds a bone to the assignment array.
	 * Maximum of 4 bones by default.
	 * 
	 * @param bone		Bone to add to list.
	 * @param weight	Weight of assignment.
	 */
	public void addBoneMod(SimpleBone bone,float weight)
	{
		if(boneCtr >= 4)
		{
			int minInd = -1;
			float val = Integer.MAX_VALUE;
			for(int i=0; i<4; i++){
				if(this.weight[i]<val){
					val = this.weight[i];
					minInd = i;
				}
			}
			if(this.weight[minInd]<weight){
				this.bones[minInd]  = bone;
				this.weight[minInd] = weight;
			}
			return;
		}
			
		this.bones[boneCtr]  = bone;
		this.weight[boneCtr] = weight;
		
		boneCtr++;
	}
	
	/**
	 * Returns true if the bone has been assigned,
	 * and false if the bone is not assigned.
	 * 
	 * @param bone	Bone to check assignment of
	 * @return	True if found, false otherwise
	 */

	public boolean assignedTo(SimpleBone bone)
	{
		for(SimpleBone b : bones)
		{
			if(b == bone)
				return true;
		}
		return false;
	}
	/**
	 * Returns true if there are bones assigned, false
	 * otherwise.
	 * @return True if bones are assigned, false otherwise
	 */
	public boolean hasAssignedBones()
	{
		if(boneCtr != 0)
			return true;
		else
			return false;
	}
}
