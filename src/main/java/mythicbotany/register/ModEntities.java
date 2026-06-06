package mythicbotany.register;

import mythicbotany.MythicBotany;
import mythicbotany.alfheim.content.AlfPixie;
import mythicbotany.alfheim.content.AlfPixieRender;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "ENTITY_TYPE")
public class ModEntities {

    public static final EntityType<AlfPixie> alfPixie = EntityType.Builder.<AlfPixie>of(AlfPixie::new, MobCategory.CREATURE).sized(1, 1).setUpdateInterval(3).setTrackingRange(16).setShouldReceiveVelocityUpdates(true).build(MythicBotany.getInstance().modid + "_alf_pixie");
    
    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        EntityRenderers.register(alfPixie, AlfPixieRender::new);
    }
    
    public static void createAttributes(EntityAttributeCreationEvent event) {
        event.put(alfPixie, AlfPixie.entityAttributes());
    }
    
    public static void createSpawnPlacement(RegisterSpawnPlacementsEvent event) {
        event.register(alfPixie, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AlfPixie::canSpawnAt, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
