package mythicbotany.alftools;

import mythicbotany.config.MythicConfig;
import mythicbotany.register.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.NeoForgeMod;
import vazkii.botania.common.item.equipment.armor.terrasteel.TerrasteelArmorItem;

import javax.annotation.Nullable;
import java.util.List;

public class CommonAlfsteelArmor {
    
    public static ItemAttributeModifiers applyModifiers(TerrasteelArmorItem item) {
        ArmorItem.Type type = item.getType();
        EquipmentSlotGroup slot = EquipmentSlotGroup.bySlot(type.getSlot());
        String name = type.getName();
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR, modifier("armor_" + name, item.getDefense()), slot)
                .add(Attributes.ARMOR_TOUGHNESS, modifier("armor_toughness_" + name, item.getToughness()), slot);

        if (item == ModItems.alfsteelHelmet) {
            builder.add(Attributes.BLOCK_INTERACTION_RANGE, modifier("block_reach_" + name, MythicConfig.alftools.reach_modifier), slot);
            builder.add(Attributes.ENTITY_INTERACTION_RANGE, modifier("entity_reach_" + name, MythicConfig.alftools.reach_modifier), slot);
        } else if (item == ModItems.alfsteelChestplate) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE, modifier("knockback_resistance_" + name, MythicConfig.alftools.knockback_resistance_modifier), slot);
        } else if (item == ModItems.alfsteelLeggings) {
            builder.add(Attributes.MOVEMENT_SPEED, modifier("movement_speed_" + name, MythicConfig.alftools.speed_modifier), slot);
            builder.add(NeoForgeMod.SWIM_SPEED, modifier("swim_speed_" + name, MythicConfig.alftools.speed_modifier), slot);
        }
        return builder.build();
    }

    private static AttributeModifier modifier(String path, double amount) {
        return new AttributeModifier(ResourceLocation.fromNamespaceAndPath("mythicbotany", "alfsteel_" + path), amount, AttributeModifier.Operation.ADD_VALUE);
    }
    
    public static void addArmorSetDescription(ItemStack stack, List<Component> list) {
        if (stack.getItem() == ModItems.alfsteelHelmet) {
            list.add(Component.translatable("item.mythicbotany.alfsteel_helmet.description").withStyle(ChatFormatting.GOLD));
        } else if (stack.getItem() == ModItems.alfsteelChestplate) {
            list.add(Component.translatable("item.mythicbotany.alfsteel_chestplate.description").withStyle(ChatFormatting.GOLD));
        } else if (stack.getItem() == ModItems.alfsteelLeggings) {
            list.add(Component.translatable("item.mythicbotany.alfsteel_leggings.description").withStyle(ChatFormatting.GOLD));
        } else if (stack.getItem() == ModItems.alfsteelBoots) {
            list.add(Component.translatable("item.mythicbotany.alfsteel_boots.description").withStyle(ChatFormatting.GOLD));
        }
    }
    
    public static boolean hasArmorSetItem(Player player, EquipmentSlot slot) {
        if (player == null) {
            return false;
        } else {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) {
                return false;
            } else {
                return switch (slot) {
                    case HEAD -> stack.getItem() == ModItems.alfsteelHelmet;
                    case CHEST -> stack.getItem() == ModItems.alfsteelChestplate;
                    case LEGS -> stack.getItem() == ModItems.alfsteelLeggings;
                    case FEET -> stack.getItem() == ModItems.alfsteelBoots;
                    default -> false;
                };
            }
        }
    }
    
    public static int getDefense(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> MythicConfig.alftools.armor_values.helmet.defense();
            case CHESTPLATE -> MythicConfig.alftools.armor_values.chestplate.defense();
            case LEGGINGS -> MythicConfig.alftools.armor_values.leggings.defense();
            case BOOTS -> MythicConfig.alftools.armor_values.boots.defense();
            default -> 0;
        };
    }
    
    public static float getToughness(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> MythicConfig.alftools.armor_values.helmet.toughness();
            case CHESTPLATE -> MythicConfig.alftools.armor_values.chestplate.toughness();
            case LEGGINGS -> MythicConfig.alftools.armor_values.leggings.toughness();
            case BOOTS -> MythicConfig.alftools.armor_values.boots.toughness();
            default -> 0;
        };
    }
}
