package be.mathiasdejong.endercrop.neoforge;

import static be.mathiasdejong.endercrop.Reference.MOD_ID;

import be.mathiasdejong.endercrop.neoforge.compat.TOPCompatibility;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

@Mod(MOD_ID)
public class EnderCrop {

  public EnderCrop(IEventBus modBus, ModContainer modContainer) {
    ModExpectPlatformImpl.setModContainer(modContainer);
    be.mathiasdejong.endercrop.EnderCrop.init();
    modBus.addListener(EnderCrop::onEnqueueIMC);
  }

  public static void onEnqueueIMC(InterModEnqueueEvent event) {
    if (ModList.get().isLoaded("theoneprobe")) TOPCompatibility.register();
  }
}
