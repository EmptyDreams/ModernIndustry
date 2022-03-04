package top.kmar.mi.content.gui;

import top.kmar.mi.api.gui.component.ButtonComponent;
import top.kmar.mi.api.gui.component.StringComponent;
import top.kmar.mi.api.gui.component.group.Group;
import top.kmar.mi.api.gui.component.group.Panels;

import java.util.function.Consumer;

/**
 * 翻页控制面板
 * @author EmptyDreams
 */
public class ControlPanel extends Group {

	private Consumer<ControlPanel> nextOperator;
	private Consumer<ControlPanel> preOperator;
	private final StringComponent shower = new StringComponent();
	
	public ControlPanel(Style style, int longSide) {
		style.init(this, longSide);
	}
	
	/** 触发翻页至下一页的操作 */
	public void next() {
		nextOperator.accept(this);
	}
	
	/** 触发翻页至上一页的操作 */
	public void pre() {
		preOperator.accept(this);
	}
	
	public void setTitle(String title) {
		shower.setString(title);
		getControlMode().accept(this);
	}
	
	public void setNextOperator(Consumer<ControlPanel> nextOperator) {
		this.nextOperator = nextOperator;
	}
	
	public void setPreOperator(Consumer<ControlPanel> preOperator) {
		this.preOperator = preOperator;
	}
	
	public enum Style {
	
		REC_UP_AND_DOWN {
			@Override
			public void init(ControlPanel control, int longSide) {
				control.setControlPanel(Panels::horizontalCenter);
				control.setSize(longSide, 15);
				ButtonComponent pre = new ButtonComponent(
									10, 15, ButtonComponent.Style.REC_PAGE_LEFT);
				ButtonComponent next = new ButtonComponent(
									10, 15, ButtonComponent.Style.REC_PAGE_RIGHT);
				pre.setAction((frame, isClient) -> control.pre());
				next.setAction((frame, isClient) -> control.next());
				control.adds(pre, control.shower, next);
			}
		},
		REC_RIGHT_AND_LEFT {
			@Override
			public void init(ControlPanel control, int longSide) {
				control.setControlPanel(Panels::verticalCenter);
				control.setSize(15, longSide);
				ButtonComponent pre = new ButtonComponent(
						15, 10, ButtonComponent.Style.REC_PAGE_UP);
				ButtonComponent next = new ButtonComponent(
						15, 10, ButtonComponent.Style.REC_PAGE_DOWN);
				pre.setAction((frame, isClient) -> control.pre());
				next.setAction((frame, isClient) -> control.next());
				control.adds(pre, control.shower, next);
			}
		},
		ARC_UP_AND_DOWN {
			@Override
			public void init(ControlPanel control, int longSide) {
				control.setControlPanel(Panels::horizontalCenter);
				control.setSize(longSide, 15);
				ButtonComponent pre = new ButtonComponent(
						18, 10, ButtonComponent.Style.ARC_PAGE_LEFT);
				ButtonComponent next = new ButtonComponent(
						18, 10, ButtonComponent.Style.ARC_PAGE_RIGHT);
				pre.setAction((frame, isClient) -> control.pre());
				next.setAction((frame, isClient) -> control.next());
				control.adds(pre, control.shower, next);
			}
		},
		SQUARE_UP_AND_DOWN {
			@Override
			public void init(ControlPanel control, int longSide) {
				control.setControlPanel(Panels::horizontalCenter);
				control.setSize(longSide, 15);
				ButtonComponent pre = new ButtonComponent(
						15, 15, ButtonComponent.Style.REC_PAGE_LEFT);
				ButtonComponent next = new ButtonComponent(
						15, 15, ButtonComponent.Style.REC_PAGE_RIGHT);
				pre.setText("<");   next.setText(">");
				pre.setAction((frame, isClient) -> control.pre());
				next.setAction((frame, isClient) -> control.next());
				control.adds(pre, control.shower, next);
			}
		},
		SQUARE_LEFT_AND_RIGHT {
			@Override
			public void init(ControlPanel control, int longSide) {
				control.setControlPanel(Panels::verticalCenter);
				control.setSize(15, longSide);
				ButtonComponent pre = new ButtonComponent(
						15, 15, ButtonComponent.Style.REC_PAGE_UP);
				ButtonComponent next = new ButtonComponent(
						15, 15, ButtonComponent.Style.REC_PAGE_DOWN);
				pre.setText("<");   next.setText(">");
				pre.setAction((frame, isClient) -> control.pre());
				next.setAction((frame, isClient) -> control.next());
				control.adds(pre, control.shower, next);
			}
		};
		
		
		abstract public void init(ControlPanel control, int longSide);
	
	}

}