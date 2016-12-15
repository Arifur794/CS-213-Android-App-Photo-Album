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

import java.util.ArrayList;
import java.io.Serializable;

public class Album implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;
	String name;
	ArrayList<Photo> photos = new ArrayList<Photo>();

	
	public Album(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNumOfPhotos() {
		return photos.size();
	}
	public ArrayList<Photo> getPhotos() {
		return photos;
	}
	public void setPhotos(ArrayList<Photo> photos) {
		this.photos = photos;
	}
	public void addOnePhoto(Photo photo){
		photos.add(photo);
	}
	public void clear(){
		photos.clear();
	}
	public String toString(){
		return name;
	}
}
