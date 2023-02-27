package top.kmar.mi.api.utils.interfaces;

/**
 * @see java.util.function.Consumer
 * @see java.util.function.BiConsumer
 * @author EmptyDreams
 */
@FunctionalInterface
public interface ThConsumer<T, U, V> {

    /**
     * @see java.util.function.Consumer#accept(Object)
     * @see java.util.function.BiConsumer#accept(Object, Object)
     */
    void accept(T t, U u, V v);

}