package be.mathiasdejong.endercrop;

import be.mathiasdejong.endercrop.config.EnderCropConfiguration;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

public final class HoeHelper {

  public static boolean canTillEndstone(ItemStack itemStack, @Nullable Player player) {
    final Item item = itemStack.getItem();
    if (player == null) return false;
    if (player.isCreative()) return true;
    if (!(item instanceof HoeItem)) return false;

    if (!EnderCropConfiguration.endstoneNeedsUnbreaking.get()) return true;

    return EnchantmentHelper.getItemEnchantmentLevel(
            player
                .level()
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.UNBREAKING),
            itemStack)
        > 0;
  }

  private HoeHelper() {}
}
