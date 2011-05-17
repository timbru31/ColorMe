package com.sparkedia.valrix.ColorMe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Property {
	private Logger log;
	protected ColorMe plugin;
	private LinkedHashMap<String, Object> properties = new LinkedHashMap<String, Object>();
	private String filename;
	private String pName;
	private String version;
	private String type;

	public Property(String filename, String type, ColorMe plugin) {
		this.plugin = plugin;
		this.pName = plugin.pName;
		this.version = plugin.getDescription().getVersion();
		this.type = type.toLowerCase().replace(type.charAt(0), type.toUpperCase().charAt(0));
		this.log = plugin.log;
		this.filename = filename;
		File file = new File(filename);

		if (file.exists()) load(); else save();
	}
	
	// Load data from file into ordered HashMap
	public void load() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
			String line;
			int cc = 0; // # of comments
			int lc = 0; // # of lines
			int delim;
			
			// While there are lines to read
			while ((line = br.readLine()) != null) {
				// Store the version of the old config. Simplified version checking method
				if (lc == 0) {
					String v = (line.indexOf('-') > 0) ? line.substring(line.indexOf('-')+1).trim() : "na";
					properties.put(pName+"Version", v);
					properties.put(pName+"Type", type);
					lc++;
					continue;
				}
				// If not the first line and is a comment, store it for later
				if (line.charAt(0) == '#' && lc > 0) {
					properties.put("#"+cc, line.substring(line.indexOf(' ')+1).trim());
					cc++;
					lc++;
					continue;
				}
				// Isn't a comment, store the key and value
				while ((delim = line.indexOf('=')) != -1) {
					properties.put(line.substring(0, delim).trim(), line.substring(delim+1).trim());
					break;
				}
				lc++;
			}
		} catch (FileNotFoundException ex) {
			log.log(Level.SEVERE, '['+pName+"]: Couldn't find file "+filename, ex);
			return;
		} catch (IOException ex) {
			log.log(Level.SEVERE, '['+pName+"]: Unable to save "+filename, ex);
			return;
		} finally {
			// Close the reader
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				log.log(Level.SEVERE, '['+pName+"]: Unable to save "+filename, ex);
			}
		}
	}
	
	// Save data from LinkedHashMap to file
	public void save() {
		BufferedWriter bw = null;
		try {
			// Construct the BufferedWriter object
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"UTF-8"));
			bw.write("# "+pName+" "+type+" File -"+version);
			bw.newLine();
			
			// Save all the properties one at a time, only if there's data to write
			if (properties.size() > 0) {
				// Grab all the entries and create an iterator to run through them all
				Set<?> set = properties.entrySet();
				Iterator<?> i = set.iterator();
				
				// While there's data to iterate through..
				while (i.hasNext()) {
					// Map the entry and save the key and value as variables
					Map.Entry<?, ?> me = (Map.Entry<?, ?>)i.next();
					String key = me.getKey().toString();
					String val = me.getValue().toString();
					
					// If it starts with "#", it's a comment so write it as such
					if (key.charAt(0) == '#') {
						// Writing a comment to the file
						bw.write("# "+val);
						bw.newLine();
						continue;
					}
					// Otherwise write the key and value pair as key=value
					if (!key.equalsIgnoreCase(pName+"Version") && !key.equalsIgnoreCase(pName+"Type")) {
						bw.write(key+'='+val);
						bw.newLine();
						continue;
					}
				}
			}
		} catch (FileNotFoundException ex) {
			log.log(Level.SEVERE, '['+pName+"]: Couldn't find file "+filename, ex);
			return;
		} catch (IOException ex) {
			log.log(Level.SEVERE, '['+pName+"]: Unable to save "+filename, ex);
			return;
		} finally {
			// Close the BufferedWriter
			try {
				if (bw != null) bw.close();
			} catch (IOException ex) {
				log.log(Level.SEVERE, '['+pName+"]: Unable to save "+filename, ex);
			}
		}
	}
	
	// Rebuild the current properties file using data from newMap
	public void rebuild(LinkedHashMap<String, Object> newMap) {
		properties.clear();
		properties.putAll(newMap);
		save();
	}
	
	// Function to check if current properties file matches a referenced one by validating every key
	public boolean match(LinkedHashMap<String, Object> prop) {
		return (properties.keySet().containsAll(prop.keySet())) ? true : false;
	}
	
	// Check if the key exists or not
	public boolean keyExists(String key) {
		return (properties.containsKey(key)) ? true : false;
	}
	
	// Check if the key no value
	public boolean isEmpty(String key) {
		return (properties.get(key).toString().length() == 0) ? true : false;
	}
	
	// Remove key from map
	public boolean remove(String key) {
		return (properties.remove(key) != null) ? true : false;
	}
	
	// Return a set of all keys currently in the properties map
	public Set<String> getKeys() {
		return properties.keySet();
	}
	
	// get and set property value as a string
	public String getString(String key) {
		return (properties.containsKey(key)) ? properties.get(key).toString() : "";
	}
	public void setString(String key, String value) {
		properties.put(key, value);
	}
	
	// get and set property value as an int
	public int getInt(String key) {
		return (properties.containsKey(key)) ? Integer.parseInt(properties.get(key).toString()) : 0;
	}
	public void setInt(String key, int value) {
		properties.put(key, String.valueOf(value));
		save();
	}
	
	// get and set property value as a double
	public double getDouble(String key) {
		return (properties.containsKey(key)) ? Double.parseDouble(properties.get(key).toString()) : 0.0D;
	}
	public void setDouble(String key, double value) {
		properties.put(key, String.valueOf(value));
	}
	
	// get and set property value as a boolean
	public boolean getBoolean(String key) {
		return (properties.containsKey(key)) ? Boolean.parseBoolean(properties.get(key).toString()) : false;
	}
	public void setBoolean(String key, boolean value) {
		properties.put(key, String.valueOf(value));
	}
}