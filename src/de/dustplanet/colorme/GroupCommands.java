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

	// Commands for groups
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
			if (sender.hasPermission("groups.create")) {
				String groupName = args[1].toLowerCase();
				// If the group exists, stop here
				if (Actions.existsGroup(groupName)) {
					message = ColorMe.localization.getString("group_already_existing");
					ColorMe.message(sender, null, message, groupName, null, null, null);
					return true;
				}
				// Create group and message
				Actions.createGroup(groupName);
				message = ColorMe.localization.getString("created_group");
				ColorMe.message(sender, null, message, groupName, null, null, null);
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Adds a member to an existing group
		else if (args.length > 2 && args[0].equalsIgnoreCase("add")) {
			if (sender.hasPermission("groups.addmember")) {
				String groupName = args[1].toLowerCase();
				String name = args[2].toLowerCase();
				// Stop if the group isn't existing
				if (!Actions.existsGroup(groupName)) {
					message = ColorMe.localization.getString("group_not_existing");
					ColorMe.message(sender, null, message, groupName, null, null, null);
					return true;
				}
				// Stop if the "name" is already a member
				if (Actions.isMember(groupName, name)) {
					message = ColorMe.localization.getString("already_a_member");
					ColorMe.message(sender, null, message, groupName, name, null, null);
					return true;
				}
				// Add and message
				Actions.addMember(groupName, name);
				message = ColorMe.localization.getString("added_member");
				ColorMe.message(sender, null, message, groupName, name, null, null);
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Removes a member from an existing group
		else if (args.length > 2 && args[0].equalsIgnoreCase("remove")) {
			if (sender.hasPermission("groups.removemember")) {
				String groupName = args[1].toLowerCase();
				String name = args[2].toLowerCase();
				// Stop if the group isn't existing
				if (!Actions.existsGroup(groupName)) {
					message = ColorMe.localization.getString("group_not_existing");
					ColorMe.message(sender, null, message, groupName, null, null, null);
					return true;
				}
				// Stop if the "name" is not a member
				if (!Actions.isMember(groupName, name)) {
					message = ColorMe.localization.getString("not_a_member");
					ColorMe.message(sender, null, message, groupName, name, null, null);
					return true;
				}
				// Remove the member and message
				Actions.removeMember(groupName, name);
				message = ColorMe.localization.getString("removed_member");
				ColorMe.message(sender, null, message, groupName, name, null, null);
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Deletes a group
		else if (args.length > 1 && args[0].equalsIgnoreCase("delete")) {
			if (sender.hasPermission("groups.delete")) {
				String groupName = args[1].toLowerCase();
				// Stop if the group isn't existing
				if (!Actions.existsGroup(groupName)) {
					message = ColorMe.localization.getString("group_not_existing");
					ColorMe.message(sender, null, message, groupName, null, null, null);
					return true;
				}
				// Delete the group and message
				Actions.deleteGroup(groupName);
				message = ColorMe.localization.getString("deleted_group");
				ColorMe.message(sender, null, message, groupName, null, null, null);
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Sets either a color, prefix or suffix for a group
		else if (args.length > 3 && args[0].equalsIgnoreCase("set")) {
			if (sender.hasPermission("groups.set")) {
				String groupName = args[1].toLowerCase(), part = args[2].toLowerCase(), value = args[3].toLowerCase();
				if (args.length > 4) world = args[4].toLowerCase();
				// Check if the wrong part is included
				if (!part.equalsIgnoreCase("colors") && !part.equalsIgnoreCase("prefix") && !part.equalsIgnoreCase("suffix")) {
					message = ColorMe.localization.getString("unrecognized_part");
					ColorMe.message(sender, null, message, part, null, null, null);
					return true;
				}
				// Check if the group exists
				if (!Actions.existsGroup(groupName)) {
					message = ColorMe.localization.getString("group_not_existing");
					ColorMe.message(sender, null, message, groupName, null, null, null);
					return true;
				}
				// Sets the group and messages
				Actions.setGroup(groupName, value, world, part);
				message = ColorMe.localization.getString("group_set_value");
				ColorMe.message(sender, null, message, groupName, world, value, null);
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Lists the current members of a group
		else if (args.length > 1 && args[0].equalsIgnoreCase("members")) {
			if (sender.hasPermission("groups.members")) {
				String groupName = args[1].toLowerCase();
				// Stop if the group isn't existing
				if (!Actions.existsGroup(groupName)) {
					message = ColorMe.localization.getString("group_not_existing");
					ColorMe.message(sender, null, message, groupName, null, null, null);
					return true;
				}
				// Get the lit and replace the "[" and "]". If the list is empty, say no members
				message = ColorMe.localization.getString("memberlist");
				ColorMe.message(sender, null, message, groupName, null, null, null);
				List<String> list = Actions.listMembers(groupName);
				String msg = list.toString();
				if (msg.equalsIgnoreCase("[]")) msg = Actions.replaceThings(ColorMe.localization.getString("no_members"));
				else msg = msg.substring(1, (list.size() - 1));
				sender.sendMessage(msg);
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Lists all current groups
		else if (args.length > 0 & args[0].equalsIgnoreCase("list")) {
			if (sender.hasPermission("groups.grouplist")) {
				message = ColorMe.localization.getString("grouplist");
				ColorMe.message(sender, null, message, null, null, null, null);
				String groups = "";
				// Iterate through the groups
				for (String key : ColorMe.group.getKeys(false)) {
					groups += key + ", ";
				}
				// If groups is still empty change message, else replace last ","
				if (groups.equalsIgnoreCase("")) groups = Actions.replaceThings(ColorMe.localization.getString("no_groups"));
				else groups = groups.substring(0, (groups.length() -2));
				sender.sendMessage(groups);
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Gets a color, prefix or suffix from a group
		else if (args.length > 2 && args[0].equalsIgnoreCase("get")) {
			if (sender.hasPermission("group.get")) {
				String groupName = args[1], part = args[2];
				if (args.length > 3) world = args[3];
				// Stop if the group isn't existing
				if (!Actions.existsGroup(groupName)) {
					message = ColorMe.localization.getString("group_not_existing");
					ColorMe.message(sender, null, message, groupName, null, null, null);
					return true;
				}
				// Check if the wrong part is included
				if (!part.equalsIgnoreCase("colors") && !part.equalsIgnoreCase("prefix") && !part.equalsIgnoreCase("suffix")) {
					message = ColorMe.localization.getString("unrecognized_part");
					ColorMe.message(sender, null, message, part, null, null, null);
					return true;
				}
				// If the group has nothing
				if (!Actions.hasGroup(groupName, world, part)) {
					message = ColorMe.localization.getString("group_has_nothing");
					ColorMe.message(sender, null, message, groupName, world, null, null);
					return true;
				}
				// Get the string and message
				String result = Actions.getGroup(groupName, world, part);
				message = ColorMe.localization.getString("group_get_value");
				ColorMe.message(sender, null, message, result, world, null, null);
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		return false;
	}
}