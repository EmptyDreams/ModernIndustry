package xyz.emptydreams.mi.content.blocks.base.pipes.enums;

import net.minecraft.block.properties.PropertyEnum;

import java.util.Arrays;

/**
 * @author EmptyDreams
 */
public class PropertyAngleFacing extends PropertyEnum<AngleFacingEnum> {
	
	public static PropertyAngleFacing create(String name) {
		return new PropertyAngleFacing(name);
	}
	
	private PropertyAngleFacing(String name) {
		super(name, AngleFacingEnum.class, Arrays.asList(AngleFacingEnum.values()));
	}
	
}