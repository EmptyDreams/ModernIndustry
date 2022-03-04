package top.kmar.mi.api.utils.data.enums;

import top.kmar.mi.api.dor.ByteDataOperator;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDorSerialize;

/**
 * 以Enum为key的布尔映射表，该类仅支持对象数量<=32的枚举类
 * @param <T> enum的类型
 * @author EmptyDreams
 */
public class IndexEnumMap<T extends Enum<?>> implements IDorSerialize {

    /** 布尔值 */
    private int value = 0;
    
    public void set(T key, boolean flag) {
        if (flag) value |= 1 << key.ordinal();
        else value &= ~(1 << key.ordinal());
    }

    public boolean get(T key) {
        return ((value >> key.ordinal()) & 1) == 1;
    }
    
    /** 判断map是否相当于没有存储值 */
    public boolean isInit() {
        return value == 0;
    }
    
    /** 获取内部值 */
    public int getValue() {
        return value;
    }
    
    /** 设置内部值 */
    public void setValue(int value) {
        this.value = value;
    }
    
    @Override
    public IDataReader serializeDor() {
        ByteDataOperator operator = new ByteDataOperator(5);
        operator.writeVarInt(value);
        return operator;
    }
    
    @Override
    public void deserializedDor(IDataReader reader) {
        value = reader.readVarInt();
    }
}