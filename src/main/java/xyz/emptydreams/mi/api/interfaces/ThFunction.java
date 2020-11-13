package xyz.emptydreams.mi.api.interfaces;

/**
 * @see java.util.function.Function
 * @see java.util.function.BiFunction
 * @author EmptyDreams
 */
@FunctionalInterface
public interface ThFunction<T, U, V, R> {

	/**
	 * @see java.util.function.Function#apply(Object)
	 * @see java.util.function.BiFunction#apply(Object, Object)
	 */
	R apply(T t, U u, V v);

}
