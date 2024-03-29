package top.kmar.mi.content.items.tools;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;
import top.kmar.mi.ModernIndustry;

/**
 * MI中的工具材质
 * @author EmptyDremas
 */
public class MITool {

    /** 铜制工具 */
    public static final Item.ToolMaterial COPPER = EnumHelper.addToolMaterial(
            "COPPER", 2, 240, 5.0F, 2.0F, 9);
    /** 铜制盔甲 */
    public static final ItemArmor.ArmorMaterial COPPER_ARMOR = EnumHelper.addArmorMaterial(
            "COPPER", ModernIndustry.MODID + ":" + "copper", 240,
            new int[] { 2, 5, 5, 2 }, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);

}