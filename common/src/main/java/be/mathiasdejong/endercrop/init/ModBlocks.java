package be.mathiasdejong.endercrop.init;

import be.mathiasdejong.endercrop.ModExpectPlatform;
import be.mathiasdejong.endercrop.Reference;
import be.mathiasdejong.endercrop.block.EnderCropBlock;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public final class ModBlocks {

  public static final DeferredRegister<Block> BLOCKS =
      DeferredRegister.create(Reference.MOD_ID, Registries.BLOCK);

  public static final RegistrySupplier<Block> ENDER_CROP =
      BLOCKS.register(Reference.Blocks.ENDER_CROP, EnderCropBlock::new);

  public static final RegistrySupplier<Block> TILLED_END_STONE =
      BLOCKS.register(
          Reference.Blocks.TILLED_END_STONE, ModExpectPlatform::getTillendEndstoneBlock);
}
