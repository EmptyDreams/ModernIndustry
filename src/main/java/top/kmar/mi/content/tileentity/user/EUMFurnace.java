package top.kmar.mi.content.tileentity.user;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.kmar.mi.api.graphics.GuiLoader;
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity;
import top.kmar.mi.content.blocks.BlockGuiList;

/**
 * 高温火炉的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("ele_mfurnace")
@Mod.EventBusSubscriber
public class EUMFurnace extends EUFurnace {
    
    public EUMFurnace() {
        setMaxEnergy(20);
    }
    
    @Override
    public int getNeedEnergy() {
        return 10;
    }
    
    @Override
    public int getNeedTime() {
        return 100;
    }
    
    @SubscribeEvent
    public static void initGui(GuiLoader.MIGuiRegistryEvent event) {
        EUFurnace.initGuiHelper(event, BlockGuiList.getEleHighFurnace());
    }
    
}