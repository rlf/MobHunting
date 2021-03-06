package au.com.mineauz.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class FriendleFireBonus implements IModifier
{
	@Override
	public String getName()
	{
		return ChatColor.DARK_GREEN + Messages.getString("bonus.friendlyfire.name"); //$NON-NLS-1$
	}

	@Override
	public double getMultiplier( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause )
	{
		return MobHunting.config().bonusFriendlyFire;
	}

	@Override
	public boolean doesApply( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause )
	{
		if(lastDamageCause == null)
			return false;
		if(lastDamageCause.getDamager() instanceof Monster || (lastDamageCause.getDamager() instanceof Projectile && (((Projectile)lastDamageCause.getDamager()).getShooter() instanceof Monster || ((Projectile)lastDamageCause.getDamager()).getShooter() instanceof Ghast)))
			return true;
		return false;
	}

}
