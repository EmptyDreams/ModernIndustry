package xyz.emptydreams.mi.api.dor;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;

/**
 * @author EmptyDreams
 */
public class SignBytes implements Iterable<Byte> {
	
	public static SignBytes read(IDataReader reader) {
		SignBytes result = new SignBytes();
		while (true) {
			byte data = reader.readByte();
			result.list.add(data);
			if ((data & 0b10000000) == 0) break;
		}
		return result;
	}
	
	private final ByteList list;
	private int size = 0;
	
	public SignBytes() {
		this(2);
	}
	
	public SignBytes(int size) {
		list = new ByteArrayList(size);
		list.add((byte) 0);
	}
	
	public SignBytes(byte[] bytes) {
		this(bytes.length);
		list.addElements(0, bytes);
	}
	
	public void add(State state) {
		int listIndex = size / 7;
		int innerIndex = size % 7;
		if (innerIndex == 6) {
			innerIndex = 0;
			list.set(listIndex, (byte) (list.getByte(listIndex) | 0b10000000));
			++listIndex;
			list.add((byte) 0);
		}
		byte input = state == State.ZERO ? (byte) (0b0000001 << innerIndex) : (byte) 0;
		list.set(listIndex, (byte) (list.get(listIndex) | input));
		++size;
	}
	
	public State get(int index) {
		int listIndex = index / 7;
		int innerIndex = index % 7;
		int b = list.getByte(listIndex);
		int test = 0b00000001 << innerIndex;
		return (test & b) == 0 ? State.ZERO : State.ONE;
	}
	
	public int size() {
		return size;
	}
	
	public void writeTo(IDataWriter writer) {
		ByteListIterator it = iterator();
		//noinspection WhileLoopReplaceableByForEach
		while (it.hasNext()) {
			byte data = it.next();
			writer.writeByte(data);
		}
	}
	
	public void writeTo(int index, IDataWriter writer) {
		ByteListIterator it = iterator();
		//noinspection WhileLoopReplaceableByForEach
		while (it.hasNext()) {
			byte data = it.next();
			writer.writeByte(index++, data);
		}
	}
	
	@Override
	public ByteListIterator iterator() {
		return list.iterator();
	}
	
	/** @see ByteList#listIterator(int)  */
	public ByteListIterator listIterator(int index) {
		return list.listIterator(index);
	}
	
	public enum State {ZERO, ONE }
	
}