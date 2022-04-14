package top.kmar.mi.api.araw.interfaces;

import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDataWriter;

/**
 * 实现该接口的类可以被自动存储系统所识别
 * @author EmptyDreams
 */
public interface IDorSerialize {
    
    /**
     * <p>将类中的信息序列化
     * <p>类不应当存储传入的{@code writer}
     */
    void serializeDor(IDataWriter writer);
    
    /** 解序列化 */
    void deserializedDor(IDataReader reader);
    
}