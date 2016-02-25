package au.com.mineauz.MobHunting.commands;

import java.util.ArrayList;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.compatability.CitizensCompat;
import au.com.mineauz.MobHunting.compatability.CompatibilityManager;

public class NpcCommand implements ICommand, Listener {

	public NpcCommand() {
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
	}

	// Used case (???)
	// /mh npc create <stat type> <period> <number>
	// /mh npc remove
	// /mh npc update
	// /mh npc select

	@Override
	public String getName() {
		return "npc";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "citizens" };
	}

	@Override
	public String getPermission() {
		return "mobhunting.npc";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {
				label + ChatColor.GOLD + " create" + ChatColor.RED
						+ " <stattype> <period> <number>" + ChatColor.WHITE +" -:to create a MasterMobHunter NPC",
				label + ChatColor.GOLD + " remove"+ ChatColor.WHITE +" -:to remove the MasterMobHunter",
				label + ChatColor.GOLD + " update" + ChatColor.WHITE +" -:to update the MasterMobHunter NPC"};
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.npc.description");
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label,
			String[] args) {

		ArrayList<String> items = new ArrayList<String>();
		if (CompatibilityManager.isPluginLoaded(CitizensCompat.class)) {
			if (args.length == 1) {
				items.add("create");
				items.add("remove");
				items.add("update");
				items.add("select");
				items.add("spawn");
				items.add("despawn");
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("create")) {
					StatType[] values = StatType.values();
					for (int i=0; i<values.length;i++){
						items.add(values[i].toString());
					}
				
				} 
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("create")) {
					items.add("alltime");
					items.add("year");
					items.add("month");
					items.add("week");
					items.add("day");
				}
			}
		}
		return items;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		if (args.length == 0)
			return false;
		Player p = (Player) sender;
		NPC npc;
		if (CompatibilityManager.isPluginLoaded(CitizensCompat.class)) {
			npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
			if (args.length == 1 && args[0].equalsIgnoreCase("remove")){
				npc.destroy();
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("update")){
				
			} else if (args.length == 1 && args[0].equalsIgnoreCase("select")){
				npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
				//Location loc =p.getEyeLocation();
				sender.sendMessage("NPC is "+npc.getName());
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("spawn")){
				npc.spawn(npc.getStoredLocation());
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("despawn")){
				npc.despawn();
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("create")){
				NPCRegistry registry = CitizensAPI.getNPCRegistry();
				npc = registry.createNPC(EntityType.PLAYER, "MasterMobHunter");
				npc.spawn(p.getEyeLocation());
				return true;
			}
			
			
		}
		return false;
	}
}
