package com.sparkedia.valrix.ColorMe;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.iConomy.iConomy;

public class ColorServer extends ServerListener {
	private ColorMe plugin;
	
	public ColorServer(ColorMe plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPluginDisable(PluginDisableEvent e) {
		if (plugin.iconomy != null) {
			if (e.getPlugin().getDescription().getName().equals("iConomy")) {
				plugin.iconomy = null;
				System.out.println('['+plugin.pName+"] un-hooked from iConomy.");
			}
		}
	}
	
	@Override
	public void onPluginEnable(PluginEnableEvent e) {
		if (plugin.iconomy == null) {
			Plugin iconomy = plugin.getServer().getPluginManager().getPlugin("iConomy");
			
			if (iconomy != null) {
				if (iconomy.isEnabled() && iconomy.getClass().getName().equals("com.iConomy.iConomy")) {
					plugin.iconomy = (iConomy)iconomy;
					System.out.println('['+plugin.pName+"] hooked into iConomy.");
				}
			}
		}
	}
}
