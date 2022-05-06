package top.kmar.mi.api.gui.component.group;

import net.minecraftforge.items.SlotItemHandler;
import top.kmar.mi.api.gui.component.interfaces.IComponentManager;
import top.kmar.mi.api.graph.utils.GuiPainter;
import top.kmar.mi.api.gui.client.StaticFrameClient;

/**
 * 抽象的SlotGroup，功能与{@link SlotGroup}相同，但是不能被添加到GUI中
 * @author EmptyDreams
 */
public class AbstractSlotGroup extends SlotGroup {
	
	public AbstractSlotGroup(SlotItemHandler... slots) {
		super(slots.length, 1, 1, 0);
		for (int i = 0; i < slots.length; i++) {
			setSlot(i, 0, slots[i]);
		}
	}
	
	@Override
	public void paint(GuiPainter painter) { }
	
	@Override
	public void onAdd2Manager(IComponentManager con) {
		throw new UnsupportedOperationException("该组件不应该被添加到GUI");
	}
	
	@Override
	public void onAdd2ClientFrame(StaticFrameClient con) {
		throw new UnsupportedOperationException("该组件不应该被添加到GUI");
	}
	
}