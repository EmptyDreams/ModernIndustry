package top.kmar.mi.content.blocks.machine;

import top.kmar.mi.content.blocks.base.pipes.AnglePipe;
import top.kmar.mi.content.blocks.base.pipes.ShuntPipe;
import top.kmar.mi.content.blocks.base.pipes.StraightPipe;
import top.kmar.mi.api.regedits.others.AutoManager;

/**
 * 包含大部分流体管道
 * @author EmptyDreams
 */
@SuppressWarnings("unused")
@AutoManager(block = true)
public final class PipeManager {
	
	public static final StraightPipe IRON_STRAIGHT = new StraightPipe("iron_straight_ft");
	public static final AnglePipe IRON_ANGLE = new AnglePipe("iron_angle_ft");
	public static final ShuntPipe IRON_SHUNT = new ShuntPipe("iron_shunt_ft");
	
}