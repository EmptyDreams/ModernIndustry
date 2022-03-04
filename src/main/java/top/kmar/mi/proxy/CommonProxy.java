package top.kmar.mi.proxy;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import top.kmar.mi.api.register.AutoRegister;
import top.kmar.mi.api.gui.common.GuiLoader;
import top.kmar.mi.data.json.block.BlockJsonBuilder;
import top.kmar.mi.data.json.fluid.FluidJsonBuilder;
import top.kmar.mi.data.json.item.ItemJsonBuilder;

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
			FluidJsonBuilder.build();
		}
		new GuiLoader();
    }
    
    public void init(FMLInitializationEvent event){ }
    
    public void postInit(FMLPostInitializationEvent event){ }
}