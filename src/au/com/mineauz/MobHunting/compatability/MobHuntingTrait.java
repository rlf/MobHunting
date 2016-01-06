package au.com.mineauz.MobHunting.compatability;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import au.com.mineauz.MobHunting.MobHunting;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

public class MobHuntingTrait extends Trait implements Listener {
	// http://wiki.citizensnpcs.co/API

	// This is your trait that will be applied to a npc using the /trait
	// mytraitname command. Each NPC gets its own instance of this class.
	// the Trait class has a reference to the attached NPC class through the
	// protected field 'npc' or getNPC().
	// The Trait class also implements Listener so you can add EventHandlers
	// directly to your trait.

	MobHunting plugin = null;

	public MobHuntingTrait() {
		super("MasterMobHunter");
		plugin = MobHunting.instance;
		//MobHunting.debug("MobHuntingTrait()");
		//Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	boolean SomeSetting = false;

	// see the 'Persistence API' section
	@Persist("mysettingname")
	boolean automaticallyPersistedSetting = false;

	// Here you should load up any values you have previously saved (optional).
	// This does NOT get called when applying the trait for the first time, only
	// loading onto an existing npc at server start.
	// This is called AFTER onAttach so you can load defaults in onAttach and
	// they will be overridden here.
	// This is called BEFORE onSpawn, npc.getBukkitEntity() will return null.
	public void load(DataKey key) {
		SomeSetting = key.getBoolean("SomeSetting", false);
	}

	// Save settings for this NPC (optional). These values will be persisted to
	// the Citizens saves file
	public void save(DataKey key) {
		key.setBoolean("SomeSetting", SomeSetting);
	}

	// An example event handler. All traits will be registered automatically as
	// Bukkit Listeners.


	// Called every tick
	@Override
	public void run() {
		// MobHunting.debug("MobHuntingTrait is running each tick");
	}

	// Run code when your trait is attached to a NPC.
	// This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
	// This would be a good place to load configurable defaults for new NPCs.
	@Override
	public void onAttach() {
		plugin.getServer().getLogger()
				.info(npc.getName() + " has been assigned MasterMobHunter");
	}

	// Run code when the NPC is despawned. This is called before the entity
	// actually despawns so npc.getBukkitEntity() is still valid.
	@Override
	public void onDespawn() {
		MobHunting.debug("NPC despawned");
	}

	// Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be
	// null until this method is called.
	// This is called AFTER onAttach and AFTER Load when the server is started.
	@Override
	public void onSpawn() {
		MobHunting.debug("NPC spawned");
	}

	// run code when the NPC is removed. Use this to tear down any repeating
	// tasks.
	@Override
	public void onRemove() {
		MobHunting.debug("NPC removed");
	}
	
	@EventHandler
	public void onClick(NPCLeftClickEvent event) {
		// Handle a click on a NPC. The event has a getNPC() method.
		// Be sure to check event.getNPC() == this.getNPC() so you only handle
		// clicks on this NPC!
		if (event.getNPC() == this.getNPC())
			event.getClicker().sendMessage(
					"YYYYYou clicked a MasterMobHunter ("
							+ event.getNPC().getName() + ")");
		MobHunting.debug("YYYYYou clicked the NPC clicker="
				+ event.getClicker().getName());
		NPC npc = event.getNPC();
		npc.setName("Gabriel333");
	}

}
