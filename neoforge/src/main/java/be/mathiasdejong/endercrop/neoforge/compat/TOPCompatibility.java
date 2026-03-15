package be.mathiasdejong.endercrop.neoforge.compat;

import be.mathiasdejong.endercrop.Reference;
import be.mathiasdejong.endercrop.compat.CompatHudInfo;
import be.mathiasdejong.endercrop.init.ModBlocks;
import com.google.common.base.Function;
import javax.annotation.Nullable;
import mcjty.theoneprobe.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.InterModComms;

public final class TOPCompatibility implements Function<ITheOneProbe, Void> {
  public static ITheOneProbe probe;

  public static void register() {
    InterModComms.sendTo("theoneprobe", "GetTheOneProbe", TOPCompatibility::new);
  }

  @Nullable @Override
  public Void apply(ITheOneProbe theOneProbe) {
    probe = theOneProbe;
    probe.registerProvider(
        new IProbeInfoProvider() {
          @Override
          public ResourceLocation getID() {
            return ResourceLocation.withDefaultNamespace(Reference.MOD_ID);
          }

          @Override
          public void addProbeInfo(
              ProbeMode mode,
              IProbeInfo probeInfo,
              Player player,
              Level world,
              BlockState blockState,
              IProbeHitData data) {
            if (blockState.is(ModBlocks.TILLED_END_STONE.get())) {
              final CompatHudInfo info = CompatHudInfo.forTilledEndstone(blockState);
              if (mode == ProbeMode.EXTENDED) {
                if (info.isMoist) {
                  probeInfo.text(CompoundText.create().label("{*endercrop.wailatop.moist*}"));
                } else {
                  probeInfo.text(CompoundText.create().label("{*endercrop.wailatop.dry*}"));
                }
              }
              if (mode == ProbeMode.DEBUG) {
                probeInfo.text(CompoundText.create().labelInfo("MOISTURE: ", info.moistureValue));
              }
            } else if (blockState.is(ModBlocks.ENDER_CROP.get())) {
              final CompatHudInfo info = CompatHudInfo.forCrop(blockState, world, data.getPos());
              if (info.isCropNotGrowing) {
                probeInfo.text(CompoundText.create().error("{*endercrop.wailatop.nogrowth*}"));
                if (mode == ProbeMode.EXTENDED) {
                  probeInfo.text(
                      CompoundText.create()
                          .label("{*endercrop.wailatop.light*}: ")
                          .info(String.valueOf(info.cropLightLevel))
                          .error(" (>7)"));
                }
              }
            } else if (blockState.is(Blocks.END_STONE)) {
              final CompatHudInfo info = CompatHudInfo.forEndstone(player);
              if (info.isHoldingHoe) {
                final IProbeInfo hori =
                    probeInfo.horizontal(
                        probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                if (info.canTill) {
                  hori.icon(
                      ResourceLocation.fromNamespaceAndPath(
                          "theoneprobe", "textures/gui/icons.png"),
                      0,
                      16,
                      13,
                      13,
                      probeInfo
                          .defaultIconStyle()
                          .width(18)
                          .height(14)
                          .textureWidth(32)
                          .textureHeight(32));
                  hori.text(CompoundText.create().ok("{*endercrop.top.hoe*}"));
                } else {
                  hori.icon(
                      ResourceLocation.fromNamespaceAndPath(
                          "theoneprobe", "textures/gui/icons.png"),
                      16,
                      16,
                      13,
                      13,
                      probeInfo
                          .defaultIconStyle()
                          .width(18)
                          .height(14)
                          .textureWidth(32)
                          .textureHeight(32));
                  hori.text(
                      CompoundText.create()
                          .warning(
                              "{*endercrop.top.hoe*}"
                                  + (info.needsUnbreaking
                                      ? " ({*enchantment.minecraft.unbreaking*} I+)"
                                      : "")));
                }
              }
            }
          }
        });
    return null;
  }
}
