package top.kmar.mi.api.utils.container;

/**
 * 用于盛放int的容器
 * @author EmptyDreams
 */
public final class IntWrapper {

    private int value;

    /**
     * 创建一个包含指定int的容器
     */
    public IntWrapper(int v) {
        value = v;
    }

    /**
     * 创建一个包含0的容器
     */
    public IntWrapper() { this(0); }

    /** 获取 */
    public int get() { return value; }
    /** 设置 */
    public void set(int v) { value = v; }
    /** 加法 */
    public void add(int plus) { value += plus; }
    /** 自加 */
    public void increment() {
        ++value;
    }
    /** 自减 */
    public void decrement() {
        --value;
    }
    /** 设置并获取 */
    public int setAndGet(int v) { return (value = v); }
    /** 加并获取 */
    public int addAndGet(int plus) { return (value += plus); }
    /** 自加并获取 */
    public int incrementAndGet() { return ++value; }
    /** 自减并获取 */
    public int decrementAndGet() { return --value; }
    /** 获取并设置 */
    public int getAndSet(int v) {
        int c = value;
        value = v;
        return c;
    }
    /** 获取并加 */
    public int getAndAdd(int plus) {
        int c = value;
        value += plus;
        return c;
    }
    /** 获取并自加 */
    public int getAndIncrement() {
        return value++;
    }
    /** 获取并自减 */
    public int getAndDecrement() {
        return value--;
    }
    /** 转换为byte */
    public byte byteValue() {
        return (byte) get();
    }
    /** 转换为short */
    public short shortValue() {
        return (short) get();
    }
    /** 装箱 */
    public Wrapper<Integer> box() {
        return new Wrapper<>(get());
    }

    @Override
    public String toString() {
        return Integer.toString(get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value == ((IntWrapper) o).value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}