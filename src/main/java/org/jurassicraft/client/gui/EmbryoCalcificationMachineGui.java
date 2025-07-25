package org.jurassicraft.client.gui;

// TODO: Rewrite for modern GUI system
// Temporarily commented out for 1.21 upgrade - old GUI APIs no longer exist

// Placeholder class for 1.21 upgrade - will be rewritten with modern GUI
public class EmbryoCalcificationMachineGui {
    // TODO: Implement modern GUI for EmbryoCalcificationMachine using Screen/AbstractContainerScreen
    
    // Constructor placeholder
    public EmbryoCalcificationMachineGui(Object playerInv, Object inventory) {
        // TODO: Initialize modern GUI
    }
}

/*
// Original 1.12.2 implementation will be restored and rewritten for 1.21 later
// This contained a GuiContainer-based GUI that needs to be modernized to Screen/AbstractContainerScreen

package org.jurassicraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.server.container.EmbryoCalcificationMachineContainer;

@SideOnly(Side.CLIENT)
public class EmbryoCalcificationMachineGui extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("jurassicraft:textures/gui/embryo_calcification_machine.png");

    private final InventoryPlayer playerInventory;
    private IInventory inventory;

    public EmbryoCalcificationMachineGui(InventoryPlayer playerInv, IInventory inventory) {
        super(new EmbryoCalcificationMachineContainer(playerInv, (TileEntity) inventory));
        this.playerInventory = playerInv;
        this.inventory = inventory;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.inventory.getDisplayName().getUnformattedText();
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        if (this.inventory instanceof EmbryoCalcificationMachineBlockEntity) {
            EmbryoCalcificationMachineBlockEntity machine = (EmbryoCalcificationMachineBlockEntity) this.inventory;

            int progress = machine.getProgressScaled(24);

            this.drawTexturedModalRect(k + 79, l + 34, 176, 14, progress + 1, 16);

            int energyScaled = machine.getEnergyScaled(78);

            this.drawTexturedModalRect(k + 15, l + 8 + 78 - energyScaled, 176, 78 - energyScaled, 16, energyScaled);
        }
    }
}
*/
