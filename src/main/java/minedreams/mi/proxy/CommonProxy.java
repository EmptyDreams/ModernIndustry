package minedreams.mi.proxy;

import javax.annotation.Nonnull;

import minedreams.mi.AutoRegister;
import minedreams.mi.blocks.world.WorldAutoCreater;
import minedreams.mi.api.gui.GuiLoader;
import minedreams.mi.api.net.NetworkLoader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	
	private static ASMDataTable ASM;
	

	@Nonnull
	public static ASMDataTable getAsm() {
		return ASM;
	}
	
	public void preInit(@Nonnull FMLPreInitializationEvent event){
		ASM = event.getAsmData();
		new AutoRegister();
		new NetworkLoader();
		new WorldAutoCreater();
		new GuiLoader();
    }
    public void init(FMLInitializationEvent event){
    }
    public void postInit(FMLPostInitializationEvent event){
    }
}
