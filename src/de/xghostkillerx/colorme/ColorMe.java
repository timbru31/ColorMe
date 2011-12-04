package de.xghostkillerx.colorme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
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
	private final ColorMePlayerListener playerListener = new ColorMePlayerListener(this);
	public Economy economy = null;
	public FileConfiguration config;
	public static FileConfiguration colors;
	public File configFile;
	public static File colorsFile;

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
		
		// Player colors config
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

		// Message
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled!");
		
		// Check for Vault
		Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
		if (x != null & x instanceof Vault) {
			// If Vault is enabled, load the economy
			log.info(String.format(pdfFile.getName() + " loaded Vault successfully"));
			setupEconomy();
		} else {
			// Else tell the admin about the missing of Vault
			log.warning(String.format("Vault was NOT found! Running without economy!"));
		}
	}
	
	// Loads the config at the start
	public void loadConfig() {
		config.options().header("For help please refer to  or ");
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Try to save the color YML
	public static void saveColors() {
		try {
			colors.save(colorsFile);
		} catch (Exception e) {
			log.warning("ColorMe failed to save the colors!");
		}
		
	}

	// If no config is found, copy the default one(s)!
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

	// Refer to ColorMeCommands
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		ColorMeCommands cmd = new ColorMeCommands(this);
			return cmd.ColorMeCommand(sender, command, commandLabel, args);
	}
}
