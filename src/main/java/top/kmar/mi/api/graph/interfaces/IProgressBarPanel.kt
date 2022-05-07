package top.kmar.mi.api.graph.interfaces

/**
 * 进度条的接口
 * @author EmptyDreams
 */
interface IProgressBarPanel {

    /** 最大值 */
    var maxValue: Int
    /** 当前值 */
    var value: Int
    /** 进度 */
    val percent: Double
        get() = value.toDouble() / maxValue

}