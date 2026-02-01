package myau.ui.overlay.dynamicisland;

import myau.module.modules.Scaffold;
import myau.Myau;
import myau.events.Render2DEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public class ScaffoldIsland implements IslandTrigger {
    @Override
    public boolean isAvailable() {
        return Myau.moduleManager.getModule(Scaffold.class).isEnabled();
    }

    @Override
    public void renderIsland(Render2DEvent event, float x, float y, float w, float h, float progress) {
        if (progress < 0.8f) return;
        Minecraft mc = Minecraft.getMinecraft();

        Scaffold scaffold = (Scaffold) Myau.moduleManager.getModule(Scaffold.class);

        String currentModeName = scaffold.rotationMode.getModeString();

        String mode = "Mode: " + EnumChatFormatting.GOLD + currentModeName;
        String blocks = "Blocks Lefts: " + EnumChatFormatting.GREEN + scaffold.getBlockCount();


        mc.fontRendererObj.drawStringWithShadow("SCAFFOLD", x + (w/2) - (mc.fontRendererObj.getStringWidth("SCAFFOLD") / 2f), y + 5, -1);

        mc.fontRendererObj.drawStringWithShadow(mode, x + 8, y + 16, -1);
        mc.fontRendererObj.drawStringWithShadow(blocks, x + w - mc.fontRendererObj.getStringWidth(blocks) - 8, y + 16, -1);
    }

    @Override
    public float getIslandWidth() { return 180; }
    @Override
    public float getIslandHeight() { return 30; }
    @Override
    public int getIslandPriority() { return 10; }
}