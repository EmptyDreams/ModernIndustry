package top.kmar.mi.api.graph.listeners

/**
 * 所有GUI事件的接口
 * @author EmptyDreams
 */
interface IListener {

    operator fun invoke(data: IListenerData)

}