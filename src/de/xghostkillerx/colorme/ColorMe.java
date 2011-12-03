package de.xghostkillerx.colorme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.TextWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Type;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

//Economy (Vault)
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;


/**
 * ColorMe for CraftBukkit/Bukkit
 * Handles some general stuff!
 * 
 * Refer to the forum thread:
 * 
 * Refer to the dev.bukkit.org page:
 * 
 *
 * @author xGhOsTkiLLeRx
 * @thanks to Valrix for the original ColorMe plugin!!
 * 
 */

public class ColorMe extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");

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
		
		

		colorsFile = new File(getDataFolder(), "players_color.yml");
		if (!colorsFile.exists()) {
			colorsFile.getParentFile().mkdirs();
			copy(getResource("players_color.yml"), colorsFile);
		}
		colors = YamlConfiguration.loadConfiguration(colorsFile);

		// Config
		configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), configFile);
		}
		config = this.getConfig();
		loadConfig();

		// Events
		//TODO Check if there are more event like chatting?!
		getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, new ColorPlayerListener(this), Priority.Normal, this);

		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled!");
		Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
		if (x != null & x instanceof Vault) {
			log.info(String.format(pdfFile.getName() + " loaded Vault successfully"));
			setupEconomy();
		} else {
			log.warning(String.format("Vault was NOT found! Running without economy!"));
		}
	}

	public void loadConfig() {
		config.options().header("For help please refer to http://bit.ly/cookmebukkitdev or http://bit.ly/cookmebukkit");
		config.addDefault("costs", 0);
		config.options().copyDefaults(true);
		saveConfig();
	}

	// Reloads the config via command /cookme reload
	public void loadConfigAgain() {
		try {
			config.load(configFile);
			saveConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// If no config is found, copy the default one!
	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while((len=in.read(buf))>0){
				out.write(buf,0,len);
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
	
	public boolean self(Player p, String n) {
		return (p.equals(getServer().getPlayer(n))) ? true : false;
	}

	public void list(Player p) {
		p.sendMessage("Color List:");
		String color;
		String msg = "";
		for (int i = 0; i < ChatColor.values().length; i++) {
			color = ChatColor.getByCode(i).name();
			if (msg.length() == 0) {
				msg = ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+' ';
				continue;
			}
			msg += (i == ChatColor.values().length-1) ? ChatColor.valueOf(color)+color.toLowerCase().replace("_", "") : ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+' ';
			TextWrapper.wrapText(msg);
		}
		p.sendMessage(msg);
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		ColorMeCommands cmd = new ColorMeCommands(this);
			return cmd.ColorMeCommand(sender, command, commandLabel, args);
	}

	// Return the player's name color
	public String getColor(String name) {
		return (colors.contains(name.toLowerCase())) ? colors.getString(name.toLowerCase()) : "";
	}

	// Set player's color and update displayname if online
	public boolean setColor(String name, String color) {
		String newColor = findColor(color); 
		if (newColor.equals(color)) return false;
		colors.set(name.toLowerCase(), newColor);
		saveColors();
		if (getServer().getPlayer(name) != null) {
			Player p = getServer().getPlayer(name);
			p.setDisplayName(ChatColor.valueOf(newColor)+ChatColor.stripColor(p.getDisplayName())+ChatColor.WHITE);
		}
		return true;
	}

	private void saveColors() {
		try {
			colors.save(colorsFile);
		} catch (IOException e) {
			// TODO Ausfüllen
			e.printStackTrace();
		}
		
	}

	// Iterate through colors to try and find a match (resource expensive)
	// TODO verstehen!
	public String findColor(String color) {
		String col;
		for (int i = 0; i <= 15; i++) {
			col = ChatColor.getByCode(i).name();
			if (color.equalsIgnoreCase(col.toLowerCase().replace("_", ""))) return col;
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
}
