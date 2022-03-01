package xyz.emptydreams.mi.api.fluid.data;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.util.EnumFacing;

/**
 * 流体运输运算报告
 * @author EmptyDreams
 */
public class TransportReport {

    /** 存储数据  */
    private final Object2IntMap<EnumFacing> priceData = new Object2IntArrayMap<>(6);
    private int total = 0;
    
    /**
     * 插入流体数据
     * @param facing 流体流动方向
     * @param data 要插入的数据
     */
    public void insert(EnumFacing facing, FluidData data) {
        if (data.isEmpty()) return;
        int value = priceData.getInt(facing);
        priceData.put(facing, value + data.getAmount());
        total += FTPriceConfig.getPrice(data, facing);
    }
    
    /** 获取总和代价 */
    public int getPriceTotal() {
        return total;
    }
    
}