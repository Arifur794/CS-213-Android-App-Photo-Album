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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.*;

public class Photo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;
	transient Bitmap image;
	String caption = "";
    private Map<String, ArrayList<String>> tagsHashTable = new HashMap<>();

	
	public String[] getTagsAsString(){
		
		String[] tagsAsSingleString = new String[tagsHashTable.size()];
		
		tagsAsSingleString = (String[]) tagsHashTable.values().toArray();
		
		return tagsAsSingleString;
	}
	
	public String[][] getTagsWithKeyValues(){
		
		int tagCount = 0;
	
		ArrayList<String> loc = tagsHashTable.get("location");
		ArrayList<String> per = tagsHashTable.get("person");
		
		if (loc != null) tagCount += loc.size();
		if (per != null) tagCount += per.size();
		
		String[][] tagsArray = new String[2][tagCount];
		
		int j = 0; 
		
		if (loc != null) {
			for(int i = 0; i < loc.size(); i++) { tagsArray[0][j] = "location";
				tagsArray[1][j] = loc.get(i); j++; 
			}
		}

		if (per != null) {
			for(int i = 0; i < per.size(); i++) { tagsArray[0][j] = "person";
				tagsArray[1][j] = per.get(i); j++;
			}
		}

		return tagsArray;
	    
	}
	
	public void removeTag(String key, String value){
		getListWithKey(key).remove(value);
	}
	
	public ArrayList<String> getListWithKey(String key) {
		return tagsHashTable.get(key);
	}
	
	public void addTag(String key, String value){
		if (tagsHashTable.containsKey(key)){
			if (tagsHashTable.get(key).contains(value)) {
				return;
			}
			tagsHashTable.get(key).add(value);
		} else {
			ArrayList<String> arrList = new ArrayList<String>();
			arrList.add(value);
			tagsHashTable.put(key, arrList);

		}
	}
	public ArrayList<String> personTags(){
		ArrayList<String> person = tagsHashTable.get("person");
		return person;
	}
	public ArrayList<String> locationTags(){
		ArrayList<String> location = tagsHashTable.get("location");
		return location;
	}
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}


	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		int b;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		while((b = ois.read()) != -1)
			byteStream.write(b);
		byte bitmapBytes[] = byteStream.toByteArray();
		image = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		if(image != null){
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
			byte bitmapBytes[] = byteStream.toByteArray();
			oos.write(bitmapBytes, 0, bitmapBytes.length);
		}
	}

}

