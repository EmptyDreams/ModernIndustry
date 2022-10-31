package top.kmar.mi.api.regedits.machines;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.kmar.mi.api.regedits.AutoRegisterMachine;
import top.kmar.mi.api.regedits.block.annotations.AutoFluid;
import top.kmar.mi.api.utils.expands.WorldExpandsKt;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

import static top.kmar.mi.api.regedits.machines.RegisterHelp.invokeStaticMethod;

/**
 * 流体的注册机
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
		Fluid fluid = (Fluid) RegisterHelp.newInstance(clazz, (Object[]) null);
		if (fluid == null) return;
		FluidRegistry.registerFluid(fluid);
		FluidRegistryMachine.Fluids.fluids.add(fluid);
		//注册对应方块
		String modid = fluid.getFlowing().getResourceDomain();
		String unlocalizedName = annotation.unlocalizedName().length() == 0 ?
				modid + "." + fluid.getName() : annotation.unlocalizedName();
		CreativeTabs tab =
				(CreativeTabs) invokeStaticMethod(clazz, annotation.creativeTab(), (Object[]) null);
		if (tab == null) {
			RegisterHelp.errField(clazz, annotation.creativeTab(), "值为空", null);
			return;
		}
		//注册流体方块
		BlockFluidClassic block = new BlockFluidClassic(fluid, Material.LAVA);
		block.setRegistryName(fluid.getName());
		block.setUnlocalizedName(unlocalizedName);
		BlockRegistryMachine.setCustomModelRegister(block, "null");
		BlockRegistryMachine.addNoItemBlock(block);
		//流体桶
		registryFluidRender(block);
		FluidRegistry.addBucketForFluid(fluid);
		//为指定字段赋值
		if (!RegisterHelp.assignField(fluid, annotation.value(), block)) return;
		//触发end
		if (annotation.end().equals("")) return;
		invokeStaticMethod(clazz, annotation.end(), (Object[]) null);
	}
	
	/** 注册桶和方块的渲染 */
	private static void registryFluidRender(Block block) {
		if (WorldExpandsKt.isServer()) return;
		//ModelBakery.registerItemVariants(item);
		String location = block.getRegistryName().toString();
		ModelResourceLocation resourceLocation = new ModelResourceLocation(location, "fluid");
		//ModelLoader.setCustomMeshDefinition(item, stack -> resourceLocation);
		ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return resourceLocation;
			}
		});
	}
	
	@SubscribeEvent
	public static void registryTexture(TextureStitchEvent.Pre event) {
		TextureMap texture = event.getMap();
		for (Fluid fluid : Fluids.fluids) {
			texture.registerSprite(fluid.getFlowing());
			texture.registerSprite(fluid.getStill());
		}
	}
	
	public static final class Fluids {
		
		/** 所有流体 */
		public static final List<Fluid> fluids = new LinkedList<>();
		
	}
}