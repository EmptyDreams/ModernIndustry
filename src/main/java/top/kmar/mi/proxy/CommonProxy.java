package top.kmar.mi.proxy;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import top.kmar.mi.api.register.AutoRegister;

import javax.annotation.Nonnull;

public class CommonProxy {
	
	protected static ASMDataTable ASM;
	
	@Nonnull
	public static ASMDataTable getAsm() {
		return ASM;
	}
	
	public void preInit(@Nonnull FMLPreInitializationEvent event){
		ASM = event.getAsmData();
		AutoRegister.init();
		/*if ("jar".equals(ModernIndustry.class.getResource("").getProtocol())) {
			BlockJsonBuilder.build();
			ItemJsonBuilder.build();
			FluidJsonBuilder.build();
		}*/
    }
    
    public void init(FMLInitializationEvent event){ }
    
    public void postInit(FMLPostInitializationEvent event){ }
}