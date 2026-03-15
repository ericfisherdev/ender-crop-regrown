package be.mathiasdejong.endercrop.neoforge;

import static be.mathiasdejong.endercrop.Reference.MOD_ID;

import be.mathiasdejong.endercrop.ModExpectPlatform;
import be.mathiasdejong.endercrop.Reference;
import be.mathiasdejong.endercrop.block.EnderCropBlock;
import be.mathiasdejong.endercrop.block.TilledEndstoneBlock;
import be.mathiasdejong.endercrop.config.EnderCropConfiguration;
import be.mathiasdejong.endercrop.init.ModBlocks;
import dev.architectury.platform.hooks.EventBusesHooks;
import java.util.function.Consumer;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.TriState;

@ParametersAreNonnullByDefault
public class ModExpectPlatformImpl extends ModExpectPlatform {

  private static ModContainer modContainer;

  public static void setModContainer(ModContainer container) {
    modContainer = container;
  }

  public static Block getTillendEndstoneBlock() {
    return new TilledEndstoneBlock() {
      @Override
      public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(this) && state.getValue(MOISTURE) > 0;
      }

      @Override
      public TriState canSustainPlant(
          BlockState state,
          BlockGetter world,
          BlockPos pos,
          Direction facing,
          BlockState plantState) {
        return plantState.getBlock() == ModBlocks.ENDER_CROP.get()
            ? TriState.TRUE
            : TriState.DEFAULT;
      }
    };
  }

  public static void initConfig() {
    modContainer.registerConfig(
        ModConfig.Type.COMMON, EnderCropConfiguration.COMMON_CONFIG, Reference.CONFIG_FILE);
    EventBusesHooks.whenAvailable(
        MOD_ID,
        bus -> {
          final Consumer<ModConfigEvent> onLoad =
              (event) ->
                  EnderCropConfiguration.onLoad(
                      event.getConfig().getFileName(), event.getConfig().getLoadedConfig());
          bus.addListener((ModConfigEvent.Loading event) -> onLoad.accept(event));
          bus.addListener((ModConfigEvent.Reloading event) -> onLoad.accept(event));
        });
  }

  /* Neoforge-specific hooks */

  public static boolean onCropsGrowPre(
      ServerLevel level, BlockPos pos, BlockState state, boolean doGrow) {
    return CommonHooks.canCropGrow(level, pos, state, doGrow);
  }

  public static void onCropsGrowPost(ServerLevel level, BlockPos pos, BlockState state) {
    CommonHooks.fireCropGrowPost(level, pos, state);
  }

  public static boolean canSustainPlant(
      BlockState blockState,
      LevelReader level,
      BlockPos pos,
      Direction facing,
      EnderCropBlock crop) {
    return blockState
        .getBlock()
        .canSustainPlant(blockState, level, pos, facing, crop.defaultBlockState())
        .isTrue();
  }

  public static boolean onFarmlandTrample(
      Level level, BlockPos pos, BlockState blockState, float fallDistance, Entity entity) {
    // This uses the entity.canTrample logic and allows interception
    return CommonHooks.onFarmlandTrample(level, pos, blockState, fallDistance, entity);
  }
}
