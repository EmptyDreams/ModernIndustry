package xyz.emptydreams.mi.api.register.machines;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.api.register.AutoRegisterMachine;
import xyz.emptydreams.mi.api.register.others.AutoFluid;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

import static xyz.emptydreams.mi.api.register.machines.RegisterHelp.assignField;
import static xyz.emptydreams.mi.api.register.machines.RegisterHelp.errField;
import static xyz.emptydreams.mi.api.register.machines.RegisterHelp.invokeStaticMethod;
import static xyz.emptydreams.mi.api.register.machines.RegisterHelp.newInstance;

/**
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public class FluidRegistryMachine extends AutoRegisterMachine<AutoFluid, Object> {
	
	@Nonnull
	@Override
	public Class<AutoFluid> getTargetClass() {
		return AutoFluid.class;
	}
	
	@Override
	public void registry(Class<?> clazz, AutoFluid annotation, Object data) {
		Fluid fluid = (Fluid) newInstance(clazz, (Object[]) null);
		if (fluid == null) return;
		FluidRegistry.registerFluid(fluid);
		FluidRegistryMachine.Fluids.fluids.add(fluid);
		//注册对应方块
		String modid = fluid.getFlowing().getResourceDomain();
		String unlocalizedName = annotation.unlocalizedName().equals("") ?
				modid + "." + fluid.getName() : annotation.unlocalizedName();
		CreativeTabs tab =
				(CreativeTabs) invokeStaticMethod(clazz, annotation.creativeTab(), (Object[]) null);
		if (tab == null) {
			errField(clazz, annotation.creativeTab(), "值为空", null);
			return;
		}
		//注册流体方块
		BlockFluidClassic block = new BlockFluidClassic(fluid, Material.WATER);
		block.setRegistryName(fluid.getName());
		block.setUnlocalizedName(unlocalizedName);
		BlockRegistryMachine.setCustomModelRegister(block, "null");
		BlockRegistryMachine.addAutoBlock(block);
		//添加流体桶
		ItemBucket item = new ItemBucket(block);
		item.setRegistryName(StringUtil.revampAddToRL(block.getRegistryName(), "_item"));
		item.setUnlocalizedName(unlocalizedName);
		item.setCreativeTab(tab);
		item.setContainerItem(Items.BUCKET);
		ItemRegistryMachine.setCustomModelRegister(item, "null");
		ItemRegistryMachine.addAutoItem(item);
		FluidRegistry.addBucketForFluid(fluid);
		registryFluidRender(block, item);
		//为指定字段赋值
		if (!assignField(fluid, annotation.value(), block)) return;
		//触发end
		if (annotation.end().equals("")) return;
		invokeStaticMethod(clazz, annotation.end(), (Object[]) null);
	}
	
	/** 注册桶和方块的渲染 */
	private static void registryFluidRender(Block block, Item item) {
		if (WorldUtil.isServer()) return;
		String location = block.getRegistryName().toString();
		ModelLoader.setCustomMeshDefinition(item, stack -> new ModelResourceLocation(location, "fluid"));
		ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(location, "fluid");
			}
		});
	}
	
	@SubscribeEvent
	public static void onFillBucket(FillBucketEvent event) {
		if (event.getTarget() == null) return;
		IBlockState state = event.getWorld().getBlockState(event.getTarget().getBlockPos());
		if (!(state.getBlock() instanceof IFluidBlock)) return;
		
		Fluid fluid = ((IFluidBlock) state.getBlock()).getFluid();
		FluidStack fs = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
		ItemStack bucket = event.getEmptyBucket();
		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(bucket);
		if (fluidHandler != null) {
			int fillAmount = fluidHandler.fill(fs, true);
			if (fillAmount > 0) {
				ItemStack filledBucket = fluidHandler.getContainer();
				event.setFilledBucket(filledBucket);
				event.setResult(Event.Result.ALLOW);
			}
		}
	}
	
	public static final class Fluids {
		
		/** 所有流体 */
		public static final List<Fluid> fluids = new LinkedList<>();
		
	}
}