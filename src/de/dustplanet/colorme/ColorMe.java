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
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.tag.TagAPI;
import de.dustplanet.colorme.commands.ColorMeCommands;
import de.dustplanet.colorme.commands.GroupCommands;
import de.dustplanet.colorme.commands.PrefixCommands;
import de.dustplanet.colorme.commands.SuffixCommands;
import de.dustplanet.colorme.listeners.ColorMeBlockListener;
import de.dustplanet.colorme.listeners.ColorMePlayerListener;
import de.dustplanet.colorme.listeners.ColorMeTagAPIListener;
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

/*
 * First char of rainbow/random above the head
 * 
 */

public class ColorMe extends JavaPlugin {
	private Actions actions;
	private ColorMePlayerListener playerListener;
	private ColorMeBlockListener blockListener;
	private ColorMeTagAPIListener tagAPIListener;
	public Economy economy = null;
	public FileConfiguration config, players, localization, colors, group;
	public File configFile, playersFile, localizationFile, colorsFile, bannedWordsFile, debugFile, groupsFile;
	public boolean tabList, playerTitle, playerTitleWithoutSpout, displayName, debug, spoutEnabled, Prefixer, Suffixer, globalSuffix, globalPrefix, globalColor, chatBrackets;
	public boolean chatColors, signColors, newColorOnJoin, displayAlwaysGlobalPrefix, displayAlwaysGlobalSuffix, blacklist, tagAPI;
	public boolean groups, ownSystem, pex, bPermissions, groupManager, softMode, otherChatPluginFound, autoChatColor, factions, tryToInsert = true, removeNameAboveHead;
	public int prefixLength, suffixLength;
	private ColorMeCommands colorExecutor;
	private PrefixCommands prefixExecutor;
	private SuffixCommands suffixExecutor;
	private GroupCommands groupExecutor;
	public List<String> values = new ArrayList<String>();
	public List<String> bannedWords = new ArrayList<String>();
	public Thread mainThread;

	// Shutdown
	public void onDisable() {
		// Clear stuff
		values.clear();
		bannedWords.clear();
		if (debug) {
			logDebug("");
			logDebug("\t-----END LOG-----");
			logDebug("");
			logDebug("");
		}
	}

