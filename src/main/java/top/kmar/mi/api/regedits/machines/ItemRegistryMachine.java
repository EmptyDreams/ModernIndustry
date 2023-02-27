package top.kmar.mi.api.regedits.machines;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import top.kmar.mi.api.regedits.AutoRegisterMachine;
import top.kmar.mi.api.regedits.item.AutoItemRegister;
import top.kmar.mi.api.regedits.sorter.ItemSorter;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 物品注册机
 * @author EmptyDreams
 */
public class ItemRegistryMachine extends AutoRegisterMachine<AutoItemRegister, Object> {

    /** 添加一个自动注册的物品 */
    public static void addAutoItem(Item item) {
        Items.autoItems.add(item);
        addItem(item);
    }

    /**
     * 设置注册customModel的方法名称
     * @param methodName 方法名称
     */
    public static void setCustomModelRegister(Item item, String methodName) {
        Items.customModelItems.put(item, methodName);
    }

    /** 添加一个物品 */
    public static void addItem(Item item) {
        Items.items.add(item);
    }

    @Nonnull
    @Override
    public Class<AutoItemRegister> getTargetClass() {
        return AutoItemRegister.class;
    }

    @Override
    public void registry(Class<?> clazz, AutoItemRegister annotation, Object data) {
        String modid = annotation.modid();
        String name = annotation.value();
        String field = annotation.field();
        String[] ores = annotation.oreDic();
        @SuppressWarnings("unchecked")
        Item item = RegisterHelp.newInstance((Class<? extends Item>) clazz, (Object[]) null);
        if (item == null) return;

        item.setRegistryName(modid, name);
        item.setUnlocalizedName(getUnlocalizedName(annotation));
        if (ores.length > 0)
            for (String ore : ores)
                OreDictionary.registerOre(ore, item);
        addAutoItem(item);
        RegisterHelp.assignField(item, field, item);
        if (!annotation.model().equals(""))
            setCustomModelRegister(item, annotation.model());
    }

    @Override
    public void atEnd() {
        Items.autoItems.sort(ItemSorter::compare);
    }

    @Nonnull
    private static String getUnlocalizedName(AutoItemRegister annotation) {
        if (annotation.unlocalizedName().length() == 0)
            return annotation.modid() + "." + annotation.value();
        return annotation.unlocalizedName();
    }

    public static final class Items {

        /** 所有物品 */
        public static final List<Item> items = new LinkedList<>();
        /** 需要注册的物品 */
        public static final List<Item> autoItems = new LinkedList<>();
        /** 手动注册model的物品 */
        public static final Map<Item, String> customModelItems = new Object2ObjectOpenHashMap<>();

    }
}