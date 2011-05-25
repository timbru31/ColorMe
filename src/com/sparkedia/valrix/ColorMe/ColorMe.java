package com.sparkedia.valrix.ColorMe;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.TextWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Type;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class ColorMe extends JavaPlugin {
	protected Logger log;
	public Property colors;
	public String pName;
	public File df;
	private Property config; // need at least one config option for non-OP use
	private PermissionHandler permission;
	public iConomy iconomy = null;
	
	@Override
	public void onDisable() {
		log.info('['+pName+"] has been disabled.");
	}

	@Override
	public void onEnable() {
		log = getServer().getLogger();
		
		pName = getDescription().getName();
		df = getDataFolder();
		
		if (!df.isDirectory()) df.mkdir();

		colors = new Property(df+"/players.color", "color", this);

		//Does the config exist, if not then make a new blank one
		if (!(new File(df+"/config.txt").exists())) {
			config = new Property(df+"/config.txt", "color", this);
			config.setBoolean("OP", true); //OP only by default
			config.setDouble("cost", 0);
			config.save();
		} else {
			config = new Property(df+"/config.txt", "config", this);
			// Check if they have the updated prefix property file, otherwise update it to new format
			if (!getDescription().getVersion().equalsIgnoreCase(config.getString(pName+"Version"))) {
				LinkedHashMap<String, Object> tmp = new LinkedHashMap<String, Object>();
				config.remove(pName+"Version");
				config.remove(pName+"Type");
				for (String key : config.getKeys()) {
					// Reformat each player
					tmp.put(key, config.getString(key));
				}
				config.rebuild(tmp);
			}
		}
		
		PluginManager pm = getServer().getPluginManager();
		
		if (pm.getPlugin("Permissions") != null)
			permission = ((Permissions)pm.getPlugin("Permissions")).getHandler();
		else
			log.info('['+pName+"]: Permission system not detected. Defaulting to OP permissions.");
		
		pm.registerEvent(Type.PLAYER_CHAT, new ColorPlayerListener(this), Priority.Lowest, this);
		
		ColorServer cs = new ColorServer(this);
		pm.registerEvent(Type.PLUGIN_ENABLE, cs, Priority.Monitor, this);
		pm.registerEvent(Type.PLUGIN_DISABLE, cs, Priority.Monitor, this);
		
		log.info('['+pName+"] v"+getDescription().getVersion()+" has been enabled.");
		
		// /color <color/name> [name] (name is optional since you can color your own name)
		getCommand("colorme").setExecutor(new CommandExecutor() {
			public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
				if (sender instanceof Player) {
					Player player = ((Player)sender);
					// Sender has permissions for /prefix or (sender is an OP or OP=false)
					if (permission != null) {
						if (args.length == 1) {
							if (args[0].equalsIgnoreCase("list") && permission.has(player, "colorme.list")) {
								// Display a list of colors for the user
								player.sendMessage("Color List:");
								String color;
								String msg = "";
								for (int i = 0; i < ChatColor.values().length; i++) {
									color = ChatColor.getByCode(i).name();
									if (msg.length() == 0) {
										msg = ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+' ';
										continue;
									}
									msg += (i == ChatColor.values().length-1) ? ChatColor.valueOf(color)+color.toLowerCase().replace("_", "") : ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+' ';
									TextWrapper.wrapText(msg);
								}
								player.sendMessage(msg);
								return true;
							}
							if ((hasColor(args[0]) && (permission.has(player, "colorme.remove"))) || args[0].equalsIgnoreCase(player.getName().toLowerCase())) {
								// Only people with permission to remove another's color or the color owner can remove
								removeColor(args[0]);
								return true;
							}
							if (permission.has(player, "colorme.self")) {
								// Set a color for the user calling the command if they have permission
								setColor(player.getName(), args[0]);
								if (iconomy != null) {
									double cost = config.getDouble("cost");
									Account acct = iConomy.getAccount(player.getName());
									if (cost > 0 && acct.getHoldings().hasEnough(cost)) {
										acct.getHoldings().subtract(cost);
										player.sendMessage(ChatColor.RED.toString()+"You have been charged "+iConomy.format(cost)+'.');
										return true;
									}
									player.sendMessage(ChatColor.RED.toString()+"It costs "+iConomy.format(cost)+" to color your name.");
									return true;
								}
							}
							player.sendMessage("You don't have permission to color your own name.");
							return true;
						}
						if (args.length == 2) {
							// /colorme <name> <color>
							if ((hasColor(args[0]) && (permission.has(player, "colorme.other"))) || (args[0].equalsIgnoreCase(player.getName().toLowerCase()) && permission.has(player, "colorme.self"))) {
								// Name exists. They have permission to set another's color or can set own.
								setColor(args[0], args[1]);
								if (iconomy != null && args[0].equalsIgnoreCase(player.getName())) {
									double cost = config.getDouble("cost");
									Account acct = iConomy.getAccount(player.getName());
									if (cost > 0 && acct.getHoldings().hasEnough(cost)) {
										acct.getHoldings().subtract(cost);
										player.sendMessage(ChatColor.RED.toString()+"You have been charged "+iConomy.format(cost)+'.');
										return true;
									}
									// Not enough
									return true;
								}
							}
							return true;
						}
					}
					if (player.isOp() || (!player.isOp() && !config.getBoolean("OP"))) {
						// Permissions isn't enabled
						if (args.length == 1) {
							if (args[0].equalsIgnoreCase("list")) {
								// Display a list of colors for the user
								player.sendMessage("Color List:");
								String color;
								String msg = "";
								for (int i = 0; i < ChatColor.values().length; i++) {
									color = ChatColor.getByCode(i).name();
									if (msg.length() == 0) {
										msg = ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+' ';
										continue;
									}
									msg += (i == ChatColor.values().length-1) ? ChatColor.valueOf(color)+color.toLowerCase().replace("_", "") : ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+' ';
									TextWrapper.wrapText(msg);
								}
								player.sendMessage(msg);
								return true;
							}
							if (hasColor(args[0]) && ((player.isOp() || (!player.isOp() && !config.getBoolean("OP"))) || args[0].equalsIgnoreCase(player.getName().toLowerCase()))) {
								// Only people with permission to remove another's color or the color owner can remove
								removeColor(args[0]);
								return true;
							}
							if (!hasColor(args[0])) {
								// If not trying to remove a color they don't already have...
								if (player.isOp() || (!player.isOp() && !config.getBoolean("OP"))) {
									// Set a color for the user calling the command if they have permission
									setColor(player.getName(), args[0]);
									if (iconomy != null) {
										double cost = config.getDouble("cost");
										Account acct = iConomy.getAccount(player.getName());
										if (cost > 0 && acct.getHoldings().hasEnough(cost)) {
											acct.getHoldings().subtract(cost);
											player.sendMessage(ChatColor.RED.toString()+"You have been charged "+iConomy.format(cost)+'.');
										}
									}
									return true;
								}
								player.sendMessage("You don't have permission to set your name color.");
								return true;
							}
						}
						if (args.length == 2) {
							// /colorme <name> <color>
							if (player.isOp() || (!player.isOp() && !config.getBoolean("OP") && args[0].equalsIgnoreCase(player.getName().toLowerCase()))) {
								// sender is OP *or* OP=false *and* setting own name
								setColor(args[0], args[1]);
								if (iconomy != null && args[0].equalsIgnoreCase(player.getName())) {
									double cost = config.getDouble("cost");
									Account acct = iConomy.getAccount(player.getName());
									if (cost > 0 && acct.getHoldings().hasEnough(cost)) {
										acct.getHoldings().subtract(cost);
										player.sendMessage(ChatColor.RED.toString()+"You have been charged "+iConomy.format(cost)+'.');
									}
								}
								return true;
							}
							return true;
						}
					}
				}
				if (sender instanceof ConsoleCommandSender) {
					// /colorme <name> [color]
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("list")) {
							// Display a list of colors for the user
							sender.sendMessage("Color List:");
							String color;
							String msg = "";
							for (int i = 0; i < ChatColor.values().length; i++) {
								color = ChatColor.getByCode(i).name();
								if (msg.length() == 0) {
									msg = ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+' ';
									continue;
								}
								msg += (i == ChatColor.values().length-1) ? ChatColor.valueOf(color)+color.toLowerCase().replace("_", "") : ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+' ';
								TextWrapper.wrapText(msg);
							}
							sender.sendMessage(msg);
							return true;
						}
						if (hasColor(args[0])) {
							removeColor(args[0]);
							sender.sendMessage("Removed color from "+args[0]);
							return true;
						}
						sender.sendMessage(args[0]+" doesn't have a colored name.");
						return true;
					}
					if (args.length == 2) {
						setColor(args[0], args[1]);
						sender.sendMessage("Colored "+args[0]+"'s name "+args[1]);
						return true;
					}
				}
				return false;
			}
		});
	}
	
	public String getColor(String name) {
		return (colors.keyExists(name.toLowerCase())) ? colors.getString(name.toLowerCase()) : "";
	}
	
	public boolean setColor(String name, String color) {
		String col;
		for (int i = 0; i <= 15; i++) {
			col = ChatColor.getByCode(i).name().toLowerCase().replace("_", "");
			if (color.equalsIgnoreCase(col)) {
				colors.setString(name.toLowerCase(), color);
				colors.save();
				return true;
			}
		}
		return false;
	}
	
	public boolean hasColor(String name) {
		return (colors.getString(name.toLowerCase()).length() > 0) ? true : false;
	}
	
	public boolean removeColor(String name) {
		if (colors.keyExists(name.toLowerCase())) {
			colors.setString(name.toLowerCase(), "");
			colors.save();
			return true;
		}
		return false;
	}
}
