package top.kmar.mi.content.blocks.machine;

import top.kmar.mi.api.electricity.cables.CableBlock;
import top.kmar.mi.api.regedits.others.AutoManager;

/**
 * 电线的管理类
 * @author EmptyDremas
 */
@AutoManager(block = true)
public final class WireManager {
    
    /** 铜质导线 */
    public final static CableBlock COPPER = new CableBlock("wire_copper");
    
}