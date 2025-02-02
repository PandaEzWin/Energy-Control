package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.utils.FluidInfo;

import ic2.api.classic.reactor.IChamberReactor;
import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.ICustomDamageItem;
import ic2.api.reactor.IReactor;
import ic2.api.tile.IEnergyStorage;
import ic2.core.block.base.tile.TileEntityGeneratorBase;
import ic2.core.block.generator.tile.*;
import ic2.core.block.machine.low.TileEntityMachineTank;
import ic2.core.block.personal.tile.TileEntityPersonalTank;
import ic2.core.item.reactor.ItemReactorUraniumRod;
import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrossIC2Classic extends CrossModBase {

	@Override
	public ItemStack getItemStack(String name) {
		switch (name) {
		case "transformer":
			return IC2Items.getItem("upgrade", "transformer");
		case "energy_storage":
			return IC2Items.getItem("upgrade", "energy_storage");
		case "machine":
			return IC2Items.getItem("resource", "machine");
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getChargedStack(ItemStack stack) {
		ElectricItem.manager.charge(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		return stack;
	}

	@Override
	public boolean isWrench(ItemStack par1) {
		return par1 != null && par1.getItem() instanceof ItemToolWrench;
	}

	@Override
	public int getNuclearCellTimeLeft(ItemStack stack) {
		if (stack.isEmpty())
			return 0;
		
		Item item = stack.getItem();
		if (item instanceof ItemReactorUraniumRod)
			return ((ICustomDamageItem)item).getMaxCustomDamage(stack) - ((ICustomDamageItem)item).getCustomDamage(stack);
		
		return 0;
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof IEnergyStorage) {
			NBTTagCompound tag = new NBTTagCompound();
			IEnergyStorage storage = (IEnergyStorage) te;
			tag.setInteger("type", 1);
			tag.setDouble("storage", storage.getStored());
			tag.setDouble("maxStorage", storage.getCapacity());
			return tag;
		}
		return null;
	}

	@Override
	public ItemStack getGeneratorCard(TileEntity te) {
		if (te instanceof TileEntitySolarPanel || te instanceof TileEntityGeneratorBase) {
			ItemStack card = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_GENERATOR);
			ItemStackHelper.setCoordinates(card, te.getPos());
			return card;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound getGeneratorData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			boolean active;
			tag.setString("euType", "EU");
			if (te instanceof TileEntitySolarPanel) {
				tag.setInteger("type", 1);
				tag.setDouble("storage", ((TileEntitySolarPanel) te).getOfferedEnergy());
				tag.setDouble("maxStorage", ((TileEntitySolarPanel) te).getOutput());
				active = ((TileEntitySolarPanel) te).getActive();
				tag.setBoolean("active", active);
				if (active)
					tag.setDouble("production", ((TileEntitySolarPanel) te).getOutput());
				else
					tag.setDouble("production", 0);
				return tag;
			}
			if (te instanceof TileEntityGeneratorBase) {
				tag.setInteger("type", 1);
				tag.setDouble("storage", ((TileEntityGeneratorBase) te).getStoredEU());
				tag.setDouble("maxStorage", ((TileEntityGeneratorBase) te).getMaxEU());
				active = ((TileEntityGeneratorBase) te).getActive();
				tag.setBoolean("active", active);
				if (active)
					tag.setDouble("production", ((TileEntityGeneratorBase) te).getOutput());
				else
					tag.setDouble("production", 0);
				return tag;
			}
		} catch (Throwable t) { }
		return null;
	}

	@Override
	public ItemStack getReactorCard(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityNuclearReactorElectric || te instanceof TileEntityReactorChamberElectric || te instanceof TileEntityNuclearSteamReactor) {
			BlockPos position = IC2ReactorHelper.getTargetCoordinates(world, pos);
			if (position != null) {
				ItemStack sensorLocationCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_REACTOR);
				ItemStackHelper.setCoordinates(sensorLocationCard, position);
				return sensorLocationCard;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound getReactorData(TileEntity te) {
		if (!(te instanceof IChamberReactor))
			return null;
		IReactor reactor = (IReactor) te;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("heat", reactor.getHeat());
		tag.setInteger("maxHeat", reactor.getMaxHeat());
		tag.setBoolean("reactorPoweredB", reactor.produceEnergy());
		tag.setInteger("output", (int) Math.round(reactor.getReactorEUEnergyOutput()));
		tag.setBoolean("isSteam", isSteamReactor(te));

		IChamberReactor chamber = (IChamberReactor) reactor;
		int size = chamber.getReactorSize();
		int dmgLeft = 0;
		for (int y = 0; y < 6; y++)
			for (int x = 0; x < size; x++) {
				ItemStack stack = chamber.getItemAt(x, y);
				if (!stack.isEmpty())
					dmgLeft = Math.max(dmgLeft, IC2ReactorHelper.getNuclearCellTimeLeft(stack));
			}

		int timeLeft;
		//Classic has a Higher Tick rate for Steam generation but damage tick rate is still the same...
		if (isSteamReactor(te)) {
			timeLeft = dmgLeft;
		} else
			timeLeft = dmgLeft * reactor.getTickRate() / 20;
		tag.setInteger("timeLeft", timeLeft);

		return tag;
	}

	public int getReactorHeat(World world, BlockPos pos) {
		IReactor reactor = IC2ReactorHelper.getReactorAround(world, pos);
		if (reactor != null)
			return reactor.getHeat();
		reactor = IC2ReactorHelper.getReactor3x3(world, pos);
		if (reactor != null)
			return reactor.getHeat();
		return -1;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof TileEntityNuclearSteamReactor) {
			result.add(new FluidInfo(((TileEntityNuclearSteamReactor) te).getWaterTank()));
			result.add(new FluidInfo(((TileEntityNuclearSteamReactor) te).getSteamTank()));
		}
		if (te instanceof TileEntityMachineTank)
			result.add(new FluidInfo(((TileEntityMachineTank) te).tank));
		if (te instanceof TileEntityPersonalTank)
			result.add(new FluidInfo(((TileEntityPersonalTank) te).tank));
		if (te instanceof TileEntityLiquidFuelGenerator)
			result.add(new FluidInfo(((TileEntityLiquidFuelGenerator) te).tank));
		if (result.size() == 0)
			return null;
		return result;
	}
}
