package xyz.emptydreams.mi.api.nbt;

/**
 * @author EmptyDreams
 */
public class ClassDataOperator extends ByteDataOperator {

	private final IClassData object;
	
	public ClassDataOperator(IClassData object) {
		this.object = object;
	}

	public void writeAll(IDataWriter writer) {
		object.writeAll(writer);
	}
	
	public void readAll(IDataReader reader) {
		object.readAll(reader);
	}
	
}