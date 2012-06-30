package de.dustplanet.colorme;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

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
		Player player = event.getPlayer();
		String name = player.getName().toLowerCase();
		String world = player.getWorld().getName().toLowerCase();
		String prefix = null, suffix = null, globalSuffix = null, globalPrefix = null;



		PermissionUser user = PermissionsEx.getUser(player);

		// Returns player's groups in particular world
		String[] groups = user.getGroupsNames(world);
		player.sendMessage(groups);
		// returns player prefix in specific world
		String presfix = user.getPrefix(world);
		player.sendMessage(presfix);

		String temp = user.getOwnPrefix();
		if (temp != null) player.sendMessage(temp);

		CheckRoutine(player, name, world);
		if (ColorMe.Prefixer) {
			// Get world prefix if available
			if (Actions.has(name, world, "prefix")) {
				prefix = Actions.get(name, world, "prefix");
			}
			// Get default prefix
			else if (Actions.has(name, "default", "prefix")) {
				prefix = Actions.get(name, "default", "prefix");
			}
			// Get the global prefix
			else if (ColorMe.globalPrefix) {
				prefix = Actions.getGlobal("prefix");
			}
			// If prefix is not null change the format
			if (prefix != null) {
				event.setFormat(prefix + ChatColor.WHITE + " " + event.getFormat());
			}
			// Display global one, too?
			if (ColorMe.globalPrefix && ColorMe.displayAlwaysGlobalPrefix) {
				globalPrefix = Actions.getGlobal("prefix");
				event.setFormat(globalPrefix + ChatColor.WHITE + " " + event.getFormat());
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
			// Search the bracket
			if (event.getFormat().contains(">")) {
				int i = event.getFormat().lastIndexOf(">") + 1;
				int length = event.getFormat().length();
				// Substring 1 until the bracket, substring 2 after the bracket
				String sub1 = event.getFormat().substring(0, i);
				String sub2 = event.getFormat().substring(i, length);
				// Insert the suffix between ;)
				if (ColorMe.globalSuffix && ColorMe.displayAlwaysGlobalSuffix) {
					globalSuffix = Actions.getGlobal("suffix");
					// Both & different
					if (suffix != null && !suffix.equals(globalSuffix)) {
						event.setFormat(sub1 + " " + globalSuffix + ChatColor.WHITE + " " + suffix + ChatColor.WHITE + ":" + sub2);
					}
					// Only global
					else event.setFormat(sub1 + " " + globalSuffix + ChatColor.WHITE + ":" + sub2);
				}
				// Only normal suffix
				else if (suffix != null) {
					event.setFormat(sub1 + " " + suffix + ChatColor.WHITE + ":" + sub2);
				}
			}
		}
		// Remove the chat brackets if wanted
		if (!ColorMe.chatBrackets) {
			String brackets = "%1$s %2$s";
			event.setFormat(brackets);
		}
		// Color the message, too?
		if (ColorMe.chatColors && player.hasPermission("colorme.chat"))	{
				event.setMessage(Actions.replaceThings(event.getMessage()));
		}
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