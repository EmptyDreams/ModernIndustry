package top.kmar.mi.api.graph.interfaces

/**
 * 具有管理控件能力的控件接口
 * @author EmptyDreams
 */
interface IPanelContainer : IPanel {

    /** 添加一个控件 */
    fun add(pane: IPanel)

    /** 移除指定控件 */
    fun remove(pane: IPanel)

    /** 遍历所有控件 */
    fun forEach(consumer: (IPanel) -> Unit)

}