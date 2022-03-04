package top.kmar.mi.api.fluid.data;

import net.minecraft.util.EnumFacing;

/**
 * 存储流体运输成本
 * @author EmptyDreams
 */
public final class FTPriceConfig {
    
    /**
     * 计算运输指定流体时需要消耗多少电力
     * @param data 运送的流体，允许data.isEmpty()返回true
     * @param facing 流体运送方向
     */
    public static int getPrice(FluidData data, EnumFacing facing) {
        return 0;
    }
    
}