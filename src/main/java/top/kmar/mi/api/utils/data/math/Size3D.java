package top.kmar.mi.api.utils.data.math;

/**
 * 三维坐标系中的尺寸.<br>
 * <b>length表示X轴方向的大小，width表示Y轴方向的大小，height表示Z轴方向的大小</b>
 * @author EmptyDreams
 */
public final class Size3D {

    private final int length;
    private final int width;
    private final int height;

    public Size3D(int length, int width, int height) {
        this.length = length;
        this.width = width;
        this.height = height;
    }

    /** 获取长度 */
    public int getLength() { return length; }
    /** 获取宽度 */
    public int getWidth() { return width; }
    /** 获取高度 */
    public int getHeight() { return height; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Size3D size3D = (Size3D) o;

        if (length != size3D.length) return false;
        if (width != size3D.width) return false;
        return height == size3D.height;
    }

    @Override
    public int hashCode() {
        int result = length;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "length=" + length +
                ", width=" + width +
                ", height=" + height;
    }
}