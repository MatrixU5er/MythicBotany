package mythicbotany;

import mythicbotany.alftools.AlfsteelSword;
import mythicbotany.config.MythicConfig;
import mythicbotany.register.ModItems;
import net.minecraft.core.component.DataComponents;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

public final class ModItemComponents {

    private ModItemComponents() {
    }

    public static void modify(ModifyDefaultComponentsEvent event) {
        event.modify(ModItems.alfsteelSword, components -> components
                .set(DataComponents.MAX_DAMAGE, MythicConfig.alftools.durability.sword.max_durability())
                .set(DataComponents.ATTRIBUTE_MODIFIERS, AlfsteelSword.createAttributes()));
        event.modify(ModItems.alfsteelPick, components -> components
                .set(DataComponents.MAX_DAMAGE, MythicConfig.alftools.durability.pickaxe.max_durability()));
        event.modify(ModItems.alfsteelAxe, components -> components
                .set(DataComponents.MAX_DAMAGE, MythicConfig.alftools.durability.axe.max_durability()));
    }
}
