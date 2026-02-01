package myau.ui.overlay.dynamicisland;

import myau.events.Render2DEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import java.awt.Color;

public class DefaultIsland implements IslandTrigger {
    @Override
    public void renderIsland(Render2DEvent event, float posX, float posY, float width, float height, float progress) {
        if (progress < 0.9f) return; // Only draw text when nearly expanded

        Minecraft mc = Minecraft.getMinecraft();
        String name = "Myau" + EnumChatFormatting.RED + "+";
        String fps = Minecraft.getDebugFPS() + "fps";

        mc.fontRendererObj.drawStringWithShadow(name, posX + 8, posY + 7, -1);

        String stats = EnumChatFormatting.GRAY + "⚡ " + EnumChatFormatting.WHITE + fps;
        mc.fontRendererObj.drawStringWithShadow(stats, posX + width - mc.fontRendererObj.getStringWidth(stats) - 8, posY + 7, -1);
    }

    @Override
    public float getIslandWidth() { return 140; }
    @Override
    public float getIslandHeight() { return 22; }
    @Override
    public int getIslandPriority() { return -5; }
}