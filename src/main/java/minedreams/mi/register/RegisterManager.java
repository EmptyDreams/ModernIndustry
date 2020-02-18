package minedreams.mi.register;

/**
 * 注册管理类，该类允许用户添加自己的注册机制，
 * 类中必须定义静态方法(可为私有)：{@code register(Class<?>)}，
 * 若需注册物品/方块需调用{@link AutoRegister}中的方法
 */
public @interface RegisterManager {
}
