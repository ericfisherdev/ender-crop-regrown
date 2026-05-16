package be.mathiasdejong.endercrop.compat;

import be.mathiasdejong.endercrop.HoeHelper;
import be.mathiasdejong.endercrop.block.EnderCropBlock;
import be.mathiasdejong.endercrop.config.EnderCropConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/** Shared lookup results consumed by the Jade HUD providers in {@link JadeCompatibility}. */
public final class CompatHudInfo {

  // --- Ender crop growth ---
  public final boolean isCropNotGrowing;
  public final int cropLightLevel;

  // --- Endstone tillability ---
  public final boolean isHoldingHoe;
  public final boolean canTill;
  public final boolean needsUnbreaking;

  private CompatHudInfo(
      boolean isCropNotGrowing,
      int cropLightLevel,
      boolean isHoldingHoe,
      boolean canTill,
      boolean needsUnbreaking) {
    this.isCropNotGrowing = isCropNotGrowing;
    this.cropLightLevel = cropLightLevel;
    this.isHoldingHoe = isHoldingHoe;
    this.canTill = canTill;
    this.needsUnbreaking = needsUnbreaking;
  }

  public static CompatHudInfo forCrop(BlockState blockState, LevelReader level, BlockPos pos) {
    // Jade registers EnderCropGrowthProvider for EnderCropBlock only, but guard the
    // cast anyway so a misregistration degrades to "no info" instead of crashing.
    if (!(blockState.getBlock() instanceof EnderCropBlock crop)) {
      return new CompatHudInfo(false, -1, false, false, false);
    }
    BlockState soilState = level.getBlockState(pos.below());
    boolean notGrowing =
        !crop.isMaxAge(blockState) && !EnderCropBlock.hasSufficientLight(soilState, level, pos);
    int light = notGrowing ? level.getRawBrightness(pos, 0) : -1;
    return new CompatHudInfo(notGrowing, light, false, false, false);
  }

  public static CompatHudInfo forEndstone(Player player) {
    if (!EnderCropConfiguration.tilledEndStone.get()) {
      return new CompatHudInfo(false, -1, false, false, false);
    }
    ItemStack hoe = HoeHelper.holdingHoeTool(player);
    boolean holding = !hoe.isEmpty();
    boolean canTill = holding && HoeHelper.canTillEndstone(hoe, player);
    boolean needsUnbreaking = EnderCropConfiguration.endstoneNeedsUnbreaking.get();
    return new CompatHudInfo(false, -1, holding, canTill, needsUnbreaking);
  }
}
