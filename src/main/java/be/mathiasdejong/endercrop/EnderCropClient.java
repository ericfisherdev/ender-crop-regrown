package be.mathiasdejong.endercrop;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Client-only mod entry point. Registers NeoForge's built-in {@link ConfigurationScreen} so the
 * mod's {@code ModConfigSpec} settings are editable from the in-game Mods list. On the {@code
 * 1.21.1} line this in-game config UI existed only on Fabric (via Mod Menu + Cloth Config); the
 * NeoForge-native screen replaces both with no third-party dependency.
 */
@Mod(value = Reference.MOD_ID, dist = Dist.CLIENT)
public final class EnderCropClient {

  public EnderCropClient(ModContainer container) {
    container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
  }
}
