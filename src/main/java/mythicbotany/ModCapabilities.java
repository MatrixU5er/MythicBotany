package mythicbotany;

import mythicbotany.base.BlockEntityMana;
import mythicbotany.functionalflora.base.FunctionalFlowerBase;
import mythicbotany.infuser.TileManaInfuser;
import mythicbotany.mimir.TileYggdrasilBranch;
import mythicbotany.register.ModBlocks;
import mythicbotany.register.ModItems;
import mythicbotany.rune.TileCentralRuneHolder;
import mythicbotany.rune.TileRuneHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import vazkii.botania.api.capability.BlockApiNoContext;
import vazkii.botania.api.capability.BlockApiWithContext;
import vazkii.botania.api.capability.ItemApiNoContext;
import vazkii.botania.api.block.WandBindable;
import vazkii.botania.api.block.WandHUD;
import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.mana.spark.SparkAttachable;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.impl.mana.DefaultManaItemImpl;

public final class ModCapabilities {

    private static final TagKey<Item> TERRA_PICK_BLACKLIST = ItemTags.create(ResourceLocation.fromNamespaceAndPath("botania", "terra_pick_blacklist"));

    private ModCapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        registerInfuser(event, ModBlocks.manaInfuser.getBlockEntityType(), true);
        registerManaBlock(event, ModBlocks.manaCollector.getBlockEntityType(), true);
        registerManaBlock(event, ModBlocks.alfsteelPylon.getBlockEntityType(), false);
        registerManaBlock(event, ModBlocks.yggdrasilBranch.getBlockEntityType(), false);

