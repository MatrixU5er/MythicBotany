package mythicbotany;

import mythicbotany.alfheim.content.AlfPixieRender;
import mythicbotany.alfheim.teleporter.RenderReturnPortal;
import mythicbotany.functionalflora.base.BlockFunctionalFlower;
import mythicbotany.functionalflora.base.FunctionalFlowerBase;
import mythicbotany.functionalflora.base.RenderFunctionalFlower;
import mythicbotany.mimir.RenderYggdrasilBranch;
import mythicbotany.mjoellnir.RenderEntityMjoellnir;
import mythicbotany.mjoellnir.RenderMjoellnir;
import mythicbotany.pylon.RenderAlfsteelPylon;
import mythicbotany.register.ModBlocks;
import mythicbotany.register.ModEntities;
import mythicbotany.rune.RenderRuneHolder;
import mythicbotany.rune.TileCentralRuneHolder;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.moddingx.libx.render.ItemStackRenderer;

final class MythicBotanyClient {

    private MythicBotanyClient() {

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
