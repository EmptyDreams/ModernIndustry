package top.kmar.mi.api.graph.listeners

import javax.annotation.Nonnull

/**
 * 所有GUI事件的接口
 * @author EmptyDreams
 */
interface IListener {

    @Nonnull
    operator fun invoke(data: IListenerData): IListenerData

}