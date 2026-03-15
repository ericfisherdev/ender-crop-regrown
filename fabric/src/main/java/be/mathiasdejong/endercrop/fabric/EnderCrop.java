package be.mathiasdejong.endercrop.fabric;

import be.mathiasdejong.endercrop.init.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class EnderCrop implements ModInitializer {

  @Override
  public void onInitialize() {
    be.mathiasdejong.endercrop.EnderCrop.init();
    BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ENDER_CROP.get(), RenderType.cutout());
  }
}