	// Start
	public void onEnable() {
		actions = new Actions(this);
		playerListener = new ColorMePlayerListener(this, actions);
		blockListener = new ColorMeBlockListener(this, actions);
		tagAPIListener = new ColorMeTagAPIListener(this, actions);
		// Events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		pm.registerEvents(blockListener, this);

		// Config
		configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), configFile);
		}
		config = getConfig();
		loadConfig();
		debug = config.getBoolean("debug");
		checkDebug();
		if (debug) {
			getLogger().info("Debug is enabled. Will log actions.");
			logDebug("\t-----BEGIN LOG------");
		}

		// Player colors config		
		playersFile = new File(getDataFolder(), "players.yml");
		// Copy if the players.yml doesn't exist
		if (!playersFile.exists()) {
			logDebug("players.yml didn't exist, creating one");
			playersFile.getParentFile().mkdirs();
			copy(getResource("players.yml"), playersFile);
		}
		// Try to load
		players = YamlConfiguration.loadConfiguration(playersFile);

		// Custom colors config
		colorsFile = new File(getDataFolder(), "colors.yml");
		// Copy if the custom colors doesn't exist
		if (!colorsFile.exists()) {
			logDebug("colors.yml didn't exist, creating one");
			colorsFile.getParentFile().mkdirs();
			copy(getResource("colors.yml"), colorsFile);
		}
		// Try to load
		colors = YamlConfiguration.loadConfiguration(colorsFile);

		// Localization
		localizationFile = new File(getDataFolder(), "localization.yml");
		if(!localizationFile.exists()) {
			logDebug("localization.yml didn't exist, creating one");
			localizationFile.getParentFile().mkdirs();
			copy(getResource("localization.yml"), localizationFile);
		}
		// Try to load
		localization = YamlConfiguration.loadConfiguration(localizationFile);
		loadLocalization();

		// Group file
		groupsFile = new File(getDataFolder(), "groups.yml");
		// Copy if the groups.yml doesn't exist
		if (!groupsFile.exists()) {
			logDebug("groups.yml didn't exist, creating one");
			groupsFile.getParentFile().mkdirs();
			copy(getResource("groups.yml"), groupsFile);
		}
		// Try to load
		group = YamlConfiguration.loadConfiguration(groupsFile);
		
		if (config.getBoolean("useWordBlacklist")) {
			// BannedWords file
			bannedWordsFile = new File(getDataFolder(), "bannedWords.txt");
			if(!bannedWordsFile.exists()) {
				logDebug("bannedWords.txt didn't exist, creating one");
				bannedWordsFile.getParentFile().mkdirs();
				copy(getResource("bannedWords.txt"), bannedWordsFile);
			}
			// Try to load
			try {
				loadBannedWords();
			} catch (IOException e) {
				getLogger().warning("Failed to load the bannedWords.txt! Please report this! IOException");
				logDebug("Failed to load the banned words");
			}
		}

		// Force to update the config (remove empty lines)
		if (config.getBoolean("updateConfig")) {
			try {
				removeEmptyLines(playersFile);
			}
			catch (IOException e) {
				getLogger().warning("Failed to update the config! Please report this! IOExcpetion");
			}
			finally {
				config.set("updateConfig", false);
				saveConfig();
			}
		}

		// Refer to ColorMeCommands
		colorExecutor = new ColorMeCommands(this, actions);
		getCommand("color").setExecutor(colorExecutor);

		// Refer to PrefixCommands
		prefixExecutor = new PrefixCommands(this, actions);
		getCommand("prefix").setExecutor(prefixExecutor);

		// Refer to SuffixCommands
		suffixExecutor = new SuffixCommands(this, actions);
		getCommand("suffix").setExecutor(suffixExecutor);
		
		// Refer to GroupCommands
		groupExecutor = new GroupCommands(this, actions);
		getCommand("groups").setExecutor(groupExecutor);

		logDebug("ColorMe enabled");

		// Check for Vault
		Plugin vault = getServer().getPluginManager().getPlugin("Vault");
		if (vault != null & vault instanceof Vault) {
			// If Vault is enabled, load the economy
			getLogger().info("Loaded Vault successfully");
			logDebug("Vault hooked and loaded");
			setupEconomy();
		}
		else {
			// Else tell the admin about the missing of Vault
			getLogger().info("Vault was not found! Running without economy!");
			logDebug("Vault not found");
		}

		// Check for Spout
		Plugin spout = getServer().getPluginManager().getPlugin("Spout");
		if (spout != null) {
			getLogger().info("Loaded Spout successfully");
			logDebug("Spout hooked and loaded");
			// Spout is enabled
			spoutEnabled = true;
		}
		else {
			getLogger().info("Running without Spout!");
			logDebug("Spout not found");
			// Spout is disabled
			spoutEnabled = false;
		}

		// What is enabled?
		checkParts();

		if (groups && !ownSystem) {
			Plugin pexPlugin = getServer().getPluginManager().getPlugin("PermissionsEx");
			Plugin bPermissionsPlugin = getServer().getPluginManager().getPlugin("bPermissions");
			Plugin groupManagerPlugin = getServer().getPluginManager().getPlugin("GroupManager");
			if (pexPlugin != null) {
				pex = true;
				getLogger().info("Found PermissionsEx. Will use it for groups!");
				logDebug("Hooked into PermissionsEx for groups");
			}
			else if (bPermissionsPlugin != null) {
				bPermissions = true;
				getLogger().info("Found bPermissions. Will use it for groups!");
				logDebug("Hooked into bPermissions for groups");
			}
			else if (groupManagerPlugin != null) {
				groupManager = true;
				getLogger().info("Found GroupManager. Will use it for groups!");
				logDebug("Hooked into GroupManager for groups");
			}
			else {
				getLogger().info("Haven't found any supported group systems. Disabling groups");
				logDebug("No group system found, disabling groups");
				groups = false;
			}
		}
		else if (groups)  {
			logDebug("Using own group system");
			getLogger().info("Using own group system!");
		}
		else {
			logDebug("Groups disabled");
			getLogger().info("Groups disabled.");
		}
		
		// SoftMode
		if (softMode) {
			logDebug("SoftMode enabled");
			getLogger().info("SoftMode enabled. If other chat plugins are found, the chat won't be affected by ColorMe.");
			Plugin chatManager = getServer().getPluginManager().getPlugin("ChatManager");
			Plugin bChatManager = getServer().getPluginManager().getPlugin("bChatManager");
			Plugin EssentialsChat = getServer().getPluginManager().getPlugin("EssentialsChat");
			Plugin mChatSuite = getServer().getPluginManager().getPlugin("mChatSuite");
			Plugin iChat = getServer().getPluginManager().getPlugin("iChat");
			Plugin factionsPlugin = getServer().getPluginManager().getPlugin("Factions");
			if (chatManager != null) {
				otherChatPluginFound = true;
				getLogger().info("Found ChatManager. Will use it for chat!");
				logDebug("Found ChatManager");
			}
			else if (bChatManager != null) {
				otherChatPluginFound = true;
				getLogger().info("Found bChatManager. Will use it for chat!");
				logDebug("Found bChatManager");
			}
			else if (EssentialsChat != null) {
				otherChatPluginFound = true;
				getLogger().info("Found EssentialsChat. Will use it for chat!");
				logDebug("Found EssentialsChat");
			}
			else if (mChatSuite != null) {
				otherChatPluginFound = true;
				getLogger().info("Found mChatSuite. Will use it for chat!");
				logDebug("Found mChatSuite");
			}
			else if (iChat != null) {
				otherChatPluginFound = true;
				getLogger().info("Found iChat. Will use it for chat!");
				logDebug("Found iChat");
			}
			else if (factionsPlugin != null) {
				factions = true;
				getLogger().info("Found Factions, will attempt to support it!");
				logDebug("Found Factions");
			}
			else {
				Plugin customChatPlugin = getServer().getPluginManager().getPlugin(config.getString("softMode.ownChatPlugin"));
				if (customChatPlugin != null) {
					otherChatPluginFound = true;
					getLogger().info("Found " + customChatPlugin.getName() + ". Will use it for chat!");
					logDebug("Found " + customChatPlugin.getName());
				}
			}
		}
		else {
			logDebug("SoftMode disabled");
			getLogger().info("SoftMode disabled. Trying to override other chat plugins.");
		}
		
		if (playerTitleWithoutSpout) {
			Plugin tagAPIPlugin = getServer().getPluginManager().getPlugin("TagAPI");
			if (tagAPIPlugin != null) {
				tagAPI = true;
				getLogger().info("Found TagAPI, will use it for names above the head!");
				logDebug("Found TagAPI");
				pm.registerEvents(tagAPIListener, this);
			}
			else {
				getLogger().info("Didn't found TagAPI!");
				logDebug("TagAPI not found");
			}
		}
		
		// Prevent ASync calls
		mainThread = Thread.currentThread();

		// Stats
		checkStatsStuff();
		try {
			Metrics metrics = new Metrics(this);
			// Custom plotter for each part
			for (final String value : values) {
				logDebug("Pushed to Metrics: " + value);
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
		logDebug("");
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
		catch (IOException e) {
			getLogger().warning("An error occured while copying the default file! IO Exception");
		}
	}

	// Log into the debug file
	public void logDebug(String string) {
		if (debug) {
			try {
				PrintWriter writer = new PrintWriter(new FileWriter(debugFile, true), true);
				if (string.equals("")) {
					writer.write(System.getProperty("line.separator"));
				}
				else {
					Date dt = new Date();
					// Standard date format
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = df.format(dt);
					writer.write(time + " [ColorMe Debug] " + string);
					writer.write(System.getProperty("line.separator"));
				}
				writer.close();
			}
			catch (IOException e) {
				getLogger().warning("An error occurred while writing to the log! IOException");
			}
		}
	}

	// Check if debug is enabled and if a file needs to be created
	private void checkDebug() {
		if (debug) {
			debugFile = new File(Bukkit.getServer().getPluginManager().getPlugin("ColorMe").getDataFolder(), "debug.log");
			if (!debugFile.exists()) {
				try {
					debugFile.createNewFile();
				}
				catch (IOException e) {
					getLogger().warning("Failed to create the debug.log! IOException");
				}
			}
		}
	}

	// Remove empty lines
	private void removeEmptyLines(File config) throws IOException, FileNotFoundException {
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
			logDebug("Updated the config");
		}
		catch (IOException e) {
			getLogger().warning("An error occurred while updating the config! IOException");
			logDebug("Failed to update the config");
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
	private void loadBannedWords() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(bannedWordsFile));
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) continue;
			bannedWords.add(line);
		}
		reader.close();
		logDebug("Loaded the banned words");
	}


	// Loads the config at the start
	private void loadConfig() {
		config.options().header("For help please refer to http://bit.ly/colormebukkit or http://bit.ly/bukkitdevcolorme");
		config.addDefault("debug", false);
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
		config.addDefault("ColorMe.playerTitle", false);
		config.addDefault("ColorMe.playerTitleWithoutSpout", true);
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
		config.addDefault("colors.mixed", true);
		config.addDefault("lengthLimit.Prefixer", 16);
		config.addDefault("lengthLimit.Suffixer", 16);
		config.addDefault("newColorOnJoin" , false);
		config.addDefault("displayAlways.globalSuffix", false);
		config.addDefault("displayAlways.globalPefix", false);
		config.addDefault("useWordBlacklist", true);
		config.addDefault("groups.enable", true);
		config.addDefault("groups.ownSystem", true);
		config.addDefault("softMode.enabled", true);
		config.addDefault("softMode.ownChatPlugin", "Herochat");
		config.addDefault("autoChatColor", false);
		config.addDefault("removeNameAboveHead", false);
		config.options().copyDefaults(true);
		saveConfig();
	}

	// Loads the localization
	private void loadLocalization() {
		localization.options().header("The underscores are used for the different lines!");
		localization.addDefault("permission_denied", "&4You don't have the permission to do this!");
		localization.addDefault("part_disabled", "&4Sorry, but this command and plugin part is disabled!");
		localization.addDefault("only_ingame", "&4Sorry, this command can only be run from ingame!");
		localization.addDefault("color_list", "Color list: (the & values are used for prefix/suffix/chat & signs)!");
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
		localization.addDefault("get_prefix_self", "&eYou &2have got the prefix &f%prefix &2in the world &e%world");
		localization.addDefault("get_prefix_other", "&e%player &2has got the prefix &f%prefix &2in the world &e%world");
		localization.addDefault("get_prefix_global", "&2The global prefix is &f%prefix");
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
		localization.addDefault("get_suffix_self", "&eYou &2have got the suffix &f%suffix &2in the world &e%world");
		localization.addDefault("get_suffix_other", "&e%player &2has got the suffix &f%suffix %2in the world &e%world");
		localization.addDefault("get_suffix_global", "&2The global suffix is &f%suffix");
		localization.addDefault("help_suffix_1", "&2Welcome to the Suffixer (part of ColorMe) version &4%version &2help!");
		localization.addDefault("help_suffix_2", "&4 <> = Required, [] = Optional");
		localization.addDefault("help_suffix_3", "/suffixer help - Shows the help");
		localization.addDefault("help_suffix_4", "/color list - Shows list of colors");
		localization.addDefault("help_suffix_5", "/suffixer get <name> [world] - Gets the actual suffix");
		localization.addDefault("help_suffix_6", "/suffixer remove <name> [world] - Removes suffix");
		localization.addDefault("help_suffix_7", "/suffixer me <suffix> [world] - Sets your own suffix");
		localization.addDefault("help_suffix_8", "/suffixer <name> <suffix> [world] - Sets player's suffix");
		localization.addDefault("help_suffix_9", "/suffixer global <suffix> - Sets the global suffix");
		localization.addDefault("too_long", "&4Sorry, this message is too long!");
		localization.addDefault("bad_words", "&4Sorry,but '%s' is on the blacklist!");
		localization.addDefault("help_group_1", "&2Welcome to the Groups (part of ColorMe) version &4%version &2help!");
		localization.addDefault("help_group_2", "&4 <> = Required, [] = Optional");
		localization.addDefault("help_group_3", "/groups help - Shows the help");
		localization.addDefault("help_group_4", "/color list - Shows list of colors");
		localization.addDefault("help_group_5", "/groups create <name> - Creates a group");
		localization.addDefault("help_group_6", "/groups delete <name> - Deletes a group");
		localization.addDefault("help_group_7", "/groups add <groupName> <name> - Adds a member");
		localization.addDefault("help_group_8", "/groups remove <groupName> <name> - Removes a member");
		localization.addDefault("help_group_9", "/groups members <groupName> - Lists all members");
		localization.addDefault("help_group_10", "/groups set <groupName> <part> <value> [world] - Sets a value of the group");
		localization.addDefault("help_group_11", "/groups get <groupName> <part> [world] - Gets a value of the group");
		localization.addDefault("help_group_12", "/groups list - Lists all groups");
		localization.addDefault("no_groups", "&4There are no groups!");
		localization.addDefault("no_members", "&4There are no members!");
		localization.addDefault("memberlist", "&2Members of the group &e%groupName");
		localization.addDefault("grouplist", "&2Currently enabled groups:");
		localization.addDefault("group_not_existing", "&4The group &e%groupName &4doesn't exist!");
		localization.addDefault("group_already_existing", "&4The group &e%groupName &4already exist!");
		localization.addDefault("unrecognized_part", "&4The part &e%part &4is not known");
		localization.addDefault("deleted_group", "&2Group &e%groupName &2deleted.");
		localization.addDefault("created_group", "&2Group &e%groupName &2created.");
		localization.addDefault("already_a_member", "&e%playerName &4is already a member of the group &e%groupName");
		localization.addDefault("not_a_member", "&e%playerName &4is not a member of the group &e%groupName");
		localization.addDefault("removed_member", "&2Removed &e%playerName &2from the group &e%groupName");
		localization.addDefault("added_member", "&2Added &e%playerName &2to the group &e%groupName");
		localization.addDefault("group_has_nothing", "&4The group &e%groupName &4doesn't have a value in the world &e%world");
		localization.addDefault("group_get_value", "&2The group &e%groupName &2has got the value &e%value &2in the world &e%world");
		localization.addDefault("group_set_value", "&2Set the value of the group &e%groupName &2to &e%value &2in the world &e%world");
		localization.options().copyDefaults(true);
		try {
			localization.save(localizationFile);
			logDebug("Default localization saved");
		} catch (IOException e) {
			getLogger().warning("Failed to save the localization! Please report this! IOException");
			logDebug("Failed to save the default localization");
		}
	}

	// Reloads the config via command /colorme reload, /prefixer reload or /suffixer reload
	public void loadConfigsAgain() {
		try {
			config.load(configFile);
			config.save(configFile);
			checkDebug();
			players.load(playersFile);
			players.save(playersFile);
			localization.load(localizationFile);
			localization.save(localizationFile);
			colors.load(colorsFile);
			colors.save(colorsFile);
			group.load(groupsFile);
			group.save(groupsFile);
			loadBannedWords();
			checkParts();
			if (tagAPI && playerTitleWithoutSpout) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					TagAPI.refreshPlayer(p);
				}
			}
			logDebug("Configs and files loaded again");
		}
		catch (FileNotFoundException e) {
			getLogger().warning("Failed to load the configs! Please report this! FileNotFoundException");
		}
		catch (IOException e) {
			getLogger().warning("Failed to load the configs! Please report this! IOException");
		}
		catch (InvalidConfigurationException e) {
			getLogger().warning("Failed to load the configs! Please report this! InvalidConfigurationException");
		}
	}

	// Maybe something changed on the fly
	private void checkParts() {
		Suffixer = config.getBoolean("Suffixer");
		Prefixer = config.getBoolean("Prefixer");
		displayName = config.getBoolean("ColorMe.displayName");
		tabList = config.getBoolean("ColorMe.tabList");
		playerTitle = config.getBoolean("ColorMe.playerTitle");
		playerTitleWithoutSpout = config.getBoolean("ColorMe.playerTitleWithoutSpout");
		chatBrackets = config.getBoolean("chatBrackets");
		signColors = config.getBoolean("ColorMe.signColors");
		chatColors = config.getBoolean("ColorMe.chatColors");
		globalPrefix = config.getString("global_default." + "prefix").isEmpty();
		globalSuffix = config.getString("global_default." + "suffix").isEmpty();
		globalColor = config.getString("global_default." + "color").isEmpty();
		prefixLength = config.getInt("lengthLimit.Prefixer");
		suffixLength = config.getInt("lengthLimit.Suffixer");
		newColorOnJoin = config.getBoolean("newColorOnJoin");
		displayAlwaysGlobalPrefix = config.getBoolean("displayAlways.globalPefix");
		displayAlwaysGlobalSuffix = config.getBoolean("displayAlways.globalSuffix");
		blacklist = config.getBoolean("useWordBlacklist");
		groups = config.getBoolean("groups.enable");
		ownSystem = config.getBoolean("groups.ownSystem");
		softMode = config.getBoolean("softMode.enabled");
		autoChatColor = config.getBoolean("autoChatColor");
		removeNameAboveHead = config.getBoolean("removeNameAboveHead");
		if (debug) {
			logDebug("Suffixer is " + Suffixer);
			logDebug("Prefixer is " + Prefixer);
			logDebug("tabList is " + tabList);
			logDebug("displayName is " + displayName);
			logDebug("playerTitle is " + playerTitle);
			logDebug("playerTitleWithoutSpout is " + playerTitleWithoutSpout);
			logDebug("chatBrackets are " + chatBrackets);
			logDebug("signColors are " + signColors);
			logDebug("chatColors are " + chatColors);
			logDebug("globalPrefix is " + globalPrefix);
			logDebug("globalSuffix is " + globalSuffix);
			logDebug("globalColor is " + globalColor);
			logDebug("prefixLength is " + prefixLength);
			logDebug("suffixLength is " + suffixLength);
			logDebug("newColorOnJoin is " + newColorOnJoin);
			logDebug("displayAlwaysGlobalPrefix is " + displayAlwaysGlobalPrefix);
			logDebug("displayAlwaysGlobalSuffix is " + displayAlwaysGlobalSuffix);
			logDebug("blacklist is " + blacklist);
			logDebug("groups is " +  groups);
			logDebug("ownSystem is " + ownSystem);
			logDebug("autoChatColor is " + autoChatColor);
			logDebug("removeNameAboveHead is " + removeNameAboveHead);
		}
	}

	// Used for Metrics
	private void checkStatsStuff() {
		if (Prefixer) values.add("Prefixer");
		if (Suffixer) values.add("Suffixer");
		if (displayName) values.add("ColorMe - displayName");
		if (tabList) values.add("ColorMe - tabList");
		if (playerTitle) values.add("ColorMe - playerTitle");
		if (playerTitleWithoutSpout) values.add("ColorMe - playerTitleWithoutSpout");
		if (chatColors) values.add("ColorMe - chatColors");
		if (signColors) values.add("ColorMe - signColors");
		if (groups && ownSystem) values.add("Groups - ownSystem");
		if (groups && pex) values.add("Groups - PermissionsEx");
		if (groups && bPermissions) values.add("Groups - bPermissions");
		if (groups && groupManager) values.add("Groups - GroupManager");
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
	public void message(CommandSender sender, Player player, String message, String value, String world, String target, Double cost) {
		if (message != null) {
			message = message
					.replaceAll("%world", world)
					.replaceAll("%color", value)
					.replaceAll("%prefix", value)
					.replaceAll("%suffix", value)
					.replaceAll("%s", value)
					.replaceAll("%playerName", world)
					.replaceAll("%player", target)
					.replaceAll("%groupName", value)
					.replaceAll("%part", value)
					.replaceAll("%value", target)
					.replaceAll("%version", "3.6");
			message = ChatColor.translateAlternateColorCodes('\u0026', message);
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
				logDebug("Message is null");
			}
			else if (sender != null) {
				sender.sendMessage(ChatColor.DARK_RED + "Somehow this message is not defined. Please check your localization.yml");
				logDebug("Message is null");
			}
		}
	}
}