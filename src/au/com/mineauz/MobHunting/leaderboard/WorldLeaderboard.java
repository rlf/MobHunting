package au.com.mineauz.MobHunting.leaderboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.material.Sign;
import org.bukkit.util.Vector;

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.storage.DataCallback;
import au.com.mineauz.MobHunting.storage.StatStore;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class WorldLeaderboard implements DataCallback<List<StatStore>> {
	private static String EMPTY_STRING = ""; 
	private Location mLocation;
	private BlockFace mFacing;

	private int mWidth;
	private int mHeight;

	private boolean mHorizontal;

	private TimePeriod[] mPeriod;
	private int mPeriodIndex = 0;
	private StatType[] mType;
	private int mTypeIndex = 0;

	private List<StatStore> mData;

	public WorldLeaderboard(Location location, BlockFace facing, int width,
			int height, boolean horizontal, StatType[] stat, TimePeriod[] period) {
		Validate.isTrue(facing == BlockFace.NORTH || facing == BlockFace.EAST
				|| facing == BlockFace.SOUTH || facing == BlockFace.WEST);

		mLocation = location;
		mFacing = facing;
		mWidth = width;
		mHeight = height;

		mHorizontal = horizontal;
		mType = stat;
		mPeriod = period;
		mPeriodIndex = 0;
		mTypeIndex = 0;
	}

	WorldLeaderboard() {
	}

	List<StatStore> getCurrentStats() {
		if (mData == null)
			return Collections.emptyList();
		return mData;
	}

	List<Block> getSignBlocks() {
		BlockFace horizontal;

		switch (mFacing) {
		case NORTH:
			horizontal = BlockFace.WEST;
			break;
		case SOUTH:
			horizontal = BlockFace.EAST;
			break;
		case WEST:
			horizontal = BlockFace.SOUTH;
			break;
		case EAST:
			horizontal = BlockFace.NORTH;
			break;
		default:
			throw new AssertionError("Invalid facing " + mFacing);
		}

		ArrayList<Block> blocks = new ArrayList<Block>();
		Location min = mLocation.clone();
		min.add(-horizontal.getModX() * (mWidth / 2), -1, -horizontal.getModZ()
				* (mWidth / 2));

		if (mHorizontal) {
			for (int y = 0; y < mHeight; ++y) {
				for (int x = 0; x < mWidth; ++x) {
					Location loc = min.clone().add(horizontal.getModX() * x,
							-y, horizontal.getModZ() * x);
					blocks.add(loc.getBlock());
				}
			}
		} else {
			for (int x = 0; x < mWidth; ++x) {
				for (int y = 0; y < mHeight; ++y) {
					Location loc = min.clone().add(horizontal.getModX() * x,
							-y, horizontal.getModZ() * x);
					blocks.add(loc.getBlock());
				}
			}
		}

		return blocks;
	}

	public boolean isSpaceAvailable() {
		for (Block block : getSignBlocks()) {
			if (!block.isEmpty()) {
				switch (block.getType()) {
				case SNOW:
				case LONG_GRASS:
				case FIRE:
				case VINE:
				case DEAD_BUSH:
				case DOUBLE_PLANT:
					continue;
				default:
					break;
				}

				if (block.getType() != Material.WALL_SIGN
						|| ((Sign) block.getState().getData()).getFacing() != mFacing)
					return false;
			}

			// Check that it will be supported
			if (!block.getRelative(mFacing.getOppositeFace()).getType()
					.isSolid())
				return false;
		}

		return true;
	}

	public void placeSigns() {
		for (Block block : getSignBlocks()) {
			Sign sign = new Sign(Material.WALL_SIGN);
			sign.setFacingDirection(mFacing);

			BlockState state = block.getState();
			state.setType(Material.WALL_SIGN);
			state.setData(sign);

			state.update(true, false);
		}
	}

	public void removeSigns() {
		for (Block block : getSignBlocks())
			block.setType(Material.AIR);
	}

	public void update() {
		++mTypeIndex;
		if (mTypeIndex >= mType.length) {
			mTypeIndex = 0;
			++mPeriodIndex;
			if (mPeriodIndex >= mPeriod.length)
				mPeriodIndex = 0;
		}

		MobHunting.instance.getDataStore().requestStats(getType(), getPeriod(),
				mWidth * mHeight * 2, this);
	}
	
	public void refresh() {
		Iterator<StatStore> it;
		if (mData == null)
			it = Collections.emptyIterator();
		else
			it = mData.iterator();

		// Update the label sign
		Block labelSign = mLocation.getBlock();
		if (isLoaded(labelSign)) {
			if (labelSign.getType() != Material.WALL_SIGN) {
				labelSign.setType(Material.WALL_SIGN);
			}
			
			org.bukkit.block.Sign sign = (org.bukkit.block.Sign) labelSign
					.getState();

			sign.setLine(0, ChatColor.BLUE + ChatColor.BOLD.toString()
					+ "MobHunting");

			String statName = getType().translateName();
			if (statName.length() > 15) {
				int splitPos = statName.indexOf(' ');

				if (splitPos == -1 || splitPos >= 15) {
					sign.setLine(1, statName.substring(0, 15).trim());
					sign.setLine(2, statName.substring(15).trim());
				} else {
					sign.setLine(1, statName.substring(0, splitPos).trim());
					sign.setLine(2, statName.substring(splitPos).trim());
				}
			} else {
				sign.setLine(1, statName);
				sign.setLine(2, EMPTY_STRING);
			}

			sign.setLine(3, ChatColor.YELLOW
					+ getPeriod().translateNameFriendly());
			sign.setData(sign.getData());
			
			((Sign) sign.getData())
			.setFacingDirection(mFacing);

			sign.update(true, false);
		} 

		// Update all the leaderboard signs
		int place = 1;

		for (Block block : getSignBlocks()) {
			StatStore stat1, stat2;
			if (it.hasNext())
				stat1 = it.next();
			else
				stat1 = null;

			if (it.hasNext())
				stat2 = it.next();
			else
				stat2 = null;

			if (isLoaded(block)) {
				if (block.getType() != Material.WALL_SIGN) {
					if (!block.getRelative(mFacing.getOppositeFace()).getType()
							.isSolid())
						block.getRelative(mFacing.getOppositeFace()).setType(
								Material.STONE);

					Sign sign = new Sign(Material.WALL_SIGN);
					sign.setFacingDirection(mFacing);

					BlockState state = block.getState();
					state.setType(Material.WALL_SIGN);
					state.setData(sign);

					state.update(true, false);
				}

				org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block
						.getState();

				if (stat1 != null) {
					sign.setLine(0, ChatColor.GREEN + String.valueOf(place)
							+ " " + ChatColor.BLACK
							+ stat1.getPlayer().getName());
					sign.setLine(1,
							ChatColor.BLUE + String.valueOf(stat1.getAmount()));
				} else {
					sign.setLine(0, EMPTY_STRING);
					sign.setLine(1, EMPTY_STRING);
				}

				if (stat2 != null) {
					sign.setLine(2, ChatColor.GREEN + String.valueOf(place + 1)
							+ " " + ChatColor.BLACK + stat2.getPlayer().getName());
					sign.setLine(3,
							ChatColor.BLUE + String.valueOf(stat2.getAmount()));
				} else {
					sign.setLine(2, EMPTY_STRING);
					sign.setLine(3, EMPTY_STRING);
				}

				sign.update(true, false);
			}
			place += 2;
		}
	}

	public boolean isInChunk(Chunk chunk) {
		BlockFace horizontal;

		switch (mFacing) {
		case NORTH:
			horizontal = BlockFace.WEST;
			break;
		case SOUTH:
			horizontal = BlockFace.EAST;
			break;
		case WEST:
			horizontal = BlockFace.SOUTH;
			break;
		case EAST:
			horizontal = BlockFace.NORTH;
			break;
		default:
			throw new AssertionError("Invalid facing " + mFacing);
		}

		Location min = mLocation.clone();
		min.add(-horizontal.getModX() * (mWidth / 2), -1, -horizontal.getModZ()
				* (mWidth / 2));

		for (int x = 0; x < mWidth; ++x) {
			int xx = min.getBlockX() + horizontal.getModX() * x;
			int zz = min.getBlockZ() + horizontal.getModZ() * x;

			if (xx >> 4 == chunk.getX() && zz >> 4 == chunk.getZ())
				return true;
		}

		return false;
	}

	public World getWorld() {
		return mLocation.getWorld();
	}

	public void setType(StatType[] type) {
		mType = type;
		mTypeIndex = 0;
	}
	
	public void setPeriod(TimePeriod[] period) {
		mPeriod = period;
		mPeriodIndex = 0;
	}

	public void setHorizontal(boolean horizontal) {
		mHorizontal = horizontal;
	}

	public StatType getType() {
		return mType[mTypeIndex];
	}

	public StatType[] getTypes() {
		return mType;
	}

	public TimePeriod getPeriod() {
		return mPeriod[mPeriodIndex];
	}

	public TimePeriod[] getPeriods() {
		return mPeriod;
	}

	public boolean isHorizontal() {
		return mHorizontal;
	}

	public Location getLocation() {
		return mLocation.clone();
	}

	public BlockFace getFacing() {
		return mFacing;
	}

	public int getHeight() {
		return mHeight;
	}

	public int getWidth() {
		return mWidth;
	}

	public boolean isInBounds(Location loc) {
		if (!loc.getWorld().equals(mLocation.getWorld()))
			return false;

		if (loc.getBlockY() < mLocation.getBlockY() - mHeight - 1
				|| loc.getBlockY() > mLocation.getBlockY())
			return false;

		BlockFace horizontal;

		switch (mFacing) {
		case NORTH:
			horizontal = BlockFace.WEST;
			break;
		case SOUTH:
			horizontal = BlockFace.EAST;
			break;
		case WEST:
			horizontal = BlockFace.SOUTH;
			break;
		case EAST:
			horizontal = BlockFace.NORTH;
			break;
		default:
			throw new AssertionError("Invalid facing " + mFacing);
		}

		Location min = mLocation.clone();
		min.add(-horizontal.getModX() * (mWidth / 2), 0, -horizontal.getModZ()
				* (mWidth / 2));
		Location max = min.clone().add(
				horizontal.getModX() * (mWidth - 1) - mFacing.getModX(), 0,
				horizontal.getModZ() * (mWidth - 1) - mFacing.getModZ());

		int minX = Math.min(min.getBlockX(), max.getBlockX());
		int minZ = Math.min(min.getBlockZ(), max.getBlockZ());
		int maxX = Math.max(min.getBlockX(), max.getBlockX());
		int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

		return (loc.getBlockX() >= minX && loc.getBlockX() <= maxX)
				&& (loc.getBlockZ() >= minZ && loc.getBlockZ() <= maxZ);
	}

	private boolean isLoaded(Block block) {
		return (mLocation.getWorld().isChunkLoaded(block.getX() >> 4,
				block.getZ() >> 4));
	}

	@Override
	public void onCompleted(List<StatStore> data) {
		ArrayList<StatStore> altData = new ArrayList<StatStore>(data.size());
		for (StatStore stat : data) {
			if (stat.getAmount() != 0)
				altData.add(stat);
		}

		mData = altData;
		refresh();
	}

	@Override
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	public void save(ConfigurationSection section) {
		section.set("world", mLocation.getWorld().getUID().toString());
		section.set("position", mLocation.toVector());
		section.set("facing", mFacing.name());

		section.set("horizontal", mHorizontal);
		ArrayList<String> periods = new ArrayList<String>(mPeriod.length);
		for (TimePeriod period : mPeriod)
			periods.add(period.name());

		section.set("periods", periods);

		ArrayList<String> stats = new ArrayList<String>(mPeriod.length);
		for (StatType type : mType)
			stats.add(type.getDBColumn());

		section.set("stats", stats);
		section.set("width", mWidth);
		section.set("height", mHeight);
	}

	public void read(ConfigurationSection section)
			throws InvalidConfigurationException, IllegalStateException {
		World world = Bukkit.getWorld(UUID.fromString(section
				.getString("world")));
		if (world == null)
			throw new IllegalStateException();

		Vector pos = section.getVector("position");

		mFacing = BlockFace.valueOf(section.getString("facing"));

		mHorizontal = section.getBoolean("horizontal");
		List<String> periods = section.getStringList("periods");
		List<String> stats = section.getStringList("stats");
		mWidth = section.getInt("width");
		mHeight = section.getInt("height");

		if (mFacing != BlockFace.NORTH && mFacing != BlockFace.SOUTH
				&& mFacing != BlockFace.WEST && mFacing != BlockFace.EAST)
			throw new InvalidConfigurationException(
					"Invalid leaderboard facing " + section.getString("facing"));
		if (periods == null)
			throw new InvalidConfigurationException("Error in time period list");
		if (stats == null)
			throw new InvalidConfigurationException("Error in stat type list");
		if (pos == null)
			throw new InvalidConfigurationException("Error in position");

		if (mWidth < 1)
			throw new InvalidConfigurationException("Invalid width");
		if (mHeight < 1)
			throw new InvalidConfigurationException("Invalid height");

		mPeriod = new TimePeriod[periods.size()];
		for (int i = 0; i < periods.size(); ++i) {
			mPeriod[i] = TimePeriod.valueOf(periods.get(i));
			if (mPeriod[i] == null)
				throw new InvalidConfigurationException("Invalid time period "
						+ periods.get(i));
		}

		mType = new StatType[stats.size()];
		for (int i = 0; i < stats.size(); ++i) {
			mType[i] = StatType.fromColumnName(stats.get(i));
			if (mType[i] == null)
				throw new InvalidConfigurationException("Invalid stat type "
						+ stats.get(i));
		}

		mPeriodIndex = 0;
		mTypeIndex = 0;

		mLocation = pos.toLocation(world);
	}

}
