package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;
import au.com.mineauz.MobHunting.MobHunting;

public class DisguiseCraftCompat implements Listener {

	// API
	// http://dev.bukkit.org/bukkit-plugins/disguisecraft/pages/developer-api/

	private static Plugin mPlugin;
	private static boolean supported = false;
	private static DisguiseCraftAPI dcAPI;

	public DisguiseCraftCompat() {
		if (isDisabledInConfig()) {
			MobHunting.instance
					.getLogger()
					.info("Compatability with DisguiseCraft is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getServer().getPluginManager()
					.getPlugin("DisguiseCraft");
			dcAPI = DisguiseCraft.getAPI();

			Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);

			MobHunting.instance.getLogger().info(
					"Enabling compatability with DisguiseCraft ("
							+ getDisguiseCraft().getDescription().getVersion()
							+ ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Plugin getDisguiseCraft() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.config().disableIntegrationDisguiseCraft;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.config().disableIntegrationDisguiseCraft;
	}

	public static boolean isDisguised(Player player) {
		return dcAPI.isDisguised(player);
	}

	public static Disguise getDisguise(Entity entiry) {
		return dcAPI.getDisguise((Player) entiry);
	}

	public static void disguisePlayer(Player player, Disguise disguise) {
		dcAPI.disguisePlayer(player, disguise);
	}

	public static void changePlayerDisguise(Player player, Disguise newDisguise) {
		dcAPI.changePlayerDisguise(player, newDisguise);
	}

	public static void undisguisePlayer(Player player) {
		dcAPI.undisguisePlayer(player);
	}
	
	public static boolean isAggresiveDisguise(Entity entity) {
		Disguise d = getDisguise(entity);
		if (d.type.equals(DisguiseType.Zombie))
			return true;
		else
			return false;
	}
	
	public static boolean isPlayerDisguise(Player player) {
		Disguise d = getDisguise(player);
		if (d.type.equals(DisguiseType.Player))
			return true;
		else
			return false;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

}
