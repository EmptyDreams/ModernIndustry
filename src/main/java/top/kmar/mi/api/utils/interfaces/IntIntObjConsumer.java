package top.kmar.mi.api.utils.interfaces;

/**
 * @see ThConsumer
 * @author EmptyDreams
 */
@FunctionalInterface
public interface IntIntObjConsumer<T> {

    void accept(int arg0, int arg1, T arg2);

}