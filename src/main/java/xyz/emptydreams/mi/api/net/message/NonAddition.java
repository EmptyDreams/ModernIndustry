package xyz.emptydreams.mi.api.net.message;

import xyz.emptydreams.mi.api.dor.IDataReader;
import xyz.emptydreams.mi.api.dor.IDataWriter;

/**
 * 空信息
 * @author EmptyDreams
 */
public final class NonAddition implements IMessageAddition {
	
	@Override
	public void writeTo(IDataWriter writer) { }
	
	@Override
	public void readFrom(IDataReader reader) { }
	
}