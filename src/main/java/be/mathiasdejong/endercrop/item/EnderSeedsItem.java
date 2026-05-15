package be.mathiasdejong.endercrop.item;

import be.mathiasdejong.endercrop.init.ModBlocks;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class EnderSeedsItem extends BlockItem {

  public EnderSeedsItem(Item.Properties properties) {
    super(ModBlocks.ENDER_CROP.get(), properties.useBlockDescriptionPrefix());
  }

  @Override
  public void appendHoverText(
      ItemStack stack,
      Item.TooltipContext context,
      TooltipDisplay display,
      Consumer<Component> tooltip,
      TooltipFlag flag) {
    super.appendHoverText(stack, context, display, tooltip, flag);
    tooltip.accept(Component.translatable("endercrop.tip.seed"));
  }
}
