package mythicbotany.mjoellnir;

import mythicbotany.config.MythicConfig;
import mythicbotany.register.ModEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class ItemMjoellnir extends BlockItem {

    private static final ResourceLocation DAMAGE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("mythicbotany", "mjoellnir_damage_modifier");
    private static final ResourceLocation SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("mythicbotany", "mjoellnir_attack_speed_modifier");
    
    public ItemMjoellnir(Block blockIn, Properties properties) {
        super(blockIn, properties);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            this.throwHammer(level, player, hand);
            if (MythicConfig.mjoellnir.ranged_cooldown > 0) {
                player.getCooldowns().addCooldown(this, MythicConfig.mjoellnir.ranged_cooldown);
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        } else {
            return super.use(level, player, hand);
        }
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        // the hammer can only be placed with shift
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            return super.useOn(context);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, @Nonnull BlockState state) {
        return BlockMjoellnir.placeInWorld(context.getItemInHand(), context.getLevel(), context.getClickedPos());
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            if (!BlockMjoellnir.canHold(player)) {
                BlockMjoellnir.putInWorld(stack.copy(), level, player.blockPosition());
                stack.shrink(stack.getCount());
                player.sendSystemMessage(Component.translatable("message.mythicbotany.mjoellnir_heavy_drop").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    private void throwHammer(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            ItemStack hammer = player.getItemInHand(hand).copy();
            player.setItemInHand(hand, ItemStack.EMPTY);
            Mjoellnir projectile = new Mjoellnir(level);
            projectile.setPos(player.getX(), player.getEyeY(), player.getZ());
            projectile.setStack(hammer);
            projectile.setThrower(player);
            projectile.setThrowPos(player.position());
            projectile.setHotBarSlot(BlockMjoellnir.getHotbarSlot(player, hand));
            projectile.setDeltaMovement(player.getLookAngle().multiply(1.2, 1.2, 1.2));
            level.addFreshEntity(projectile);
        }
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return this.isSupportedHammerEnchantment(enchantment);
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        return this.isSupportedHammerEnchantment(enchantment);
    }

    private boolean isSupportedHammerEnchantment(Holder<Enchantment> enchantment) {
        return (enchantment.value().isSupportedItem(Items.DIAMOND_SWORD.getDefaultInstance())
                || enchantment.is(ModEnchantments.HAMMER_MOBILITY)
                || enchantment.is(Enchantments.POWER) || enchantment.is(Enchantments.PUNCH)
                || enchantment.is(Enchantments.FLAME) || enchantment.is(Enchantments.LOYALTY))
                && !enchantment.is(Enchantments.SWEEPING_EDGE) && !enchantment.is(Enchantments.SMITE)
                && !enchantment.is(Enchantments.BANE_OF_ARTHROPODS);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return true;
    }

    @Nonnull
    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        int sharpness = ModEnchantments.getLevel(stack, Enchantments.SHARPNESS);
        int mobility = ModEnchantments.getLevel(stack, ModEnchantments.HAMMER_MOBILITY);
        float dmgModifier = sharpness > 0 ? 0.5f * sharpness + 0.5f : 0;
        float speedModifier = MythicConfig.mjoellnir.attack_speed_multiplier * mobility;
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(DAMAGE_MODIFIER_ID, (MythicConfig.mjoellnir.base_damage_melee - 1) + ((MythicConfig.mjoellnir.enchantment_multiplier - 1) * dmgModifier), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(SPEED_MODIFIER_ID, MythicConfig.mjoellnir.base_attack_speed + speedModifier, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Deprecated
    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return this.getDefaultAttributeModifiers(this.getDefaultInstance());
    }
}
