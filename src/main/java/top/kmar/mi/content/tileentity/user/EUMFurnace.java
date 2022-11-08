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
    
    @Override
    public int getNeedEnergy() {
        return super.getNeedEnergy() + 800;
    }
    
    @Override
    public int getEfficiency() {
        return super.getEfficiency() + 70;
    }
    
    @SubscribeEvent
    public static void initGui(GuiLoader.MIGuiRegistryEvent event) {
        EUFurnace.initGuiHelper(event, BlockGuiList.getEleHighFurnace());
    }
    
}