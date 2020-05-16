package xyz.emptydreams.mi.proxy;

import javax.annotation.Nonnull;

import xyz.emptydreams.mi.register.AutoRegister;
import xyz.emptydreams.mi.api.gui.GuiLoader;
import xyz.emptydreams.mi.api.net.NetworkLoader;
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
		AutoRegister.init();
		new NetworkLoader();
		new GuiLoader();
    }
    public void init(FMLInitializationEvent event){
    }
    public void postInit(FMLPostInitializationEvent event){
    }
}
