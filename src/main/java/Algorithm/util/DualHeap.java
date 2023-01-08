package Algorithm.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class DualHeap {
    private final PriorityQueue<Integer> small;  // 大根堆，维护较小的一半元素
    private final PriorityQueue<Integer> large;  // 小根堆，维护较大的一半元素
    private final Map<Integer, Integer> delayed;  // 哈希表，记录"延迟删除"的元素，key 为元素，value 为需要删除的次数

    private int smallSize, largeSize, size;  // 大根堆,小根堆和全部包含的元素个数，需要扣除被"延迟删除"的元素

    public DualHeap() {
        this.small = new PriorityQueue<>(Comparator.reverseOrder());
        this.large = new PriorityQueue<>(Comparator.naturalOrder());
        this.delayed = new HashMap<>();
        this.smallSize = 0;
        this.largeSize = 0;
        this.size = 0;
    }

    public double getMedian() {
        if (small.isEmpty() || large.isEmpty()) return -1;  // error
        return (size & 1) == 1 ? small.peek() : ((double) small.peek() + large.peek()) / 2;
    }

    public void insert(int num) {
        if (small.isEmpty() || num <= small.peek()) {
            small.offer(num);
            ++smallSize;
        } else {
            large.offer(num);
            ++largeSize;
        }
        ++size;
        makeBalance();
    }

    public void erase(int num) {  // 删除的数没有添加并不会报错
        if (small.isEmpty() || large.isEmpty()) return;  // error
        delayed.put(num, delayed.getOrDefault(num, 0) + 1);
        if (num <= small.peek()) {
            --smallSize;
            if (num == small.peek()) {
                prune(small);
            }
        } else {
            --largeSize;
            if (num == large.peek()) {
                prune(large);
            }
        }
        --size;
        makeBalance();
    }

    // 不断地弹出 heap 的堆顶元素，并且更新哈希表
    private void prune(PriorityQueue<Integer> heap) {
        while (!heap.isEmpty()) {
            int num = heap.peek();
            if (delayed.containsKey(num)) {
                delayed.put(num, delayed.get(num) - 1);
                if (delayed.get(num) == 0) {
                    delayed.remove(num);
                }
                heap.poll();
            } else {
                break;
            }
        }
    }

    // 调整 small 和 large 中的元素个数，使得二者的元素个数满足要求
    private void makeBalance() {
        if (smallSize > largeSize + 1) {
            // small 比 large 元素多 2 个
            large.offer(small.poll());
            --smallSize;
            ++largeSize;
            // small 堆顶元素被移除，需要进行 prune
            prune(small);
        } else if (smallSize < largeSize) {
            // large 比 small 元素多 1 个
            small.offer(large.poll());
            ++smallSize;
            --largeSize;
            // large 堆顶元素被移除，需要进行 prune
            prune(large);
        }
    }
}