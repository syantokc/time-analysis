import java.util.HashMap;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * -----------------------------------------------------------------------------
 * This program demonstrates how to write an application that saves the data
 * content of an arbitrary object by use of Java Object Serialization. In its
 * simplest form, object serialization is an automatic way to save and load
 * the state of an object. Basically, an object of any class that implements
 * the Serialization interface can be saved and restored from a stream. Special
 * stream subclasses, "ObjectInputStream" and "ObjectOutputStream", are used to
 * serialize primitive types and objects. Subclasses of Serializable classes are
 * also serializable. The default serialization mechanism saves the value of an
 * object's nonstatic and nontransient member variables.
 * 
 * One of the most important (and tricky) aspects about serialization is that
 * when an object is serialized, any object references it contains are also
 * serialized. Serialization can capture entire "graphs" of interconnected
 * objects and put them back together on the receiving end. The implication is
 * that any object we serialize must contain only references to other 
 * Serializable objects. We can take control of marking nonserializable
 * members as transient or overriding the default serialization mechanisms. The
 * transient modifier can be applied to any instance variable to indicate that
 * its contents are not useful outside of the current context and should never
 * be saved.
 * 
 * In the following example, we create a Hashtable and write it to a disk file
 * called HTExample.ser. The Hashtable object is serializable because it 
 * implements the Serializable interface.
 * 
 * The doLoad method, reads the Hashtable from the HTExample.ser file, using 
 * the readObject() method of ObjectInputStream. The ObjectInputStream class
 * is a lot like DataInputStream, except that it includes the readObject()
 * method. The return type of readObject() is Object, so we will need to cast
 * it to a Hashtable.
 * 
 * @version 1.0
 * @author  Jeffrey M. Hunter  (jhunter@idevelopment.info)
 * @author  http://www.idevelopment.info
 * -----------------------------------------------------------------------------
 */

public class UseSerialization {

	/**
	 * Create a simple Hashtable and serialize it to a file called
	 * HTExample.ser.
	 */
	public static void doSave(Object o, String path) {
		// Save Object o in Path path
		/*
		System.out.println();
		System.out.println("+------------------------------+");
		System.out.println("| doSave Method                |");
		System.out.println("+------------------------------+");
		System.out.println();
		*/

		try {

			//System.out.println("Creating File/Object output stream...");

			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);

			//System.out.println("Writing Hashtable Object...");
			out.writeObject(o);

			//System.out.println("Closing all output streams...\n");
			out.close();
			fileOut.close();

		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Loads the contents of a previously serialized object from a file called
	 * HTExample.ser.
	 */
	public static Object doLoad(String path) {

		/*System.out.println();
		System.out.println("+------------------------------+");
		System.out.println("| doLoad Method                |");
		System.out.println("+------------------------------+");
		System.out.println();*/

		Object o = null;


		try {

			//System.out.println("Creating File/Object input stream...");

			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);

			//System.out.println("Loading Hashtable Object...");
			o = in.readObject();

			//System.out.println("Closing all input streams...\n");
			in.close();
			fileIn.close();


		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return o;    
	}

	public static void main(String [] a){
		HashMap<Integer, String> map = (HashMap<Integer, String>) UseSerialization.doLoad("D:\\arjun\\workspace\\LatentSpamicityCommunity\\vocab_map");
		for(Integer i : map.keySet()){
			System.out.println(i + ":" + map.get(i));
		}
	}

	/**
	 * Sole entry point to the class and application.
	 * @param args Array of String arguments.
	 */
	

}
