package de.dustplanet.colorme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {
    private ColorMe plugin;
    
    public FileUtils(ColorMe instance) {
	plugin = instance;
    }

    // If no config is found, copy the default one(s)!
    public void copy(InputStream in, File file) {
	OutputStream out = null;
	try {
	    out = new FileOutputStream(file);
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
		out.write(buf, 0, len);
	    }
	} catch (IOException e) {
	    plugin.getLogger().warning("Failed to copy the default config! (I/O)");
	    logDebugException(e);
	    e.printStackTrace();
	} finally {
	    try {
		if (out != null) {
		    out.flush();
		    out.close();
		}
	    } catch (IOException e) {
		plugin.getLogger().warning("Failed to close the streams! (I/O -> Output)");
		logDebugException(e);
		e.printStackTrace();
	    }
	    try {
		if (in != null) {
		    in.close();
		}
	    } catch (IOException e) {
		plugin.getLogger().warning("Failed to close the streams! (I/O -> Input)");
		logDebugException(e);
		e.printStackTrace();
	    }
	}
    }

    // Log into the debug file
    public void logDebug(String string) {
	if (plugin.debug) {
	    BufferedWriter writer = null;
	    try {
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(plugin.debugFile, true), "UTF-8"));
		if (string.equals("")) {
		    writer.write(System.getProperty("line.separator"));
		} else {
		    Date dt = new Date();
		    // Standard date format
		    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    String time = df.format(dt);
		    writer.write(time + " [ColorMe Debug] " + string);
		    writer.write(System.getProperty("line.separator"));
		}
	    } catch (IOException e) {
		plugin.getLogger().warning("An error occurred while writing to the log! IOException");
		e.printStackTrace();
	    } finally {
		if (writer != null) {
		    try {
			writer.flush();
			writer.close();
		    } catch (IOException e) {
			plugin.getLogger().warning("An error occurred while writing to the log! IOException");
			e.printStackTrace();
		    }
		}
	    }
	}
    }

    // Log a stacktrace
    public void logDebugException(Exception ex) {
	FileOutputStream fos = null;
	PrintStream ps = null;
	logDebug("-------------------");
	try {
	    fos = new FileOutputStream(plugin.debugFile, true);
	    ps = new PrintStream(fos);
	    ex.printStackTrace(ps);
	} catch (FileNotFoundException e) {
	    plugin.getLogger().warning("An error occurred while writing to the log! IOException");
	    e.printStackTrace();
	} finally {
	    if (ps != null) {
		ps.flush();
		ps.close();
	    }
	    if (fos != null) {
		try {
		    fos.flush();
		    fos.close();
		} catch (IOException e) {
		    plugin.getLogger().warning("An error occurred while writing to the log! IOException");
		    e.printStackTrace();
		}
	    }
	}
	logDebug("-------------------");
    }
    
    // Check if debug is enabled and if a file needs to be created
    public void checkDebug() {
	if (plugin.debug) {
	    plugin.debugFile = new File(plugin.getDataFolder(), "debug.log");
	    if (!plugin.debugFile.exists()) {
		try {
		    plugin.debugFile.createNewFile();
		} catch (IOException e) {
		    plugin.getLogger().warning("Failed to create the debug.log! IOException");
		    e.printStackTrace();
		}
	    }
	}
    }
    
    // Remove empty lines
    public void removeEmptyLines(File config) throws IOException, FileNotFoundException {
	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(config), "UTF-8"));
	File tempFile = new File(plugin.getDataFolder(), "temp.txt");
	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile, true), "UTF-8"));
	String line;
	try {
	    while ((line = reader.readLine()) != null) {
		if (line.isEmpty()) {
		    continue;
		}
		writer.write(line);
		writer.newLine();
	    }
	    logDebug("Updated the config");
	} catch (IOException e) {
	    plugin.getLogger().warning("An error occurred while updating the config! IOException");
	    logDebug("Failed to update the config");
	    logDebugException(e);
	} finally {
	    reader.close();
	    writer.flush();
	    writer.close();
	    if (config.delete()) {
		if (!tempFile.renameTo(config)) {
		    plugin.getLogger().warning("The tempFile could not be renamed while updating");
		}
	    } else {
		plugin.getLogger().warning("Config could not be deleted while updating!");
	    }
	}
    }
}
