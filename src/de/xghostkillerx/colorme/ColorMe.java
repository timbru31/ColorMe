package de.xghostkillerx.colorme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileReader;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
// Economy (Vault)
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

/**
 * ColorMe for CraftBukkit/Bukkit
 * Handles some general stuff.
 * And the checks for color etc.
 * 
 * Refer to the forum thread:
 * http://bit.ly/colormebukkit
 * Refer to the dev.bukkit.org page:
 * http://bit.ly/bukkitdevcolorme
 *
 * @author xGhOsTkiLLeRx
 * @thanks to Valrix for the original ColorMe plugin!!
 * 
 */

public class ColorMe extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");
	private final ColorMePlayerListener playerListener = new ColorMePlayerListener(this);
	public Economy economy = null;
	public FileConfiguration config;
	public FileConfiguration colors;
	public FileConfiguration localization;
	public File configFile;
	public File colorsFile;
	public File localizationFile;
	public boolean spoutEnabled;
	private ColorMeCommands colorExecutor;
	private PrefixCommands prefixExecutor;
	private SuffixCommands suffixExecutor;

	// Shutdown
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " " + pdfFile.getVersion()	+ " has been disabled!");
	}

	// Start
	public void onEnable() {
		// Events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);

		// Player colors config		
		colorsFile = new File(getDataFolder(), "players.color");
		// Copy if the config doesn't exist
		if (!colorsFile.exists()) {
			colorsFile.getParentFile().mkdirs();
			copy(getResource("players.color"), colorsFile);
		}
		// Try to load
		try {
			colors = YamlConfiguration.loadConfiguration(colorsFile);
		}
		// if it failed, tell about the update progress
		catch (Exception e) {
			log.warning("ColorMe failed to load the players.color! Trying to update...");
			try {
				// Update colors
				updateConfig(colorsFile);
			}
			catch (Exception exp) {
				// if update fails, tell the admin
				log.warning("ColorMe failed to update the players.color. Please report this!");
			}
		}

		// Config
		configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), configFile);
		}
		config = this.getConfig();
		loadConfig();
		
		// Localization
		localizationFile = new File(getDataFolder(), "localization.yml");
		if(!localizationFile.exists()){
			localizationFile.getParentFile().mkdirs();
			copy(getResource("localization.yml"), localizationFile);
		}
		// Try to load
		try {
			localization = YamlConfiguration.loadConfiguration(localizationFile);
			loadLocalization();
		}
		// if it failed, tell it
		catch (Exception e) {
			log.warning("ColorMe failed to load the localization!");
		}
		
		// Refer to ColorMeCommands
		colorExecutor = new ColorMeCommands(this);
		getCommand("color").setExecutor(colorExecutor);

		// Refer to PrefixCommands
		prefixExecutor = new PrefixCommands(this);
		getCommand("prefix").setExecutor(prefixExecutor);
		
		// Refer to SuffixCommands
		suffixExecutor = new SuffixCommands(this);
		getCommand("suffix").setExecutor(suffixExecutor);
		
		// Message
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled!");

		// Check for Vault
		Plugin vault = this.getServer().getPluginManager().getPlugin("Vault");
		if (vault != null & vault instanceof Vault) {
			// If Vault is enabled, load the economy
			log.info(pdfFile.getName() + " loaded Vault successfully");
			setupEconomy();
		} else {
			// Else tell the admin about the missing of Vault
			log.warning("Vault was NOT found! Running without economy!");
		}

		//Check for Spout
		Plugin spout = this.getServer().getPluginManager().getPlugin("Spout");
		if (spout != null) {
			log.info(String.format(pdfFile.getName() + " loaded Spout successfully"));
			// Spout is enabled
			spoutEnabled = true;
		}
		else {
			log.warning("Running without Spout!");
			// Spout is disabled
			spoutEnabled = false;
		}

		// Update if forced
		if (config.getBoolean("forceUpdate") == true) {
			try {
				// Update colors
				updateConfig(colorsFile);
			}
			catch (Exception exp) {
				// if update fails, tell the admin
				log.warning("ColorMe failed to update the players.color. Please report this!");
			}
			finally {
				// Sets the forceUpdate value false again
				config.set("forceUpdate", false);
				saveConfig();
			}
		}

		// Stats
		try {
			Metrics metrics = new Metrics();
			metrics.beginMeasuringPlugin(this);
		}
		catch (IOException e) {}
	}

	// Updated the config to the new system
	public void updateConfig(File config) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(config));
		// Create a file called temp.txt
		File tempFile = new File(getDataFolder(), "temp.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				// Replaces all wrong colors, wrong cases, TABs and the old format (=)
				writer.write(line
						.replace("\t", "    ")
						.replace("=", ": ")
						.replaceAll("(?i)darkred", "dark_red")
						.replaceAll("(?i)darkblue", "dark_blue")
						.replaceAll("(?i)darkgreen", "dark_green")
						.replaceAll("(?i)darkaqua", "dark_aqua")
						.replaceAll("(?i)darkpurple", "dark_purple")
						.replaceAll("(?i)darkgray", "dark_gray")
						.replaceAll("(?i)red", "red")
						.replaceAll("(?i)green", "green")
						.replaceAll("(?i)aqua", "aqua")
						.replaceAll("(?i)gold", "gold")
						.replaceAll("(?i)yellow", "yellow")
						.replaceAll("(?i)blue", "blue")
						.replaceAll("(?i)black", "black")
						.replaceAll("(?i)gray", "gray")
						.replaceAll("(?i)white", "white")
						.replaceAll("(?i)rainbow", "rainbow")
						.replaceAll("(?i)random", "random")
						.replaceAll("(?i)lightpurple", "light_purple"));
				writer.newLine(); 
			}
		}
		catch (Exception e) {
			log.warning("ColorMe failed to update the colors! Report this please!");
		}
		finally {
			// Close all
			reader.close();
			writer.flush();
			writer.close();
			// Delete old players.color and rename temp file
			config.delete();
			tempFile.renameTo(colorsFile);
		}
	}

	// Loads the config at the start
	public void loadConfig() {
		config.options().header("For help please refer to http://bit.ly/colormebukkit or http://bit.ly/bukkitdevcolorme");
		config.addDefault("costs", 5.00);
		config.addDefault("forceUpdate", false);
		config.addDefault("tabList", true);
		config.addDefault("playerTitle", true);
		config.addDefault("Prefixer", true);
		config.addDefault("Suffixer", true);
		config.addDefault("ColorMe", true);
		// As long as all colors aren't reached
		for (int i = 0; i < ChatColor.values().length; i++) {
			// get the name from the integer
			@SuppressWarnings("deprecation")
			String color = ChatColor.getByCode(i).name();
			// color the name of the color
			config.addDefault("colors." + color.toLowerCase(), true);
		}
		config.options().copyDefaults(true);
		saveConfig();
	}
	
	// Loads the localization
	public void loadLocalization() {
		localization.options().header("The underscores are used for the different lines!");
		localization.options().copyDefaults(true);
		saveLocalization();
	}

	// Reloads the config via command /colorme reload
	public void loadConfigsAgain() {
		try {
			config.load(configFile);
			saveConfig();
			colors.load(colorsFile);
			saveColors();
			localization.load(localizationFile);
			saveLocalization();
		}
		catch (Exception e) {
			log.warning("ColorMe failed to load the configs! Trying to update...");
			try {
				updateConfig(colorsFile);
			}
			catch (Exception exp) {
				log.warning("ColorMe failed to update the players.color. Please report this!");
			}
		}
	}

	// Try to save the color YML
	public void saveColors() {
		try {
			colors.save(colorsFile);
		} catch (Exception e) {
			log.warning("ColorMe failed to save the colors! Please report this!");
		}

	}
	
	// Saves the localization
	public void saveLocalization() {
		try {
			localization.save(localizationFile);
		}
		catch (IOException e) {
			log.warning("ColorMe failed to save the localization! Please report this!");
		}
	}

	// If no config is found, copy the default one(s)!
	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Initialized to work with Vault
	private Boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}
}
