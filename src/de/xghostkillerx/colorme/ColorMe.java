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
	public final static Logger log = Logger.getLogger("Minecraft");
	private final ColorMePlayerListener playerListener = new ColorMePlayerListener(this);
	public Economy economy = null;
	public static FileConfiguration config;
	public static FileConfiguration players;
	public FileConfiguration localization;
	public File configFile;
	public static File playersFile;
	public File localizationFile;
	public static boolean spoutEnabled;
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
		playersFile = new File(getDataFolder(), "players.yml");
		// Copy if the config doesn't exist
		if (!playersFile.exists()) {
			playersFile.getParentFile().mkdirs();
			copy(getResource("players.yml"), playersFile);
		}
		// Try to load
		try {
			players = YamlConfiguration.loadConfiguration(playersFile);
		}
		// if it failed, tell about the update progress
		catch (Exception e) {
			log.warning("ColorMe failed to load the players.color! Trying to update...");
			try {
				// Update colors
				updateConfig(playersFile);
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
				updateConfig(playersFile);
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
			tempFile.renameTo(playersFile);
		}
	}

	// Loads the config at the start
	public void loadConfig() {
		config.options().header("For help please refer to http://bit.ly/colormebukkit or http://bit.ly/bukkitdevcolorme");
		config.addDefault("costs.color", 5.00);
		config.addDefault("costs.prefix", 5.00);
		config.addDefault("costs.suffix", 5.00);
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
		config.addDefault("colors.random", true);
		config.addDefault("colors.rainbow", true);
		config.options().copyDefaults(true);
		saveConfig();
	}
	
	// Loads the localization
	public void loadLocalization() {
		localization.options().header("The underscores are used for the different lines!");
		localization.addDefault("permission_denied", "&4You don''t have the permission to do this!");
		localization.addDefault("part_disabled", "&4Sorry, but this command and plugin part is disabled!");
		localization.addDefault("only_ingame", "&4Sorry, this command can only be run from ingame!");
		localization.addDefault("color_list", "Color list:");
		localization.addDefault("reload", "&2ColorMe &4%version &2reloaded!");
		localization.addDefault("no_color_self", "&eYou &4don''t have a colored name in the world %world!");
		localization.addDefault("no_color_other", "&e%player &4doesn''t have a colored name in the world %world!");
		localization.addDefault("same_color_self", "&eYou &4already have got this color in the world %world!");
		localization.addDefault("same_color_other", "&e%player &4already have got this color in the world %world!");
		localization.addDefault("invalid_color", "&4'' &e%color &4'' is not a supported color.");
		localization.addDefault("disabled_color", "&4'' &e%color &4'' is disabled.");
		localization.addDefault("removed_color_self", "&eYour &2name color in the world %world has been removed.");
		localization.addDefault("removed_color_other", "&2Removed &e%player&2's color in the world %world.");
		// localization.addDefault("", "");
		/*
		sender.sendMessage(ChatColor.GREEN	+ "Welcome to the ColorMe version " + ChatColor.RED + pdfFile.getVersion() + ChatColor.GREEN + " help!");
		sender.sendMessage(ChatColor.RED + "<> = Required, [] = Optional");
		sender.sendMessage("</command> help - Shows the help");
		sender.sendMessage("/<command> list - Shows list of colors");
		sender.sendMessage("/<command> get [name] - Gets the actual color");
		sender.sendMessage("/<command> remove [name] - Removes color");
		sender.sendMessage("/<command> me <color> - Sets your own color");
		sender.sendMessage("/<command> <name> <color> - Sets player's color");
		 */
		localization.options().copyDefaults(true);
		saveLocalization();
	}

	// Reloads the config via command /colorme reload
	public void loadConfigsAgain() {
		try {
			config.load(configFile);
			saveConfig();
			players.load(playersFile);
			savePlayers();
			localization.load(localizationFile);
			saveLocalization();
		}
		catch (Exception e) {
			log.warning("ColorMe failed to load the configs! Trying to update...");
			try {
				updateConfig(playersFile);
			}
			catch (Exception exp) {
				log.warning("ColorMe failed to update the players.color. Please report this!");
			}
		}
	}

	// Try to save the color YML
	public static void savePlayers() {
		try {
			players.save(playersFile);
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