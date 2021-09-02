package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.containers.ContainerInfoPanel;
import com.zuxelus.energycontrol.gui.controls.GuiInfoPanelCheckBox;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardText;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.controls.GuiButtonGeneral;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiInfoPanel extends GuiPanelBase<ContainerInfoPanel> { 
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_info_panel.png");

	public GuiInfoPanel(ContainerInfoPanel container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title, TEXTURE);
		ySize = 201;
		panel = (TileEntityInfoPanel) container.te;
		name = I18n.format("block.energycontrol.info_panel");
	}

	protected void initButtons() {
		addButton(new GuiButtonGeneral(guiLeft + xSize - 24, guiTop + 42, 16, 16, TEXTURE, 176, panel.getShowLabels() ? 15 : 31, (button) -> { actionPerformed(button, ID_LABELS); }).setGradient());
		if (panel.isColoredEval())
			addButton(new GuiButtonGeneral(guiLeft + xSize - 24, guiTop + 42 + 17, 16, 16, TEXTURE, 192, 0, (button) -> { actionPerformed(button, ID_COLORS); }).setGradient().setScale(2));
		addButton(new GuiButtonGeneral(guiLeft + xSize - 24, guiTop + 42 + 17 * 3, 16, 16, new StringTextComponent(Integer.toString(panel.getTickRate())), (button) -> { actionPerformed(button, ID_TICKRATE); }).setGradient());
	}

	protected void initControls() {
		ItemStack stack = panel.getCards().get(activeTab);
		if (!ItemStack.areItemsEqual(stack, oldStack)) {
			if (!oldStack.isEmpty() && stack.isEmpty())
				updateTitle();
			oldStack = stack.copy();
			buttons.clear();
			children.clear();
			initButtons();
			if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
				int slot = panel.getCardSlot(stack);
				if (stack.getItem() instanceof ItemCardText)
					addButton(new GuiButtonGeneral(guiLeft + xSize - 24, guiTop + 42 + 17 * 2, 16, 16, new StringTextComponent("txt"), (button) -> { actionPerformed(button, ID_TEXT); }).setGradient());
				List<PanelSetting> settingsList = ((ItemCardMain) stack.getItem()).getSettingsList();
	
				int hy = font.FONT_HEIGHT + 1;
				int y = 1;
				int x = guiLeft + 24;
				if (settingsList != null)
					for (PanelSetting panelSetting : settingsList) {
						addButton(new GuiInfoPanelCheckBox(x + 4, guiTop + 28 + hy * y, panelSetting, panel, slot, font));
						y++;
					}
				if (!modified) {
					textboxTitle = new TextFieldWidget(font, guiLeft + 7, guiTop + 16, 162, 18, null, StringTextComponent.EMPTY);
					textboxTitle.changeFocus(true);
					textboxTitle.setText(new ItemCardReader(stack).getTitle());
					children.add(textboxTitle);
					setFocusedDefault(textboxTitle);
				}
			} else {
				modified = false;
				textboxTitle = null;
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		if (textboxTitle != null)
			textboxTitle.renderButton(matrixStack, mouseX, mouseY, partialTicks);
	}
}
