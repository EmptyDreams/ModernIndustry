package xyz.emptydreams.mi.api.register.machines;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import xyz.emptydreams.mi.api.register.AutoRegisterMachine;
import xyz.emptydreams.mi.api.register.others.AutoFluid;

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
		Material material =
				(Material) invokeStaticMethod(clazz, annotation.material(), (Object[]) null);
		CreativeTabs tab =
				(CreativeTabs) invokeStaticMethod(clazz, annotation.creativeTab(), (Object[]) null);
		if (material == null) {
			errField(clazz, annotation.material(), "值为空", null);
			return;
		}
		if (tab == null) {
			errField(clazz, annotation.creativeTab(), "值为空", null);
			return;
		}
		BlockFluidClassic block = new BlockFluidClassic(fluid, material);
		block.setUnlocalizedName(unlocalizedName);
		if (!assignField(fluid, annotation.value(), block)) return;
		//注册物品
		Item itemFluid = new ItemBlock(block);
		itemFluid.setRegistryName(modid, fluid.getName());
		ModelLoader.setCustomMeshDefinition(
				itemFluid, stack -> new ModelResourceLocation(unlocalizedName, "fluid"));
		ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(unlocalizedName, "fluid");
			}
		});
		ItemRegistryMachine.addAutoItem(itemFluid);
		ItemRegistryMachine.setCustomModelRegister(itemFluid, "null");
		//触发end
		if (annotation.end().equals("")) return;
		invokeStaticMethod(clazz, annotation.end(), (Object[]) null);
	}
	
	public static final class Fluids {
		
		/** 所有流体 */
		public static final List<Fluid> fluids = new LinkedList<>();
		
	}
}