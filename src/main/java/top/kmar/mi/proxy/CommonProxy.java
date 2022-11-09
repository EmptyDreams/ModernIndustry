package top.kmar.mi.proxy;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.craft.json.CraftJsonRegedit;
import top.kmar.mi.api.regedits.AutoRegister;
import top.kmar.mi.data.json.block.BlockJsonBuilder;
import top.kmar.mi.data.json.fluid.FluidJsonBuilder;
import top.kmar.mi.data.json.item.ItemJsonBuilder;

import javax.annotation.Nonnull;

public class CommonProxy {
    
    protected static ASMDataTable ASM;
    
    @Nonnull
    public static ASMDataTable getAsm() {
        return ASM;
    }
    
    public void preInit(@Nonnull FMLPreInitializationEvent event) {
        ASM = event.getAsmData();
        AutoRegister.init();
        //noinspection ConstantConditions
        if (!"jar".equals(ModernIndustry.class.getResource("").getProtocol())) {
			BlockJsonBuilder.build();
			ItemJsonBuilder.build();
			FluidJsonBuilder.build();
		}
    }
    
    public void init(FMLInitializationEvent event) {
        CraftJsonRegedit.INSTANCE.parserAll();
    }
    
    public void postInit(FMLPostInitializationEvent event) {}
    
}