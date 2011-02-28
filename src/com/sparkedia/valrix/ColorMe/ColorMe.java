package com.sparkedia.valrix.ColorMe;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ColorMe extends JavaPlugin {
	private final ColorPlayerListener pListener = new ColorPlayerListener(this);
	protected static final Logger log = Logger.getLogger("Minecraft");
	public static Property colors = null;
	
	public void onDisable() {
		PluginDescriptionFile pdf = this.getDescription();
		log.info("["+pdf.getName()+"] v"+pdf.getVersion()+" has been disabled.");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.pListener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdf = this.getDescription();
		log.info("["+pdf.getName()+"] v"+pdf.getVersion()+" has been enabled.");
		
		if (!(new File("plugins/"+pdf.getName()).isDirectory())) {
			(new File("plugins/"+pdf.getName())).mkdir();
		}
		if (colors == null) {
			colors = new Property("plugins/"+pdf.getName()+"/players.color");
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		String cmdName = cmd.getName();
		if (sender instanceof Player) {
			if (((Player)sender).isOp()) {
				String name = args[0].toLowerCase(); //player name or list
				if (cmdName.equalsIgnoreCase("color")) {
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("list")) {
							// /color list
							((Player)sender).sendMessage("Color List:");
							String color;
							String msg1 = "";
							String msg2 = "";
							for (int i = 0; i <= 15; i++) {
								color = ChatColor.getByCode(i).name();
								if (i == 0) {
									msg1 = ChatColor.valueOf(color)+color.toLowerCase().replace("_", "");
								} else if (i > 0 && i < 7) {
									msg1 += " "+ChatColor.valueOf(color)+color.toLowerCase().replace("_", "");
								} else if (i == 7) {
									msg2 = ChatColor.valueOf(color)+color.toLowerCase().replace("_", "");
								} else {
									msg2 += " "+ChatColor.valueOf(color)+color.toLowerCase().replace("_", "");
								}
							}
							((Player)sender).sendMessage(msg1);
							((Player)sender).sendMessage(msg2);
							return true;
						} else if (ColorMe.colors.keyExists(name)) {
							//remove color preset
							ColorMe.colors.remove(name);
							return true;
						}
					} else if (args.length == 2) {
						// /color <name> <color>
						String col = args[1];
						colors.setString(name, col); //name=color
						return true;
					}
				}
			}
		}
		return false;
	}
}
