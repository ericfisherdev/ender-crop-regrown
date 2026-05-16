package be.mathiasdejong.endercrop.init;

import be.mathiasdejong.endercrop.Reference;
import be.mathiasdejong.endercrop.block.EnderCropBlock;
import be.mathiasdejong.endercrop.block.TilledEndstoneBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {

  public static final DeferredRegister.Blocks BLOCKS =
      DeferredRegister.createBlocks(Reference.MOD_ID);

  // registerBlock(name, factory, properties-supplier) lets DeferredRegister set the
  // block id on the Properties before the block is constructed. The plain
  // register(name, supplier) form skips that step, which crashes on load under
  // MC 1.21.2+ ("Block id not set").
  public static final DeferredBlock<EnderCropBlock> ENDER_CROP =
      BLOCKS.registerBlock(
          Reference.Blocks.ENDER_CROP, EnderCropBlock::new, EnderCropBlock::baseProperties);

  public static final DeferredBlock<TilledEndstoneBlock> TILLED_END_STONE =
      BLOCKS.registerBlock(
          Reference.Blocks.TILLED_END_STONE,
          TilledEndstoneBlock::new,
          TilledEndstoneBlock::baseProperties);

  private ModBlocks() {}
}
