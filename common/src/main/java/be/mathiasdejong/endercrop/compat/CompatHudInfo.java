package be.mathiasdejong.endercrop.compat;

import be.mathiasdejong.endercrop.HoeHelper;
import be.mathiasdejong.endercrop.block.EnderCropBlock;
import be.mathiasdejong.endercrop.block.TilledEndstoneBlock;
import be.mathiasdejong.endercrop.config.EnderCropConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Shared lookup results used by both the Jade and TOP HUD providers. */
public final class CompatHudInfo {

  // --- Ender crop growth ---
  public final boolean isCropNotGrowing;
  public final int cropLightLevel;

  // --- Tilled endstone moisture ---
  public final boolean isMoist;
  public final int moistureValue;

  // --- Endstone tillability ---
  public final boolean isHoldingHoe;
  public final boolean canTill;
  public final boolean needsUnbreaking;

  private CompatHudInfo(
      boolean isCropNotGrowing,
      int cropLightLevel,
      boolean isMoist,
      int moistureValue,
      boolean isHoldingHoe,
      boolean canTill,
      boolean needsUnbreaking) {
    this.isCropNotGrowing = isCropNotGrowing;
    this.cropLightLevel = cropLightLevel;
    this.isMoist = isMoist;
    this.moistureValue = moistureValue;
    this.isHoldingHoe = isHoldingHoe;
    this.canTill = canTill;
    this.needsUnbreaking = needsUnbreaking;
  }

  public static CompatHudInfo forCrop(BlockState blockState, LevelReader level, BlockPos pos) {
    EnderCropBlock crop = (EnderCropBlock) blockState.getBlock();
    boolean notGrowing =
        !crop.isMaxAge(blockState) && !EnderCropBlock.hasSufficientLight(level, pos);
    int light = notGrowing ? level.getRawBrightness(pos, 0) : -1;
    return new CompatHudInfo(notGrowing, light, false, -1, false, false, false);
  }

  public static CompatHudInfo forTilledEndstone(BlockState blockState) {
    int moisture = blockState.getValue(TilledEndstoneBlock.MOISTURE);
    return new CompatHudInfo(
        false, -1, moisture == FarmBlock.MAX_MOISTURE, moisture, false, false, false);
  }

  public static CompatHudInfo forEndstone(Player player) {
    if (!EnderCropConfiguration.tilledEndStone.get()) {
      return new CompatHudInfo(false, -1, false, -1, false, false, false);
    }
    ItemStack hoe = HoeHelper.holdingHoeTool(player);
    boolean holding = !hoe.isEmpty();
    boolean canTill = holding && HoeHelper.canTillEndstone(hoe, player);
    boolean needsUnbreaking = EnderCropConfiguration.endstoneNeedsUnbreaking.get();
    return new CompatHudInfo(false, -1, false, -1, holding, canTill, needsUnbreaking);
  }
}
