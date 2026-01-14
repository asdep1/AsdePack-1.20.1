package fr.asdepack.client.screen.components;

import fr.asdepack.types.Kit;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

public class ButtonScrollList extends ObjectSelectionList<ButtonScrollList.Entry> {

    public ButtonScrollList(
            Minecraft mc,
            int width,
            int height,
            int top,
            int bottom,
            int itemHeight
    ) {
        super(mc, width, height, top, bottom, itemHeight);
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getRight() - 6;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() - 50;
    }

    // ─────────────────────────────────────────────────────────────
    // NEW: Override renderSelection with an empty body
    // ─────────────────────────────────────────────────────────────
    @Override
    protected void renderSelection(GuiGraphics guiGraphics, int top, int width, int height, int outerColor, int innerColor) {
        // Do nothing. This hides the gray selection box behind the entry.
    }

    @Override
    public int getRowLeft() {
        return super.getRowLeft();
    }

    @Override
    public int addEntry(@NotNull Entry pEntry) {
        return super.addEntry(pEntry);
    }

    // ───────────────────────────
    // ENTRY
    // ───────────────────────────
    public static class Entry extends ObjectSelectionList.Entry<Entry> {

        private final Button button;
        private Kit kit;

        public Entry(Component text, Runnable onClick, Kit kit) {
            this.button = Button.builder(text, b -> onClick.run())
                    .bounds(0, 0, 100, 20)
                    .tooltip(Tooltip.create(
                            Component.literal(
                                    "Kit : "
                            ).append(
                                    Component.literal((kit != null ? kit.getName() : "Unknown") + "\n").withStyle(ChatFormatting.RED)
                            ).append(
                                    Component.literal("Prix : ")
                            ).append(
                                    Component.literal(kit != null ? String.valueOf(kit.getCost()) : "0").withStyle(ChatFormatting.GOLD)
                            ).append(
                                    Component.literal("$")
                            ).append(
                                    Component.literal("\nCooldown : "))
                            .append(
                                    Component.literal(kit != null ? String.valueOf(kit.getCooldown()) : "0").withStyle(ChatFormatting.AQUA)
                            ).append(" secondes").append(
                                    Component.literal("\n")
                            ).append(
                                Component.literal("Pour voir les items du kit, cliquez sur le bouton.").withStyle(Style.EMPTY).withStyle(ChatFormatting.ITALIC)
                            )
                    )).build();
            this.kit = kit;
        }

        @Override
        public void render(
                @NotNull GuiGraphics gui,
                int index,
                int y,
                int x,
                int rowWidth,
                int rowHeight,
                int mouseX,
                int mouseY,
                boolean hovered,
                float partialTick
        ) {
            button.setX(x + (rowWidth - 100) / 2);
            button.setY(y);
            button.render(gui, mouseX, mouseY, partialTick);
            if (kit != null && !kit.getIcon().isEmpty()) {
                gui.renderItem(kit.getIcon(), 30 +x + (rowWidth - 100) / 2 - 20, 1 + y);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.button.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return this.button.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public @NotNull Component getNarration() {
            return button.getMessage();
        }
    }
}