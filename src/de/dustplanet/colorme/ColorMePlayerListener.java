package de.dustplanet.colorme;

import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
// PEX Import
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;
//bPermissions Import
import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
// GroupManager Import
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;

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
	public ColorMe plugin;
	public ColorMePlayerListener(ColorMe instance) {
		plugin = instance;
	}

	private String[] pluginPart = {"colors", "prefix", "suffix"};
	public static WorldsHolder groupManagerWorldsHolder;

	// Loads the the values and set them to default one if not known
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String name = player.getName().toLowerCase();
		String world = player.getWorld().getName().toLowerCase();
		// New color onJoin?
		if (ColorMe.newColorOnJoin) {
			// Normal colors + rainbow & random
			int color = (int) (Math.random()*(ChatColor.values().length + 2));
			// 22 == Reset -> bad
			while (color == 22) color = (int) (Math.random()*(ChatColor.values().length + 2));
			// Set it.
			if (color == ChatColor.values().length + 1) Actions.set(name, "rainbow", world, pluginPart[0]);
			else if (color == ChatColor.values().length + 2) Actions.set(name, "random", world, pluginPart[0]);
			else Actions.set(name, ChatColor.values()[color].name().toLowerCase(), world, pluginPart[0]);
		}
		CheckRoutine(player, name, world);
	}


	// Loads the the values and set them to default one if not known
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		ColorMe.logDebug("\t---PlayerChatEvent Begin---");
		Player player = event.getPlayer();
		String name = player.getName().toLowerCase(), nameExact = player.getName();
		String world = player.getWorld().getName().toLowerCase();
		String prefix = "", suffix = "", globalSuffix = "", globalPrefix = "", groupPrefix = "", groupSuffix = "";
		// Group check!
		if (ColorMe.groups) {
			if (ColorMe.pex) {
				PermissionUser user = PermissionsEx.getUser(player);
				// Only first group
				PermissionGroup group = user.getGroups(world)[0];
				// Get the prefix from the pex config
				groupPrefix = Actions.replaceThings(group.getPrefix(world));
				// Get the suffix from the pex config
				groupSuffix = Actions.replaceThings(group.getSuffix(world));
			}
			else if (ColorMe.bPermissions) {
				// Only fist group
				String group = ApiLayer.getGroups(world, CalculableType.USER, nameExact)[0];
				groupPrefix = Actions.replaceThings(ApiLayer.getValue(player.getWorld().getName(), CalculableType.GROUP, group, "prefix"));
				groupSuffix = Actions.replaceThings(ApiLayer.getValue(player.getWorld().getName(), CalculableType.GROUP, group, "suffix"));
			}
			else if (ColorMe.groupManager) {
				// World data -> then groups (only first) & finally the suffix & prefix!
				OverloadedWorldHolder groupManager = groupManagerWorldsHolder.getWorldData(world);
				if (groupManager != null) {
					String group = groupManager.getUser(nameExact).getGroupName();
					groupPrefix = Actions.replaceThings(groupManager.getGroup(group).getVariables().getVarString("prefix"));
					groupSuffix = Actions.replaceThings(groupManager.getGroup(group).getVariables().getVarString("suffix"));
				}
			}
			else {
				// Own system
			}
			if (!groupPrefix.equals("")) groupPrefix += " ";
		}

		CheckRoutine(player, name, world);
		if (ColorMe.Prefixer) {
			// Get world prefix if available
			if (Actions.has(name, world, "prefix")) {
				prefix = Actions.get(name, world, "prefix") + " ";
			}
			// Get default prefix
			else if (Actions.has(name, "default", "prefix")) {
				prefix = Actions.get(name, "default", "prefix") + " ";
			}
			// Get the global prefix
			else if (ColorMe.globalPrefix) {
				prefix = Actions.getGlobal("prefix") + " ";
			}
			// Display global one, too?
			if (ColorMe.globalPrefix && ColorMe.displayAlwaysGlobalPrefix) {
				globalPrefix = Actions.getGlobal("prefix") + " ";
				if (globalPrefix.equals(prefix)) globalPrefix = "";
			}
		}
		if (ColorMe.Suffixer) {
			// Get world suffix if available
			if (Actions.has(name, world, "suffix")) {
				suffix = Actions.get(name, world, "suffix");
			}
			// Get default suffix
			else if (Actions.has(name, "default", "suffix")) {
				suffix = Actions.get(name, "default", "suffix");
			}
			// Get the global suffix
			else if (ColorMe.globalSuffix) {
				suffix = Actions.getGlobal("suffix");
			}
			// Display global one, too?
			if (ColorMe.globalSuffix && ColorMe.displayAlwaysGlobalSuffix) {
				globalSuffix = Actions.getGlobal("suffix");
				if (globalSuffix.equals(suffix)) globalSuffix = "";
			}
		}
		// Remove the chat brackets if wanted
		if (!ColorMe.chatBrackets) {
			String format = "";
			if (globalSuffix.equals("") && groupSuffix.equals("") && suffix.equals("")) {
				format = globalPrefix + ChatColor.RESET + groupPrefix + ChatColor.RESET + prefix + ChatColor.RESET + "%1$s" + ChatColor.RESET + groupSuffix + ChatColor.RESET + suffix + ChatColor.RESET + globalSuffix + ": %2$s";
			}
			else {
				format = globalPrefix + ChatColor.RESET + groupPrefix + ChatColor.RESET + prefix + ChatColor.RESET + "%1$s " + ChatColor.RESET + groupSuffix + ChatColor.RESET + suffix + ChatColor.RESET + globalSuffix + ": %2$s";
			}
			event.setFormat(format);
		}
		else {
			if (!globalSuffix.equals("")) globalSuffix += ChatColor.RESET + ": ";
			else if (!suffix.equals("")) suffix += ChatColor.RESET + ": ";
			else if (!groupSuffix.equals("")) groupSuffix += ChatColor.RESET + ": ";
			if (!groupSuffix.equals("") && !suffix.equals("")) groupSuffix += " ";
			if (!suffix.equals("") && !globalSuffix.equals("")) suffix += " ";
			String format = globalPrefix + ChatColor.RESET + groupPrefix + ChatColor.RESET + prefix + ChatColor.RESET + "<%1$s> " + ChatColor.RESET + groupSuffix + ChatColor.RESET + suffix + ChatColor.RESET + globalSuffix + "%2$s";
			event.setFormat(format);
		}
		// Color the message, too?
		if (ColorMe.chatColors && player.hasPermission("colorme.chat"))	{
			event.setMessage(Actions.replaceThings(event.getMessage()));
		}
		ColorMe.logDebug("\t---PlayerChatEvent End---");
		ColorMe.logDebug("");
	}

	// Check for the player and update the file is values are unknown
	private void CheckRoutine(Player player, String name, String world) {
		// If the player isn't in the players.yml add him
		if (!ColorMe.players.contains(name)) {
			ColorMe.players.set(name + ".colors.default", "");
			ColorMe.players.set(name + ".prefix.default", "");
			ColorMe.players.set(name + ".suffix.default", "");
			ColorMe.players.set(name + ".colors." + world, "");
			ColorMe.players.set(name + ".prefix." + world, "");
			ColorMe.players.set(name + ".suffix." + world, "");
			try {
				ColorMe.players.save(ColorMe.playersFile);
			} catch (IOException e) {
				plugin.getServer().getLogger().warning("Failed to save the players.yml! Please report this! IOException");
			}
		}
		for (int i = 0; i <= 2; i++) {
			String actualPart = pluginPart[i];
			if (!ColorMe.players.contains(name + "." + actualPart + "." + world)) {
				ColorMe.players.set(name + "." + actualPart + "." + world, "");
				try {
					ColorMe.players.save(ColorMe.playersFile);
				} catch (IOException e) {
					plugin.getServer().getLogger().warning("Failed to save the players.yml! Please report this! IOException");
				}
			}
			if (!ColorMe.players.contains(name + "." + actualPart + "." + "default")) {
				ColorMe.players.set(name + "." + actualPart + "." + "default", "");
				try {
					ColorMe.players.save(ColorMe.playersFile);
				} catch (IOException e) {
					plugin.getServer().getLogger().warning("Failed to save the players.yml! Please report this! IOException");
				}
			}
		}
		Actions.checkNames(name, world);
	}
}