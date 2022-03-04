package top.kmar.mi.api.fluid.data;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.ListIterator;

import static java.lang.Math.min;

/**
 * 流体运输队列（先进先出）
 * @author EmptyDreams
 */
public class FluidQueue {
    
    public static FluidQueue empty() {
        return new FluidQueue();
    }
    
    public static FluidQueue from(FluidData... content) {
        FluidQueue result = new FluidQueue();
        for (FluidData data : content) {
            result.pushTail(data);
        }
        return result;
    }
    
    private final LinkedList<FluidData> queue;
    
    private FluidQueue() {
        queue = new LinkedList<>();
    }
    
    private FluidQueue(LinkedList<FluidData> content) {
        queue = content;
    }
    
    /**
     * 从队列头弹出一个流体数据
     * @param max 最多弹出的流体量
     */
    @Nonnull
    public FluidData popHead(int max) {
        if (queue.isEmpty()) return FluidData.empty();
        FluidData first = queue.getFirst();
        int value = min(max, first.getAmount());
        FluidData result = first.copy(value);
        first.minusAmount(value);
        if (first.isEmpty()) queue.removeFirst();
        return result;
    }
    
    /**
     * 从队列尾弹出一个流体数据
     * @param max 最多弹出的流体量
     */
    @Nonnull
    public FluidData popTail(int max) {
        if (queue.isEmpty()) return FluidData.empty();
        FluidData last = queue.getLast();
        int value = min(max, last.getAmount());
        FluidData result = last.copy(value);
        last.minusAmount(value);
        if (last.isEmpty()) queue.removeLast();
        return result;
    }
    
    /**
     * <p>向队列头部添加流体数据
     * <p>如果当前队列不为空且队列头部流体种类和插入的流体数据一样，则会合并两个数据
     */
    public void pushHead(FluidData data) {
        if (data.isEmpty()) return;
        if (isEmpty()) queue.addFirst(data.copy());
        else {
            FluidData first = queue.getFirst();
            if (first.matchFluid(data)) first.plusAmount(data.getAmount());
            else queue.addFirst(data.copy());
        }
    }
    
    /**
     * <p>向队尾添加流体数据
     * <p>如果当前队列不为空且队尾流体种类和插入的流体数据一样，则会合并两个数据
     */
    public void pushTail(FluidData data) {
        if (data.isEmpty()) return;
        if (isEmpty()) queue.addLast(data.copy());
        else {
            FluidData last = queue.getLast();
            if (last.matchFluid(data)) last.plusAmount(data.getAmount());
            else queue.addLast(data.copy());
        }
    }
    
    /**
     * <p>推送另一个队列中地所有元素到当前队列中
     * <p>假如：
     * <ul>
     *     <li>当前队列为：4-5-6
     *     <li>要推入地队列为：1-2-3
     * </ul>
     * <p>那么推送后当前队列将变为：1-2-3-4-5-6
     * @return 推送成功的流体量
     */
    public int pushHead(FluidQueue queue) {
        int result = 0;
        ListIterator<FluidData> it = queue.queue.listIterator(queue.queue.size());
        while (it.hasPrevious()) {
            FluidData data = it.previous();
            this.queue.addFirst(data);
            result += data.getAmount();
        }
        return result;
    }
    
    /**
     * <p>推送另一个队列中地所有元素到当前队列中
     * <p>假如：
     * <ul>
     *     <li>当前队列为：1-2-3
     *     <li>要推入地队列为：4-5-6
     * </ul>
     * <p>那么推送后当前队列将变为：1-2-3-4-5-6
     * @return 推送成功的流体量
     */
    public int pushTail(FluidQueue queue) {
        int result = 0;
        for (FluidData data : queue.queue) {
            this.queue.addLast(data);
            result += data.getAmount();
        }
        return result;
    }
    
    public boolean isEmpty() {
        return queue.isEmpty();
    }
    
    public FluidQueue copy() {
        //noinspection unchecked
        return new FluidQueue((LinkedList<FluidData>) queue.clone());
    }
    
}