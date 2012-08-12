package de.dustplanet.colorme;

import java.util.List;

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
		String message, world = "default";
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
				if (Actions.existsGroup(groupName)) {
					sender.sendMessage("Already existing!");
					return true;
				}
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
				if (!Actions.existsGroup(groupName)) {
					sender.sendMessage("Not existing!");
					return true;
				}
				if (Actions.isMember(groupName, name)) {
					sender.sendMessage("Already a member");
					return true;
				}
				Actions.addMember(groupName, name);
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
				if (!Actions.existsGroup(groupName)) {
					sender.sendMessage("Not existing!");
					return true;
				}
				if (!Actions.isMember(groupName, name)) {
					sender.sendMessage("Not a member");
					return true;
				}
				Actions.removeMember(groupName, name);
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
				if (!Actions.existsGroup(groupName)) {
					sender.sendMessage("Not existing!");
					return true;
				}
				Actions.deleteGroup(groupName);
				return true;
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		else if (args.length > 1 && args[0].equalsIgnoreCase("set")) {
			String groupName = args[1], part = args[2], value = args[3];
			world = args[4];
			if (!part.equalsIgnoreCase("colors") && !part.equalsIgnoreCase("prefix") && !part.equalsIgnoreCase("suffix")) {
				sender.sendMessage("wrong part!");
				return true;
			}
			if (!Actions.existsGroup(groupName)) {
				sender.sendMessage("Not existing!");
				return true;
			}
			Actions.setGroup(groupName, value, world, part);
			return true;
		}
		else if (args.length > 1 && args[0].equalsIgnoreCase("members")) {
			String groupName = args[1];
			List<String> list = Actions.listMembers(groupName);
			sender.sendMessage(list.toString());
		}
		else if (args.length > 1 && args[0].equalsIgnoreCase("get")) {
			String groupName = args[1], part = args[2];
			world = args[3];
			if (!part.equalsIgnoreCase("colors") && !part.equalsIgnoreCase("prefix") && !part.equalsIgnoreCase("suffix")) {
				sender.sendMessage("wrong part!");
				return true;
			}
			if (!Actions.existsGroup(groupName)) {
				sender.sendMessage("Not existing!");
				return true;
			}
			if (!Actions.hasGroup(groupName, world, part)) {
				sender.sendMessage("Doesn't have anythign");
				return true;
			}
			sender.sendMessage("yeah");
			String temp = Actions.getGroup(groupName, world, part);
			sender.sendMessage(temp);
			return true;
		}
		return false;
	}
}
