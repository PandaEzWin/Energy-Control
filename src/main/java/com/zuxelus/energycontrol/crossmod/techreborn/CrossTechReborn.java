package com.zuxelus.energycontrol.crossmod.techreborn;

import cpw.mods.fml.common.Loader;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;

public abstract class CrossTechReborn {

	public static CrossTechReborn getMod() {
		try {
			if (Loader.isModLoaded("techreborn")) {
				Class clz = Class.forName("com.zuxelus.energycontrol.crossmod.techreborn.TechReborn");
				if (clz != null)
					return (CrossTechReborn) clz.newInstance();
			}
		} catch (Exception e) { }
		return new TechRebornNoMod();
	}

	public abstract ItemStack getEnergyCard(World world, int x, int y, int z);

	public abstract NBTTagCompound getEnergyData(TileEntity te);

	public abstract ItemStack getGeneratorCard(World world, int x, int y, int z);

	public abstract NBTTagCompound getGeneratorData(TileEntity te);

	public abstract FluidTankInfo[] getAllTanks(TileEntity te);

	public abstract ItemStack getChargedStack(ItemStack stack);

	public abstract ItemStack getItemStack(String name);
}
