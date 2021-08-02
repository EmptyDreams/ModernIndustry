package xyz.emptydreams.mi.content.blocks.machine;

import xyz.emptydreams.mi.api.register.others.AutoManager;
import xyz.emptydreams.mi.content.blocks.base.pipes.AnglePipe;
import xyz.emptydreams.mi.content.blocks.base.pipes.StraightPipe;

/**
 * 包含大部分流体管道
 * @author EmptyDreams
 */
@SuppressWarnings("unused")
@AutoManager(block = true)
public final class PipeManager {
	
	public static final StraightPipe IRON_STRAIGHT = new StraightPipe("iron_straight_ft");
	public static final AnglePipe IRON_ANGLE = new AnglePipe("iron_angle_ft");
	
}