package de.dustplanet.colorme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
// Economy
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
	private final ColorMePlayerListener playerListener = new ColorMePlayerListener(this);
	private final ColorMeBlockListener blockListener = new ColorMeBlockListener(this);
	public Economy economy = null;
	public static FileConfiguration config, players, localization, colors;
	public static File configFile, playersFile, localizationFile, colorsFile, bannedWordsFile;
	public static boolean spoutEnabled, Prefixer, Suffixer, globalSuffix, globalPrefix, globalColor, chatBrackets, chatColors, signColors, newColorOnJoin, displayAlwaysGlobalPrefix, displayAlwaysGlobalSuffix, blacklist;
	public static boolean groups, ownSystem, pex, bPermissions, groupManager;
	public static int prefixLength, suffixLength;
	private ColorMeCommands colorExecutor;
	private PrefixCommands prefixExecutor;
	private SuffixCommands suffixExecutor;
	public List<String> values = new ArrayList<String>();
	public static List<String> bannedWords = new ArrayList<String>();

	// Shutdown
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.getServer().getLogger().info(pdfFile.getName() + " " + pdfFile.getVersion()	+ " has been disabled!");
		// Clear stuff
		values.clear();
		bannedWords.clear();
	}

	// Start
	public void onEnable() {
		// Events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		pm.registerEvents(blockListener, this);

		// Player colors config		
		playersFile = new File(getDataFolder(), "players.yml");
		// Copy if the config doesn't exist
		if (!playersFile.exists()) {
			playersFile.getParentFile().mkdirs();
			copy(getResource("players.yml"), playersFile);
		}
		// Try to load
		players = YamlConfiguration.loadConfiguration(playersFile);

		// Custom colors config		
		colorsFile = new File(getDataFolder(), "colors.yml");
		// Copy if the config doesn't exist
		if (!colorsFile.exists()) {
			colorsFile.getParentFile().mkdirs();
			copy(getResource("colors.yml"), colorsFile);
		}
		// Try to load
		colors = YamlConfiguration.loadConfiguration(colorsFile);

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
		localization = YamlConfiguration.loadConfiguration(localizationFile);
		loadLocalization();

		if (config.getBoolean("useWordBlacklist")) {
			// BannedWords file
			bannedWordsFile = new File(getDataFolder(), "bannedWords.txt");
			if(!bannedWordsFile.exists()){
				bannedWordsFile.getParentFile().mkdirs();
				copy(getResource("bannedWords.txt"), bannedWordsFile);
			}
			// Try to load
			try {
				loadBannedWords();
			} catch (IOException e) {
				this.getServer().getLogger().warning("[ColorMe] Failed to load the bannedWords.txt! Please report this! IOException");
			}
		}

		// Force to update the config (remove empty lines)
		if (config.getBoolean("updateConfig")) {
			try {
				updateConfig(playersFile);
			}
			catch (IOException e) {
				this.getServer().getLogger().warning("[ColorMe] Failed to update the config! Please report this! IOExcpetion");
			}
			finally {
				config.set("updateConfig", false);
			}
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
		this.getServer().getLogger().info(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled!");

		// Check for Vault
		Plugin vault = this.getServer().getPluginManager().getPlugin("Vault");
		if (vault != null & vault instanceof Vault) {
			// If Vault is enabled, load the economy
			this.getServer().getLogger().info("[ColorMe]  loaded Vault successfully");
			setupEconomy();
		}
		else {
			// Else tell the admin about the missing of Vault
			this.getServer().getLogger().info("[ColorMe] Vault was NOT found! Running without economy!");
		}

		// Check for Spout
		Plugin spout = this.getServer().getPluginManager().getPlugin("Spout");
		if (spout != null) {
			this.getServer().getLogger().info("[ColorMe] loaded Spout successfully");
			// Spout is enabled
			spoutEnabled = true;
		}
		else {
			this.getServer().getLogger().info("[ColorMe] Running without Spout!");
			// Spout is disabled
			spoutEnabled = false;
		}

		// What is enabled?
		checkParts();

		if (groups && !ownSystem) {
			Plugin pexPlugin = this.getServer().getPluginManager().getPlugin("PermissionsEx");
			Plugin bPermissionsPlugin = this.getServer().getPluginManager().getPlugin("bPermissions");
			Plugin groupManagerPlugin = this.getServer().getPluginManager().getPlugin("GroupManager");
			if (pexPlugin != null) {
				pex = true;
				this.getServer().getLogger().info("[ColorMe] Found PermissionsEx. Will use it for groups!");
			}
			else if (bPermissionsPlugin != null) {
				bPermissions = true;
				this.getLogger().info("[ColorMe] Found bPermissions. Will use it for groups!");
			}
			else if (groupManagerPlugin != null) {
				groupManager = true;
				this.getServer().getLogger().info("[ColorMe] Found GroupManager. Will use it for groups!");
				ColorMePlayerListener.groupManagerWorldsHolder = ((GroupManager) groupManagerPlugin).getWorldsHolder();
			}
			else {
				this.getServer().getLogger().info("[ColorMe] Haven't found any supported group systems. Disabling groups");
				groups = false;
			}
		}
		else if (groups) this.getServer().getLogger().info("[ColorMe] Using own group system!");
		else this.getServer().getLogger().info("[ColorMe] Groups disabled.");

		// Stats
		checkStatsStuff();
		try {
			Metrics metrics = new Metrics(this);
			// Custom plotter for each part
			for (int i = 0; i < values.size(); i++) {
				final String value = values.get(i);
				metrics.addCustomData(new Metrics.Plotter() {
					@Override
					public String getColumnName() {
						return value;
					}

					@Override
					public int getValue() {
						return 1;
					}
				});
			}
			metrics.start();
		}
		catch (IOException e) {}
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Remove empty lines
	public void updateConfig(File config) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(config));
		File tempFile = new File(getDataFolder(), "temp.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				writer.write(line);
				writer.newLine();
			}
		}
		catch (Exception e) {
			this.getServer().getLogger().warning("[ColorMe] An error occurred while updating the config!");
		}
		finally {
			reader.close();
			writer.flush();
			writer.close();
			config.delete();
			tempFile.renameTo(config);
		}
	}

	// Load the banned words
	private static void loadBannedWords() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(bannedWordsFile));
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) continue;
			bannedWords.add(line);
		}
		reader.close();
	}


	// Loads the config at the start
	public void loadConfig() {
		config.options().header("For help please refer to http://bit.ly/colormebukkit or http://bit.ly/bukkitdevcolorme");
		config.addDefault("updateConfig", false);
		config.addDefault("costs.color", 5.00);
		config.addDefault("costs.prefix", 5.00);
		config.addDefault("costs.suffix", 5.00);
		config.addDefault("global_default.prefix", "");
		config.addDefault("global_default.suffix", "");
		config.addDefault("global_default.color", "");
		config.addDefault("Prefixer", true);
		config.addDefault("Suffixer", true);
		config.addDefault("chatBrackets", true);
		config.addDefault("ColorMe.displayName", true);
		config.addDefault("ColorMe.tabList", false);
		config.addDefault("ColorMe.playerTitle", true);
		config.addDefault("ColorMe.signColors", true);
		config.addDefault("ColorMe.chatColors", true);
		for (ChatColor value : ChatColor.values()) {
			if (value.getChar() == 'r') continue;
			// get the name from the ChatColor
			String color = value.name().toLowerCase();
			// write to the config
			config.addDefault("colors." + color, true);
		}
		config.addDefault("colors.random", true);
		config.addDefault("colors.rainbow", true);
		config.addDefault("colors.custom", true);
		config.addDefault("lengthLimit.Prefixer", 16);
		config.addDefault("lengthLimit.Suffixer", 16);
		config.addDefault("newColorOnJoin" , false);
		config.addDefault("displayAlways.globalSuffix", false);
		config.addDefault("displayAlways.globalPefix", false);
		config.addDefault("useWordBlacklist", true);
		config.addDefault("groups.enable", true);
		config.addDefault("groups.ownSystem", true);
		config.options().copyDefaults(true);
		saveConfig();
	}

	// Loads the localization
	public void loadLocalization() {
		localization.options().header("The underscores are used for the different lines!");
		localization.addDefault("permission_denied", "&4You don't have the permission to do this!");
		localization.addDefault("part_disabled", "&4Sorry, but this command and plugin part is disabled!");
		localization.addDefault("only_ingame", "&4Sorry, this command can only be run from ingame!");
		localization.addDefault("color_list", "Color list: (the & values are used for prefix/suffix!)");
		localization.addDefault("reload", "&2ColorMe version &4%version &2reloaded!");
		localization.addDefault("charged", "&2You have been charged &4$%costs");
		localization.addDefault("not_enough_money_1", "&4Sorry, you don't have enough money to do this.");
		localization.addDefault("not_enough_money_2", "&4It costs &e$%costs &4to do this!");
		localization.addDefault("no_color_self", "&eYou &4don't have a colored name in the world &e%world");
		localization.addDefault("no_color_other", "&e%player &4doesn't have a colored name in the world &e%world");
		localization.addDefault("no_color_global", "&4The global color isn't set!");
		localization.addDefault("same_color_self", "&eYou &4already have got this color in the world &e%world");
		localization.addDefault("same_color_other", "&e%player &4already has got this color in the world &e%world");
		localization.addDefault("same_color_global", "&4The global color is already this color!");
		localization.addDefault("invalid_color", "&4'&e%color&4' is not a supported color.");
		localization.addDefault("disabled_color", "&4Sorry, but the color '&e%color&4' is disabled.");
		localization.addDefault("removed_color_self", "&eYour &2name color in the world &e%world &2has been removed.");
		localization.addDefault("removed_color_other", "&2Removed &e%player&2's color in the world &e%world.");
		localization.addDefault("removed_color_global", "&2Removed the global color.");
		localization.addDefault("changed_color_self", "&eYour &2name color has been changed to &e%color &2in the world &e%world");
		localization.addDefault("changed_color_other", "&2Changed &e%player&2's color to &e%color &2in the world &e%world");
		localization.addDefault("changed_color_global", "&2The global color has been changed to &e%color");
		localization.addDefault("get_color_self", "&eYou &2have got the color &e%color &2in the world &e%world");
		localization.addDefault("get_color_other", "&e%player &2has got the color &e%color &2in the world &e%world");
		localization.addDefault("get_color_global", "&2The global color is &e%color");
		localization.addDefault("help_color_1", "&2Welcome to the ColorMe version &4%version &2help!");
		localization.addDefault("help_color_2", "&4 <> = Required, [] = Optional");
		localization.addDefault("help_color_3", "/color help - Shows the help");
		localization.addDefault("help_color_4", "/color list - Shows list of colors");
		localization.addDefault("help_color_5", "/color get <name> [world] - Gets the actual color");
		localization.addDefault("help_color_6", "/color remove <name> [world] - Removes color");
		localization.addDefault("help_color_7", "/color me <color> [world] - Sets your own color");
		localization.addDefault("help_color_8", "/color <name> <color> [world] - Sets player's color");
		localization.addDefault("help_color_9", "/color global <color> - Sets the global color");
		localization.addDefault("no_prefix_self", "&eYou &4don't have a prefix in the world &e%world");
		localization.addDefault("no_prefix_other", "&e%player &4doesn't have a prefix in the world &e%world");
		localization.addDefault("no_prefix_global", "&4The global prefix isn't set!");
		localization.addDefault("same_prefix_self", "&eYou &4already have got this prefix in the world &e%world");
		localization.addDefault("same_prefix_other", "&e%player &4already has got this prefix in the world &e%world");
		localization.addDefault("same_prefix_global", "&4The global prefix is already this prefix!");
		localization.addDefault("removed_prefix_self", "&eYour &2prefix in the world &e%world &2has been removed.");
		localization.addDefault("removed_prefix_other", "&2Removed &e%player&2's prefix in the world &e%world.");
		localization.addDefault("removed_prefix_global", "&2Removed the global prefix.");
		localization.addDefault("changed_prefix_self", "&eYour &2prefix has been changed to &f%prefix &2in the world &e%world");
		localization.addDefault("changed_prefix_other", "&2Changed &e%player&2's prefix to &f%prefix &2in the world &e%world");
		localization.addDefault("changed_prefix_global", "&2The global prefix has been changed to &f%prefix");
		localization.addDefault("get_prefix_self", "&eYou &2have got the prefix %prefix &2in the world &e%world");
		localization.addDefault("get_prefix_other", "&e%player &2has got the prefix %prefix %2in the world &e%world");
		localization.addDefault("get_prefix_global", "&2The global prefix is %prefix");
		localization.addDefault("help_prefix_1", "&2Welcome to the Prefixer (part of ColorMe) version &4%version &2help!");
		localization.addDefault("help_prefix_2", "&4 <> = Required, [] = Optional");
		localization.addDefault("help_prefix_3", "/prefixer help - Shows the help");
		localization.addDefault("help_prefix_4", "/color list - Shows list of colors");
		localization.addDefault("help_prefix_5", "/prefixer get <name> [world] - Gets the actual prefix");
		localization.addDefault("help_prefix_6", "/prefixer remove <name> [world] - Removes prefix");
		localization.addDefault("help_prefix_7", "/prefixer me <prefix> [world] - Sets your own prefix");
		localization.addDefault("help_prefix_8", "/prefixer <name> <prefix> [world] - Sets player's prefix");
		localization.addDefault("help_prefix_9", "/prefixer global <prefix> - Sets the global prefix");
		localization.addDefault("no_suffix_self", "&eYou &4don't have a suffix in the world &e%world");
		localization.addDefault("no_suffix_other", "&e%player &4doesn't have a suffix in the world &e%world");
		localization.addDefault("no_suffix_global", "&4The global suffix isn't set!");
		localization.addDefault("same_suffix_self", "&eYou &4already have got this suffix in the world &e%world");
		localization.addDefault("same_suffix_other", "&e%player &4already has got this suffix in the world &e%world");
		localization.addDefault("same_suffix_global", "&4The global suffix is already this suffix!");
		localization.addDefault("removed_suffix_self", "&eYour &2suffix in the world &e%world &2has been removed.");
		localization.addDefault("removed_suffix_other", "&2Removed &e%player&2's suffix in the world &e%world.");
		localization.addDefault("removed_suffix_global", "&2Removed the global suffix.");
		localization.addDefault("changed_suffix_self", "&eYour &2suffix has been changed to &f%suffix &2in the world &e%world");
		localization.addDefault("changed_suffix_other", "&2Changed &e%player&2's suffix to &f%suffix &2in the world &e%world");
		localization.addDefault("changed_suffix_global", "&2The global suffix has been changed to &f%suffix");
		localization.addDefault("get_suffix_self", "&eYou &2have got the suffix %suffix &2in the world &e%world");
		localization.addDefault("get_suffix_other", "&e%player &2has got the suffix %suffix %2in the world &e%world");
		localization.addDefault("get_suffix_global", "&2The global suffix is &e%suffix");
		localization.addDefault("help_suffix_1", "&2Welcome to the Suffixer (part of ColorMe) version &4%version &2help!");
		localization.addDefault("help_suffix_2", "&4 <> = Required, [] = Optional");
		localization.addDefault("help_suffix_3", "/suffixer help - Shows the help");
		localization.addDefault("help_suffix_4", "/color list - Shows list of colors");
		localization.addDefault("help_suffix_5", "/suffixer get <name> [world] - Gets the actual suffix");
		localization.addDefault("help_suffix_6", "/suffixer remove <name> [world] - Removes suffix");
		localization.addDefault("help_suffix_7", "/suffixer me <suffix> [world] - Sets your own suffix");
		localization.addDefault("help_suffix_8", "/suffixer <name> <suffix> [world] - Sets player's suffix");
		localization.addDefault("help_suffix_9", "/suffixer global <suffix> - Sets the global suffix");
		localization.addDefault("custom_colors_enabled", "&4Custom colors are enabled, too! Please ask your admin for them!");
		localization.addDefault("too_long", "&4Sorry, this message is too long!");
		localization.addDefault("bad_words", "&4Sorry,but '%s' is on the blacklist!");
		localization.options().copyDefaults(true);
		try {
			localization.save(localizationFile);
		} catch (IOException e) {
			this.getServer().getLogger().warning("[ColorMe] Failed to save the localization! Please report this! IOException");
		}
	}

	// Reloads the config via command /colorme reload, /prefixer reload or /suffixer reload
	public static void loadConfigsAgain() {
		try {
			config.save(configFile);
			config.load(configFile);
			players.load(playersFile);
			players.save(playersFile);
			localization.load(localizationFile);
			localization.save(localizationFile);
			colors.load(colorsFile);
			colors.save(colorsFile);
			loadBannedWords();
			checkParts();
		}
		catch (FileNotFoundException e) {
			Bukkit.getServer().getLogger().warning("[ColorMe] Failed to load the configs! Please report this! FileNotFoundException");
		} catch (IOException e) {
			Bukkit.getServer().getLogger().warning("[ColorMe] Failed to load the configs! Please report this! IOException");
		} catch (InvalidConfigurationException e) {
			Bukkit.getServer().getLogger().warning("[ColorMe] Failed to load the configs! Please report this! InvalidConfigurationException");
		}
	}

	// Maybe something changed on the fly
	private static void checkParts() {
		Suffixer = config.getBoolean("Suffixer");
		Prefixer = config.getBoolean("Prefixer");
		chatBrackets = config.getBoolean("chatBrackets");
		signColors = config.getBoolean("ColorMe.signColors");
		chatColors = config.getBoolean("ColorMe.chatColors");
		globalPrefix = config.getString("global_default." + "prefix").trim().length() > 1 ? true : false;
		globalSuffix = config.getString("global_default." + "suffix").trim().length() > 1 ? true : false;
		globalColor = config.getString("global_default." + "color").trim().length() > 1 ? true : false;
		prefixLength = config.getInt("lengthLimit.Prefixer");
		suffixLength = config.getInt("lengthLimit.Suffixer");
		newColorOnJoin = config.getBoolean("newColorOnJoin");
		displayAlwaysGlobalPrefix = config.getBoolean("displayAlways.globalPefix");
		displayAlwaysGlobalSuffix = config.getBoolean("displayAlways.globalSuffix");
		blacklist = config.getBoolean("useWordBlacklist");
		groups = config.getBoolean("groups.enable");
		ownSystem = config.getBoolean("groups.ownSystem");
	}

	// Used for Metrics
	private void checkStatsStuff() {
		if (Prefixer) values.add("Prefixer");
		if (Suffixer) values.add("Suffixer");
		if (config.getBoolean("ColorMe.displayName")) values.add("ColorMe - displayName");
		if (config.getBoolean("ColorMe.tabList")) values.add("ColorMe - tabList");
		if (config.getBoolean("ColorMe.playerTitle")) values.add("ColorMe - playerTitle");
		if (chatColors) values.add("ColorMe - chatColors");
		if (signColors) values.add("ColorMe - signColors");
	}

	// Initialized to work with Vault
	private Boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}

	// Message sender
	public static void message(CommandSender sender, Player player, String message, String value, String world, String target, Double cost) {
		if (message != null) {
			message = message
					.replaceAll("&([0-9a-fk-or])", "\u00A7$1")
					.replaceAll("%world", world)
					.replaceAll("%color", value)
					.replaceAll("%prefix", value)
					.replaceAll("%suffix", value)
					.replaceAll("%s", value)
					.replaceAll("%player", target)
					.replaceAll("%version", "3.5");
			if (cost != null) {
				message = message.replaceAll("%costs", Double.toString(cost));
			}
			if (player != null) {
				player.sendMessage(message);
			}
			else if (sender != null) {
				sender.sendMessage(message);
			}
		}
		// If message is null
		else {
			if (player != null) {
				player.sendMessage(ChatColor.DARK_RED + "Somehow this message is not defined. Please check your localization.yml");
			}
			else if (sender != null) {
				sender.sendMessage(ChatColor.DARK_RED + "Somehow this message is not defined. Please check your localization.yml");
			}
		}
	}
}