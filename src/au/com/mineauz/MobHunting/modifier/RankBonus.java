package au.com.mineauz.MobHunting.modifier;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class RankBonus implements IModifier {

	@Override
	public String getName() {
		return ChatColor.GRAY + Messages.getString("bonus.rank.name");
	}

	@Override
	public double getMultiplier(LivingEntity deadEntity, Player killer,
			HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		Set<String> ranks = MobHunting.config().rankMultiplier.keySet();
		for (String rank : ranks) {
			if (killer.hasPermission(rank)) {
					return Double.valueOf(MobHunting.config().rankMultiplier.get(rank));
					}
			}
		return 1;
	}

	@Override
	public boolean doesApply(LivingEntity deadEntity, Player killer,
			HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		Set<String> ranks = MobHunting.config().rankMultiplier.keySet();
		for (String rank : ranks) {
			if (killer.hasPermission(rank)) {
				String mul = MobHunting.config().rankMultiplier.get(rank);
				if (mul != null) {
					MobHunting
							.debug("Reward is multiplied by rankMultiplier permissionNode=%s multiplier=%s",
									rank, mul);
					return true;
				} else {
					MobHunting.instance
							.getServer()
							.getLogger()
							.severe(Messages
									.getString("[MobHunting] Missing Rank Multiplier in config.yml:"
											+ mul));
				}
			}
		}
		return false;
	}
}
