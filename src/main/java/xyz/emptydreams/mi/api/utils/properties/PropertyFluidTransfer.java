package xyz.emptydreams.mi.api.utils.properties;

import net.minecraft.block.properties.PropertyEnum;
import xyz.emptydreams.mi.content.blocks.base.pipes.enums.FTStateEnum;

import java.util.Arrays;
import java.util.Collection;

/**
 * 流体管道的样式
 * @author EmptyDreams
 */
public class PropertyFluidTransfer extends PropertyEnum<FTStateEnum> {
	
	protected PropertyFluidTransfer(String name, Collection<FTStateEnum> allowedValues) {
		super(name, FTStateEnum.class, allowedValues);
	}
	
	/**
	 * 通过名称创建一个新的实例
	 */
	public static PropertyFluidTransfer create(String name) {
		return new PropertyFluidTransfer(name, Arrays.asList(FTStateEnum.values()));
	}
	
}