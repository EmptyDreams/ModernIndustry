package top.kmar.mi.api.utils

/**
 * tick任务
 * @param cycle 任务周期
 * @author EmptyDreams
 */
class TickClock(var cycle: Int) {

    var counter = 0

    /** 判断是否不需要执行任务并让计数器加一 */
    fun notContinue(): Boolean {
        if (++counter == cycle) counter = 0
        return counter != 0
    }

    /** 判断是否需要执行任务并让计数器加一 */
    fun isContinue() = !notContinue()

}