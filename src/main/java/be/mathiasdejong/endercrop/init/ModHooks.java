package be.mathiasdejong.endercrop.init;

import be.mathiasdejong.endercrop.HoeHelper;
import be.mathiasdejong.endercrop.config.EnderCropConfiguration;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class ModHooks {

  private static final Component UNTILLABLE_MESSAGE =
      Component.translatable("endercrop.alert.hoe")
          .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true));

  @SubscribeEvent
  public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
    final ItemStack stack = event.getItemStack();
    if (!(stack.getItem() instanceof HoeItem)) return;
    if (event.getFace() == Direction.DOWN) return;
    if (!EnderCropConfiguration.tilledEndStone.get()) return;

    final Level level = event.getLevel();
    final BlockPos pos = event.getPos();
    final BlockState clicked = level.getBlockState(pos);
    if (!clicked.is(Blocks.END_STONE)) return;
    if (!level.getBlockState(pos.above()).isAir()) return;

    final Player player = event.getEntity();
    if (!HoeHelper.canTillEndstone(stack, player)) {
      if (player instanceof ServerPlayer serverPlayer) {
        serverPlayer.sendSystemMessage(UNTILLABLE_MESSAGE, true);
      }
      event.setCanceled(true);
      return;
    }

    if (!level.isClientSide()) {
      level.setBlock(pos, ModBlocks.TILLED_END_STONE.get().defaultBlockState(), 11);
      final EquipmentSlot slot =
          event.getHand() == InteractionHand.MAIN_HAND
              ? EquipmentSlot.MAINHAND
              : EquipmentSlot.OFFHAND;
      stack.hurtAndBreak(1, (LivingEntity) player, slot);
    }
    event.setCanceled(true);
  }

  private ModHooks() {}
}
