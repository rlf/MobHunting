package au.com.mineauz.MobHunting.storage;

import org.bukkit.OfflinePlayer;

import au.com.mineauz.MobHunting.MobHunting;

public class PlayerStore {
	private OfflinePlayer player;
	private boolean learning_mode;

	public PlayerStore(OfflinePlayer player, boolean learning_mode) {
		this.player = player;
		this.learning_mode = learning_mode;
	}

	public PlayerStore(OfflinePlayer player) {
		this.player = player;
		learning_mode = true;
	}

	/**
	 * @return the type
	 */
	public boolean getLearningMode() {
		return learning_mode;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(boolean learning_mode) {
		this.learning_mode = learning_mode;
	}

	/**
	 * @return the player
	 */
	public OfflinePlayer getPlayer() {
		if (player.getName().isEmpty())
			MobHunting.debug("PlayerStore-Playername for ID:%s was empty (%s)",
					player.getUniqueId(), player.getName());
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}

	@Override
	public String toString() {
		return String.format("PlayerStore: {player: %s Learning Mode: %s}",
				player.getName(), learning_mode);
	}
}
