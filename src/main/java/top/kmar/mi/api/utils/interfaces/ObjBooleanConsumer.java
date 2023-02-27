package top.kmar.mi.api.utils.interfaces;

/**
 * @see java.util.function.Consumer
 * @author EmptyDreams
 */
@FunctionalInterface
public interface ObjBooleanConsumer<T> {

    void accept(T t, boolean bool);

}