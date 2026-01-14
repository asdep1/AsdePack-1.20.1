package fr.asdepack.client.screen;

import fr.asdepack.Asdepack;
import fr.asdepack.client.screen.components.ButtonScrollList;
import fr.asdepack.common.menus.KitPreviewMenu;
import fr.asdepack.common.network.PacketHelper;
import fr.asdepack.common.network.packets.SRequestKitPacket;
import fr.asdepack.types.Kit;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class ScreenKitList
        extends AbstractContainerScreen<KitPreviewMenu> {

    private static final ResourceLocation BG =
            ResourceLocation.fromNamespaceAndPath(Asdepack.MODID, "textures/gui/kit_preview.png");

    private final List<Kit> kits;
    private ButtonScrollList buttonScrollList;
    private Button selectButton;

    public ScreenKitList(KitPreviewMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 114 + 18 * 6;
        this.inventoryLabelY = this.imageHeight - 94;
        this.kits = this.getMenu().getKits();
    }

    @Override
    protected void init() {
        super.init();

        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;

        buttonScrollList = new ButtonScrollList(
                this.minecraft,
                110,
                150,
                top,
                top + 150,
                21
        );

        buttonScrollList.setLeftPos(left - 115);
        this.addWidget(buttonScrollList);

        for (Kit kit : kits) {
            buttonScrollList.addEntry(new ButtonScrollList.Entry(
                    Component.literal(kit.getName()),
                    () -> this.menu.showKit(kit),
                    kit
            ));
        }

        if (!kits.isEmpty()) {
            this.menu.showKit(kits.get(0));
        }

        this.selectButton = this.addRenderableWidget(Button.builder(
                Component.translatable(Asdepack.MODID + ".kitlistscreen.select_button"),
                button -> {
                    Kit selectedKit = this.menu.getSelectedKit();
                    if (selectedKit != null) {
                        PacketHelper.sendToServer(new SRequestKitPacket(selectedKit));
                    }
                }
                ).bounds(left + 185, top, 100, 20).build()
        );
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.buttonScrollList.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        this.selectButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        g.blit(BG, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }
}
