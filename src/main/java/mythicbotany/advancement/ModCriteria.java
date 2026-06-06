package mythicbotany.advancement;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ModCriteria {

    public static final AlfRepairTrigger ALF_REPAIR = new AlfRepairTrigger();
    public static final MjoellnirTrigger MJOELLNIR = new MjoellnirTrigger();
    public static void register(RegisterEvent event) {
        event.register(Registries.TRIGGER_TYPE, helper -> {
            helper.register(ResourceLocation.fromNamespaceAndPath("mythicbotany", "alf_repair"), ALF_REPAIR);
            helper.register(ResourceLocation.fromNamespaceAndPath("mythicbotany", "mjoellnir"), MJOELLNIR);
        });
    }
}
