package be.mathiasdejong.endercrop.init;

import be.mathiasdejong.endercrop.Reference;
import be.mathiasdejong.endercrop.item.EnderSeedsItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {

  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Reference.MOD_ID);

  public static final DeferredItem<EnderSeedsItem> ENDER_SEEDS =
      ITEMS.registerItem(Reference.Items.SEEDS, EnderSeedsItem::new);

  public static final DeferredItem<BlockItem> TILLED_END_STONE =
      ITEMS.registerSimpleBlockItem(Reference.Blocks.TILLED_END_STONE, ModBlocks.TILLED_END_STONE);

  public static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
      event.accept(ENDER_SEEDS);
      event.accept(TILLED_END_STONE);
    }
  }

  private ModItems() {}
}
