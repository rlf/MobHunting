package au.com.mineauz.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class BrawlerBonus implements IModifier {

	@Override
	public String getName() {
		return ChatColor.LIGHT_PURPLE
				+ Messages.getString("bonus.brawler.name");
	}

	@Override
	public double getMultiplier(LivingEntity deadEntity, Player killer,
			HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		return MobHunting.config().bonusNoWeapon;
	}

	@Override
	public boolean doesApply(LivingEntity deadEntity, Player killer,
			HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		return !extraInfo.usedWeapon && !extraInfo.wolfAssist;
	}

}
