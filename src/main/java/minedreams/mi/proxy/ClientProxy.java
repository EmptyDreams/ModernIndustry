package minedreams.mi.proxy;

import javax.annotation.Nonnull;

import minedreams.mi.register.AutoRegister;
import minedreams.mi.blocks.world.WorldAutoCreater;
import minedreams.mi.api.gui.GuiLoader;
import minedreams.mi.api.net.NetworkLoader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	
	private static ASMDataTable ASM;
	
	@Nonnull
	public static ASMDataTable getAsm() {
		return ASM;
	}
	
	@Override
	public void preInit(@Nonnull FMLPreInitializationEvent event){
		ASM = event.getAsmData();
		AutoRegister.init();
		super.preInit(event);
		new WorldAutoCreater();
		new GuiLoader();
		new NetworkLoader();
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
