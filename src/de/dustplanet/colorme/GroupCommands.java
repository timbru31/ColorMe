package de.dustplanet.colorme;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GroupCommands implements CommandExecutor {

	ColorMe plugin;
	public GroupCommands(ColorMe instance) {
		plugin = instance;
	}

	// Commands for prefixing
	public boolean onCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
		String message, globalColor, globalPrefix, target, senderName, pluginPart = "groups", world = "default";
		// Reloads the configs
		if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("groups.reload")) {
				Actions.reload(sender);
				return true;
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		// Stop here if own group system is unwanted
		if (!ColorMe.groups || !ColorMe.ownSystem) {
			message = ColorMe.localization.getString("part_disabled");
			ColorMe.message(sender, null, message, null, null, null, null);
			return true;
		}
		// Displays the help
		if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
			Actions.help(sender, "group");
			return true;
		}
		// Create a group
		if (args.length > 1 && args[0].equalsIgnoreCase("create")) {
			if (sender.hasPermission(/*TODO Permission*/"")) {
				String groupName = args[1];
				Actions.createGroup(groupName);
				return true;
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		else if (args.length > 2 && args[0].equalsIgnoreCase("add")) {
			if (sender.hasPermission("")) {
				String groupName = args[1];
				String name = args[2];
				Actions.addMember(groupName, name);
				//TODO Check ob es die gibt
				//TODO Check ob Spieler bereits Memeber ist
				return true;
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		else if (args.length > 2 && args[0].equalsIgnoreCase("remove")) {
			if (sender.hasPermission("")) {
				String groupName = args[1];
				String name = args[2];
				Actions.removeMember(groupName, name);
				//TODO Check ob es die gibt
				return true;
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		else if (args.length > 1 && args[0].equalsIgnoreCase("delete")) {
			if (sender.hasPermission("")) {
				String groupName = args[1];
				//Actions.deleteGroup(groupName);
				//TODO Check ob es die gibt
				return true;
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		else if (args.length > 1 && args[0].equalsIgnoreCase("set")) {
		}
		else if (args.length > 1 && args[0].equalsIgnoreCase("members")) {
		}
		else if (args.length > 1 && args[0].equalsIgnoreCase("get")) {
		}
		return false;
	}
}
