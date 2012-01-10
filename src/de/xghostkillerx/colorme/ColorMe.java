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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
// Stats
import com.randomappdev.pluginstats.Ping;
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
	public File configFile;
	public File colorsFile;
	public boolean spoutEnabled;

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
				log.warning("ColorMe failed to update the players.color. Please report this! (Stage 1)");
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
				log.warning("ColorMe failed to update the players.color. Please report this! (Stage 2)");
			}
			finally {
				// Sets the forceUpdate value false again
				config.set("forceUpdate", false);
				saveConfig();
			}
		}

		// Stats
		Ping png = new Ping();
		png.init(this);
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
		config.options().header("For help please refer to http://bit.ly/colormebukkit or http://bit.ly/colormebukkitdev ");
		config.addDefault("costs", 5.00);
		config.addDefault("forceUpdate", false);
		config.addDefault("tabList", true);
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
	private Boolean setupEconomy() {
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
		// Player in the config? Yes -> get the config, no -> nothing
		return (colors.contains(name.toLowerCase())) ? colors.getString(name.toLowerCase()) : "";
	}

	// Set player's color
	public boolean setColor(String name, String color) {
		String actualColor = getColor(name);
		// If the colors are the same return false
		if (actualColor.equalsIgnoreCase(color)) {
			return false;
		}
		// Write to the config and save and update the names
		colors.set(name, color.toLowerCase());
		saveColors();
		updateName(name);
		return true;
	}

	// Update the displayName, tabName & title (after setting, removing, onJoin and onChat)
	@SuppressWarnings("deprecation")
	public void updateName(String name) {
		Player player = getServer().getPlayerExact(name);
		if (player != null) {
			String color = getColor(name);
			String displayName = player.getDisplayName();
			String cleanDisplayName = ChatColor.stripColor(displayName);
			String newName = "";
			boolean tabList = config.getBoolean("tabList");
			// If the player has a color change the displayname
			if (hasColor(name)) {
				if (validColor(colors.getString(name)) == true) {
					// Random
					if (color.equalsIgnoreCase("random")) {
						player.setDisplayName(randomColor(cleanDisplayName) + ChatColor.WHITE);
						if (tabList == true) {
							// If the TAB name is longer than 16 shorten it!
							newName = randomColor(cleanDisplayName);
							if (newName.length() > 16) {
								newName = newName.substring(0, 12) + ChatColor.WHITE + "..";
							}
							player.setPlayerListName(newName);
						}
					}
					// Rainbow
					if (color.equalsIgnoreCase("rainbow")) {
						player.setDisplayName(rainbowColor(cleanDisplayName) + ChatColor.WHITE);
						if (tabList == true) {
							// If the TAB name is longer than 16 shorten it!
							newName = rainbowColor(cleanDisplayName);
							if (newName.length() > 16) {
								newName = newName.substring(0, 12) + ChatColor.WHITE + "..";
							}
							player.setPlayerListName(newName);
						}
					}
					// Normal
					else if (!color.equalsIgnoreCase("random") && !color.equalsIgnoreCase("rainbow")) {
						player.setDisplayName(ChatColor.valueOf(color.toUpperCase()) + ChatColor.stripColor(displayName) + ChatColor.WHITE);
						if (tabList == true) {
							// If the TAB name is longer than 16 shorten it!
							newName = ChatColor.valueOf(color.toUpperCase()) + ChatColor.stripColor(displayName);
							if (newName.length() > 16) {
								newName = newName.substring(0, 12) + ChatColor.WHITE + "..";
							}
							player.setPlayerListName(newName);
						}
					}
					// Check for Spout
					if (spoutEnabled == true) {
						// Random color
						if (getColor(name).equalsIgnoreCase("random")) {
							SpoutManager.getAppearanceManager().setGlobalTitle(player, randomColor(displayName));
						}
						// Rainbow
						if (getColor(name).equalsIgnoreCase("rainbow")) {
							SpoutManager.getAppearanceManager().setGlobalTitle(player, rainbowColor(displayName));
						}
						// Normal color
						else if (!getColor(name).equalsIgnoreCase("random") && !getColor(name).equalsIgnoreCase("rainbow")) {
							SpoutManager.getAppearanceManager().setGlobalTitle(player, ChatColor.valueOf(color.toUpperCase()) + ChatColor.stripColor(displayName));
						}
					}
				}
				else {
					// Tell player to report it, but suppress the error -> uses color before.
					player.sendMessage("Your name colors seems to be invalid. Ask your admin to check it,");
					player.sendMessage("or try re-coloring!");
				}
			}
			if (!hasColor(name)) {
				// No name -> back to white
				player.setDisplayName(ChatColor.WHITE + ChatColor.stripColor(displayName));
				if (tabList == true) {
					// If the TAB name is longer than 16 shorten it!
					newName = cleanDisplayName;
					if (newName.length() > 16) {
						newName = cleanDisplayName.substring(0, 12) + ChatColor.WHITE + "..";
					}
					player.setPlayerListName(newName);
				}
				if (spoutEnabled == true) {
					SpoutManager.getAppearanceManager().setGlobalTitle(player, ChatColor.WHITE + ChatColor.stripColor(displayName));
				}
			}
		}
	}

	// Check if a player has a color or not
	public boolean hasColor(String name) {
		if (colors.contains(name.toLowerCase())) {
			// if longer than 1 it's a color, return true - otherwise (means '') return false
			return (colors.getString(name.toLowerCase())).trim().length() >1 ? true : false;
		}
		return false;
	}

	// Removes a color if exists, otherwise returns false | Spout causes deprecation
	public boolean removeColor(String name) {
		name = name.toLowerCase();
		// If the player has got a color
		if (hasColor(name)) {
			colors.set(name, "");
			saveColors();
			updateName(name);
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
		String msg = "";
		// As long as all colors aren't reached
		for (int i = 0; i < ChatColor.values().length; i++) {
			// get the name from the byte
			String color = ChatColor.getByCode(i).name();
			// color the name of the color
			msg += ChatColor.valueOf(color) + color.toLowerCase() + " ";
		}
		// Include custom colors
		sender.sendMessage(msg + randomColor("random") + " " + rainbowColor("rainbow"));
	}

	// Used to create a random effect
	public String randomColor(String name) {
		String newName = "";
		char ch;
		// As long as the length of the name isn't reached
		for (int i = 0; i < name.length(); i++) {
			// Roll the dice between 0 and 15 ;)
			int x = (int)(Math.random()*16);
			ch = name.charAt(i);
			// Color the character
			newName += ChatColor.getByCode(x) + Character.toString(ch);
		}
		return newName;
	}

	// Used to create a rainbow effect
	public String rainbowColor(String name) {
		String newName = "";
		char ch;
		int z = 0;
		// Had to store the rainbow manually. Why did Mojang store it so..., forget it
		String rainbow[] = {"DARK_RED", "RED", "GOLD", "YELLOW", "GREEN", "DARK_GREEN", "AQUA", "DARK_AQUA", "BLUE", "DARK_BLUE", "LIGHT_PURPLE", "DARK_PURPLE"};
		// As long as the length of the name isn't reached
		for (int i = 0; i < name.length(); i++) {
			// Reset if z reaches 12
			if (z == 12) z = 0;
			ch = name.charAt(i);
			// Add to the new name the colored character
			newName += ChatColor.valueOf(rainbow[z]) + Character.toString(ch);
			z++;
		}
		return newName;
	}

	// Check if the color is possible
	public boolean validColor(String color) {
		// if it's random or rainbow -> possible
		if (color.equalsIgnoreCase("rainbow") || color.equalsIgnoreCase("random")) {
			return true;
		}
		// Second place, cause random and rainbow aren't possible normally ;)
		else {
			for (int i=0; i < 16; i++) {
				// Check if the color is one of the 16
				if (color.equalsIgnoreCase(ChatColor.getByCode(i).name())) {
					return true;
				}
			}
			return false;
		}
	}
}
