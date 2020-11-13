package xyz.emptydreams.mi.api.interfaces;

/**
 * @see java.util.function.Consumer
 * @author EmptyDreams
 */
@FunctionalInterface
public interface ObjBooleanConsumer<T> {
	
	void accept(T t, boolean bool);
	
}