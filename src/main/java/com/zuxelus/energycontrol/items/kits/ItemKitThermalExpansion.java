package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitThermalExpansion extends ItemKitBase {

	public ItemKitThermalExpansion() {
		super(ItemCardType.KIT_THERMAL_EXPANSION, "kit_thermal_expansion");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.THERMAL_EXPANSION).getCardData(te);
		if (tag != null) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_THERMAL_EXPANSION);
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}
