package top.kmar.mi.coremod.other;

import net.minecraftforge.common.capabilities.Capability;

import java.util.function.Predicate;

/**
 * @author EmptyDreams
 */
public interface ICapManagerCheck {

    /**
     * 遍历所有cap
     * @param test 返回值用于检测是否终止遍历
     */
    void forEachCaps(Predicate<Capability<?>> test);

}