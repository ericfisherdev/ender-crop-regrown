package be.mathiasdejong.endercrop;

import be.mathiasdejong.endercrop.config.EnderCropConfiguration;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HoeHelper {

  public static boolean canTillEndstone(@NotNull ItemStack itemStack, @Nullable Player player) {
    if (player == null || !(itemStack.getItem() instanceof HoeItem)) return false;
    if (player.isCreative()) return true;
    if (!EnderCropConfiguration.endstoneNeedsUnbreaking.get()) return true;
    return hasUnbreaking(itemStack, player);
  }

  /**
   * Returns the hoe the player is holding in either hand, or {@link ItemStack#EMPTY} if neither
   * hand holds a hoe. Used by the Jade HUD provider to decide whether to show tilling info.
   */
  public static ItemStack holdingHoeTool(Player player) {
    for (InteractionHand hand : InteractionHand.values()) {
      final ItemStack itemStack = player.getItemInHand(hand);
      if (itemStack.getItem() instanceof HoeItem) return itemStack;
    }
    return ItemStack.EMPTY;
  }

  private static boolean hasUnbreaking(@NotNull ItemStack itemStack, @NotNull Player player) {
    return player
        .level()
        .registryAccess()
        .lookup(Registries.ENCHANTMENT)
        .flatMap(enchantments -> enchantments.get(Enchantments.UNBREAKING))
        .map(unbreaking -> EnchantmentHelper.getItemEnchantmentLevel(unbreaking, itemStack) > 0)
        .orElse(false);
  }

  private HoeHelper() {}
}
