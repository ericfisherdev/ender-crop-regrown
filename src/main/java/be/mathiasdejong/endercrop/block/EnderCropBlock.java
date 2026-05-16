package be.mathiasdejong.endercrop.block;

import be.mathiasdejong.endercrop.config.EnderCropConfiguration;
import be.mathiasdejong.endercrop.init.ModBlocks;
import be.mathiasdejong.endercrop.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.NotNull;

public class EnderCropBlock extends CropBlock {

  private static final Properties PROPERTIES =
      Properties.of()
          .mapColor(MapColor.PLANT)
          .noCollision()
          .noOcclusion()
          .randomTicks()
          .instabreak()
          .sound(SoundType.CROP);

  public EnderCropBlock() {
    super(PROPERTIES);
  }

  static boolean isOnEndstone(BlockState soilState) {
    return soilState.is(ModBlocks.TILLED_END_STONE.get());
  }

  @Override
  protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
    return state.is(Blocks.FARMLAND) || state.is(ModBlocks.TILLED_END_STONE.get());
  }

  @Override
  protected void randomTick(
      BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (!level.isLoaded(pos)) return;
    final BlockState soilState = level.getBlockState(pos.below());
    if (hasSufficientLight(soilState, level, pos)) {
      final int age = this.getAge(state);
      if (!this.isMaxAge(state)) {
        final float growthChance = computeGrowthSpeed(this, soilState, level, pos);
        final boolean doGrow =
            growthChance > 0 && random.nextInt((int) (25.0F / growthChance) + 1) == 0;
        if (CommonHooks.canCropGrow(level, pos, state, doGrow)) {
          level.setBlock(pos, this.getStateForAge(age + 1), 2);
          CommonHooks.fireCropGrowPost(level, pos, state);
        }
      }
    }
  }

  public static boolean hasSufficientLight(
      BlockState soilState, LevelReader worldIn, BlockPos pos) {
    return isOnEndstone(soilState) || worldIn.getRawBrightness(pos, 0) <= 7;
  }

  // Reimplementing CropBlock#getGrowthSpeed so we can honour the
  // tilled-end-stone multiplier. The vanilla helper only checks farmland moisture.
  protected static float computeGrowthSpeed(
      Block block, BlockState centerSoilState, Level level, BlockPos pos) {
    float f = 1.0F;
    BlockPos soilOrigin = pos.below();
    BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

    for (int i = -1; i <= 1; ++i) {
      for (int j = -1; j <= 1; ++j) {
        float g = 0.0F;
        BlockState soilState =
            (i == 0 && j == 0)
                ? centerSoilState
                : level.getBlockState(mutable.setWithOffset(soilOrigin, i, 0, j));
        if (canSustainEnderCrop(soilState)) {
          g = 1.0F;
          if (soilState.hasProperty(FarmlandBlock.MOISTURE)
              && soilState.getValue(FarmlandBlock.MOISTURE) > 0) {
            g = 3.0F;
          }
        }
        if (i != 0 || j != 0) {
          g /= 4.0F;
        }
        f += g;
      }
    }

    BlockPos west = pos.west();
    BlockPos east = pos.east();
    BlockPos north = pos.north();
    BlockPos south = pos.south();
    boolean bl = level.getBlockState(west).is(block) || level.getBlockState(east).is(block);
    boolean bl2 = level.getBlockState(north).is(block) || level.getBlockState(south).is(block);
    if (bl && bl2) {
      f /= 2.0F;
    } else {
      boolean bl3 =
          level.getBlockState(west.north()).is(block)
              || level.getBlockState(east.north()).is(block)
              || level.getBlockState(east.south()).is(block)
              || level.getBlockState(west.south()).is(block);
      if (bl3) {
        f /= 2.0F;
      }
    }

    if (isOnEndstone(centerSoilState)) {
      f *= EnderCropConfiguration.tilledEndMultiplier.get();
    } else {
      f *= EnderCropConfiguration.tilledSoilMultiplier.get();
    }
    return f;
  }

  private static boolean canSustainEnderCrop(BlockState soilState) {
    return soilState.is(Blocks.FARMLAND) || soilState.is(ModBlocks.TILLED_END_STONE.get());
  }

  @Override
  protected int getBonemealAgeIncrease(Level level) {
    return 0;
  }

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    final BlockState soilState = level.getBlockState(pos.below());
    return hasSufficientLight(soilState, level, pos) && canSustainEnderCrop(soilState);
  }

  @Override
  protected @NotNull ItemLike getBaseSeedId() {
    return ModItems.ENDER_SEEDS.get();
  }

  @Override
  public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
    return false;
  }
}
