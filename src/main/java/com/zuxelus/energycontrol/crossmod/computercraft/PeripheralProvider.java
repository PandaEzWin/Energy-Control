package com.zuxelus.energycontrol.crossmod.computercraft;

import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

public class PeripheralProvider implements IPeripheralProvider {

	@Override
	public LazyOptional<IPeripheral> getPeripheral(Level world, BlockPos pos, Direction side) {
		if (world == null)
			return LazyOptional.empty();

		BlockEntity te = world.getBlockEntity(pos);
		if (te == null)
			return LazyOptional.empty();

		if (te instanceof TileEntityAdvancedInfoPanel)
			return LazyOptional.of(() -> new AdvancedInfoPanelPeripheral((TileEntityAdvancedInfoPanel) te));
		if (te instanceof TileEntityInfoPanel)
			return LazyOptional.of(() -> new InfoPanelPeripheral((TileEntityInfoPanel) te));
		return LazyOptional.empty();
	}
}
