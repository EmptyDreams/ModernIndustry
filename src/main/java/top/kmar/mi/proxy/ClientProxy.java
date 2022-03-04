package top.kmar.mi.proxy;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	@Nonnull
	public static ASMDataTable getAsm() {
		return ASM;
	}
	
	@Override
	public void preInit(@Nonnull FMLPreInitializationEvent event){
		super.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event){
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event){
		super.postInit(event);
	}
}