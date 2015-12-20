package au.com.mineauz.MobHunting.compatability;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import au.com.mineauz.MobHunting.MobHunting;

public class LibsDisguisesCompat implements Listener {

	// API
	// https://www.spigotmc.org/wiki/lib-s-disguises/

	private static Plugin mPlugin;
	private static boolean supported = false;

	// private static DisguiseAPI api;

	public LibsDisguisesCompat() {
		if (isDisabledInConfig()) {
			MobHunting.instance
					.getLogger()
					.info("Compatability with LibsDisguises is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getServer().getPluginManager()
					.getPlugin("LibsDisguises");
			// api = DisguiseAPI.getAPI();

			Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);

			MobHunting.instance.getLogger().info(
					"Enabling compatability with LibsDisguises ("
							+ getLbsDisguises().getDescription().getVersion()
							+ ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Plugin getLbsDisguises() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.config().disableIntegrationLibsDisguises;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.config().disableIntegrationLibsDisguises;
	}

	public static boolean isDisguised(Player player) {
		return DisguiseAPI.isDisguised(player);
	}

	public static me.libraryaddict.disguise.disguisetypes.Disguise getDisguise(
			Entity entiry) {
		return DisguiseAPI.getDisguise(entiry);
	}

	public static void disguisePlayer(Player player,
			me.libraryaddict.disguise.disguisetypes.Disguise disguise) {
		DisguiseAPI.disguiseEntity(player, disguise);
	}

	public static boolean isAggresiveDisguise(Entity entity) {
		me.libraryaddict.disguise.disguisetypes.Disguise d = getDisguise(entity);
		if (d.getType().equals(DisguiseType.ZOMBIE))
			return true;
		else
			return false;
	}
	
	public static boolean isPlayerDisguise(Entity entity) {
		me.libraryaddict.disguise.disguisetypes.Disguise d = getDisguise(entity);
		if (d.getType().equals(DisguiseType.PLAYER))
			return true;
		else
			return false;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

	// DisguiseEvent

	// UndisguiseEvent

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDisguiseEvent(final DisguiseEvent event) {

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onUndisguiseEvent(final UndisguiseEvent event) {

	}
}
