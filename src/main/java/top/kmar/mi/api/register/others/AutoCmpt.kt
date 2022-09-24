package top.kmar.mi.api.register.others

/** 自动注册控件 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class AutoCmpt(
    /** 控件的tag */
    val tag: String
)