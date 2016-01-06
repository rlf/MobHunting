package au.com.mineauz.MobHunting.compatability;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.CitizensPlugin;
import net.citizensnpcs.api.event.CitizensDisableEvent;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.event.PlayerCreateNPCEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.MobPlugins;
import au.com.mineauz.MobHunting.MobRewardData;

public class CitizensCompat implements Listener {

	private static boolean supported = false;
	private static CitizensPlugin mPlugin;
	private static HashMap<String, MobRewardData> mNPCData = new HashMap<String, MobRewardData>();
	private File file = new File(MobHunting.instance.getDataFolder(),
			"citizens-rewards.yml");
	private YamlConfiguration config = new YamlConfiguration();

	public CitizensCompat() {
		if (isDisabledInConfig()) {
			MobHunting.instance.getLogger().info(
					"Compatability with Citizens2 is disabled in config.yml");
		} else {
			mPlugin = (CitizensPlugin) Bukkit.getPluginManager().getPlugin(
					"Citizens");

			// Register MobHunting Trait with Citizens.
			net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(
					net.citizensnpcs.api.trait.TraitInfo.create(
							MobHuntingTrait.class).withName("MasterMobHunter"));

			Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
			Bukkit.getPluginManager().registerEvents(new MobHuntingTrait(), MobHunting.instance);

			MobHunting.instance.getLogger().info(
					"Enabling compatability with Citizens ("
							+ getCitizensPlugin().getDescription().getVersion()
							+ ")");
			supported = true;

			loadCitizensData();
			saveCitizensData();

		}
	}

	// **************************************************************************
	// LOAD & SAVE
	// **************************************************************************
	public void loadCitizensData() {
		try {
			if (!file.exists())
				return;

			config.load(file);
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config
						.getConfigurationSection(key);
				MobRewardData npc = new MobRewardData();
				npc.read(section);
				mNPCData.put(key, npc);
			}
			MobHunting.debug("Loaded %s NPC's", mNPCData.size());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void loadCitizensData(String key) {
		try {
			if (!file.exists())
				return;

			config.load(file);
			ConfigurationSection section = config.getConfigurationSection(key);
			MobRewardData npc = new MobRewardData();
			npc.read(section);
			mNPCData.put(key, npc);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void saveCitizensData() {
		try {
			config.options()
					.header("This a extra MobHunting config data for the Citizens/NPC's on your server.");

			if (mNPCData.size() > 0) {

				int n = 0;
				for (String key : mNPCData.keySet()) {
					ConfigurationSection section = config.createSection(key);
					mNPCData.get(key).save(section);
					n++;
				}

				if (n != 0) {
					MobHunting.debug("Saving Mobhunting extra NPC data.");
					config.save(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveCitizensData(String key) {
		try {
			if (mNPCData.containsKey(key)) {
				ConfigurationSection section = config.createSection(key);
				mNPCData.get(key).save(section);
				MobHunting.debug("Saving Mobhunting extra NPC data.");
				config.save(file);
			} else {
				MobHunting.debug("ERROR! Mob ID (%s) is not found in mNPCData",
						key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	public CitizensPlugin getCitizensPlugin() {
		return mPlugin;
	}

	public static boolean isCitizensSupported() {
		return supported;
	}

	public static boolean isNPC(Entity entity) {
		return CitizensAPI.getNPCRegistry().isNPC(entity);
	}

	public static int getNPCId(Entity entity) {
		return CitizensAPI.getNPCRegistry().getNPC(entity).getId();
	}

	public static String getNPCName(Entity entity) {
		return CitizensAPI.getNPCRegistry().getNPC(entity).getName();
	}

	public static NPC getNPC(Entity entity) {
		return CitizensAPI.getNPCRegistry().getNPC(entity);
	}

	public static boolean isSentry(Entity entity) {
		if (CitizensAPI.getNPCRegistry().isNPC(entity))
			return CitizensAPI
					.getNPCRegistry()
					.getNPC(entity)
					.hasTrait(
							CitizensAPI.getTraitFactory().getTraitClass(
									"Sentry"));
		else
			return false;
	}

	public static boolean isMasterMobHunter(Entity entity) {
		if (CitizensAPI.getNPCRegistry().isNPC(entity))
			return CitizensAPI
					.getNPCRegistry()
					.getNPC(entity)
					.hasTrait(
							CitizensAPI.getTraitFactory().getTraitClass(
									"MasterMobHunter"));
		else
			return false;
	}

	public static HashMap<String, MobRewardData> getNPCData() {
		return mNPCData;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.config().disableIntegrationCitizens;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.config().disableIntegrationCitizens;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCDeathEvent(NPCDeathEvent event) {

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCDamageEvent(NPCDamageEvent event) {

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCDamageByEntityEvent(NPCDamageByEntityEvent event) {

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onCitizensEnableEvent(CitizensEnableEvent event) {

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onCitizensDisableEvent(CitizensDisableEvent event) {
		// MobHunting.debug("CitizensDisableEvent - saving");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCSpawnEvent(NPCSpawnEvent event) {

		for (Iterator<NPCRegistry> registry = CitizensAPI.getNPCRegistries()
				.iterator(); registry.hasNext();) {
			NPCRegistry n = registry.next();
			for (Iterator<NPC> npcList = n.iterator(); npcList.hasNext();) {
				NPC npc = npcList.next();
				// MobHunting.debug("NPC=%s", npc.getFullName());

				if (mNPCData != null
						&& !mNPCData.containsKey(String.valueOf(npc.getId()))) {
					if (isSentry(npc.getEntity())) {
						MobHunting.debug("New NPC found=%s,%s", npc.getId(),
								npc.getFullName());
						mNPCData.put(String.valueOf(npc.getId()),
								new MobRewardData(
										MobPlugins.MobPluginNames.Citizens,
										"npc", npc.getFullName(), "10",
										"give {player} iron_sword 1",
										"You got an Iron sword.", 100, 100));
						saveCitizensData(String.valueOf(npc.getId()));
					}
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCDespawnEvent(NPCDespawnEvent event) {
		// MobHunting.debug("NPCDespawnEvent");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerCreateNPCEvent(PlayerCreateNPCEvent event) {
		// MobHunting.debug("NPCCreateNPCEvent");
	}

}
