package au.com.mineauz.MobHunting.compatability;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import au.com.mineauz.MobHunting.MobHunting;

public class WorldGuardCompat implements Listener {

	private static boolean supported = false;
	private static WorldGuardPlugin mPlugin;
	private static HashMap<String, String> mobHuntingRegions = new HashMap<String, String>();
	private static RegionContainer regionContainer;
	private static final StateFlag MOBHUNTINGFLAG = new StateFlag("MobHunting",
			true);
	private final static File configFile = new File(
			MobHunting.instance.getDataFolder(), "worldguard_regions.yml");

	public WorldGuardCompat() {
		if (isDisabledInConfig()) {
			MobHunting.instance.getLogger().info(
					"Compatability with WorldGuard is disabled in config.yml");
		} else {
			mPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin(
					"WorldGuard");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);

			if (getWorldGuardPlugin() != null)
				if (getWorldGuardPlugin().getDescription().getVersion()
						.startsWith("5")) {
					MobHunting.instance
							.getLogger()
							.warning(
									"Your current version of WorldGuard ("
											+ getWorldGuardPlugin()
													.getDescription()
													.getVersion()
											+ ") is not supported by MobHunting. Mobhunting does only support 6.0+");
				} else {

					MobHunting.instance.getLogger().info(
							"Enabling compatability with WorldGuard ("
									+ getWorldGuardPlugin().getDescription()
											.getVersion() + ")");
					supported = true;

					// adding customflag
					try {
						Field field = DefaultFlag.class
								.getDeclaredField("flagsList");
						Field modifiersField = Field.class
								.getDeclaredField("modifiers");
						modifiersField.setAccessible(true);
						modifiersField.setInt(field, field.getModifiers()
								& ~Modifier.FINAL);
						field.setAccessible(true);
						List<Flag<?>> flags = new ArrayList<Flag<?>>(
								Arrays.asList(DefaultFlag.getFlags()));

						flags.add(MOBHUNTINGFLAG);
						field.set(null, flags.toArray(new Flag[flags.size()]));
						WorldGuardPlugin.class
								.cast(Bukkit.getPluginManager().getPlugin(
										"WorldGuard")).getGlobalStateManager()
								.load();
						regionContainer = WorldGuardCompat
								.getWorldGuardPlugin().getRegionContainer();
						loadMobHuntingRegions();
					} catch (Exception e) {
						e.printStackTrace();
					}
					MobHunting.instance.isWorldGuardLoaded = true;

				}
			else
				MobHunting
						.debug("WORLDGUARD IS NOT LOADED!!!!!!!!!!!!!!!!!!!!!");
		}
	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	public static WorldGuardPlugin getWorldGuardPlugin() {
		return mPlugin;
	}

	@SuppressWarnings("unused")
	private static boolean isWorldGuardSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.config().disableIntegrationWorldGuard;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.config().disableIntegrationWorldGuard;
	}

	public static StateFlag getMobHuntingFlag() {
		return MOBHUNTINGFLAG;
	}

	public static RegionContainer getRegionContainer() {
		return regionContainer;
	}

	public static LocalPlayer getLocalPlayer(Player player) {
		return getWorldGuardPlugin().wrapPlayer(player);
	}

	// *******************************************************************
	// SAVE / LOAD
	// *******************************************************************
	public static void saveMobHuntingRegions() {
		try {
			YamlConfiguration config = new YamlConfiguration();
			config.options()
					.header("This file is automatically generated. Do NOT edit this file manually or you risk losing data.");
			config.createSection("regions", mobHuntingRegions);
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadMobHuntingRegions() {
		if (!configFile.exists())
			return;
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(configFile);

			ConfigurationSection regionSection = config
					.getConfigurationSection("regions");

			Set<String> regions = regionSection.getKeys(true);
			if (regions == null) {
				return;
			}
			mobHuntingRegions.clear();
			for (String region : regions) {
				String flagValue = regionSection.getString(region);
				mobHuntingRegions.put(region, flagValue);
				for (World world : Bukkit.getWorlds()) {
					Iterator<Entry<String, ProtectedRegion>> i = regionContainer
							.get(world).getRegions().entrySet().iterator();
					while (i.hasNext()) {
						Entry<String, ProtectedRegion> s = i.next();
						if (s.getKey().equalsIgnoreCase(region)) {
							setCurrentRegionFlag(null, s.getValue(),
									MOBHUNTINGFLAG, flagValue);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	// *******************************************************************
	// SET / REMOVE FLAG
	// *******************************************************************
	public static boolean setCurrentRegionFlag(CommandSender sender,
			ProtectedRegion region, StateFlag stateFlag, String flagstate) {
		try {
			region.setFlag(getMobHuntingFlag(), stateFlag.parseInput(
					getWorldGuardPlugin(), sender, flagstate));
			mobHuntingRegions.put(region.getId(), flagstate);
			saveMobHuntingRegions();
			if (sender != null)
				sender.sendMessage(ChatColor.YELLOW
						+ "Region flag MobHunting set on '" + region.getId()
						+ "' to '" + flagstate + "'");
			String flagstring = "";
			Iterator<Entry<Flag<?>, Object>> i = region.getFlags().entrySet()
					.iterator();
			while (i.hasNext()) {
				Entry<Flag<?>, Object> s = i.next();
				flagstring = flagstring + s.getKey().getName() + ": "
						+ s.getValue();
				if (i.hasNext())
					flagstring = flagstring + ",";
			}
			if (sender != null)
				sender.sendMessage(ChatColor.GRAY + "(Current flags: "
						+ flagstring + ")");

		} catch (InvalidFlagFormat e) {
			e.printStackTrace();
		}
		return true;
	}

	public static boolean removeCurrentRegionFlag(CommandSender sender,
			ProtectedRegion region, StateFlag stateFlag) {
		region.setFlag(stateFlag, null);
		mobHuntingRegions.remove(region.getId());
		saveMobHuntingRegions();
		if (sender != null)
			sender.sendMessage(ChatColor.YELLOW + "Region flag '"
					+ stateFlag.getName() + "' removed from region '"
					+ region.getId() + "'");
		return true;
	}

	// *******************************************************************
	// EVENTS
	// *******************************************************************
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(final PlayerMoveEvent event) {
		if (isDisabledInConfig() || !supported)
			return;
		Player player = event.getPlayer();
		ApplicableRegionSet set = WorldGuardCompat.getWorldGuardPlugin()
				.getRegionManager(player.getWorld())
				.getApplicableRegions(player.getLocation());
		if (set.size() > 0) {
			Iterator<ProtectedRegion> i = set.getRegions().iterator();
			while (i.hasNext()) {
				ProtectedRegion pr = i.next();
				if (pr.getFlags().containsKey(MOBHUNTINGFLAG)) {
					if (!mobHuntingRegions.containsKey(pr.getId())
							|| pr.getFlag(MOBHUNTINGFLAG).name() != mobHuntingRegions
									.get(pr.getId())) {
						MobHunting
								.debug("Found unregistered flag or flag with changed State found in region '%s' with value %s",
										pr.getId(), pr.getFlag(MOBHUNTINGFLAG)
												.name());
						mobHuntingRegions.put(pr.getId(),
								pr.getFlag(MOBHUNTINGFLAG).name());
						saveMobHuntingRegions();
					}
				}
			}
		}
	}
}
