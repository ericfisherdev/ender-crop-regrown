package be.mathiasdejong.endercrop.init;

import be.mathiasdejong.endercrop.Reference;
import be.mathiasdejong.endercrop.block.EnderCropBlock;
import be.mathiasdejong.endercrop.block.TilledEndstoneBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {

  public static final DeferredRegister.Blocks BLOCKS =
      DeferredRegister.createBlocks(Reference.MOD_ID);

  public static final DeferredBlock<EnderCropBlock> ENDER_CROP =
      BLOCKS.register(Reference.Blocks.ENDER_CROP, EnderCropBlock::new);

  public static final DeferredBlock<TilledEndstoneBlock> TILLED_END_STONE =
      BLOCKS.register(Reference.Blocks.TILLED_END_STONE, TilledEndstoneBlock::new);

  private ModBlocks() {}
}
