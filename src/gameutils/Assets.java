package gameutils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class Assets<T> {
	protected Map<String,T> assets;
	
	public Assets() {
		assets = new HashMap<String,T>();
	}
	
	public static String getFileName(String file) {
		file = file.replace("\\","/");
		return file.substring(file.lastIndexOf("/")+1,file.lastIndexOf("."));
	}
	
	public T add(String file) throws IOException {
		return add(file,getFileName(file));
	}
	
	public T add(String file, String name) throws IOException {
		return add(getClass().getResource("/"+file),name);
	}
	
	public T add(URL url, String name) throws IOException {
		return add(extract(url),name);
	}
	
	public abstract T extract(URL url) throws IOException;
	
	public synchronized T add(T t, String name) {
		return assets.put(name,t);
	}
	
	public T get(String name) {
		return assets.get(name);
	}
	
	public synchronized String getName(T t) {
		for(String s : assets.keySet()) {
			if(assets.get(s) == t)
				return s;
		}
		
		return null;
	}
	
	public void rename(String oldName, String newName) {
		add(remove(oldName),newName);
	}
	
	public T replace(String oldName, T t) {
		if(get(oldName) == null)
			throw new IllegalArgumentException("Invalid name");
		
		return add(t,oldName);
	}
	
	public void swap(String first, String second) {
		T t = assets.get(first);
		T t2 = assets.get(second);
		
		if(t == null)
			throw new IllegalArgumentException("First name is invalid.");
		if(t2 == null)
			throw new IllegalArgumentException("Second name is invalid");
		
		assets.put(second,assets.put(first,assets.get(second)));
	}
	
	public synchronized T remove(String name) {
		T t = assets.get(name);
		assets.remove(name);
		return t;
	}
	
	public int size() {
		return assets.size();
	}
	
	public class Loader implements Runnable {
		private Map<String,String> files;
		private int status;
		
		public Loader() {
			files = new HashMap<String,String>();
		}
		
		public int getTotal() {
			return files.size();
		}
		
		public int getStatus() {
			return status;
		}
		
		public void addFile(String file) {
			addFile(file,getFileName(file));
		}
		
		public synchronized void addFile(String file, String name) {
			files.put(name,file);
		}
		
		public void addFiles(String ... files) {
			for(String s : files)
				addFile(s);
		}
		
		public void addFiles(String[] ... files) {
			for(String[] s : files) {
				addFile(s[0],s[1]);
			}
		}
		
		public synchronized void start() {
			new Thread(this).start();
		}
		
		public synchronized void run() {
			for(String s : files.keySet()) {
				try{
					add(files.get(s),s);
					status++;
				}
				catch(Exception exc) {
					System.out.println(s);
					exc.printStackTrace();
					status = -1;
					return;
				}
			}
		}
	}
}
