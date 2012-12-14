package de.dustplanet.colorme.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
// PEX Import
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
// bPermissions Import
import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.dustplanet.colorme.Actions;
import de.dustplanet.colorme.ColorMe;
// GroupManager Import
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;

/**
 * ColorMe for CraftBukkit/Bukkit
 * Handles the player activities
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

public class ColorMePlayerListener implements Listener {
	private ColorMe plugin;
	private Actions actions;
	public ColorMePlayerListener(ColorMe instance, Actions actionsInstance) {
		plugin = instance;
		actions = actionsInstance;
	}

	private String[] pluginPart = {"colors", "prefix", "suffix"};

	// Loads the the values and set them to default one if not known
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.logDebug("\t---PlayerJoinEvent Begin---");
		Player player = event.getPlayer();
		String name = player.getName().toLowerCase();
		String world = player.getWorld().getName().toLowerCase();
		// New color onJoin?
		if (plugin.newColorOnJoin) {
			// Normal colors + rainbow & random
			int customColors = plugin.colors.getKeys(false).size();
			int colors = ChatColor.values().length;
			int color = (int) (Math.random()* ((colors + 2 + customColors)));
			// 22 == Reset -> bad
			while (color == 22) color = (int) (Math.random()* ((colors + 2 + customColors)));
			// Set it.
			if (color == ChatColor.values().length + 1) actions.set(name, "rainbow", world, pluginPart[0]);
			else if (color == ChatColor.values().length + 2) actions.set(name, "random", world, pluginPart[0]);
			else if (color > ChatColor.values().length + 2) {
				color -= (2 + ChatColor.values().length);
				int i = 0;
				for (String colorName : plugin.colors.getKeys(false)) {
					if (i == color) actions.set(name, colorName, world, pluginPart[0]);
					i++;
				}
			}
			else actions.set(name, ChatColor.values()[color].name().toLowerCase(), world, pluginPart[0]);
		}
		actions.checkNames(name, world);
		plugin.logDebug("\t---PlayerJoinEvent End---");
		plugin.logDebug("");
	}

	// Loads the the values and set them to default one if not known

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatLowest(AsyncPlayerChatEvent event) {
		if (plugin.softMode) {
			plugin.logDebug("\t---PlayerChatEvent LowestPriority Begin---");
			modifyChat(event);
			plugin.logDebug("\t---PlayerChatEvent LowestPriority End---");
			plugin.logDebug("");
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChatLow(AsyncPlayerChatEvent event) {
		if (plugin.softMode) {
			plugin.logDebug("\t---PlayerChatEvent LowPriority Begin---");
			modifyChat(event);
			plugin.logDebug("\t---PlayerChatEvent LowPriority End---");
			plugin.logDebug("");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChatNormal(AsyncPlayerChatEvent event) {
		if (plugin.softMode) {
			plugin.logDebug("\t---PlayerChatEvent NormalPriority Begin---");
			modifyChat(event);
			plugin.logDebug("\t---PlayerChatEvent NormalPriority End---");
			plugin.logDebug("");
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChatHigh(AsyncPlayerChatEvent event) {
		if (!plugin.softMode) {
			plugin.logDebug("\t---PlayerChatEvent HighPriority Begin---");
			modifyChat(event);
			plugin.logDebug("\t---PlayerChatEvent HighPriority End---");
			plugin.logDebug("");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChatHighest(AsyncPlayerChatEvent event) {
		if (!plugin.softMode) {
			plugin.logDebug("\t---PlayerChatEvent HighestPriority Begin---");
			modifyChat(event);
			plugin.logDebug("\t---PlayerChatEvent HighestPriorityEnd---");
			plugin.logDebug("");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChatMonitor(AsyncPlayerChatEvent event) {
		if (!plugin.softMode) {
			plugin.logDebug("\t---PlayerChatEvent MonitorPriority Begin---");
			modifyChat(event);
			plugin.logDebug("\t---PlayerChatEvent MonitorPriority End---");
			plugin.logDebug("");
		}
	}

	private void modifyChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String name = player.getName().toLowerCase(), nameExact = player.getName();
		String world = player.getWorld().getName().toLowerCase();
		String prefix = "", suffix = "", globalSuffix = "", globalPrefix = "", groupPrefix = "", groupSuffix = "";
		// Group check!
		if (plugin.groups) {
			if (plugin.pex) {
				PermissionUser user = PermissionsEx.getUser(player);
				// Only first group
				PermissionGroup group = user.getGroups(world)[0];
				// Get the prefix from the pex config
				groupPrefix = actions.replaceThings(group.getPrefix(world));
				// Get the suffix from the pex config
				groupSuffix = actions.replaceThings(group.getSuffix(world));
			}
			else if (plugin.bPermissions) {
				// Only fist group
				if (ApiLayer.getGroups(world, CalculableType.USER, name).length > 0) {
					String group = ApiLayer.getGroups(world, CalculableType.USER, nameExact)[0];
					groupPrefix = actions.replaceThings(ApiLayer.getValue(player.getWorld().getName(), CalculableType.GROUP, group, "prefix"));
					groupSuffix = actions.replaceThings(ApiLayer.getValue(player.getWorld().getName(), CalculableType.GROUP, group, "suffix"));
				}
			}
			else if (plugin.groupManager) {
				// World data -> then groups (only first) & finally the suffix & prefix!
				OverloadedWorldHolder groupManager = plugin.groupManagerWorldsHolder.getWorldData(world);
				String group = groupManager.getUser(nameExact).getGroupName();
				groupPrefix = actions.replaceThings(groupManager.getGroup(group).getVariables().getVarString("prefix"));
				groupSuffix = actions.replaceThings(groupManager.getGroup(group).getVariables().getVarString("suffix"));
			}
			else if (plugin.ownSystem) {
				if (actions.playerHasGroup(name)) {
					String group = actions.playerGetGroup(name);
					if (actions.hasGroup(group, world, "prefix")) groupPrefix = actions.getGroup(group, world, "prefix");
					if (actions.hasGroup(group, "default", "prefix")) groupPrefix = actions.getGroup(group, "default", "prefix");
					if (actions.hasGroup(group, world, "suffix")) groupSuffix = actions.getGroup(group, world, "suffix");
					if (actions.hasGroup(group, "default", "suffix")) groupSuffix = actions.getGroup(group, "default", "suffix");
				}
			}
		}

		if (plugin.Prefixer) {
			// Get world prefix if available
			if (actions.has(name, world, "prefix")) {
				prefix = actions.get(name, world, "prefix");
			}
			// Get default prefix
			else if (actions.has(name, "default", "prefix")) {
				prefix = actions.get(name, "default", "prefix");
			}
			// Get the global prefix
			else if (plugin.globalPrefix) {
				prefix = actions.getGlobal("prefix");
			}
			// Display global one, too?
			if (plugin.globalPrefix && plugin.displayAlwaysGlobalPrefix) {
				globalPrefix = actions.getGlobal("prefix");
				if (globalPrefix.equals(prefix)) globalPrefix = "";
			}
		}
		if (plugin.Suffixer) {
			// Get world suffix if available
			if (actions.has(name, world, "suffix")) {
				suffix = actions.get(name, world, "suffix");
			}
			// Get default suffix
			else if (actions.has(name, "default", "suffix")) {
				suffix = actions.get(name, "default", "suffix");
			}
			// Get the global suffix
			else if (plugin.globalSuffix) {
				suffix = actions.getGlobal("suffix");
			}
			// Display global one, too?
			if (plugin.globalSuffix && plugin.displayAlwaysGlobalSuffix) {
				globalSuffix = actions.getGlobal("suffix");
				if (globalSuffix.equals(suffix)) globalSuffix = "";
			}
		}
		// Remove the chat brackets if wanted
		if (!plugin.otherChatPluginFound) {
			if (!plugin.useLegacyFormat) {
				String format = plugin.format;
				format = format.replace("[Prefix]", prefix)
						.replace("[Suffix]", suffix)
						.replace("[GlobalPrefix]", globalPrefix)
						.replace("[GlobalSuffix]", globalSuffix)
						.replace("[GroupPrefix]", groupPrefix)
						.replace("[GroupSuffix]", groupSuffix)
						.replace("[name]", "%1$s")
						.replace("[message]", "%2$s");
				format = ChatColor.translateAlternateColorCodes('\u0026', format);
				if (plugin.factions && !format.contains("[FACTION]")) format = "[FACTION]" + format;
				event.setFormat(format);
			}
			else {
				String format = "";
				if (!plugin.chatBrackets) {
					if (globalSuffix.equals("") && groupSuffix.equals("") && suffix.equals("")) {
						format = globalPrefix + ChatColor.RESET + groupPrefix + ChatColor.RESET + prefix + ChatColor.RESET + "%1$s" + ChatColor.RESET + groupSuffix + ChatColor.RESET + suffix + ChatColor.RESET + globalSuffix + ": %2$s";
					}
					else {
						format = globalPrefix + ChatColor.RESET + groupPrefix + ChatColor.RESET + prefix + ChatColor.RESET + "%1$s " + ChatColor.RESET + groupSuffix + ChatColor.RESET + suffix + ChatColor.RESET + globalSuffix + ": %2$s";
					}
					if (!plugin.factions) event.setFormat(format);
					else event.setFormat("[FACTION] " + format);
				}
				else {
					if (!globalSuffix.equals("")) globalSuffix += ChatColor.RESET + ": ";
					else if (!suffix.equals("")) suffix += ChatColor.RESET + ": ";
					else if (!groupSuffix.equals("")) groupSuffix += ChatColor.RESET + ": ";
					if (!groupSuffix.equals("") && !suffix.equals("")) groupSuffix += " ";
					if (!suffix.equals("") && !globalSuffix.equals("")) suffix += " ";
					format = globalPrefix + ChatColor.RESET + groupPrefix + ChatColor.RESET + prefix + ChatColor.RESET + "<%1$s" + ChatColor.RESET + "> " + ChatColor.RESET + groupSuffix + ChatColor.RESET + suffix + ChatColor.RESET + globalSuffix + "%2$s";
					if (!plugin.factions) event.setFormat(format);
					else event.setFormat("[FACTION] " + format);
				}
			}
		}
		else {
			// Replace the different values
			String newFormat = event.getFormat();
			newFormat = newFormat.replace("[ColorMePrefix]", prefix)
					.replace("[ColorMeGroupPrefix]", groupPrefix)
					.replace("[ColorMeGlobalPrefix]", globalPrefix)
					.replace("[ColorMeSuffix]", suffix)
					.replace("[ColorMeGroupSuffix]", groupSuffix)
					.replace("[ColorMeGlobalSuffix]", globalSuffix);
			event.setFormat(newFormat);
		}
		// Color the message, too?
		if (plugin.chatColors && player.hasPermission("colorme.chat")) {
			// Should the chat be auto colored?
			if (plugin.autoChatColor) {
				for (ChatColor value : ChatColor.values()) {
					// get the name from the integer
					String color = value.name().toLowerCase();
					if (player.hasPermission("colorme.autochatcolor." + color)) {
						event.setMessage(value + event.getMessage());
						break;
					}
				}
			}
			// Still color message
			event.setMessage(actions.replaceThings(event.getMessage()));
		}
	}
}