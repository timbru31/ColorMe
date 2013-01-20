package de.dustplanet.colorme.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import de.dustplanet.colorme.Actions;
import de.dustplanet.colorme.ColorMe;
// TagAPI
import org.kitteh.tag.PlayerReceiveNameTagEvent;

/**
 * ColorMe for CraftBukkit/Bukkit
 * Handles the player name (TagAPI)
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

public class ColorMeTagAPIListener implements Listener {
	private ColorMe plugin;
	private Actions actions;

	public ColorMeTagAPIListener(ColorMe instance, Actions actionsInstance) {
		plugin = instance;
		actions = actionsInstance;
	}

	@EventHandler(ignoreCancelled = true)
	public void onNameTag(final PlayerReceiveNameTagEvent event) {
		if (event.getNamedPlayer().hasPermission("colorme.nametag")) {
			if (plugin.removeNameAboveHead) {
				event.setTag("");
				return;
			}
			String name = event.getNamedPlayer().getName();
			String world = event.getNamedPlayer().getWorld().getName();
			
			// Fallback
			String color = null;
			
			// Specific world
			if (actions.has(name, world, "colors"))	color = actions.get(name, world, "colors");

			// Check player default
			else if (actions.has(name, "default", "colors")) color = actions.get(name, "default", "colors");
			
			// If groups enabled
			else if (plugin.groups) {
				if (plugin.ownSystem) {
					// If group available
					if (actions.playerHasGroup(name)) {
						String group = actions.playerGetGroup(name);
						// Group specific world
						if (actions.hasGroup(group, world, "colors")) color = actions.getGroup(group, world, "colors");
						// Group default
						else if (actions.hasGroup(group, "default", "colors")) color = actions.getGroup(group, "default", "colors");
					}
				}
				color = actions.getColorFromGroup(name, world);
			}
			// Then check if still nothing found and globalColor
			if (plugin.globalColor && color == null && actions.hasGlobal("color")) color = actions.getGlobal("color");
			
			// Additional null check
			if (color == null || color.equals("")) color = "WHITE";
			
			// Check if valid
			String[] colors = color.split("-");
			for (String colorActual : colors) {
				// Special case: not a standard, use first char then!
				if (!actions.isStandard(colorActual)) {
					if (colorActual.equalsIgnoreCase("rainbow")) colorActual = "dark_red";

					// Generate a random color
					else if (colorActual.equalsIgnoreCase("random")) {
						int x = (int) (Math.random() * ChatColor.values().length);
						colorActual = ChatColor.values()[x].name().toLowerCase();
					}
					else {
						String colorChars = ChatColor.translateAlternateColorCodes('\u0026', plugin.colors.getString(colorActual));
						// No section sign or ampersand? Not valid; doesn't start with a section sign? Not valid! Ending without a char? Not valid!
						if (!colorChars.contains("\u00A7") || colorChars.contains("\u0026") || !colorChars.startsWith("\u00A7") || colorChars.endsWith("\u00A7")) colorActual = "white";
						// Split the color values
						else {
							String colorValues[] = colorChars.split(",");
							// Special here, since we already have got the section sign sign!
							colorActual = colorValues[0];
							name = colorActual + name;
							continue;
						}
					}
				}
				name = ChatColor.valueOf(colorActual.toUpperCase()) + name;
			}
			if (name != null && !name.equals("")) event.setTag(name);
		}
	}
}
