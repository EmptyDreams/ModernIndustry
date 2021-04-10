package xyz.emptydreams.mi.api.dor;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;

import java.util.Iterator;

/**
 * @author EmptyDreams
 */
public class SignBytes implements Iterable<SignBytes.State> {
	
	public static SignBytes read(IDataReader reader, int size) {
		SignBytes result = new SignBytes((size /8) + 1);
		result.list.clear();
		for (int k = 0; k < size; k += 8) {
			byte data = reader.readByte();
			result.list.add(data);
		}
		result.size = size;
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
		int listIndex = size / 8;
		int innerIndex = size % 8;
		if (innerIndex == 7) {
			innerIndex = 0;
			++listIndex;
			list.add((byte) 0);
		}
		byte input = state.isOne() ? (byte) (0b0000001 << innerIndex) : (byte) 0;
		list.set(listIndex, (byte) (list.get(listIndex) | input));
		++size;
	}
	
	public State get(int index) {
		int listIndex = index / 8;
		int innerIndex = index % 8;
		int b = list.getByte(listIndex);
		int test = 0b00000001 << innerIndex;
		return (test & b) == 0 ? State.ZERO : State.ONE;
	}
	
	public int size() {
		return size;
	}
	
	public void writeTo(IDataWriter writer) {
		ByteListIterator it = list.iterator();
		//noinspection WhileLoopReplaceableByForEach
		while (it.hasNext()) {
			byte data = it.next();
			writer.writeByte(data);
		}
	}
	
	@Override
	public Iterator<State> iterator() {
		return new SignIterator();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(size());
		for (State state : this) {
			builder.append(state.getChar());
		}
		return builder.toString();
	}
	
	public enum State {
		ZERO('0'),
		ONE('1');
		
		private final char SIGN;
		
		State(char sign) {
			this.SIGN = sign;
		}
		
		public boolean isOne() {
			return this == ONE;
		}
		
		public boolean isZero() {
			return this == ZERO;
		}
		
		public char getChar() {
			return SIGN;
		}
		
	}
	
	private final class SignIterator implements Iterator<State> {
		
		private int index = 0;
		
		@Override
		public boolean hasNext() {
			return index < size();
		}
		
		@Override
		public State next() {
			return get(index++);
		}
		
	}
	
}