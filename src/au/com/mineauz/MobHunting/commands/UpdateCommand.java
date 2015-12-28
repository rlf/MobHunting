package au.com.mineauz.MobHunting.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.update.UpdateHelper;
import au.com.mineauz.MobHunting.update.UpdateStatus;

public class UpdateCommand implements ICommand {
	@Override
	public String getName() {
		return "update";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "mobhunting.update";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { label };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.update.description");
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		if (UpdateHelper.getUpdateAvailable() == UpdateStatus.AVAILABLE) {
			if (UpdateHelper.downloadAndUpdateJar()) {
				sender.sendMessage(ChatColor.GREEN
						+ Messages
								.getString("mobhunting.commands.update.complete"));
			} else {
				sender.sendMessage(ChatColor.GREEN
						+ Messages
								.getString("mobhunting.commands.update.could-not-update"));
			}
		} else if (UpdateHelper.getUpdateAvailable() == UpdateStatus.RESTART_NEEDED) {
			sender.sendMessage(ChatColor.GREEN
					+ Messages.getString("mobhunting.commands.update.complete"));
		} else {
			UpdateHelper.pluginUpdateCheck(sender, true, false);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label,
			String[] args) {
		return null;
	}

}
