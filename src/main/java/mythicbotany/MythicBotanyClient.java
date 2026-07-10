package mythicbotany;

import mythicbotany.alfheim.content.AlfPixieRender;
import mythicbotany.alfheim.teleporter.RenderReturnPortal;
import mythicbotany.alftools.AlfsteelAxe;
import mythicbotany.alftools.AlfsteelPick;
import mythicbotany.functionalflora.base.BlockFunctionalFlower;
import mythicbotany.functionalflora.base.FunctionalFlowerBase;
import mythicbotany.functionalflora.base.RenderFunctionalFlower;
import mythicbotany.mimir.RenderYggdrasilBranch;
import mythicbotany.mjoellnir.RenderEntityMjoellnir;
import mythicbotany.mjoellnir.RenderMjoellnir;
import mythicbotany.pylon.RenderAlfsteelPylon;
import mythicbotany.register.ModBlocks;
import mythicbotany.register.ModEntities;
import mythicbotany.register.ModItems;
import mythicbotany.rune.RenderRuneHolder;
import mythicbotany.rune.TileCentralRuneHolder;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.moddingx.libx.render.ItemStackRenderer;
import vazkii.botania.client.model.armor.ArmorModels;

import java.util.Objects;

final class MythicBotanyClient {

    private MythicBotanyClient() {

    }

    static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.alfsteelPick, MythicBotany.getInstance().resource("tipped"),
                    (stack, level, entity, seed) -> AlfsteelPick.isTipped(stack) ? 1F : 0F);
            ItemProperties.register(ModItems.alfsteelPick, MythicBotany.getInstance().resource("active"),
                    (stack, level, entity, seed) -> AlfsteelPick.isEnabled(stack) ? 1F : 0F);
            ItemProperties.register(ModItems.alfsteelAxe, MythicBotany.getInstance().resource("active"),
                    (stack, level, entity, seed) -> entity instanceof Player player && !AlfsteelAxe.shouldBreak(player) ? 0F : 1F);
            ItemStackRenderer.addRenderBlock(ModBlocks.yggdrasilBranch.getBlockEntityType(), false);
        });
    }

    static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        if (!event.isItemRegistered(ModBlocks.alfsteelPylon.asItem())) {
            event.registerItem(new IClientItemExtensions() {

                private BlockEntityWithoutLevelRenderer renderer;

                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    if (this.renderer == null) {
                        this.renderer = new RenderAlfsteelPylon.ItemRenderer();
                    }
                    return this.renderer;
                }
            }, ModBlocks.alfsteelPylon.asItem());
        }

        if (!event.isItemRegistered(ModBlocks.yggdrasilBranch.asItem())) {
            event.registerItem(ItemStackRenderer.createProperties(), ModBlocks.yggdrasilBranch.asItem());
        }

        // Alfsteel armor extends Botania's terrasteel armor and uses a 64x128 Botania-layout texture,
        // which only renders correctly with Botania's custom ArmorModel. Botania registers that model
        // extension only for items in its own "botania" namespace, so our alfsteel pieces would fall
        // back to the vanilla humanoid model and render garbled. Register the same extension ourselves.
        IClientItemExtensions alfsteelArmorModel = new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                return Objects.requireNonNullElse(ArmorModels.get(stack), defaultModel);
            }
        };
        for (Item armor : new Item[]{ModItems.alfsteelHelmet, ModItems.alfsteelChestplate, ModItems.alfsteelLeggings, ModItems.alfsteelBoots}) {
            if (!event.isItemRegistered(armor)) {
                event.registerItem(alfsteelArmorModel, armor);
            }
        }
    }

    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.alfPixie, AlfPixieRender::new);
        event.registerEntityRenderer(ModBlocks.mjoellnir.getEntityType(), RenderEntityMjoellnir::new);

        event.registerBlockEntityRenderer(ModBlocks.alfsteelPylon.getBlockEntityType(), RenderAlfsteelPylon::new);
        event.registerBlockEntityRenderer(ModBlocks.yggdrasilBranch.getBlockEntityType(), ctx -> new RenderYggdrasilBranch());
        event.registerBlockEntityRenderer(ModBlocks.runeHolder.getBlockEntityType(), ctx -> new RenderRuneHolder());
        event.registerBlockEntityRenderer(ModBlocks.centralRuneHolder.getBlockEntityType(), ctx -> centralRuneHolderRenderer());
        event.registerBlockEntityRenderer(ModBlocks.mjoellnir.getBlockEntityType(), ctx -> new RenderMjoellnir());
        event.registerBlockEntityRenderer(ModBlocks.returnPortal.getBlockEntityType(), ctx -> new RenderReturnPortal());

        registerFlowerRenderer(event, ModBlocks.exoblaze);
        registerFlowerRenderer(event, ModBlocks.witherAconite);
        registerFlowerRenderer(event, ModBlocks.aquapanthus);
        registerFlowerRenderer(event, ModBlocks.hellebore);
        registerFlowerRenderer(event, ModBlocks.raindeletia);
        registerFlowerRenderer(event, ModBlocks.feysythia);
        registerFlowerRenderer(event, ModBlocks.petrunia);
    }

    private static <T extends FunctionalFlowerBase> void registerFlowerRenderer(EntityRenderersEvent.RegisterRenderers event, BlockFunctionalFlower<T> flower) {
        event.registerBlockEntityRenderer(flower.getBlockEntityType(), ctx -> new RenderFunctionalFlower<>());
        event.registerBlockEntityRenderer(flower.getFloatingBlock().getBlockEntityType(), ctx -> new RenderFunctionalFlower<>());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockEntityRenderer<TileCentralRuneHolder> centralRuneHolderRenderer() {
        return (BlockEntityRenderer) new RenderRuneHolder();
    }
}
