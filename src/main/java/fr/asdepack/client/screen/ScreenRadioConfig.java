package fr.asdepack.client.screen;

import fr.asdepack.Asdepack;
import fr.asdepack.client.screen.components.ButtonScrollList;
import fr.asdepack.common.menus.KitPreviewMenu;
import fr.asdepack.common.menus.RadioConfigMenu;
import fr.asdepack.common.network.PacketHelper;
import fr.asdepack.common.network.packets.SPacketChangeRadioFreq;
import fr.asdepack.common.network.packets.SRequestKitPacket;
import fr.asdepack.types.Kit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ScreenRadioConfig
        extends AbstractContainerScreen<RadioConfigMenu> {

    private static final ResourceLocation BG =
            ResourceLocation.fromNamespaceAndPath(Asdepack.MODID, "textures/gui/radio_setup.png");

    private MultiLineEditBox frequencyField;
    private Button confirmButton;

    public ScreenRadioConfig(RadioConfigMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 133;
        this.inventoryLabelY = 40;
    }

    @Override
    protected void init() {
        super.init();

        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;

        frequencyField = new MultiLineEditBox(
                Minecraft.getInstance().font, left + 10, top + 17, 100, 20,
        Component.literal(this.getMenu().getRadio().getOrCreateTag().getInt("frequency") + " Mhz"), Component.literal("000 kHz"));

        this.addWidget(frequencyField);

        this.confirmButton = this.addRenderableWidget(Button.builder(
                Component.translatable(Asdepack.MODID + ".radioconfig.confirm_button"),
                button -> {
                    PacketHelper.sendToServer(
                            new SPacketChangeRadioFreq(
                                    Integer.parseInt(frequencyField.getValue())
                            )
                    );
                }
                ).bounds(left + 120, top + 17, 50, 20).build()
        );
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.confirmButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.frequencyField.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);

    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        g.blit(BG, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }
}
