package top.kmar.mi.api.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDataWriter;
import top.kmar.mi.api.gui.component.interfaces.IComponentManager;
import top.kmar.mi.api.graph.listener.mouse.IMouseActionListener;
import top.kmar.mi.api.graph.listener.mouse.IMouseEnteredListener;
import top.kmar.mi.api.graph.listener.mouse.IMouseExitedListener;
import top.kmar.mi.api.interfaces.ObjBooleanConsumer;
import top.kmar.mi.api.utils.StringUtil;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.api.gui.common.IFrame;
import top.kmar.mi.api.gui.common.MIFrame;

import javax.annotation.Nullable;

/**
 * 隐形按钮
 * @author EmptyDreams
 */
public class InvisibleButton extends MComponent {
	
	private static final ObjBooleanConsumer<IFrame> SRC_ACTION = (frame, isClient) -> {};
	
	/** 鼠标是否在控件中 */
	private boolean mouse = false;
	/** 是否不可用 */
	private boolean isInvalid = false;
	/** 点击时执行的操作 */
	private ObjBooleanConsumer<IFrame> onAction = SRC_ACTION;
	
	public InvisibleButton(int width, int height) {
		setSize(width, height);
	}
	
	/** 判断鼠标是否在控件中 */
	protected boolean isMouseIn() { return mouse; }
	
	/** 设置按钮被点击时的操作，默认为无任何操作 */
	public void setAction(ObjBooleanConsumer<IFrame> consumer) {
		onAction = StringUtil.checkNull(consumer, "consumer");
	}
	
	/** 判断是否可用 */
	@SuppressWarnings("unused")
	public boolean isInvalid() {
		return isInvalid;
	}
	
	/** 设置是否不可用 */
	@SuppressWarnings("unused")
	public void setInvalid(boolean value) {
		isInvalid = value;
	}
	
	@Override
	protected void initForManager(IComponentManager manager) {
		super.initForManager(manager);
		registryListener((IMouseEnteredListener) (mouseX, mouseY) -> mouse = true);
		registryListener((IMouseExitedListener) () -> mouse = false);
		registryListener(new IMouseActionListener() {
			final MIFrame frame = manager.getFrame();
			
			@Override
			public void mouseAction(float mouseX, float mouseY) {
				if (isInvalid()) return;
				onAction.accept(frame, WorldUtil.isClient());
				if (WorldUtil.isClient()) {
					playSound();
				}
			}
			
			@Nullable
			@Override
			public boolean writeTo(IDataWriter writer) {
				return !isInvalid();
			}
			
			@Override
			public void readFrom(IDataReader data) {
				mouseAction(-1, -1);
			}
		});
	}
	
	@SideOnly(Side.CLIENT)
	private static void playSound() {
		Minecraft.getMinecraft().getSoundHandler().playSound(
				PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}
	
}