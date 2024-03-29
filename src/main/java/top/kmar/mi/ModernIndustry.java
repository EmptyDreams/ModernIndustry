package top.kmar.mi;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import top.kmar.mi.api.regedits.others.AutoManager;
import top.kmar.mi.api.utils.MISysInfo;
import top.kmar.mi.content.blocks.common.CommonBlocks;
import top.kmar.mi.content.blocks.machine.WireManager;
import top.kmar.mi.content.items.common.CommonItems;
import top.kmar.mi.content.items.tools.ToolRegister;
import top.kmar.mi.proxy.CommonProxy;

import javax.annotation.Nonnull;

import static top.kmar.mi.api.utils.expands.ItemExpandsKt.newStack;

@Mod(modid = ModernIndustry.MODID, name = ModernIndustry.NAME, version = ModernIndustry.VERSION)
@AutoManager(item = true)
public final class ModernIndustry {
    
    public static final String MODID = "mi";
    public static final String NAME = "ModernIndustry";
    public static final String VERSION = "1.0";
    
    @Mod.Instance(ModernIndustry.MODID)
    public static ModernIndustry instance;
    
    @SidedProxy(clientSide = "top.kmar.mi.proxy.ClientProxy",
            serverSide = "top.kmar.mi.proxy.CommonProxy")
    public static CommonProxy proxy;
    
    public static Logger logger;
    
    static {
        FluidRegistry.enableUniversalBucket();
    }
    
    /** 物品栏-物品 */
    public static final CreativeTabs TAB_ITEM = new CreativeTabs(MODID + "_item") {
        @Override
        @Nonnull
        public ItemStack getTabIconItem() {
            return newStack(CommonItems.ITEM_COPPER, 1);
        }
    };
    /** 物品栏-工具 */
    public static final CreativeTabs TAB_TOOL = new CreativeTabs(MODID + "_tool") {
        @Override
        @Nonnull
        public ItemStack getTabIconItem() {
            return newStack(ToolRegister.ITEM_COPPER_PICKAXE, 1);
        }
    };
    /** 物品栏-方块 */
    public static final CreativeTabs TAB_BLOCK = new CreativeTabs(MODID + "_block") {
        @Override
        @Nonnull
        public ItemStack getTabIconItem() {
            return newStack(CommonBlocks.ORE_COPPER.getBlockItem(), 1);
        }
    };
    /** 物品栏-线缆 */
    public static final CreativeTabs TAB_WIRE = new CreativeTabs(MODID + "_wire") {
        @Override
        @Nonnull
        public ItemStack getTabIconItem() {
            return newStack(WireManager.COPPER.getBlockItem(), 1);
        }
    };
    //内部物品，用于加载图片
    @SuppressWarnings("NoTranslation")
    public static final Item DEBUG = new Item().setRegistryName(MODID, "debug_").setUnlocalizedName("debug_");
    /** 物品栏-测试 */
    public static final CreativeTabs TAB_DEBUG = new CreativeTabs(MODID + "_debug") {
        @Override
        @Nonnull
        public ItemStack getTabIconItem() { return newStack(DEBUG, 1); }
    };
    
    @EventHandler
    public static void preInit(@Nonnull FMLPreInitializationEvent event) {
        logger = event.getModLog();
        MISysInfo.LOGGER = logger;
        proxy.preInit(event);
    }
    
    @EventHandler
    public static void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
    
    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
    
}