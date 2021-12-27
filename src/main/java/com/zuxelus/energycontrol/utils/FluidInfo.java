package com.zuxelus.energycontrol.utils;

import com.zuxelus.energycontrol.api.ICardReader;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.client.resource.language.I18n;

public class FluidInfo {
	String translationKey;
	String texture;
	long amount;
	long capacity;
	int color;

	public FluidInfo(String translationKey, String texture, long amount, long capacity) {
		this.translationKey = translationKey;
		this.texture = texture;
		this.amount = amount;
		this.capacity = capacity;
	}

	/*public FluidInfo(IFluidTank tank) {
		if (tank.getFluid() != null) {
			amount = tank.getFluidAmount();
			if (amount > 0) {
				translationKey = tank.getFluid().getTranslationKey();
				texture = tank.getFluid().getFluid().getAttributes().getStillTexture().toString();
				color = tank.getFluid().getFluid().getAttributes().getColor();
			}
		}
		capacity = tank.getCapacity();
	}*/

	public FluidInfo(FluidVolume stack, long capacity) {
		if (stack != null) {
			amount = stack.amount().whole;
			if (amount > 0) {
				/*translationKey = stack.getTranslationKey();
				texture = stack.fluidKey.getRawFluid().getAttributes().getStillTexture().toString();
				color = stack.fluidKey.getRawFluid().getAttributes().getColor();*/
			}
		}
		this.capacity = capacity;
	}

	public FluidInfo(SingleSlotStorage<FluidVariant> stack) {
		if (stack != null) {
			amount = stack.getAmount() / 81;
			if (amount > 0) {
				/*translationKey = stack.getTranslationKey();
				texture = stack.fluidKey.getRawFluid().getAttributes().getStillTexture().toString();
				color = stack.fluidKey.getRawFluid().getAttributes().getColor();*/
			}
		}
		this.capacity = stack.getCapacity() / 81;
	}

	public void write(ICardReader reader) {
		if (translationKey != null)
			reader.setString("name", I18n.translate(translationKey));
		else
			reader.setString("name", "");
		if (texture != null)
			reader.setString("texture", texture);
		else
			reader.setString("texture", "");
		reader.setLong("amount", amount);
		reader.setLong("capacity", capacity);
		reader.setInt("color", color);
	}

	public void write(ICardReader reader, int i) {
		if (translationKey != null)
			reader.setString(String.format("_%dname", i), I18n.translate(translationKey));
		else
			reader.setString(String.format("_%dname", i), "");
		reader.setLong(String.format("_%damount", i), amount);
		reader.setLong(String.format("_%dcapacity", i), capacity);
	}
}
