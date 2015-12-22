package au.com.mineauz.MobHunting.compatability;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Plugin getDisguiseCraft() {
		return mPlugin;
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

	// public static void changePlayerDisguise(Player player, Disguise
	// newDisguise) {
	// dcAPI.changePlayerDisguise(player, newDisguise);
	// }

	public static void undisguisePlayer(Entity entity) {
		if (entity instanceof Player)
			dcAPI.undisguisePlayer((Player) entity);
	}

	private static final DisguiseType aggresiveList[] = { DisguiseType.Blaze,
			DisguiseType.CaveSpider, DisguiseType.Creeper,
			DisguiseType.EnderDragon, DisguiseType.Enderman,
			DisguiseType.Endermite, DisguiseType.Ghast, DisguiseType.Giant,
			DisguiseType.Guardian, DisguiseType.PigZombie,
			DisguiseType.Skeleton, DisguiseType.Slime, DisguiseType.Spider,
			DisguiseType.Witch, DisguiseType.Wither, DisguiseType.Zombie };
	public static final Set<DisguiseType> aggresiveMobs = new HashSet<DisguiseType>(
			Arrays.asList(aggresiveList));

	private static final DisguiseType passiveList[] = { DisguiseType.Bat,
			DisguiseType.Chicken, DisguiseType.Cow, DisguiseType.Horse,
			DisguiseType.IronGolem, DisguiseType.MagmaCube,
			DisguiseType.MushroomCow, DisguiseType.Ocelot, DisguiseType.Pig,
			DisguiseType.Rabbit, DisguiseType.Sheep, DisguiseType.Silverfish,
			DisguiseType.Snowman, DisguiseType.Squid };
	public static final Set<DisguiseType> passiveMobs = new HashSet<DisguiseType>(
			Arrays.asList(passiveList));

	private static final DisguiseType otherList[] = { DisguiseType.Boat,
			DisguiseType.EnderCrystal, DisguiseType.FallingBlock,
			DisguiseType.Minecart, DisguiseType.Player, DisguiseType.Villager,
			DisguiseType.Wolf };
	public static final Set<DisguiseType> otherDisguiseTypes = new HashSet<DisguiseType>(
			Arrays.asList(otherList));

	public static boolean isAggresiveDisguise(Entity entity) {
		Disguise d = getDisguise(entity);
		if (aggresiveMobs.contains(d.type))
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
