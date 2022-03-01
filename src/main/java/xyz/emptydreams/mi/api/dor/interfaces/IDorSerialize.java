package xyz.emptydreams.mi.api.dor.interfaces;

/**
 * 实现该接口的类可以被自动存储系统所识别
 * @author EmptyDreams
 */
public interface IDorSerialize {
    
    /** 将类中的信息序列化 */
    IDataReader serializeDor();
    
    /** 解序列化 */
    void deserializedDor(IDataReader reader);
    
}