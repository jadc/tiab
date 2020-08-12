package red.jad.tiab.backend;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

/*
    Allows use of custom Action
    Used with permission: https://gist.github.com/shartte/6a4e61a73ef7a243c615760d6368ca08
 */
public interface CrouchlessInterface {
    ActionResult onItemUseFirst(ItemUsageContext context);
}
