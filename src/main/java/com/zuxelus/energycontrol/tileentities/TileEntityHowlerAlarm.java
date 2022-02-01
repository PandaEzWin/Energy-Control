package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.utils.TileEntitySound;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityHowlerAlarm extends TileEntityFacing implements ITickableTileEntity, ITilePacketHandler {
	private static final String DEFAULT_SOUND_NAME = "default";
	private static final String SOUND_PREFIX = "energycontrol:alarm-";

	public int range;
	public boolean powered;

	public String soundName;
	private String prevSoundName;

	protected int updateTicker;
	protected int tickRate;
	private TileEntitySound sound;

	public TileEntityHowlerAlarm(TileEntityType<?> type) {
		super(type);
		tickRate = ConfigHandler.ALARM_PAUSE.get();
		updateTicker = 0;
		powered = false;
		soundName = prevSoundName = DEFAULT_SOUND_NAME;
		range = ConfigHandler.HOWLER_ALARM_RANGE.get();
	}

	public TileEntityHowlerAlarm() {
		this(ModTileEntityTypes.howler_alarm.get());
	}

	public int getRange() {
		return range;
	}

	public void setRange(int r) {
		if (!level.isClientSide && range != r)
			notifyBlockUpdate();
		range = r;
	}

	public String getSoundName() {
		return soundName;
	}

	public void setSoundName(String name) {
		soundName = name;
		if (!level.isClientSide && !prevSoundName.equals(soundName))
			notifyBlockUpdate();
		if (level.isClientSide) {
			if (EnergyControl.INSTANCE.availableAlarms != null && !EnergyControl.INSTANCE.availableAlarms.contains(soundName)) {
				EnergyControl.LOGGER.info(String.format("Can't set sound '%s' at %d,%d,%d, using default", soundName, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()));
				soundName = DEFAULT_SOUND_NAME;
			}
		}
		prevSoundName = soundName;
	}

	public boolean getPowered() {
		return powered;
	}

	public void updatePowered(boolean isPowered) {
		if (level.isClientSide && isPowered != powered) {
			powered = isPowered;
			checkStatus();
		}
	}

	@Override
	public void onServerMessageReceived(CompoundNBT tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("string"))
				setSoundName(tag.getString("string"));
			break;
		case 2:
			if (tag.contains("value"))
				setRange(tag.getInt("value"));
			break;
		}
	}

	@Override
	public void onClientMessageReceived(CompoundNBT tag) { }

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(worldPosition, 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readProperties(pkt.getTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag = writeProperties(tag);
		powered = level.hasNeighborSignal(worldPosition);
		tag.putBoolean("powered", powered);
		return tag;
	}

	@Override
	protected void readProperties(CompoundNBT tag) {
		super.readProperties(tag);
		if (tag.contains("soundName"))
			soundName = prevSoundName = tag.getString("soundName");
		if (tag.contains("range"))
			range = tag.getInt("range");
		if (tag.contains("powered"))
			updatePowered(tag.getBoolean("powered"));
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		readProperties(tag);
	}

	@Override
	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag = super.writeProperties(tag);
		tag.putString("soundName", soundName);
		tag.putInt("range", range);
		return tag;
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		return writeProperties(super.save(tag));
	}

	@Override
	public void setRemoved() {
		if (level.isClientSide && sound != null)
			sound.stopAlarm();
		super.setRemoved();
	}

	@Override
	public void tick() {
		if (level.isClientSide)
			checkStatus();
	}

	protected void checkStatus() {
		if (sound == null)
			sound = new TileEntitySound();
		if (!sound.isPlaying())
			updateTicker--;
		if (!powered && sound.isPlaying()) {
			sound.stopAlarm();
			updateTicker = tickRate;
		}
		if (powered && !sound.isPlaying() && updateTicker < 0) {
			sound.playAlarm(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D, SOUND_PREFIX + soundName, range);
			updateTicker = tickRate;
		}
	}
}