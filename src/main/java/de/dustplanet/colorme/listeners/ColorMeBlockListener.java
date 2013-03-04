package de.dustplanet.colorme.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import de.dustplanet.colorme.Actions;
import de.dustplanet.colorme.ColorMe;

/**
 * ColorMe for CraftBukkit/Bukkit
 * Handles the sign event
 * 
 * Refer to the forum thread:
 * http://bit.ly/colormebukkit
 * 
 * Refer to the dev.bukkit.org page:
 * http://bit.ly/bukkitdevcolorme
 * 
 * @author xGhOsTkiLLeRx
 * thanks to Valrix for the original ColorMe plugin!!
 * 
 */

public class ColorMeBlockListener implements Listener {
    private ColorMe plugin;
    private Actions actions;

    public ColorMeBlockListener(ColorMe instance, Actions actionsInstance) {
	plugin = instance;
	actions = actionsInstance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent event) {
	if (!plugin.signColors || !event.getPlayer().hasPermission("colorme.sign")) {
	    return;
	}
	// 4 lines a sign
	for (int i = 0; i < 4; i++) {
	    // Leave empty out
	    if (event.getLine(i).isEmpty()) {
		continue;
	    }
	    // Update the string
	    String updated = actions.replaceThings(event.getLine(i));
	    event.setLine(i, updated);
	}
    }
}