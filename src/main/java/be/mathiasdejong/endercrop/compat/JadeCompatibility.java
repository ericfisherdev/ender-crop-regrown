package be.mathiasdejong.endercrop.compat;

import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.ChatFormatting.YELLOW;

import be.mathiasdejong.endercrop.Reference;
import be.mathiasdejong.endercrop.block.EnderCropBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

/** Jade in-world HUD integration. Discovered by Jade's {@code @WailaPlugin} scan. */
@WailaPlugin
public class JadeCompatibility implements IWailaPlugin {

  @Override
  public void registerClient(IWailaClientRegistration registration) {
    registration.registerBlockComponent(EnderCropGrowthProvider.INSTANCE, EnderCropBlock.class);
    registration.registerBlockComponent(EndStoneTillingProvider.INSTANCE, Block.class);
  }

  public enum EnderCropGrowthProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final Component NO_GROWTH =
        Component.translatable("endercrop.wailatop.nogrowth").withStyle(RED);
    private static final Component LIGHT_LEVEL =
        Component.translatable("endercrop.wailatop.light").append(": ").withStyle(YELLOW);

    @Override
    public void appendTooltip(
        ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
      final CompatHudInfo info =
          CompatHudInfo.forCrop(
              blockAccessor.getBlockState(), blockAccessor.getLevel(), blockAccessor.getPosition());
      if (info.isCropNotGrowing) {
        tooltip.add(NO_GROWTH);
        if (blockAccessor.getPlayer().isCrouching()) {
          tooltip.add(
              LIGHT_LEVEL
                  .copy()
                  .append(Component.literal(info.cropLightLevel + " (>7)").withStyle(RED)));
        }
      }
    }

    @Override
    public Identifier getUid() {
      return Identifier.fromNamespaceAndPath(Reference.MOD_ID, Reference.Blocks.ENDER_CROP);
    }
  }

  public enum EndStoneTillingProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final Component CHECK = Component.literal("✔ ").withStyle(GREEN);
    private static final Component CROSS = Component.literal("✕ ").withStyle(RED);
    private static final Component TILL = Component.translatable("endercrop.waila.till");
    private static final Component UNBREAKING_HINT =
        Component.literal(" (")
            .append(
                Component.translatable("enchantment.minecraft.unbreaking")
                    .append(" I+")
                    .withStyle(RED))
            .append(")");

    @Override
    public void appendTooltip(
        ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
      if (!blockAccessor.getBlock().equals(Blocks.END_STONE)) return;
      final CompatHudInfo info = CompatHudInfo.forEndstone(blockAccessor.getPlayer());
      if (!info.isHoldingHoe) return;
      tooltip.add(
          (info.canTill ? CHECK : CROSS)
              .copy()
              .append(TILL.copy().append(unbreakingHint(info.canTill, info.needsUnbreaking))));
    }

    private Component unbreakingHint(boolean canTill, boolean needsUnbreaking) {
      if (needsUnbreaking && !canTill) return UNBREAKING_HINT;
      else return Component.empty();
    }

    @Override
    public Identifier getUid() {
      return Identifier.fromNamespaceAndPath(Reference.MOD_ID, Reference.Blocks.TILLED_END_STONE);
    }
  }
}
