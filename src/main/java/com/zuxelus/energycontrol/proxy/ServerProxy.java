package com.zuxelus.energycontrol.proxy;

import java.io.File;

import com.zuxelus.energycontrol.ServerTickHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy implements IProxy {

	@Override
	public void loadConfig(FMLPreInitializationEvent event) {}

	@Override
	public void registerSpecialRenderers() {}

	@Override
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(ServerTickHandler.instance);
	}

	@Override
	public void importSound(File configFolder) {}

	@Override
	public String getItemName(ItemStack stack) {
		return stack.getItem().getUnlocalizedName();
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
}
