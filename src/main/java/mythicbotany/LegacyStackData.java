package mythicbotany;

import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import vazkii.botania.api.item.CosmeticAttachable;
import vazkii.botania.api.item.PhantomInkable;

/** Migrates item data that vanilla's item-stack data fixer cannot see inside legacy custom NBT. */
final class LegacyStackData {

    private static final String MOD_ID = "mythicbotany";
    private static final String COSMETIC_ITEM = "cosmeticItem";
    private static final String PHANTOM_INK = "phantomInk";
    private static final int LEGACY_DATA_VERSION = 3465; // Minecraft 1.20.1

    private LegacyStackData() {

    }

    static void migrate(Player player) {
        HolderLookup.Provider registries = player.registryAccess();
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            migrate(player.getInventory().getItem(slot), registries);
        }

        CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
            curios.getCurios().values().forEach(handler -> {
                migrate(handler.getStacks(), registries);
                migrate(handler.getCosmeticStacks(), registries);
            });
        });
    }

    private static void migrate(IDynamicStackHandler handler, HolderLookup.Provider registries) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (migrate(stack, registries)) {
                // Mutating a stack's components in place does not invoke the
                // Curios handler's contents-changed callback. Re-setting the
                // same stack marks the slot for persistence and client sync.
                handler.setStackInSlot(slot, stack);
            }
        }
    }

    private static boolean migrate(ItemStack stack, HolderLookup.Provider registries) {
        if (stack.isEmpty() || !MOD_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace())) {
            return false;
        }

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return false;
        }

        CompoundTag tag = customData.copyTag();
        boolean changed = false;

        if (stack.getItem() instanceof PhantomInkable phantomInkable && tag.contains(PHANTOM_INK, Tag.TAG_BYTE)) {
            if (tag.getBoolean(PHANTOM_INK)) {
                phantomInkable.setPhantomInk(stack, true);
            }
            tag.remove(PHANTOM_INK);
            changed = true;
        }

        if (stack.getItem() instanceof CosmeticAttachable cosmeticAttachable && tag.contains(COSMETIC_ITEM, Tag.TAG_COMPOUND)) {
            CompoundTag legacyCosmetic = tag.getCompound(COSMETIC_ITEM);
            boolean migrated = legacyCosmetic.isEmpty() || !cosmeticAttachable.getCosmeticItem(stack).isEmpty();
            if (!migrated) {
                ItemStack cosmetic = parseLegacyItem(legacyCosmetic, registries);
                if (!cosmetic.isEmpty()) {
                    cosmeticAttachable.setCosmeticItem(stack, cosmetic);
                    migrated = true;
                }
            }
            // Keep unresolved optional-mod cosmetics in legacy data so they can
            // be migrated later if their defining mod becomes available again.
            if (migrated) {
                tag.remove(COSMETIC_ITEM);
                changed = true;
            }
        }

        if (changed) {
            if (tag.isEmpty()) {
                stack.remove(DataComponents.CUSTOM_DATA);
            } else {
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
        }
        return changed;
    }

    private static ItemStack parseLegacyItem(CompoundTag legacy, HolderLookup.Provider registries) {
        if (legacy.isEmpty()) {
            return ItemStack.EMPTY;
        }

        try {
            CompoundTag fixed = (CompoundTag) DataFixers.getDataFixer().update(
                    References.ITEM_STACK,
                    new Dynamic<>(NbtOps.INSTANCE, legacy.copy()),
                    LEGACY_DATA_VERSION,
                    SharedConstants.getCurrentVersion().getDataVersion().getVersion()
            ).getValue();
            ItemStack stack = ItemStack.parseOptional(registries, fixed);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }

            // NeoForge 1.20 item capabilities and any other unknown root keys
            // are intentionally outside vanilla's current ItemStack codec.
            // Preserve that remainder in custom_data instead of discarding it.
            CompoundTag remainder = fixed.copy();
            remainder.remove("id");
            remainder.remove("count");
            remainder.remove("components");
            if (!remainder.isEmpty()) {
                CompoundTag customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                customData.merge(remainder);
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));
            }
            return stack;
        } catch (RuntimeException ignored) {
            return ItemStack.EMPTY;
        }
    }
}
