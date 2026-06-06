package mythicbotany;

import mythicbotany.alftools.GreatestManaRing;
import mythicbotany.base.BlockEntityMana;
import mythicbotany.functionalflora.base.FunctionalFlowerBase;
import mythicbotany.infuser.TileManaInfuser;
import mythicbotany.mimir.TileYggdrasilBranch;
import mythicbotany.register.ModBlocks;
import mythicbotany.register.ModItems;
import mythicbotany.rune.TileRuneHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;
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
import vazkii.botania.common.impl.mana.DefaultManaItemImpl;

public final class ModCapabilities {

    private ModCapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        registerInfuser(event, ModBlocks.manaInfuser.getBlockEntityType(), true);
        registerManaBlock(event, ModBlocks.manaCollector.getBlockEntityType(), true);
        registerManaBlock(event, ModBlocks.alfsteelPylon.getBlockEntityType(), false);
        registerManaBlock(event, ModBlocks.yggdrasilBranch.getBlockEntityType(), false);

        registerFlower(event, ModBlocks.exoblaze.getBlockEntityType());
        registerFlower(event, ModBlocks.witherAconite.getBlockEntityType());
        registerFlower(event, ModBlocks.aquapanthus.getBlockEntityType());
        registerFlower(event, ModBlocks.hellebore.getBlockEntityType());
        registerFlower(event, ModBlocks.raindeletia.getBlockEntityType());
        registerFlower(event, ModBlocks.feysythia.getBlockEntityType());
        registerFlower(event, ModBlocks.petrunia.getBlockEntityType());

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlocks.runeHolder.getBlockEntityType(), (TileRuneHolder be, net.minecraft.core.Direction direction) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlocks.yggdrasilBranch.getBlockEntityType(), (TileYggdrasilBranch be, net.minecraft.core.Direction direction) -> be.getInventory());

        var manaItem = itemApi(ManaItem.LOOKUP);
        event.registerItem(manaItem, (stack, ignored) -> new DefaultManaItemImpl(stack), ModItems.alfsteelPick);
        event.registerItem(manaItem, (stack, ignored) -> new GreatestRingManaItem(stack), ModItems.manaRingGreatest);
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

    private record GreatestRingManaItem(ItemStack stack, DefaultManaItemImpl delegate) implements ManaItem {

        private GreatestRingManaItem(ItemStack stack) {
            this(stack, new DefaultManaItemImpl(stack));
        }

        @Override
        public int getMana() {
            return this.delegate.getMana();
        }

        @Override
        public int getMaxMana() {
            return GreatestManaRing.MAX_MANA * this.stack.getCount();
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
            return this.delegate.acceptDispatchedManaFromItem(otherStack);
        }

        @Override
        public boolean refuseRequestedManaFromItem(ItemStack otherStack) {
            return this.delegate.refuseRequestedManaFromItem(otherStack);
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
