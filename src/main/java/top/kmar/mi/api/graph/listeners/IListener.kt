package top.kmar.mi.api.graph.listeners

import top.kmar.mi.api.dor.interfaces.IDataReader
import javax.annotation.Nonnull

/**
 * 所有GUI事件的接口
 * @author EmptyDreams
 */
interface IListener {

    @Nonnull
    operator fun invoke(data: IListenerData): IDataReader

}