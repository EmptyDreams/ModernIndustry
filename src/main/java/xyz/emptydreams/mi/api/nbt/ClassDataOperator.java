package xyz.emptydreams.mi.api.nbt;

/**
 * @author EmptyDreams
 */
public class ClassDataOperator extends ByteDataOperator {

	private final IClassData object;
	
	public ClassDataOperator(IClassData object) {
		this.object = object;
	}

	public void writeAll() {
		object.writeAll();
	}
	
	public void readAll() {
		object.readAll();
	}
	
}