        registerFlower(event, ModBlocks.exoblaze.getBlockEntityType());
        registerFlower(event, ModBlocks.exoblaze.getFloatingBlock().getBlockEntityType());
        registerFlower(event, ModBlocks.witherAconite.getBlockEntityType());
        registerFlower(event, ModBlocks.witherAconite.getFloatingBlock().getBlockEntityType());
        registerFlower(event, ModBlocks.aquapanthus.getBlockEntityType());
        registerFlower(event, ModBlocks.aquapanthus.getFloatingBlock().getBlockEntityType());
        registerFlower(event, ModBlocks.hellebore.getBlockEntityType());
        registerFlower(event, ModBlocks.hellebore.getFloatingBlock().getBlockEntityType());
        registerFlower(event, ModBlocks.raindeletia.getBlockEntityType());
        registerFlower(event, ModBlocks.raindeletia.getFloatingBlock().getBlockEntityType());
        registerFlower(event, ModBlocks.feysythia.getBlockEntityType());
        registerFlower(event, ModBlocks.feysythia.getFloatingBlock().getBlockEntityType());
        registerFlower(event, ModBlocks.petrunia.getBlockEntityType());
        registerFlower(event, ModBlocks.petrunia.getFloatingBlock().getBlockEntityType());

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlocks.runeHolder.getBlockEntityType(), (TileRuneHolder be, net.minecraft.core.Direction direction) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlocks.centralRuneHolder.getBlockEntityType(), (TileCentralRuneHolder be, net.minecraft.core.Direction direction) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlocks.yggdrasilBranch.getBlockEntityType(), (TileYggdrasilBranch be, net.minecraft.core.Direction direction) -> be.getInventory());

        var manaItem = itemApi(ManaItem.LOOKUP);
        event.registerItem(manaItem, (stack, ignored) -> new AlfsteelPickManaItem(stack), ModItems.alfsteelPick);
        event.registerItem(manaItem, (stack, ignored) -> defaultManaItem(stack), ModItems.manaRingGreatest);
    }

    private static <T extends BlockEntityMana> void registerManaBlock(RegisterCapabilitiesEvent event, BlockEntityType<T> type, boolean sparkAttachable) {
        event.registerBlockEntity(blockApi(ManaReceiver.LOOKUP), type, (be, direction) -> be);
        event.registerBlockEntity(blockApi(Wandable.LOOKUP), type, (be, direction) -> be);
        event.registerBlockEntity(blockApi(WandHUD.BLOCK_LOOKUP), type, (be, ignored) -> be);
        if (sparkAttachable) {
            event.registerBlockEntity(blockApi(SparkAttachable.LOOKUP), type, (be, ignored) -> be);
        }
    }

    private static void registerInfuser(RegisterCapabilitiesEvent event, BlockEntityType<TileManaInfuser> type, boolean sparkAttachable) {
        event.registerBlockEntity(blockApi(ManaReceiver.LOOKUP), type, (be, direction) -> be);
        if (sparkAttachable) {
            event.registerBlockEntity(blockApi(SparkAttachable.LOOKUP), type, (be, ignored) -> be);
        }
    }

    private static <T extends FunctionalFlowerBase> void registerFlower(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
        event.registerBlockEntity(blockApi(Wandable.LOOKUP), type, (be, direction) -> be);
        event.registerBlockEntity(blockApi(WandBindable.LOOKUP), type, (be, direction) -> be);
        event.registerBlockEntity(blockApi(WandHUD.BLOCK_LOOKUP), type, (be, ignored) -> be);
    }

    private static <A> BlockCapability<A, Void> blockApi(BlockApiNoContext<A> api) {
        return BlockCapability.create(api.getId(), api.getApiClass(), Void.class);
    }

    private static <A, C> BlockCapability<A, C> blockApi(BlockApiWithContext<A, C> api) {
        return BlockCapability.create(api.getId(), api.getApiClass(), api.getContextClass());
    }

    private static <A> ItemCapability<A, Void> itemApi(ItemApiNoContext<A> api) {
        return ItemCapability.create(api.getId(), api.getApiClass(), Void.class);
    }

    private static DefaultManaItemImpl defaultManaItem(ItemStack stack) {
        migrateLegacyMana(stack);
        return new DefaultManaItemImpl(stack);
    }

    private static void migrateLegacyMana(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return;
        }

        var tag = customData.copyTag();
        if (!tag.contains("mana", Tag.TAG_INT)) {
            return;
        }
        int legacyMana = tag.getInt("mana");
        tag.remove("mana");
        if (!stack.has(BotaniaDataComponents.MANA)) {
            int maxMana = stack.getOrDefault(BotaniaDataComponents.MAX_MANA, 0);
            int migratedMana = Math.min(legacyMana, maxMana);
            if (migratedMana > 0) {
                stack.set(BotaniaDataComponents.MANA, migratedMana);
            }
        }
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
    }

    private record AlfsteelPickManaItem(DefaultManaItemImpl delegate) implements ManaItem {

        private AlfsteelPickManaItem(ItemStack stack) {
            this(defaultManaItem(stack));
        }

        @Override
        public int getMana() {
            return this.delegate.getMana();
        }

        @Override
        public int getMaxMana() {
            return this.delegate.getMaxMana();
        }

        @Override
        public void addMana(int mana) {
            this.delegate.addMana(mana);
        }

        @Override
        public boolean canReceiveManaFromPool(BlockEntity pool) {
            return this.delegate.canReceiveManaFromPool(pool);
        }

        @Override
        public boolean acceptDispatchedManaFromItem(ItemStack otherStack) {
            return !otherStack.is(TERRA_PICK_BLACKLIST);
        }

        @Override
        public boolean refuseRequestedManaFromItem(ItemStack otherStack) {
            return otherStack.is(TERRA_PICK_BLACKLIST);
        }

        @Override
        public boolean canDrainManaToPool(BlockEntity pool) {
            return this.delegate.canDrainManaToPool(pool);
        }

        @Override
        public boolean canSendRequestedManaToItem(ItemStack otherStack) {
            return this.delegate.canSendRequestedManaToItem(otherStack);
        }

        @Override
        public boolean isNoExport() {
            return this.delegate.isNoExport();
        }
    }
}
