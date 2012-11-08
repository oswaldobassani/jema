/*---------------------------------------------------------------------------------------

ITESM - Campus Monterrey
Maestria en Sistemas Inteligentes

Proyecto de Tesis : Seguimiento Visual de Multiples Objetos con Camara Movil en Ambientes Dinamicos
Creado por        : Hugo Ortega Hernandez - hugorteg@yahoo.com
Fecha             : 14/08/2003

-----------------------------------------------------------------------------------------*/

package itesm.gvision.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Vector;

//----------------------------------------------------------------------------------
/**
 * Permite leer y escribir archivos de configuraci�n en formato INI.
 * @author  hugo
 */
public class ConfigurationFile
{
	/** Nombre del archivo de configuracion */
	private String file;
	
	/** Nombre del archivo de configuracion por default */
	private String defaultFile;
	
	/** Tabla hash con los parametros de configuracion leidos del archivo */
	private Hashtable params;
	
	//----------------------------------------------------------------------------------
	/** 
	 * Creates a new instance of ConfigurationFile.
	 * @param thefile El archivo con los datos de configuracion.
	 */
	public ConfigurationFile(String thefile)
	{
		file = thefile;
		defaultFile = thefile;
		params = new Hashtable();
		parse();
	}
	
	
	//----------------------------------------------------------------------------------
	/**
	 * Analiza el archivo y 
	 */
	private void parse()
	{
		// Intentamos abrir el archivo para construir la tabla hash con su contenido...
		try{
			BufferedReader in = new BufferedReader(new FileReader(file));
			try{
				int n = 0;
				String line;
				while ((line = in.readLine()) != null){
					n++;
					// lineas en blanco...
					if (line.trim().length() == 0) continue;
					// secciones y comentarios de linea completa...
					if (line.trim().charAt(0) == ';' || line.trim().charAt(0) == '[') continue;
					// todas las demas...
					if (line.indexOf('=') > 0){ // linea valida??
						StringTokenizer tok = new StringTokenizer(line, "=", false);
						String key = tok.nextToken();
						String value = tok.nextToken();
						if (key == null || value == null){
							System.out.println("ConfigurationFile : Syntax error in line " + n + " : " + file);
						}
						else{
							params.put(key.trim().toUpperCase(), value.trim());
						}
					}
					else{
						System.out.println("ConfigurationFile : Syntax error in line " + n + " : " + file);
					}
				}
			}
			catch (IOException e){
				System.out.println("ConfigurationFile : IOException : " + file);
			}
		}
		catch(FileNotFoundException e){
			System.out.println("ConfigurationFile : FileNotFoundException : " + file);
		}		
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Obtiene el conjunto de valores de un string de numeros separados por coma
	 */
	private Vector parseArray(String list)
	{
		Vector ret = new Vector();
		StringTokenizer tok = new StringTokenizer(list, " ", false);
		while (tok.hasMoreTokens()){
			try{
				ret.add(new Double(tok.nextToken()));
			}
			catch (NumberFormatException e){
				System.out.println("ConfigurationFile.parseCommaArray : Invalid value.");
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Redefine el metido toString para visualizar el contenido del archivo.
	 * @return Un string con la representacion de los parametros en orden alfabetico.
	 */
	public String toString()
	{
		TreeMap tmap = new TreeMap(params);
		String output = "";
		for (Iterator i = tmap.keySet().iterator(); i.hasNext(); ){
			String k = (String)i.next();
			String v = (String)params.get(k);
			output += k + " = " + v + "\n";
		}
		return output;
     }
	
	//----------------------------------------------------------------------------------
	/**
	 * Retorna el parametro de configuracion especificado, en forma de String.
	 * @param p El parametro que se quiere obtener.
	 * @return El valor del parametro en forma de String.
	 */
	public String getParam(String p)
	{
		String v = (String)params.get(p.trim().toUpperCase());
		if (v == null) System.out.println("ConfigurationFile : Param not found : " + p);
		return v;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Retorna el parametro de configuracion especificado, como entero.
	 * @param p Parametro que se quiere obtener.
	 * @return El valor del parametro en forma de entero.
	 */
	public int getParamAsInteger(String p)
	{
		String v = (String)params.get(p.trim().toUpperCase());
		int nv = 0;
		if (v != null){
			try{
				nv = Integer.parseInt(v);
			}
			catch (NumberFormatException e) {
				System.out.println("ConfigurationFile : Unable to get integer value for param : " + p);
			}
		}
		else{
			System.out.println("ConfigurationFile : Param not found : " + p);
		}
		return nv;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Retorna el parametro de configuracion especificado, como double.
	 * @param p Parametro que se desea obtener.
	 * @return El valor del parametro en forma de double.
	 */
	public double getParamAsDouble(String p)
	{
		String v = (String)params.get(p.trim().toUpperCase());
		double nv = 0;
		if (v != null) {
			try{
				nv = Double.parseDouble(v);
			}
			catch (NumberFormatException e) {
				System.out.println("ConfigurationFile : Unable to get double value for param : " + p);
			}
		}
		else{
			System.out.println("ConfigurationFile : Param not found : " + p);
		}
		return nv;
	}

	//----------------------------------------------------------------------------------
	/**
	 * Retorna el parametro de configuracion especificado, como double.
	 * @param p Parametro que se desea obtener.
	 * @return El valor del parametro en forma de booleano.
	 */
	public boolean getParamAsBoolean(String p)
	{
		String v = (String)params.get(p.trim().toUpperCase());
		boolean nv = true;
		if (v != null) {
			nv = Boolean.valueOf(v).booleanValue();
		}
		else{
			System.out.println("ConfigurationFile : Param not found : " + p);
		}
		return nv;
	}

	//----------------------------------------------------------------------------------
	/**
	 * Retorna el parametro de configuracion especificado, como un arreglo de doubles.
	 * @param p El parametro que se desea obtener
	 * @return Un arreglo double con los valores.
	 */
	public double[] getParamAsDoubleArray(String p)
	{
		String v = (String)params.get(p.trim());
		Vector numbers = this.parseArray(v);
		double[] ret = new double[numbers.size()];
		for(int i=0; i<ret.length; i++){
			ret[i] = ((Double)numbers.get(i)).doubleValue();
		}
		return ret;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Asigna el valor especificado al parametro p. 
	 * @param p El parametro que se desea modificar
	 * @param v El valor que se asignara al parametro
	 * @return true si pudo asignar el valor; false en caso de que el parametro no exista.
	 */
	public boolean setParam(String p, String v)
	{
		if (params.containsKey(p.trim().toUpperCase())){
			params.put(p.toUpperCase(), v);
			return true;
		}
		return false;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Agrega un nuevo parametro de configuracion
	 * @param p El nombre del parametro
	 * @param v El valor inicial
	 */
	public void addParam(String p, String v)
	{
		params.put(p.trim().toUpperCase(), v.trim());
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Asigna el valor entero especificado al parametro.
	 * @param p El parametro que se desea modificar
	 * @param v El valor entero que se asiganara al parametro
	 * @return true si pudo asignar el valor; false en caso de que el parametro no exista.
	 */
	public boolean setParam(String p, int v)
	{
		if (params.containsKey(p.trim().toUpperCase())){
			String val = Integer.toString(v);
			params.put(p.trim().toUpperCase(), val);
			return true;
		}
		return false;
	}

	//----------------------------------------------------------------------------------
	/**
	 * Asigna el valor double especificado al parametro.
	 * @param p El parametro que se desea modificar
	 * @param v El valor double que se asiganara al parametro
	 * @return true si pudo asignar el valor; false en caso de que el parametro no exista.
	 */
	public boolean setParam(String p, double v)
	{
		if (params.containsKey(p.toUpperCase())){
			String val = Double.toString(v);
			params.put(p.trim().toUpperCase(), val);
			return true;
		}
		return false;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Asigna los valores del arreglo double especificado al parametro.
	 * @param p El parametro que se desea modificar
	 * @param v El valor double que se asiganara al parametro
	 * @return true si pudo asignar el valor; false en caso de que el parametro no exista.
	 */
	public boolean setParam(String p, double v[])
	{
		if (params.containsKey(p.toUpperCase())){
			String val = "";
			for(int i=0; i<v.length; i++){
				val += v[i] + " ";
			}
			params.put(p.trim().toUpperCase(), val);
			return true;
		}
		return false;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Asigna el valor booleano especificado al parametro.
	 * @param p El parametro que se desea modificar
	 * @param v El valor booleano que se asiganara al parametro
	 * @return true si pudo asignar el valor; false en caso de que el parametro no exista.
	 */
	public boolean setParam(String p, boolean v)
	{
		if (params.containsKey(p.toUpperCase())){
			String val = Boolean.toString(v);
			params.put(p.trim().toUpperCase(), val);
			return true;
		}
		return false;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Guarda el archivo de configuracion con el contenido actual
	 */
	public void save()
	{
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss:S";
		SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getDefault());
		String fecha = sdf.format(cal.getTime()).toUpperCase();
		
		try{
			FileWriter fw = new FileWriter(file, false);
			fw.write(";----------------------------------------------\n");
			fw.write("; " + file + "\n");
			fw.write("; Configuration file.\n");
			fw.write("; Updated at " + fecha + "\n");
			fw.write(";----------------------------------------------\n");
			fw.write(this.toString());
			fw.close();
		}
		catch (Exception e){
			System.out.println("ConfigurationFile : Error saving the configuration file: " + file);
		}
	}
	
	
	//----------------------------------------------------------------------------------
	/**
	 * Guarda el archivo de configuracion con el contenido actual
	 * @param name El nombre del archivo.
	 */
	public void save(String name)
	{
		String orig = this.file;
		this.file   = name;
		try{
			this.save();
		}
		finally{
			this.file = orig;
		}
	}

	
	//----------------------------------------------------------------------------------
	/**
	 * Guarda el archivo de configuracion con el contenido actual
	 */
	public void saveDefault()
	{
		String orig = this.file;
		this.file   = this.defaultFile;
		try{
			this.save();
		}
		finally{
			this.file = orig;
		}
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Lee el archivo con la configuracion especificada
	 * @param name El nombre del archivo a leer.
	 */
	public void load(String name)
	{
		file = name;
		params.clear(); // tan solo por si acaso.
		params = new Hashtable();
		parse();			
	}	
	
	//----------------------------------------------------------------------------------
	/*
	 public static void main(String args[])
	{
		ConfigurationFile cfg = new ConfigurationFile("system.config");
		System.out.println(cfg);
		double[] foo = cfg.getParamAsDoubleArray("CAMERA_INTRINSICS");
		System.out.print("Intrinsic = ");
		for(int i=0; i<foo.length; i++) System.out.print(foo[i] + ",");
		System.out.println();
		foo[0] = 0;
		foo[2] = 0;
		foo[8] = 12345.6789;
		cfg.setParam("CAMERA_INTRINSICS", foo);
		foo = cfg.getParamAsDoubleArray("CAMERA_INTRINSICS");
		System.out.print("Intrinsic = ");
		for(int i=0; i<foo.length; i++) System.out.print(foo[i] + ",");
		System.out.println();		
	}
	*/
}
