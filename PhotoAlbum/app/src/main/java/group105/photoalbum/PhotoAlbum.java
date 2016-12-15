package group105.photoalbum;

/**
 * 
 * Group 105
 * 
 * @author Arifur Rahman
 * @author Monique Gordon
 * 
 * 
 */

	
import java.io.*;
import java.util.ArrayList;
import android.content.Context;

public class PhotoAlbum implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;
	public static final String fileName = "photoalbum.ser";
	public ArrayList<Album> albums;
	public static Album searchResults = new Album("results");

	
	public static PhotoAlbum loadFromDisk(Context context){
		PhotoAlbum pa = null;
		try {
			FileInputStream fis = context.openFileInput(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			pa = (PhotoAlbum) ois.readObject();

			if (pa.albums == null) {
				pa.albums = new ArrayList<Album>();
			}
			fis.close();
			ois.close();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
		return pa;
	}
	
	public void saveToDisk(Context context){
		ObjectOutputStream oos;
		try {
			FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(this);
			objectOutputStream.close();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	
}
