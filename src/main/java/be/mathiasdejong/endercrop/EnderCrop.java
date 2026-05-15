package be.mathiasdejong.endercrop;

import be.mathiasdejong.endercrop.config.EnderCropConfiguration;
import be.mathiasdejong.endercrop.init.ModBlocks;
import be.mathiasdejong.endercrop.init.ModHooks;
import be.mathiasdejong.endercrop.init.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public final class EnderCrop {

  public static final Logger LOGGER = LogManager.getLogger();

  public EnderCrop(IEventBus modBus, ModContainer container) {
    ModBlocks.BLOCKS.register(modBus);
    ModItems.ITEMS.register(modBus);

    container.registerConfig(
        ModConfig.Type.COMMON, EnderCropConfiguration.COMMON_CONFIG, Reference.CONFIG_FILE);
    modBus.addListener(EnderCrop::onConfigLoad);
    modBus.addListener(EnderCrop::onConfigReload);
    modBus.addListener(ModItems::onBuildCreativeTabs);

    NeoForge.EVENT_BUS.register(ModHooks.class);
  }

  @SubscribeEvent
  public static void onConfigLoad(ModConfigEvent.Loading event) {
    EnderCropConfiguration.onLoad(
        event.getConfig().getFileName(), event.getConfig().getLoadedConfig());
  }

  @SubscribeEvent
  public static void onConfigReload(ModConfigEvent.Reloading event) {
    EnderCropConfiguration.onLoad(
        event.getConfig().getFileName(), event.getConfig().getLoadedConfig());
  }
}
