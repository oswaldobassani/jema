/* 
 * PROJECT: NyARToolkit
 * --------------------------------------------------------------------------------
 * This work is based on the original ARToolKit developed by
 *   Hirokazu Kato
 *   Mark Billinghurst
 *   HITLab, University of Washington, Seattle
 * http://www.hitl.washington.edu/artoolkit/
 *
 * The NyARToolkit is Java edition ARToolKit class library.
 * Copyright (C)2008-2009 Ryo Iizuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * For further information please contact.
 *	http://nyatla.jp/nyatoolkit/
 *	<airmail(at)ebony.plala.or.jp> or <nyatla(at)nyatla.jp>
 * 
 */
package jp.nyatla.nyartoolkit.core.transmat;



import jp.nyatla.nyartoolkit.core.types.NyARDoublePoint3d;
import jp.nyatla.nyartoolkit.core.types.matrix.*;

/**
 * NyARTransMat戻り値専用のNyARMat
 * 
 */
public class NyARTransMatResult extends NyARDoubleMatrix44
{
	/**
	 * この行列に1度でも行列をセットしたかを返します。
	 */
	public boolean has_value = false;
	/**
	 * 観測値とのずれを示すエラーレート値です。SetValueにより更新されます。
	 * {@link #has_value}がtrueの時に使用可能です。
	 */
	public double last_error;
	/**
	 * コンストラクタです。
	 */
	public NyARTransMatResult()
	{
		this.m30=this.m31=this.m32=0;
		this.m33=1.0;
	}
	/**
	 * 平行移動量と回転行列をセットします。この関数は、INyARTransmatインタフェイスのクラスが結果を保存するために使います。
	 * @param i_rot
	 * @param i_trans
	 */
	public final void setValue(NyARDoubleMatrix33 i_rot, NyARDoublePoint3d i_trans,double i_error)
	{
		this.m00=i_rot.m00;
		this.m01=i_rot.m01;
		this.m02=i_rot.m02;
		this.m03=i_trans.x;

		this.m10 =i_rot.m10;
		this.m11 =i_rot.m11;
		this.m12 =i_rot.m12;
		this.m13 =i_trans.y;

		this.m20 = i_rot.m20;
		this.m21 = i_rot.m21;
		this.m22 = i_rot.m22;
		this.m23 = i_trans.z;

		this.m30=this.m31=this.m32=0;
		this.m33=1.0;		
		this.has_value = true;
		this.last_error=i_error;
		return;
	}	
}
