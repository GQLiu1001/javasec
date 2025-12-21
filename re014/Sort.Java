package re014;

public class Sort{
    // ====================== 冒泡排序（Bubble Sort） ======================
    /**
     * 冒泡排序（优化版）：升序排列int数组
     * 核心思路：
     * 1. 相邻元素两两比较，若前一个大于后一个则交换，每一轮将当前未排序部分的最大值"冒泡"到末尾；
     * 2. 外层循环控制排序轮数，总共需要length-1轮（最后一个元素无需再排）；
     * 3. 内层循环控制每轮的比较次数，每轮结束后末尾i个元素已排序，因此内层循环次数为length-1-i；
     * 4. 优化：添加标志位判断本轮是否发生交换，若未交换说明数组已完全有序，直接退出循环。
     *
     * @param arr 待排序的int数组（会直接修改原数组）
     */
    public static void bubbleSort(int[] arr) {
        // 边界条件：数组为空或长度为1，无需排序
        if (arr == null || arr.length <= 1) {
            return;
        }
        int length = arr.length;
        // 外层循环：控制排序轮数，共length-1轮
        for (int i = 0; i < length - 1; i++) {
            // 优化标志：记录本轮是否发生交换
            boolean isSwapped = false;
            // 内层循环：每轮比较到未排序部分的末尾，次数为length-1-i
            for (int j = 0; j < length - 1 - i; j++) {
                // 相邻元素比较，前一个大于后一个则交换
                if (arr[j] > arr[j + 1]) {
                    // 交换元素
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    // 标记本轮发生了交换
                    isSwapped = true;
                }
            }
            // 本轮未发生交换，说明数组已完全有序，提前退出循环
            if (!isSwapped) {
                break;
            }
        }
    }

    // ====================== 快速排序（Quick Sort） ======================
    /**
     * 快速排序入口方法：升序排列int数组
     * 核心思路（分治思想）：
     * 1. 选一个基准值（pivot），将数组分为两部分：左边元素≤基准，右边元素≥基准；
     * 2. 递归对左边和右边的子数组重复上述操作，直到子数组长度为1（天然有序）；
     * 3. 关键是「分区（partition）」操作：通过指针交换，将基准值放到正确的位置。
     *
     * @param arr 待排序的int数组（会直接修改原数组）
     */
    public static void quickSort(int[] arr) {
        // 边界条件：数组为空或长度为1，无需排序
        if (arr == null || arr.length <= 1) {
            return;
        }
        // 调用递归快速排序方法，初始范围是整个数组（左边界0，右边界length-1）
        recursiveQuickSort(arr, 0, arr.length - 1);
    }

    /**
     * 快速排序递归方法：处理指定范围的子数组
     * @param arr 待排序数组
     * @param left 子数组左边界（包含）
     * @param right 子数组右边界（包含）
     */
    private static void recursiveQuickSort(int[] arr, int left, int right) {
        // 递归终止条件：左边界≥右边界，说明子数组长度≤1，无需排序
        if (left >= right) {
            return;
        }
        // 分区操作：获取基准值的最终位置，同时将数组分为左右两部分
        int pivotIndex = partition(arr, left, right);
        // 递归处理左子数组（小于基准的部分）
        recursiveQuickSort(arr, left, pivotIndex - 1);
        // 递归处理右子数组（大于基准的部分）
        recursiveQuickSort(arr, pivotIndex + 1, right);
    }

    /**
     * 快速排序核心：分区操作
     * 思路：
     * 1. 选择右边界元素作为基准值（pivot）；
     * 2. 定义指针i，初始为left-1，代表「小于基准的区域的最后一个位置」；
     * 3. 遍历指针j从left到right-1，若arr[j]≤pivot，将i右移并交换arr[i]和arr[j]，扩大小于基准的区域；
     * 4. 遍历结束后，将基准值与arr[i+1]交换，此时基准值位于正确位置（左边都≤它，右边都≥它）；
     * 5. 返回基准值的最终索引。
     *
     * @param arr 待分区数组
     * @param left 分区左边界
     * @param right 分区右边界（基准值初始位置）
     * @return 基准值的最终索引
     */
    private static int partition(int[] arr, int left, int right) {
        // 选择右边界元素作为基准值（也可选择中间/随机元素，优化最坏情况）
        int pivot = arr[right];
        // i：小于基准的区域的尾指针，初始为left-1（区域为空）
        int i = left - 1;
        // j：遍历指针，从左边界到右边界前一个元素
        for (int j = left; j < right; j++) {
            // 若当前元素≤基准值，将其加入小于基准的区域
            if (arr[j] <= pivot) {
                i++; // 扩大小于基准的区域
                // 交换arr[i]和arr[j]，将当前元素放入小于基准的区域
                swap(arr, i, j);
            }
        }
        // 遍历结束后，将基准值放到小于区域的下一个位置（i+1），此时基准值位置正确
        swap(arr, i + 1, right);
        // 返回基准值的最终索引
        return i + 1;
    }
}