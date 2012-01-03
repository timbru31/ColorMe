package de.xghostkillerx.colorme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileReader;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.TextWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
// Stats
import com.randomappdev.pluginstats.Ping;
// Economy (Vault)
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
// Spout
import org.getspout.spoutapi.SpoutManager;

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
 * @param <SimpleClans>
 * @thanks to Valrix for the original ColorMe plugin!!
 * 
 */

public class ColorMe<SimpleClans> extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");
	private final ColorMePlayerListener playerListener = new ColorMePlayerListener(this);
	public Economy economy = null;
	public FileConfiguration config;
	public FileConfiguration colors;
	public File configFile;
	public File colorsFile;

	// Shutdown
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " " + pdfFile.getVersion()	+ " has been disabled!");
	}

	// Start

	public void onEnable() {
		// Events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.Normal, this);

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
		}
		else {
			log.warning("Running without Spout!");
		}

		// Stats
		Ping.init(this);
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
				writer.write(line.replace("\t", "    ").replace("=", ": "));
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
		config.options().header("For help please refer to http://bit.ly/colormebukkit or http://bit.ly/colormebukkitdev ");
		config.addDefault("costs", 0);
		config.options().copyDefaults(true);
		saveConfig();
	}

	// Reloads the config via command /colorme reload
	public void loadConfigAgain() {
		try {
			config.load(configFile);
			saveConfig();
			colors.load(colorsFile);
			saveColors();
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
	private Boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}

	// Refer to ColorMeCommands
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		ColorMeCommands cmd = new ColorMeCommands(this);
		return cmd.ColorMeCommand(sender, command, commandLabel, args);
	}

	/*
	 * Different Actions
	 */

	// Return the player's name color
	public String getColor(String name) {
		return (colors.contains(name.toLowerCase())) ? colors.getString(name.toLowerCase()) : "";
	}

	// Set player's color and update displayname if online
	@SuppressWarnings("deprecation")
	public boolean setColor(String name, String color) {
		String oldColor = getColor(name);
		String newColor = findColor(color);
		// If the colors are the same return false
		if (oldColor.equalsIgnoreCase(newColor)) {
			return false;
		}
		// If the color is not suitable return false
		if (newColor.equals(color)) {
			return false;
		}
		colors.set(name, newColor);
		saveColors();
		if (getServer().getPlayerExact(name) != null) {
			Player player =getServer().getPlayerExact(name);
			player.setDisplayName(ChatColor.valueOf(newColor) + ChatColor.stripColor(player.getDisplayName()) + ChatColor.WHITE);
			Plugin spout = getServer().getPluginManager().getPlugin("Spout");
			if (spout != null) {
				SpoutManager.getAppearanceManager().setGlobalTitle(player, ChatColor.valueOf(newColor) + ChatColor.stripColor(player.getDisplayName()));
			}
		}
		return true;
	}


	// Iterate through colors to try and find a match (resource expensive)
	public String findColor(String color) {
		byte i = 0;
		while (i < 16) {
			if (color.equalsIgnoreCase(ChatColor.getByCode(i).name().toLowerCase().replace("_", ""))) {
				return ChatColor.getByCode(i).name();
			}
			i++;
		}
		return color;
	}

	// Check if a player has a color or not
	public boolean hasColor(String name) {
		if (colors.contains(name.toLowerCase())) {
			return (colors.getString(name.toLowerCase()).trim().length()>1) ? true : false;
		}
		return false;
	}

	// Removes a color if exists, otherwise returns false
	public boolean removeColor(String name) {
		name = name.toLowerCase();
		if (hasColor(name)) {
			colors.set(name, "");
			saveColors();
			return true;
		}
		return false;
	}

	// Checks if the player is itself
	public boolean self(CommandSender sender, String name) {
		return (sender.equals(getServer().getPlayerExact(name))) ? true : false;
	}

	// The list of colors
	public void list (CommandSender sender) {
		sender.sendMessage("Color List:");     
		String color;
		String clc;
		String msg = "";
		byte i = 0;
		while (i < 16) {
			color = ChatColor.getByCode(i).name();
			clc = color.toLowerCase();
			if (msg.length() < 1) {
				msg = ChatColor.valueOf(color) + clc.replace("_", "") + ' ';
				i++;
				continue;
			}
			msg += (i < 10) ? ChatColor.valueOf(color) + clc.replace("_", "") : ChatColor.valueOf(color) + clc.replace("_", "") + ' ';
			TextWrapper.wrapText(msg);
			i++;
		}
		sender.sendMessage(msg);
	}
}
