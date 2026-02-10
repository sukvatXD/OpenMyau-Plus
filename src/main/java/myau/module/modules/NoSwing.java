package myau.module.modules;

import myau.event.EventTarget;
import myau.event.types.EventType;
import myau.events.PacketEvent;
import myau.module.Module;
import myau.property.properties.BooleanProperty;
import net.minecraft.client.Minecraft;

public class NoSwing extends Module {
  private static final Minecraft mc = Minecraft.func_71410_x();
  
  public final BooleanProperty serverSide = new BooleanProperty("server-side", Boolean.valueOf(false));
  
  public NoSwing() {
    super("NoSwing", false);
  }
  
  @EventTarget
  public void onPacket(PacketEvent event) {
    if (!isEnabled())
      return; 
    if (event.getType() != EventType.SEND)
      return; 
    if (((Boolean)this.serverSide.getValue()).booleanValue() && event.getPacket() instanceof net.minecraft.network.play.client.C0APacketAnimation)
      event.setCancelled(true); 
  }
}
