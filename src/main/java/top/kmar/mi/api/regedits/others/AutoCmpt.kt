package top.kmar.mi.api.regedits.others

/** 自动注册控件 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class AutoCmpt(
    /** 控件的tag */
    val value: String
)