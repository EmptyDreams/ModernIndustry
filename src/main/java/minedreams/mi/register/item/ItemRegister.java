package minedreams.mi.register.item;

import javax.annotation.Nonnull;

import minedreams.mi.register.AutoRegister;
import minedreams.mi.tools.MISysInfo;
import minedreams.mi.ModernIndustry;
import minedreams.mi.api.craftguide.CraftGuide;
import minedreams.mi.api.craftguide.CraftGuideManager;
import minedreams.mi.blocks.ore.OreBlock;
import minedreams.mi.items.tools.ToolRegister;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author EmptyDremas
 * @version V1.0
 */
@Mod.EventBusSubscriber
public class ItemRegister {
	
	private static final class MItem extends Item {
		public MItem(String registryName) {
			setRegistryName(registryName);
			setUnlocalizedName(registryName);
			setNoRepair();
			setMaxDamage(0);
			setCreativeTab(ModernIndustry.TAB_ITEM);
		}
	}
	
	/** 铜锭 */
	public static final String NAME_COPPER = "copper_item";
	public static final Item ITEM_COPPER = new MItem(NAME_COPPER);
	/** 铜粉 */
	public static final String NAME_COPPER_POWDER = "copper_powder_item";
	public static final Item ITEM_COPPER_POWDER = new MItem(NAME_COPPER_POWDER);
	/** 锡锭 */
	public static final String NAME_TIN = "tin_item";
	public static final Item ITEM_TIN = new MItem(NAME_TIN);
	/** 锡粉 */
	public static final String NAME_TIN_POWDER = "tin_powder_item";
	public static final Item ITEM_TIN_POWER = new MItem(NAME_TIN_POWDER);
	
	/**
	 * 注册物品以及合成表
	 */
	@SubscribeEvent
	public static void registerItems(@Nonnull RegistryEvent.Register<Item> event) {
		MISysInfo.print("注册MOD物品......");
		
		IForgeRegistry<Item> register = event.getRegistry();
	   if (FMLCommonHandler.instance().getSide().isClient()) {
		   for (Item i : AutoRegister.Items.autoItems) {
		    	register.register(i);
				ModelLoader.setCustomModelResourceLocation(
						i, 0, new ModelResourceLocation(i.getRegistryName(), "inventory"));
		    }
	   } else {
		   for (Item i : AutoRegister.Items.autoItems) {
		    	register.register(i);
		    }
	   }
	    
	    MISysInfo.print("注册合成表......");
	    registerRecipe_();
	    for (OreBlock block : OreBlock.LIST.keySet()) {
	    	GameRegistry.addSmelting(block.getBlockItem(), new ItemStack(OreBlock.LIST.get(block)), 0.5F);
	    }
	    
	    //注册压缩机合成表
	    CraftGuideManager.Compressor.add(new CraftGuide(235).addMeterial(
			new ItemStack(ItemRegister.ITEM_COPPER_POWDER, 2)).addProduct(new ItemStack(ItemRegister.ITEM_COPPER)))
													   .add(new CraftGuide(235).addMeterial(
			new ItemStack(ItemRegister.ITEM_TIN_POWER, 2)).addProduct(new ItemStack(ItemRegister.ITEM_TIN)));
	}
	
	/** 注册合成表 */
	private static void registerRecipe_() {
		//铜锭
		registerRecipe(ITEM_COPPER,
				new Object[] {
						"##", "##", '#', ITEM_COPPER_POWDER
				}
		);
		//锡锭
		registerRecipe(ITEM_TIN_POWER,
				new Object[] {
						"##", "##", '#', ITEM_TIN_POWER
				}
		);
		ToolRegister.registerRecipe();
	}
	
	/**
	 * 注册合成表
	 */
	public static void registerRecipe(@Nonnull Item output, Object[] params) {
		GameRegistry.addShapedRecipe(output.getRegistryName(), output.getRegistryName(),
				new ItemStack(output), params);
	}
	
	/** 通过名称获取Item */
	public static Item getItem(String name) {
		return AutoRegister.Items.items.get(name);
	}
	
	/** 通过名称获取ItemStack */
	public static ItemStack getStack(String name, int amount) {
		return new ItemStack(AutoRegister.Items.items.get(name), amount);
	}
	
}
