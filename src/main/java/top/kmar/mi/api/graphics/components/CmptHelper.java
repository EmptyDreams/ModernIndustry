package top.kmar.mi.api.graphics.components;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.dor.ByteDataOperator;
import top.kmar.mi.api.dor.interfaces.IDataOperator;
import top.kmar.mi.api.graphics.components.interfaces.CmptClient;
import top.kmar.mi.api.graphics.components.interfaces.slots.IGraphicsSlot;
import top.kmar.mi.api.graphics.utils.GraphicsStyle;

/**
 * @author EmptyDreams
 */
final class CmptHelper {
    
    private CmptHelper() {}
    
    @SideOnly(Side.CLIENT)
    static void updateSlotInfo(CmptClient client, IGraphicsSlot slot) {
        GraphicsStyle style = client.getStyle();
        int x = style.getX() + (style.getWidth() >> 1) + style.getBorderLeft().getWeight() - 9;
        int y = style.getY() + (style.getHeight() >> 1) + style.getBorderTop().getWeight() - 9;
        if (x == slot.getXPos() && y == slot.getYPos()) return;
        IDataOperator operator = new ByteDataOperator(4);
        operator.writeVarInt(x);
        operator.writeVarInt(y);
        client.send2Service(operator, false);
        slot.setXPos(x);
        slot.setYPos(y);
    }
    
}