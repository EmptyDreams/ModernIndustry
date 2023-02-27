package top.kmar.mi.content.blocks.fluids;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.regedits.block.annotations.AutoFluid;

import javax.annotation.Nonnull;

/**
 * 铁水
 * @author EmptyDreams
 */
@SuppressWarnings("unused")
@AutoFluid
public class FluidIron extends Fluid {

    public static final ResourceLocation STILL =
            new ResourceLocation(ModernIndustry.MODID, "fluid/iron_still");
    public static final ResourceLocation FLOWING =
            new ResourceLocation(ModernIndustry.MODID, "fluid/iron_flowing");

    /** 由注册机分配 */
    private static BlockFluidClassic block;

    public FluidIron() {
        super("iron_melt", STILL, FLOWING);
        setDensity(7860);
        setViscosity(6000);
        setLuminosity(5);
        setTemperature(1535 + 273);
    }

    /** 获取方块对象 */
    @Nonnull
    public static BlockFluidClassic blockInstance() {
        if (block == null) throw new NullPointerException("block对象还未赋值");
        return block;
    }

    public static CreativeTabs getBlockCreativeTab() {
        return ModernIndustry.TAB_TOOL;
    }

}