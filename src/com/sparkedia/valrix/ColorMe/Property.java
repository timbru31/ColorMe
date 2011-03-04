package com.sparkedia.valrix.ColorMe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Property {
	private static final Logger log = Logger.getLogger("Minecraft");
	protected ColorMe plugin;
	private Properties properties;
	private String fileName;

	public Property(String fileName, ColorMe plugin) {
		this.plugin = plugin;
		this.fileName = fileName;
		this.properties = new Properties();
		File file = new File(fileName);

		if (file.exists()) {
			load();
		} else {
			save();
		}
	}

	public void load() {
		try {
			FileInputStream inFile = new FileInputStream(this.fileName);
			this.properties.load(inFile);
			inFile.close();
		} catch (IOException ex) {
			log.log(Level.SEVERE, "["+plugin.pName+"]: Unable to load "+this.fileName, ex);
		}
	}

	public void save() {
		try {
			FileOutputStream outFile = new FileOutputStream(this.fileName);
			this.properties.store(outFile, "Minecraft Properties File");
			outFile.close();
		} catch (IOException ex) {
			log.log(Level.SEVERE, "["+plugin.pName+"]: Unable to save "+this.fileName, ex);
		}
	}

	public Map<String, String> returnMap() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new FileReader(this.fileName));
		String line;
		while ((line = reader.readLine()) != null) {
			if ((line.trim().length() == 0) || 
					(line.charAt(0) == '#')) {
				continue;
			}
			int delimPosition = line.indexOf('=');
			String key = line.substring(0, delimPosition).trim();
			String value = line.substring(delimPosition + 1).trim();
			map.put(key, value);
		}
		reader.close();
		return map;
	}

	public boolean keyExists(String key) {
		return this.properties.containsKey(key);
	}
	
	public void remove(String key) {
		this.properties.remove(key);
		save();
	}
	
	// STRING
	public String getString(String key) {
		if (this.properties.containsKey(key)) {
			return this.properties.getProperty(key);
		}
		return "";
	}
	public void setString(String key, String value) {
		this.properties.setProperty(key, value);
		save();
	}
	
	// INT
	public int getInt(String key) {
		if (this.properties.containsKey(key)) {
			return Integer.parseInt(this.properties.getProperty(key));
		}
		return 0;
	}
	public void setInt(String key, int value) {
		this.properties.setProperty(key, String.valueOf(value));
		save();
	}
	
	// DOUBLE
	public double getDouble(String key) {
		if (this.properties.containsKey(key)) {
			return Double.parseDouble(this.properties.getProperty(key));
		}
		return 0.0D;
	}
	public void setDouble(String key, double value) {
		this.properties.setProperty(key, String.valueOf(value));
		save();
	}
	
	// LONG
	public long getLong(String key) {
		if (this.properties.containsKey(key)) {
			return Long.parseLong(this.properties.getProperty(key));
		}
		return 0L;
	}
	public void setLong(String key, long value) {
		this.properties.setProperty(key, String.valueOf(value));
		save();
	}
	
	// FLOAT
	public float getFloat(String key) {
		if (this.properties.containsKey(key)) {
			return Float.parseFloat(this.properties.getProperty(key));
		}
		return 0F;
	}
	public void setFloat(String key, float value) {
		this.properties.setProperty(key, String.valueOf(value));
		save();
	}
	
	// BOOLEAN
	public boolean getBoolean(String key) {
		if (this.properties.containsKey(key)) {
			return Boolean.parseBoolean(this.properties.getProperty(key));
		}
		return false;
	}
	public void setBoolean(String key, boolean value) {
		this.properties.setProperty(key, String.valueOf(value));
		save();
	}
}