package de.xghostkillerx.colorme;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ColorMeBlockListener implements Listener {
	public ColorMe plugin;
	public ColorMeBlockListener(ColorMe instance) {
		plugin = instance;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if (!ColorMe.signColors) return;
		// 4 lines a sign
		for (int i = 0; i < 4; i++) {
			// Leave empty out
			if (event.getLine(i).isEmpty()) continue;
			// Update the string
			String updated = Actions.replaceThings(event.getLine(i));
			event.setLine(i, updated);
		}
	}
}