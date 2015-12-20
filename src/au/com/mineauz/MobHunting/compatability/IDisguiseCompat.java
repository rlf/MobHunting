package au.com.mineauz.MobHunting.compatability;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import au.com.mineauz.MobHunting.MobHunting;

public class IDisguiseCompat implements Listener {

	// API
	// http://dev.bukkit.org/bukkit-plugins/idisguise/pages/api/

	private static Plugin mPlugin;
	private static boolean supported = false;
	private DisguiseAPI api;

	public IDisguiseCompat() {
		if (isDisabledInConfig()) {
			MobHunting.instance.getLogger().info(
					"Compatability with iDisguise is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getServer().getPluginManager()
					.getPlugin("iDisguise");
			api = MobHunting.instance.getServer().getServicesManager()
					.getRegistration(DisguiseAPI.class).getProvider();

			Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);

			MobHunting.instance.getLogger().info(
					"Enabling compatability with iDisguise ("
							+ getiDisguise().getDescription().getVersion()
							+ ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Plugin getiDisguise() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.config().disableIntegrationIDisguise;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.config().disableIntegrationIDisguise;
	}

	public static boolean isDisguised(Entity entity) {
		return DisguiseAPI.isDisguised(entity);
	}

	public static Disguise getDisguise(Player player) {
		return DisguiseAPI.getDisguise(player);
	}

	public static boolean isAggresiveDisguise(Player player) {
		Disguise d = getDisguise(player);
		if (d.getType().equals(DisguiseType.ZOMBIE))
			return true;
		else
			return false;
	}

	public static boolean isPlayerDisguise(Player player) {
		Disguise d = getDisguise(player);
		if (d.getType().equals(DisguiseType.PLAYER))
			return true;
		else
			return false;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

}
