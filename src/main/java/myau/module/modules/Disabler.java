package myau.module.modules;

import myau.Myau;
import myau.event.EventTarget;
import myau.event.types.EventType;
import myau.events.PacketEvent;
import myau.events.UpdateEvent;
import myau.module.Module;
import myau.property.properties.BooleanProperty;
import myau.property.properties.ModeProperty;
import myau.util.ChatUtil;
import myau.util.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.ArrayList;
import java.util.List;

public class Disabler extends Module {

    public final ModeProperty mode = new ModeProperty("Mode", 0, new String[]{"GRIM-Inv", "Cubecraft"});
    public final BooleanProperty digValue = new BooleanProperty("Spoof Dig Release", true);
    public final BooleanProperty debug = new BooleanProperty("Debug", false);

    private final List<Packet<?>> packetsQueue = new ArrayList<>();

    public Disabler() {
        super("Disabler", false);
    }

    @Override
    public void onEnabled() {
        packetsQueue.clear();
        if (mode.getValue() == 0) {
            ChatUtil.sendFormatted(Myau.clientName + "Disabler: GRIM-Inv mode enabled.");
        }
    }

    @Override
    public void onDisabled() {
        flushQueue();
    }

    private void flushQueue() {
        if (!packetsQueue.isEmpty()) {
            for (Packet<?> packet : new ArrayList<>(packetsQueue)) {
                PacketUtil.sendPacketNoEvent(packet);
            }
            packetsQueue.clear();
            if (debug.getValue()) {
                ChatUtil.sendFormatted(Myau.clientName + "Disabler: Flushed packets on disable.");
            }
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (!this.isEnabled()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mode.getValue() != 0) return;

        if (mc.thePlayer.onGround) return;

        if (!packetsQueue.isEmpty()) {
            for (Packet<?> packet : new ArrayList<>(packetsQueue)) {
                PacketUtil.sendPacketNoEvent(packet);
                if (debug.getValue()) {
                    ChatUtil.sendFormatted(Myau.clientName + "Sent: " + packet.getClass().getSimpleName());
                }
            }
            packetsQueue.clear();
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!this.isEnabled() || mc.thePlayer == null) return;

        Packet<?> packet = event.getPacket();

        if (mode.getValue() == 0 && event.getType() == EventType.SEND) {
            if (packet instanceof C0EPacketClickWindow) {
                event.setCancelled(true);
                packetsQueue.add(packet);
                if (debug.getValue()) {
                    C0EPacketClickWindow c0e = (C0EPacketClickWindow) packet;
                    ChatUtil.sendFormatted(Myau.clientName + "Cached C0E: win=" + c0e.getWindowId()
                            + ", slot=" + c0e.getSlotId() + ", action=" + c0e.getActionNumber());
                }
                return;
            }
        }

        if (event.getType() == EventType.SEND && digValue.getValue()) {
            if (mc.thePlayer.getHeldItem() != null
                    && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
                    && packet instanceof C07PacketPlayerDigging) {
                C07PacketPlayerDigging c07 = (C07PacketPlayerDigging) packet;
                if (c07.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                    event.setCancelled(true);
                    int current = mc.thePlayer.inventory.currentItem;
                    int next = (current + 1) % 9;
                    PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(next));
                    PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(current));
                    if (debug.getValue()) {
                        ChatUtil.sendFormatted(Myau.clientName + "Spoofed C07 release with C09 swap.");
                    }
                    return;
                }
            }
        }
    }
}