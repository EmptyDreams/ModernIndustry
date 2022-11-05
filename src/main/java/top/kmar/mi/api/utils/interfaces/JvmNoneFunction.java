package top.kmar.mi.api.utils.interfaces;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * @author EmptyDreams
 */
@FunctionalInterface
public interface JvmNoneFunction extends Function0<Unit> {
    
    @Override
    default Unit invoke() {
        apply();
        return Unit.INSTANCE;
    }
    
    void apply();
    
}