package be.mathiasdejong.endercrop.block;

import static net.minecraft.world.level.block.Blocks.END_STONE;

import be.mathiasdejong.endercrop.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.CommonHooks;

public class TilledEndstoneBlock extends FarmlandBlock {

  public TilledEndstoneBlock(Properties properties) {
    super(properties);
  }

  /**
   * Base block properties. Supplied to {@code DeferredRegister.Blocks#registerBlock} so the
   * registry sets the block id on them before the block is constructed — MC 1.21.2+ rejects a
   * {@code Properties} instance that has no id.
   */
  public static Properties baseProperties() {
    return Properties.of()
        .mapColor(MapColor.SAND)
        .randomTicks()
        .destroyTime(0.6F)
        .requiresCorrectToolForDrops()
        .strength(3.0F, 9.0F)
        .sound(SoundType.GRAVEL);
  }

  @Override
  public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
    return state.is(this) && state.getValue(MOISTURE) > 0;
  }

  @Override
  public TriState canSustainPlant(
      BlockState state, BlockGetter level, BlockPos pos, Direction facing, BlockState plantState) {
    return plantState.is(ModBlocks.ENDER_CROP.get()) ? TriState.TRUE : TriState.DEFAULT;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
        ? END_STONE.defaultBlockState()
        : super.getStateForPlacement(context);
  }

  @Override
  protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource pRand) {
    if (!state.canSurvive(level, pos)) turnToEndStone(state, level, pos);
  }

  @Override
  protected void randomTick(
      BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    final int moisture = state.getValue(MOISTURE);
    if (!isNearWater(level, pos) && !level.isRainingAt(pos.above())) {
      if (moisture > 0) {
        level.setBlock(pos, state.setValue(MOISTURE, moisture - 1), 2);
      } else if (!hasCropAbove(level, pos)) {
        turnToEndStone(state, level, pos);
      }
    } else if (moisture < MAX_MOISTURE) {
      level.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), 2);
    }
  }

  @Override
  public void fallOn(
      Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
    if (level instanceof ServerLevel serverLevel
        && CommonHooks.onFarmlandTrample(serverLevel, pos, state, (float) fallDistance, entity)) {
      turnToEndStone(level.getBlockState(pos), level, pos);
    }
    entity.causeFallDamage((float) fallDistance, 1.0F, entity.damageSources().fall());
  }

  public static void turnToEndStone(BlockState state, Level level, BlockPos pos) {
    level.setBlockAndUpdate(pos, pushEntitiesUp(state, END_STONE.defaultBlockState(), level, pos));
  }

  private static boolean hasCropAbove(BlockGetter level, BlockPos pos) {
    return level.getBlockState(pos.above()).is(BlockTags.MAINTAINS_FARMLAND);
  }
}
