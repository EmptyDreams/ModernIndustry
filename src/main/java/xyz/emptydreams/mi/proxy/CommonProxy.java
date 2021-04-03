package xyz.emptydreams.mi.proxy;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import xyz.emptydreams.mi.api.gui.common.GuiLoader;
import xyz.emptydreams.mi.api.register.AutoRegister;
import xyz.emptydreams.mi.data.json.block.BlockJsonBuilder;
import xyz.emptydreams.mi.data.json.item.ItemJsonBuilder;

import javax.annotation.Nonnull;
import java.io.File;

public class CommonProxy {
	
	protected static ASMDataTable ASM;
	
	@Nonnull
	public static ASMDataTable getAsm() {
		return ASM;
	}
	
	public void preInit(@Nonnull FMLPreInitializationEvent event){
		ASM = event.getAsmData();
		
		AutoRegister.init();
		if (!new File(".").getAbsolutePath().endsWith(".jar")) {
			BlockJsonBuilder.build();
			ItemJsonBuilder.build();
		}
		new GuiLoader();
    }
    public void init(FMLInitializationEvent event){
    }
    public void postInit(FMLPostInitializationEvent event){
    }
}