<!-- GFM-TOC -->

- [二分查找](#一二分查找)
- [排序](#二排序)
- [双指针](#三双指针)
- [贪心法](#四贪心法)
- [分治法](#五分治法)
- [数学题](#六数学题)
- [搜索](#七搜索)
- [动态规划](#八动态规划)

<!-- GFM-TOC -->

**注：本篇所有题目均来自[LeetCode](https://leetcode-cn.com)**

### 一、二分查找

二分查找是顺序查找的优化，时间复杂度为O(logN)。二分查找中有几个需要注意的地方：

- 中值mid的计算：使用mid=(low+high)/2的方式可能会导致加法的结果超出整型能表示的范围，因此使用mid=low+(high-low)/2的方式
- 返回值。如果找到返回对应的index；如果未找到一般有两种返回值，-1和low(将key插入到数组中的正确位置)，有时可能是high，需要仔细分析。
- 边界值。有些特殊情况下low=mid+1和high=mid-1需要相应的调整；循环条件low<=high也有可能改为low<high（low=high时可能循环无法停止）

针对以上几个需要注意的地方，可以将二分查找分为以下几种问题（参考典型题目#34）：

- 寻找一个数。

  - while循环条件为<=。如果是<，则循环退出时还有一个区间为[left,right]且left=right，这种情况下可以在返回值处打一个补丁：nums[left]=target? left:-1;
  - 搜索区间，left=mid+1,right=mid-1，因为上一个搜索区间中mid已经检索过，所以应将mid移除搜索区间。
  - 如果没找到，返回-1。

- 寻找左侧边界。

  - 循环条件为<。初始right=nums.length，意味着搜索区间是[left,right)左闭右开的。当循环到left=right时，区间左闭右开为空，可以认为正确终止。

  - 搜索区间。right=mid，left=mid+1，因为左闭右开。当nums[mid]==target时，right=mid，向左搜索。

  - 返回值为left。该算法搜索到结果并不会立即返回，而是不断将区间向左收缩，以达到锁定左侧边界的目的。

  - 返回值为-1。target比数组所有数都大，不存在左侧边界。或者左侧边界值与target不相等。

    ```java
    return left < nums.length && nums[left] == target ? left : -1;
    ```
    

- 寻找右侧边界。

  - 与找左侧类似。循环条件为<，初始right=nums.length，

  - 搜索区间，当nums[mid]==target时，left=mid+1，向右搜索。

  - 返回值为left-1。当循环结束时，nums[left]一定不等于target，而nums[left-1]才有可能是。

  - 返回值为-1。当target比所有数都小，不存在右侧边界。或者右侧边界值与target不相等。

    ```java
    return left >0 && nums[left-1] == target ? (left-1) : -1;
    ```
    


#### 1. Pow(x,n)#50

描述：计算x的n次幂。

思路：快速幂。运用到折半的思想。

```java
class Solution {
    public double myPow(double x, int n) {
        if(n==0 || x==1) return 1;
        long N=n;
        if(N<0){
            x=1/x;
            N=-N;
        }
        double res=1;
        while(N>0){
            if((N&1)==1){
                res*=x;
            }
            x*=x;
            N=N>>1;
        }
        return res;
    }
}
```

#### 2. x的平方根#69

描述：实现int sqrt(int x)方法

思路：x的平方根一定在1-x的范围内，利用二分查找在这个区间内进行搜索。注意：整数部分向下取整，因此应返回left-1。

```java
class Solution {
    public int mySqrt(int x) {
        if(x==0) return 0;
        int low=1,high=x;
        while(low<=high){
            int mid=low+(high-low)/2;
            if(mid==x/mid) return mid;
            else if(mid<x/mid){
                low=mid+1;
            }else high=mid-1;
        }
        return low-1;
    }
}
```

#### 3.搜索二维矩阵#74

描述：编写一个高效的算法来判断 m x n 矩阵中，是否存在一个目标值。该矩阵具有如下特性：

- 每行中的整数从左到右按升序排列。
- 每行的第一个整数大于前一行的最后一个整数。

思路：由于矩阵的特性，可以转为一维矩阵，且有序，适合使用二分查找。（也可以采用双指针，从右上或者左下开始查找）

```java
class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        if(matrix.length==0) return false;
        int rows=matrix.length;
        int cols=matrix[0].length;
        int low=0,high=rows*cols-1;
        while(low<=high){
            int mid=low+(high-low)/2;
            if(matrix[mid/cols][mid%cols]==target) return true;
            if(matrix[mid/cols][mid%cols]>target) high=mid-1;
            else low=mid+1;
        }
        return false;
    }
} 
```

#### 4.搜索旋转数组#33

描述：假设按照升序排序的数组在预先未知的某个点上进行了旋转。搜索一个给定的目标值，如果数组中存在这个目标值，则返回它的索引，否则返回 `-1` 。你可以假设数组中不存在重复的元素。你的算法时间复杂度必须是 *O*(log *n*) 级别。

思路：使用二分查找，本题的难点在于判断target是在左区间还是右区间，判断条件如下：

- 当nums[low]<=nums[mid]，则[low,mid]序列是升序，当target>nums[mid] || target<nums[low]时，向右区间查找。此处判断时注意要判断mid和low是否相等。
- 当nums[low]>nums[mid]，则[low,mid]序列中有旋转，当target>nums[mid] && target<nums[low]时，向右区间查找。
- 其余情况向左区间查找。

```java
class Solution {
    public int search(int[] nums, int target) {
        int low=0,high=nums.length-1;
        while(low<high){
            int mid=low+(high-low)/2;
            if(nums[low]<=nums[mid]&&(target>nums[mid]||target<nums[low])){
                low=mid+1;
            }else if(target>nums[mid] && target<nums[low]){
                low=mid+1;
            }else high=mid;
        }
        return low<nums.length && nums[low]==target? low:-1;
    }
}
```

#### 5.查找元素的开始结束位置#34

描述：给定一个按照升序排列的整数数组 `nums`，和一个目标值 `target`。找出给定目标值在数组中的开始位置和结束位置。

思路：分别二分搜索左边界和右边界。

```java
class Solution {
    public int[] searchRange(int[] nums, int target) {
        int [] res=new int [2];
        int left=0,right=nums.length-1;
        while(left<right){
            int mid=left+(right-left)/2;
            if(nums[mid]==target) right=mid;
            else if(nums[mid]>target) right=mid;
            else left=mid+1;
        }
        res[0]=left<nums.length && nums[left]==target?left:-1;
        left=0;
        right=nums.length;
        while(left<right){
            int mid=left+(right-left)/2;
            if(nums[mid]==target) left=mid+1;
            else if(nums[mid]>target) right=mid;
            else left=mid+1;
        }
        res[1]=left>0 && nums[left-1]==target?(left-1):-1;
        return res;
    }
}
```

#### 6.第一个错误的版本#278

描述：假设你有 n 个版本 [1, 2, ..., n]，你想找出导致之后所有版本出错的第一个错误的版本。你可以通过调用 bool isBadVersion(version) 接口来判断版本号 version 是否在单元测试中出错。实现一个函数来查找第一个错误的版本。你应该尽量减少对调用 API 的次数。

思路：二分查找寻找左侧边界。此题有个小坑，需要判断n是否为Intger.MAX_VALUE。

```java
public class Solution extends VersionControl {
    public int firstBadVersion(int n) {
        int low=1,high;
        if(n==Integer.MAX_VALUE) high=n;
        else high=n+1;
        while(low<high){
            int mid=low+(high-low)/2;
            if(isBadVersion(mid)) high=mid;
            else low=mid+1;
        }
        return low;
    }
}
```

#### 7.大于给定元素的最小元素#744

描述：给定一个只包含小写字母的有序数组`letters` 和一个目标字母 `target`，寻找有序数组里面比目标字母大的最小字母。数组里字母的顺序是循环的。举个例子，如果目标字母`target = 'z'` 并且有序数组为 `letters = ['a', 'b']`，则答案返回 `'a'`。

思路：二分查找寻找右侧边界。此处的结果为右侧边界+1，所以返回值就是low。

```java
class Solution {
    public char nextGreatestLetter(char[] letters, char target) {
        int low=0,high=letters.length;
        while(low<high){
            int mid=low+(high-low)/2;
            if(letters[mid]==target) low=mid+1;
            else if(letters[mid]<target) low=mid+1;
            else high=mid;
        }
        return low<letters.length ? letters[low]:letters[0];
    }
}
```



### 二、排序

#### 1.排序算法

##### 1.1 三种基本排序

冒泡排序

最简单的排序算法，每次比较两个元素，如果顺序错误就交换。

```java
public static int[] bubbleSort(int[] array){
    if(array==null || array.length<2) return array;
    for(int i=0;i<array.length;i++){
        boolean isSwap=false;//标识每轮是否发生交换
        for(int j=0;j<array.length-1-i;j++){//每趟排完,后i个数有序
            if(array[j]>array[j+1]){
                swap(array,j,j+1);
                isSwap=true;
            }
        }
        if(!isSwap) break;
    }
    return array;
}
```

选择排序

依次从剩余数组元素中选出最小元素并排列。

```java
public static int[] selectionSort(int[] array){
    if(array==null || array.length<2) return array;
    for(int i=0;i<array.length;i++){
        int minIndex=i;
        for(int j=i+1;j<array.length;j++){
            if(array[j]<array[minIndex]){
                minIndex=j;
            }
        }
        if(minIndex!=i){
            swap(array,i,minIndex);
        }
    }
    return array;
}
```

插入排序

每次将当前元素插入到左侧有序数组中，使得插入后的左侧依然有序。

```java
public static int[] insertionSort(int[] array){
    if(array==null || array.length<2) return array;
    for(int i=0;i<array.length-1;i++){
        int index=i;
        int tmp=array[index+1];
        while(index>=0 && tmp<array[index]){
            array[index+1]=array[index];
            index--;
        }
        array[index+1]=tmp;
    }
    return array;
}
```

##### 1.2 希尔排序

希尔排序是对插入排序的改进。又称为缩小增量排序。与插入排序的区别在于，每次移动的间隔元素个数由1个变为增量个，然后每轮移动完后增量减少为一半。

```java
    public static int[] shellSort(int[] array){
        if(array==null || array.length<2) return array;
        int increment=array.length/2;
        while(increment>0){
            for(int i=increment;i<array.length;i++){
                int index=i-increment;
                int tmp=array[i];
                while(index>=0 && tmp<array[index]){
                    array[index+increment]=array[index];
                    index-=increment;
                }
                array[index+increment]=tmp;
            }
            increment/=2;//增量每次减小一半
        }
        return array;
    }
```

##### 1.3 归并排序

归并排序是一种高效的排序方法。将序列逐次拆分为子序列直至子序列元素为1，然后将有序的子序列进行合并。

```java
public static int[] mergeSort(int[] array){
    if(array==null || array.length<2) return array;
    int mid=array.length/2;
    int[] left= Arrays.copyOfRange(array,0,mid);
    int[] right=Arrays.copyOfRange(array,mid,array.length);
    return merge(mergeSort(left),mergeSort(right));
}
private static int[] merge(int[]left,int[] right){
    int [] res=new int[left.length+right.length];
    int index=0,leftIndex=0,rightIndex=0;
    while(index<res.length){
        if(leftIndex>=left.length) res[index++]=right[rightIndex++];
        else if(rightIndex>=right.length) res[index++]=left[leftIndex];
        else if(left[leftIndex]<right[rightIndex]){
            res[index++]=left[leftIndex++];
        }else res[index++]=right[rightIndex++];
    }
    return res;
}
```

##### 1.4 快速排序

快速排序也是一种高效的排序算法。通过一个切分元素将数组切分为左右两个子数组，左边小于切分元素，右边大于该元素。递归对子数组进行快速排序。

```java
public static int[] quickSort(int[] array){
    return quickSort(array,0,array.length-1);
}
private static int[] quickSort(int[] array,int start,int end){
    if(array==null || array.length<2) return array;
    int index=partition(array,start,end);
    if(index>start) quickSort(array,start,index-1);
    if(index<end) quickSort(array,index+1,end);
    return array;
}
private static int partition(int[] array,int start,int end){
    int i=start,j=end;
    while(i<j){
        while(array[j]>=array[end] && i<j) j--;
        while(array[i]<array[end] && i<j) i++;
        if(i<j) swap(array,i,j );
    }
    return i;
}
```

算法改进：

- 对于小数组，插入排序的性能更好，可以在小数组时切换到插入排序。
- 基准值的选取。上述代码中选取的基准值是最后一个数，最好的情况是每次都能取中位数作为切分元素。一般折中的办法是取三个元素并将居中元素作为切分元素。
- 对于有大量重复元素的数组，可以将数组切分为三部分，分别对应为小于、等于和大于切分元素。即三向切分快速排序

##### 1.5 堆排序

堆排序是利用堆设计的排序算法。堆是一个完全二叉树，并满足其子节点都大于/小于等于其父节点的值。堆也可以用数组表示，其中位置为k（位置=索引+1）的节点的父节点位置为k/2，其子节点的位置为2k和2k+1。

堆的有序化方式有两种：

- 上浮。当一个节点比其父节点大，那么交换两个节点。不断地向上进行比较和交换，这种操作称为上浮。

```java
private void swim(int k){
    while(k>1 && heap[k]>heap[k/2]){
        swap(heap,k,k/2);
        k/=2;
    }
}
```

- 下沉。当一个节点比其子节点小，那么交换两个节点。不断地向下进行比较和交换称为下沉。一个节点如果有两个子节点，那么应与两个子节点中大的进行交换。

```java
private void sink(int k){
    while(2*k<=N){//至少要有子节点
        int j=2*k;
        if(j<N && heap[j]<heap[j+1]) j++;
        if(heap[k]>=heap[j]) break;
        swap(heap,k,j);
        k=j;
    }
}
```

从右向左用sink()构造子堆效率较高。堆排序代码:

```java
public static int[] heapSort(int [] array){
    int N=array.length-1;
    for(int k=N/2;k>=1;k--){
        sink(array,k,N);//从右向左构造有序堆
    }
    while(N>1){
        swap(array,1,N--);
        sink(array,1,N);
    }
    return array;
}
private static void sink(int[] array, int k, int N){
    while(2*k<=N){
        int j=2*k;
        if(j<N && array[j]<array[j+1]) j++;
        if(array[k]>=array[j]) break;
        swap(array,k,j);
        k=j;
    }
}
```



##### 1.6 排序算法总结

| 算法     | 稳定性 | 时间复杂度（平均） | 空间复杂度 |
| -------- | ------ | ------------------ | ---------- |
| 冒泡排序 | 有     | n^2                | 1          |
| 选择排序 | 无     | n^2                | 1          |
| 插入排序 | 有     | n^2                | 1          |
| 希尔排序 | 无     | nlogn~n^2          | 1          |
| 归并排序 | 有     | nlogn              | n          |
| 快速排序 | 无     | nlogn              | logn~n     |
| 堆排序   | 无     | nlogn              | 1          |

一般来说，快速排序是最快的排序算法，还可以利用缓存。

Java中的排序方法为java.util.Arrays.sort()，对于基本数据类型使用三向切分快速排序，对于引用类型使用归并排序。



#### 2.应用

##### 2.1荷兰国旗问题#75

描述：给定一个包含红色、白色和蓝色，一共 *n* 个元素的数组，**原地**对它们进行排序，使得相同颜色的元素相邻，并按照红色、白色、蓝色顺序排列。此题中，我们使用整数 0、 1 和 2 分别表示红色、白色和蓝色。

思路：利用快速排序中的partition算法，每次将数组分为三个部分，即小于、等于和大于切分点。这个问题以1为切分点。

```java
class Solution {
    public void sortColors(int[] nums) {
        int p0=0,p2=nums.length-1;//p0记录0的右边界，p2记录2的左边界
        int cur=0;//工作指针
        while(cur<=p2){
            if(nums[cur]==0) swap(nums,p0++,cur++);
            else if(nums[cur]==1) cur++;
            else swap(nums,cur,p2--);
        }
    }
    private void swap(int[]a,int i,int j){
        int t=a[i];
        a[i]=a[j];
        a[j]=t;
    }
}
```

##### 2.数组中第k个最大元素#215

描述：在未排序的数组中找到第 **k** 个最大的元素。请注意，你需要找的是数组排序后的第 k 个最大的元素，而不是第 k 个不同的元素。

思路：排序中的经典问题。可以采用三种思路：

- 先对数组进行排序。时间复杂度O（nlogn），空间复杂度O（1）

```java
class Solution {
    public int findKthLargest(int[] nums, int k) {
        Arrays.sort(nums);
        return nums[nums.length-k];
    }
}
```

- 使用堆排序。维护一个包含最大的k个元素的小顶堆。向堆中添加元素的复杂度为O（logk），需执行n次，因此时间复杂度O（nlogk），空间复杂度O（k）。java默认为小顶堆。

```java
class Solution {
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap=new PriorityQueue<>();
        for(int i:nums){
            minHeap.add(i);
            if(minHeap.size()>k){
                minHeap.poll();
            }
        }
        return minHeap.peek();
    }
}
```

- 快速选择。参考快速排序中的partition方法。时间复杂度O（n），空间复杂度O（1）

```java
class Solution {
    public int findKthLargest(int[] nums, int k) {
        k=nums.length-k;
        int l=0,r=nums.length-1;
        while(l<r){
            int j=partition(nums,l,r);
            if(j==k) break;
            else if(j<k) l=j+1;
            else r=j-1;
        }
        return nums[k];
    }
    public int partition(int[]a,int l,int r){
        int i=l,j=r+1;
        while(true){
            while(a[++i]<a[l] && i<r);
            while(a[--j]>a[l] && j>l);
            if(i>=j) break;
            swap(a,i,j);
        }
        swap(a,l,j);
        return j;
    } 
    private void swap(int []a,int i,int j){
        int temp=a[i];
        a[i]=a[j];
        a[j]=temp;
    }
}
```

##### 3.出现频率最多的k个元素#347

描述：给定一个非空的整数数组，返回其中出现频率前 **k** 高的元素。

思路：使用桶排序。桶排序是利用映射函数将原数组中的元素映射到每个桶中，然后依次将各桶结果记录出来即可得到有序序列。时间效率很高O(n+k)，但空间开销较大O(n+k)。

先用HashMap统计每个数出现的频率。再设置若干个桶，每个桶存储出现频率相同的数，桶的下标存储数出现的频率。将数都放到桶中后，从后向前遍历桶，最先得到的k个数就是频率最高的k个数。

```java
class Solution {
    public List<Integer> topKFrequent(int[] nums, int k) {
        HashMap<Integer,Integer> map=new HashMap<>();
        for(int i:nums){
            if(map.containsKey(i)){
                map.put(i,map.get(i)+1);
            }else {
                map.put(i,1);
            }
        }
        List<Integer> [] buckets = new ArrayList[nums.length+1];//此处注意桶的最大索引等于元素出现频率的最大值
        for(int key:map.keySet()){
            int freq=map.get(key);
            if(buckets[freq]==null) buckets[freq]=new ArrayList<>();
            buckets[freq].add(key);
        }
        List<Integer> res=new ArrayList<>();
        for(int i=buckets.length-1;i>=0 && res.size()<k; i--){
            if(buckets[i]==null) continue;
            if(buckets[i].size()<=(k-res.size())){
                res.addAll(buckets[i]);
            }else {
                res.addAll(buckets[i].subList(0,k-res.size()));
            }
        }
        return res;
    }
}
```

此题也可以对map保存的结果进行堆排序，堆存放键，按照出现频率排序。

```java
class Solution {
    public List<Integer> topKFrequent(int[] nums, int k) {
        HashMap<Integer,Integer> map=new HashMap<>();
        for(int i:nums){
            if(map.containsKey(i)){
                map.put(i,map.get(i)+1);
            }else {
                map.put(i,1);
            }
        }
        PriorityQueue<Integer> minHeap=new PriorityQueue<>(new Comparator<Integer>(){
            @Override
            public int compare(Integer a,Integer b){
                return map.get(a)-map.get(b);
            }
        });
        for(int key:map.keySet()){
            minHeap.add(key);
            if(minHeap.size()>k){
                minHeap.poll();
            }
        }
        List<Integer> res=new ArrayList<>();
        while(!minHeap.isEmpty()){
            res.add(minHeap.poll());
        }
        return res;
    }
}
```



### 三、双指针

#### 1.无重复字符的最长子串#3

描述：给定一个字符串，请你找出其中不含有重复字符的 **最长子串** 的长度。

思路：双指针，维护一个set保存当前双指针之间出现的字符。

```java
class Solution {
    public int lengthOfLongestSubstring(String s) {
        if(s==null || s.length()==0) return 0;
        int res=1;
        HashSet<Character> set=new HashSet<>();
        int left=0,right=1;
        set.add(s.charAt(left));
        while(left<=right && right<s.length()){
            if(!set.contains(s.charAt(right))){
                res=Math.max(res,right-left+1);
                set.add(s.charAt(right++));
            }else{
                set.remove(s.charAt(left++));
            }
        }
        return res;
    }
}
```

#### 2.三数之和#15

描述：给定一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？找出所有满足条件且不重复的三元组。

示例：例如, 给定数组 nums = [-1, 0, 1, 2, -1, -4]，满足要求的三元组集合为：[ [-1, 0, 1],  [-1, -1, 2] ]。

思路：先用排序将数组有序化；任取一个数，取左指针i+1，右指针length-1，如果和恰好为0，则找到，两边指针向中间靠拢；如果大于0，右指针左移；小于0，左指针右移。

本题要注意去重，每个指针发生变化时，判断一次当前位置的值是否与上一个值相等。

优化：如果当前数字大于0，则三数之和一定大于0，所以结束循环。

```java
class Solution {
    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> ans = new ArrayList();
        int len = nums.length;
        if( len < 3) return ans;
        Arrays.sort(nums); // 排序
        for (int i = 0; i < len ; i++) {
            if(nums[i] > 0) break;//优化
            if(i > 0 && nums[i] == nums[i-1]) continue; // 去重
            int L = i+1;
            int R = len-1;
            while(L < R){
                int sum = nums[i] + nums[L] + nums[R];
                if(sum == 0){
                    ans.add(Arrays.asList(nums[i],nums[L],nums[R]));
                    while (L<R && nums[L] == nums[L+1]) L++; // 去重
                    while (L<R && nums[R] == nums[R-1]) R--; // 去重
                    L++;
                    R--;
                }
                else if (sum < 0) L++;
                else if (sum > 0) R--;
            }
        }        
        return ans;
    }
}
```

#### 3.合并两个有序数组#88

描述：给定两个有序整数数组 *nums1* 和 *nums2*，将 *nums2* 合并到 *nums1* 中*，*使得 *num1* 成为一个有序数组。初始化 nums1 和 nums2 的元素数量分别为 m 和 n。你可以假设 nums1 有足够的空间（空间大小大于或等于 m + n）来保存 nums2 中的元素。

思路：如果从头部开始归并，新的数组值会将num1的值覆盖掉；因此需要从尾部开始

```java
class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int i1=m-1;
        int i2=n-1;
        int indexMerge=m+n-1;
        while(i1>=0 || i2>=0){
            if(i1<0){
                nums1[indexMerge--]=nums2[i2--];
            }else if(i2<0){
                nums1[indexMerge--]=nums1[i1--];
            }else if(nums1[i1]>nums2[i2]){
                nums1[indexMerge--]=nums1[i1--];
            }else{
                nums1[indexMerge--]=nums2[i2--];
            }
        }
    }
}
```

#### 4.判断链表是否存在环#141

思路：对于链表成环问题，典型思路就是快慢指针，如果相遇，则存在环

延伸：剑指offer题找到环型链表环的入口。快慢指针相遇后，将快指针改为慢指针，调整至表头，再走一次，相遇点即为入口。

```java
public class Solution {
    public boolean hasCycle(ListNode head) {
        if(head==null) return false;
        ListNode slow=head;
        ListNode fast=head.next;
        while(fast!=null && slow!=null && fast.next!=null){
            if(fast==slow) return true;
            fast=fast.next.next;
            slow=slow.next;
        }
        return false;
    }
}
```

#### 5.有序数组的两数之和#167

描述：给定一个已按照升序排列 的有序数组，找到两个数使得它们相加之和等于目标数。函数应该返回这两个下标值 index1 和 index2，其中 index1 必须小于 index2。（注：下标值从1开始）

思路：双指针。一个指向头，一个指向尾。sum=target即为结果，sum偏大移动右侧元素，偏小移动左侧元素。

```java
class Solution {
    public int[] twoSum(int[] numbers, int target) {
        int index1=0,index2=numbers.length-1;
        int []res=new int[2];
        while(index1<index2){
            if(numbers[index1]+numbers[index2]==target){
                res[0]=index1+1;
                res[1]=index2+1;
                return res;
            }else if(numbers[index1]+numbers[index2]>target){
                index2--;
            }else index1++;
        }
        return res;
    }
}
```

#### 6.反转字符串中的元音字符#345

描述：编写一个函数，以字符串作为输入，反转该字符串中的元音字母。如

```
输入: "leetcode"
输出: "leotcede"
```

思路：设置双指针一个从头到尾，一个从尾到头遍历，最后指向需要反转的两个字符。

```java
class Solution {
    private static final HashSet<Character> vowelSet=new HashSet<>(Arrays.asList('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U'));
    
    public String reverseVowels(String s) {
        //双指针相向遍历
        int i=0,j=s.length()-1;
        char[] res=new char[s.length()];
        while(i<=j){
            if(!vowelSet.contains(s.charAt(i))){
                res[i]=s.charAt(i);
                i++;
            }else if(!vowelSet.contains(s.charAt(j))){
                res[j]=s.charAt(j);
                j--;
            }else{
                res[j]=s.charAt(i);
                res[i]=s.charAt(j);
                j--;
                i++;
            }
        }
        return new String(res);
    }
}
```

#### 7.通过删除字母匹配到字典里最长单词#524

```
Input:
s = "abpcplea", d = ["ale","apple","monkey","plea"]

Output:
"apple"
```

描述：给定一个字符串和一个字符串字典，找到字典里面最长的字符串，该字符串可以通过删除给定字符串的某些字符来得到。如果答案不止一个，返回长度最长且字典顺序最小的字符串。如果答案不存在，则返回空字符串。

思路：双指针判断目标串是不是s的子序列。

注：compareTo() 方法用于比较Number类型的两个数的大小，指定数比参数大，返回1，小返回-1，相等返回0。用在字符串比较中可比较字符串的字典序，例："a".compareTo("b")= -1。**注意不能用于判断基本数据类型**。

```java
class Solution {
    public String findLongestWord(String s, List<String> d) {
        String res="";
        for(String target:d){
            if(res.length()>target.length()||(res.length()==target.length() && res.compareTo(target)<0)){
                continue;
            }
            if(isSubString(s,target)){
                res=target;
            }
        }
        return res;
    }
    private boolean isSubString(String s,String target){
        int i=0,j=0;
        while(i<s.length() && j<target.length()){
            if(s.charAt(i)==target.charAt(j)){
                j++;
            }
            i++;
        }
        return j==target.length();
    }
}
```

#### 8.两数平方和#633

描述：判断一个数是否为两个数的平方和

思路：取0和根号c作为两个指针，平方和大于c则右指针--，小于c则左指针++

```java
class Solution {
    public boolean judgeSquareSum(int c) {
        int i=0,j=(int)Math.sqrt(c);
        while(i<=j){
            int powSum=i*i+j*j;
            if(powSum==c) return true;
            else if(powSum>c) j--;
            else i++;
        }
        return false;
    }
}
```

#### 9.回文字符串#680

描述：给定一个非空字符串 s，最多删除一个字符。判断是否能成为回文字符串。

思路：取两端作为左右指针，同时向中间移动。若两指针不相等，则如果删除一个后是回文串，一定是删除这两个指针之一。

```java
class Solution {
    public boolean validPalindrome(String s) {
        for(int i=0,j=s.length()-1;i<j;i++,j--){
            if(s.charAt(i)!=s.charAt(j)){
                return isPalindrome(s,i,j-1) || isPalindrome(s,i+1,j);
            }
        }
        return true;
    }
    private boolean isPalindrome(String s,int i,int j){
        while(i<j){
            if(s.charAt(i)!=s.charAt(j)){
                return false;
            }
            i++;
            j--;
        }
        return true;
    }
}
```

#### 10.盛最多水的容器#11

描述：给定 n 个非负整数 a1，a2，...，an，每个数代表坐标中的一个点 (i, ai) 。在坐标内画 n 条垂直线，垂直线 i 的两个端点分别为 (i, ai) 和 (i, 0)。找出其中的两条线，使得它们与 x 轴共同构成的容器可以容纳最多的水。

思路：左右指针先拉到最大范围，然后移动较矮一端的指针。

```java
class Solution {
    public int maxArea(int[] height) {
        int l=0,r=height.length-1;
        int res=0;
        while(l<r){
            int h=Math.min(height[l],height[r]);
            res=Math.max(res,h*(r-l));
            if(h==height[r]){
                r--;
            }else l++;
        }
        return res;
    }
}
```



### 四、贪心法

贪心算法是指在对问题进行求解时，总是做出当前看来最好的选择，所求出的解是局部最优解。

贪心算法不是对所有问题都能得到整体最优解，关键是贪心策略的选择，选择的贪心策略必须具备**无后效性**，即某个状态以前的过程不会影响以后的状态，只与当前状态有关。

#### 1.买卖股票的最佳收益#121,#122

描述：给定一个数组，它的第 i 个元素是一支给定股票第 i 天的价格。

如果你最多只允许完成一笔交易（即买入和卖出一支股票），设计一个算法来计算你所能获取的最大利润。注意你不能在买入股票前卖出股票。

思路：记录之前出现的最低价格作为买入价格，每次判断当前价格卖出后的利润是否为最大。

```java
class Solution {
    public int maxProfit(int[] prices) {
        if(prices.length==0) return 0;
        int buy,max,profit;
        buy=max=prices[0];
        profit=0;
        for(int i=1;i<prices.length;i++){
            if(buy>prices[i]){
                buy=max=prices[i];
            }else {
                max=Math.max(max,prices[i]);
            }
            profit=Math.max(profit,max-buy);
        }
        return profit;
    }
}
```

描述：设计一个算法来计算你所能获取的最大利润。可以多次买卖一支股票。多笔交易之间不能交叉（即每次买入前要空仓）。

思路：每访问到一个价格，如果比前一个价格大，则将该价格加到总利润中。

```java
class Solution {
    public int maxProfit(int[] prices) {
        int profit=0;
        for(int i=1;i<prices.length;i++){
            if(prices[i]>prices[i-1]){
                profit+=prices[i]-prices[i-1];
            }
        }
        return profit;
    }
}
```

#### 2.根据身高和序号重组队列#406

描述：假设有打乱顺序的一群人站成一个队列。 每个人由一个整数对(h, k)表示，其中h是这个人的身高，k是排在这个人前面且身高大于或等于h的人数。 编写一个算法来重建这个队列。

思路：用一个list来保存队列，为使插入操作无后效性，应该先插入身高高的人，因为再插入身高较低的人不会影响之前的排序。因此先按身高降序排列，再按k升序排列，每次将人插入到队列的第k个位置即可。

```java
class Solution {
    public int[][] reconstructQueue(int[][] people) {
        Arrays.sort(people,(a,b)->(a[0]==b[0])?(a[1]-b[1]):(b[0]-a[0]));
        List<int []> queue=new ArrayList<>();
        for(int [] p:people){
            queue.add(p[1],p);
        }
        return queue.toArray(new int [queue.size()][]);
    }
}
```

这里使用了lambda表达式创建Comparator，会增加算法运行时间，如果注重时间，可以修改为一般的创建语句。如下：

```java
Arrays.sort(people,new Comparator <int []>(){
    @Override
    public int compare(int[] a,int []b){
        return (a[0]==b[0])?(a[1]-b[1]):(b[0]-a[0]);
    }
});
```

#### 3.无重叠区间#435

描述：给定一个区间的集合，找到需要移除区间的最小数量，使剩余区间互不重叠。

```
输入: [ [1,2], [2,3], [3,4], [1,3] ]

输出: 1

输入: [ [1,2], [1,2], [1,2] ]

输出: 2
```

思路：先计算最多能组成的不重叠区间个数。然后用总个数减去不重叠区间的个数。

每次选择结尾最小的区间，这样可以给后面的区间留出最大的空间。注意每次判断是否与之前的区间存在重叠。

```java
class Solution {
    public int eraseOverlapIntervals(int[][] intervals) {
        if(intervals.length==0) return 0;
        Arrays.sort(intervals,Comparator.comparingInt(o->o[1]));
        int count=1;
        int end=intervals[0][1];
        for(int i=1;i<intervals.length;i++){
            if(intervals[i][0]<end){
                continue;
            }
            end=intervals[i][1];
            count++;
        }
        return intervals.length-count;
    }
}
```

这里的Comparator用了另一种lambda表达式写法。也可以写成：

```java
Arrays.sort(intervals,(o1,o2)->(o1[1]-o2[1]));
```

如果注重运行时间，可以写成一般形式：

```java
Arrays.sort(intervals,new Comparator<int[]>(){
    @Override
    public int compare(int[]o1,int[] o2){
        return o1[1]-(o2[1]);
    }
});
```

#### 4.用最少数量的箭引爆气球#452

描述：气球在一个水平数轴上摆放，可以重叠，飞镖垂直投向坐标轴，使得路径上的气球都被刺破。求解最小的投飞镖次数使所有气球都被刺破。

```
输入:
[[10,16], [2,8], [1,6], [7,12]]

输出:
2
```

思路：本题的本质还是求不重叠区间的个数，与上题的区别在于相邻区间也算作重叠区间。

```java
class Solution {
    public int findMinArrowShots(int[][] points) {
        if(points.length==0) return 0;
        Arrays.sort(points,new Comparator<int[]>(){
            @Override
            public int compare(int[]o1,int []o2){
                return o1[1]-o2[1];
            }
        });
        int count=1;
        int end=points[0][1];
        for(int i=1;i<points.length;i++){
            if(points[i][0]<=end){
                continue;
            }
            count++;
            end=points[i][1];
        }
        return count;
    }
}
```

#### 5.分发饼干#455

描述：每个孩子有一个胃口值gi，每块饼干有一个尺寸sj，如果sj>=gi，则可以将这块饼干分配给该孩子，目标是满足尽可能多的孩子，并求出这个最大数值。

思路：给每个孩子应尽量小，这样就可以留出更多饼干满足胃口更大的孩子。

```java
class Solution {
    public int findContentChildren(int[] g, int[] s) {
        Arrays.sort(g);
        Arrays.sort(s);
        int i=0,j=0;
        while(i<g.length && j<s.length){
            if(g[i]<=s[j]){
                i++;
                j++;
            }else j++;
        }
        return i;
    }
}
```

#### 6.种花问题#605

描述：给定一个花坛，两朵花之间至少要有一个单位的间隔，求是否可以种下n朵花。

思路：每三个连续的0就种一朵花（这种策略是无后效性的），判断最后数量是不是大于n。注意两端的处理，头尾分别都再 补一个0.

```java
class Solution {
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        int count=0;
        for(int i=0;i<flowerbed.length && count<n;i++){
            if(flowerbed[i]==1) continue;
            int pre=i==0?0:flowerbed[i-1];
            int next=i==flowerbed.length-1?0:flowerbed[i+1];
            if(pre==0 &&next==0) {
                count++;
                flowerbed[i]=1;
            }
        }
        return count>=n;
    }
}
```

#### 7.修改一个数成为非递减数列#665

描述：给定一个长度为 `n` 的整数数组，你的任务是判断在**最多**改变 `1` 个元素的情况下，该数组能否变成一个非递减数列。

思路：（主要利用无后效性考虑优先策略，不完全是贪心算法）遍历数组，当出现nums[i]<nums[i-1]的情况时，判断需要修改的是哪个数。为不影响后续操作，优先考虑修改nums[i-1]。但如果出现nums[i]<nums[i-2]时，则应修改的数时nums[i]

```java
class Solution {
    public boolean checkPossibility(int[] nums) {
        int count=0;
        for(int i=1;i<nums.length;i++){
            if(nums[i]>=nums[i-1]){
                continue;
            }
            count++;
            if(i-2>=0 && nums[i]<nums[i-2]){
                nums[i]=nums[i-1];
            }else nums[i-1]=nums[i];
        }
        return count<=1;
    }
}
```

#### 8.单调递增的数字#738

描述：给定一个非负整数 `N`，找出小于或等于 `N` 的最大的整数，同时这个整数需要满足其各个位数上的数字是单调递增。

```
输入: N = 332
输出: 299
```

思路：对N从高位向低位遍历，记录遇到的最大值和最大值所在的位置，当遇到后一位比当前位小时，停止遍历。将最大值所在位-1，后面全部置为9即可。（保证高位的数字尽可能大）

```java
class Solution {
    public int monotoneIncreasingDigits(int N) {
        String s=String.valueOf(N);
        char[] cs=s.toCharArray();
        int max=cs[0],index=0;
        for(int i=0;i<cs.length;i++){
            if(cs[i]>max){
                index=i;
                max=cs[i];
            }
            if (i<cs.length-1 && cs[i]>cs[i+1]){
                break;
            }
        }
        if(index!=cs.length-1){
            cs[index]=(char)(cs[index]-1);
            for(int i=index+1;i<cs.length;i++){
                cs[i]='9';
            }
            s=String.copyValueOf(cs);
           	N=Integer.parseInt(s);
        }
        return N;
    }
}
```

#### 9.划分字母区间#763

描述：字符串 `S` 由小写字母组成。我们要把这个字符串划分为尽可能多的片段，同一个字母只会出现在其中的一个片段。返回一个表示每个字符串片段的长度的列表。

```
输入: S = "ababcbacadefegdehijhklij"
输出: [9,7,8]
```

思路：用一个数组保存26个字母最后出现的位置。遍历字符串，一个指针保存上个片段的右端点，另一个指针保存当前片段的右端点。每遍历一个字符，如果该字符的右端点大于当前右端点，则取更大的右端点；如果遍历指针i与当前片段的右端点相等，则该片段可以作为结果中的一个片段。

```java
class Solution {
    public List<Integer> partitionLabels(String S) {
        int [] last=new int[26];
        for(int i=0;i<S.length();i++){
            last[S.charAt(i)-'a']=i;
        }
        List <Integer> res=new ArrayList<>();
        int preIndex=-1,maxIndex=0;
        for(int i=0;i<S.length();i++){
            maxIndex=Math.max(maxIndex, last[S.charAt(i)-'a']);
            if(i==maxIndex){
                res.add(maxIndex-preIndex);
                preIndex=maxIndex;
            }
        }
        return res;
    }
}
```

### 五、分治法

分治法，指的是分而治之的算法思想，是构建基于多项分支递归的一种算法范式。主要思路是将一个复杂的问题分解成两个或多个相同或相似的子问题，原问题即为子问题解的合并。

#### 1.求众数#169

思路：一般方法，用HashMap存储每个数出现次数，然后遍历map。时间空间复杂度都为O(n)

```java
class Solution {
    public int majorityElement(int[] nums) {
        Map<Integer,Integer> map=new HashMap<>();
        for(Integer i:nums){
            if(map.containsKey(i)){
                map.put(i,map.get(i)+1);
            }
            else map.put(i,1);
        }
        for(Integer key:map.keySet()){
            if(map.get(key)>nums.length/2){
                return key;
            }
        }
        return -1;
    }
}
```

分治法：将数组分为子数组，每次寻找子数组中的众数。在合并子数组时，如果两个子数组的众数相同，则该数即为数组的众数；否则比较两个众数在整个数组中出现的次数来确定。时间复杂度O(nlogn)，空间复杂度O(logn)

```java
class Solution {
    public int majorityElement(int[] nums) {
        return helper(nums,0,nums.length-1);
    }
    private int helper(int nums[],int l,int r){
        if(l==r) return nums[l];
        int mid=l+(r-l)/2;
        int left=helper(nums,l,mid);
        int right=helper(nums,mid+1,r);
        if(left==right) return left;
        int leftCount=0,rightCount=0;
        for(int i=l;i<=r;i++){
            if(nums[i]==left) leftCount++;
            if(nums[i]==right) rightCount++;
        }
        return leftCount>rightCount?left:right;
    }
}
```

排序法：先对数组排序，最中间那个数出现次数一定多于n/2。

```java
class Solution {
    public int majorityElement(int[] nums) {
        Arrays.sort(nums);
        return nums[nums.length/2];
    }
}
```

摩尔投票算法（ Boyer-Moore Majority Vote Algorithm ）。时间复杂度O(N)

```java
class Solution {
    public int majorityElement(int[] nums) {
        int cnt=0,res=nums[0];
        for(int i=0;i<nums.length;i++){
            res=(cnt==0)?nums[i]:res;
            cnt=(res==nums[i])?cnt+1:cnt-1;
        }
        return res;
    }
}
```

#### 2.为运算表达式设计优先级#241

描述：给定一个含有数字和运算符的字符串，为表达式添加括号，改变其运算优先级以求出不同的结果。你需要给出所有可能的组合的结果。有效的运算符号包含 +, - 以及 * 。

思路：遍历字符串，如遇运算符，对字符串进行分割。递归调用自身计算出左右子串的结果，再根据运算符计算出结果。

```java
class Solution {
    public List<Integer> diffWaysToCompute(String input) {
        List<Integer> res=new ArrayList<>();
        if(!input.contains("+") && !input.contains("-") && !input.contains("*")){
            res.add(Integer.valueOf(input));
        }
        for(int i=0;i<input.length();i++){
            char c=input.charAt(i);
            if(c=='+'||c=='-'|| c=='*'){
                List<Integer> left=diffWaysToCompute(input.substring(0,i));
                List<Integer> right=diffWaysToCompute(input.substring(i+1));
                for(Integer l:left){
                    for(Integer r:right){
                        if(c=='+') res.add(l+r);
                        else if(c=='-') res.add(l-r);
                        else res.add(l*r);
                    }
                }
            }
        }
        return res;
    }
}
```

#### 3.翻转对#493

描述：给定一个数组 `nums` ，如果 `i < j` 且 `nums[i] > 2*nums[j]` 我们就将 `(i, j)` 称作一个**重要翻转对**。你需要返回给定数组中的重要翻转对的数量。

思路：参考逆序对问题的思路。先一个循环统计翻转对的数量，再将两个数组归并起来。注意统计翻转对时，可能出现超过整型范围的情况，需要转为long型

```java
class Solution {
    public int reversePairs(int[] nums) {
        if(nums.length==0) return 0;
        int []help=new int[nums.length];
        for(int i=0;i<nums.length;i++) help[i]=nums[i];
        return mergeCount(nums,help,0,nums.length-1);
    }
    private int mergeCount(int[] nums,int [] help,int l,int r){
        if(l==r) return 0;
        int mid=l+(r-l)/2;
        int leftCount=mergeCount(nums,help,l,mid);
        int rightCount=mergeCount(nums,help,mid+1,r);
        int count=0;
        int i=l,j=mid+1;
        //统计翻转对的数量
        while(i<=mid && j<=r){
            if((long)nums[i]>(long)2*nums[j]){
                count+=mid+1-i;
                j++;
            }else i++;
        }
        //归并排序
        i=l;
        j=mid+1;
        int k=l;
        while(i<=mid && j<=r){
            if(nums[i]>nums[j]) help[k++]=nums[j++];
            else help[k++]=nums[i++];
        }
        while(i<=mid) help[k++]=nums[i++];
        while(j<=r) help[k++]=nums[j++];
        for(int s=l;s<=r;s++) nums[s]=help[s];
        return count+leftCount+rightCount;
    }
}
```

#### 4.不同的二叉搜索树II#95

描述：给定一个整数 *n*，生成所有由 1 ... *n* 为节点所组成的**二叉搜索树**。

思路：依次将1-n作为根节点，当根节点为i时，比i小的构成左子树，比i大的构成右子树。

```java
class Solution {
    public List<TreeNode> generateTrees(int n) {
        List<TreeNode> res=new ArrayList<TreeNode>();
        if(n<1) return res;
        return helper(1,n);
    }
    private List<TreeNode> helper(int l,int r){
        List<TreeNode> res=new ArrayList<TreeNode>();
        if(l>r) {
            res.add(null);
            return res;
        }
        if(l==r){
            res.add(new TreeNode(l));
            return res;
        }
        for(int i=l;i<=r;i++){
            List<TreeNode> leftSubTree=helper(l,i-1);
            List<TreeNode> rightSubTree=helper(i+1,r);
            for(TreeNode left:leftSubTree){
                for(TreeNode right:rightSubTree){
                    TreeNode root=new TreeNode(i);
                    root.left=left;
                    root.right=right;
                    res.add(root);
                }
            }
        }
        return res;
    }
}
```



### 六、数学题

#### 1.进制转换

##### 7进制#504

```java
class Solution {
    public String convertToBase7(int num) {
        if(num==0) return "0";
        StringBuilder sb=new StringBuilder();
        boolean isNegative=num<0;
        if(isNegative) num=-num;
        while(num>0){
            sb.append(num%7);
            num/=7;
        }
        String res=sb.reverse().toString();
        if(isNegative) res="-"+res;
        return res;
    }
}
```

java中的static String toString(int num, int radix)方法 可以将一个整数转换为 radix 进制表示的字符串。

##### 16进制#405

```java
class Solution {
    public String toHex(int num) {
        if(num == 0) {
            return "0";
        }
        char[] map = new char[] {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        StringBuilder sb = new StringBuilder("");
        while(num != 0) {
            sb.append(map[num & 15]);
            num >>>= 4;//因考虑为补码形式，符号位不能有特殊的意义，所以使用无符号右移
        }
        return sb.reverse().toString();
    }
}
```

##### 26进制#168

描述：给定一个正整数，返回它在 Excel 表中相对应的列名称。

```java
class Solution {
    public String convertToTitle(int n) {
        if(n==0) return "";
        StringBuilder sb=new StringBuilder();
        while(n>0){
            n--;//从1开始计算，而不是0
            sb.append((char)(n%26+'A'));
            n=n/26;
        }
        return sb.reverse().toString();
    }
}
```



#### 2.质数相关

性质：每个整数都可以分解为质数的乘积。

利用这一性质，有以下推论：

- 令x = 2^m0 * 3^m1 * 5^m2 * 7^m3 * 11^m4 * …

  令 y = 2^n0 * 3^n1 * 5^n2 * 7^n3 * 11^n4 * …

  若y %x =0，则有mi<=ni

- 最大公约数 gcd(x,y)等于每一位上取mi和ni的较小值

  最小公倍数lcm(x,y)等于每一位上取mi和ni的较大值

##### 统计质数#204

描述：统计所有小于非负整数 *n* 的质数的数量。

思路：使用暴力法时间复杂度过高。采用厄拉多塞筛法可以优化时间效率。每遇到一个数，则将其倍数划掉，继续遍历，遍历到n后没被划掉的即为所有质数。

可以用一个长度为n的数组，每遍历一个数，将其倍数对应位置的数改为1，最后遍历数组中0的个数即是质数的个数。

```java
class Solution {
    public int countPrimes(int n) {
        int[] num=new int[n];
        for(int i=2;i<n;i++){
            if(num[i]==0){
                for(int j=2;i*j<n;j++){
                    num[i*j]=1;
                }
            }
        }
        int count=0;
        for(int i=2;i<n;i++){
            if(num[i]==0) count++;
        }
        return count;
    }
}
```

##### 丑数#263，#264，#313

丑数就是只包含质因数 `2, 3, 5` 的**正整数**。

```java
//判断一个数是否为丑数
class Solution {
    public boolean isUgly(int num) {
        if(num<1) return false; 
        while(num%5==0) num/=5;
        while(num%3==0) num/=3;
        while(num%2==0) num>>=1;
        return num==1;
    }
}
```

```java
//找出第n个丑数。说明:1是丑数，n不超过1690。
//方法：动态规划
class Solution {
    public int nthUglyNumber(int n) {
        int []dp=new int[n];
        int c2,c3,c5;
        c2=c3=c5=0;
        dp[0]=1;
        for(int i=1;i<n;i++){
            dp[i]=Math.min(dp[c2]*2,Math.min(dp[c3]*3,dp[c5]*5));
            if(dp[i]==dp[c2]*2) c2++;
            if(dp[i]==dp[c3]*3) c3++;
            if(dp[i]==dp[c5]*5) c5++;
        }
        return dp[n-1];
    }
}
```

```java
//超级丑数。说明：给定质数列表，找出第n个丑数
//方法：动态规划
class Solution {
    public int nthSuperUglyNumber(int n, int[] primes) {
        int [] dp=new int[n];
        int [] count=new int[primes.length];
        dp[0]=1;
        for(int i=1;i<n;i++){
            dp[i]=primes[0]*dp[count[0]];
            for(int j=0;j<count.length;j++){
                dp[i]=Math.min(dp[i],primes[j]*dp[count[j]]);
            }
            for(int j=0;j<count.length;j++){
                if(dp[i]/primes[j]==dp[count[j]]) count[j]++;
            }
        }
        return dp[n-1];
    }
}
```

##### 求最大公约数和最小公倍数

```java
//辗转相除法求最大公约数
int gcdMod(int a,int b){
    return (b==0)? a : gcdMod(b, a%b);
}
//更相减损法求最大公约数
int gcdMinus(int a,int b){
    if(a<b) return gcdMinus(b,a);
    return (b==0)? a: gcdMinus(b,a-b);
}
//最小公倍数为ab相乘除以最大公约数
int lcm(int a,int b){
    return a*b/gcdMod(a,b);
}
```



#### 3.阶乘

##### 质数排列#1175

描述：给1到n的数设计排列方案，使其中的所有质数都在索引为质数的位置上。索引从1开始

思路：找出1到n的质数的个数c，结果等于c ! * (n-c) !

```java
class Solution {
    public int numPrimeArrangements(int n) {
        return (int)(factorial(countPrime(n)) * factorial(n-countPrime(n))%1000000007);
    }
    private int countPrime(int n){
        int[] num =new int[n+1];
        for(int i=2;i<n+1;i++){
            if(num[i]==0){
                for(int j=2;i*j<n+1;j++){
                    num[i*j]=1;
                }
            }
        }
        int count=0;
        for(int i=2;i<n+1;i++){
            if(num[i]==0) count++;
        }
        return count;
    }
    private long factorial(int n){
        if(n<=1) return 1;
        else return (long)n*factorial(n-1)%1000000007;
    }
}
```

##### 阶乘后的0#172

描述：给定一个整数 *n*，返回 *n*! 结果尾数中零的数量。

思路：尾部的0都是由2*5得到，5的数量远少于2，因此找出1到n中5的数量即可得出结果。其所包含的5的个数为N/5+N/5^2+N/5^3...即N/5是5的倍数贡献一个5，N/5^2是5^2的倍数再贡献一个5...

```java
class Solution {
    public int trailingZeroes(int n) {
        if(n==0) return 0;
        int count=0;
        while(n!=0){
            n/=5;
            count+=n;
        }
        return count;
    }
}
```

补充：类似的，如果是统计N!的二进制中最低位1的位置，只需要统计有多少个2即可。

##### 阶乘函数后k个0#793

描述： `f(x)` 是 `x!` 末尾是0的数量。给定 `K`，找出多少个非负整数`x` ，有 `f(x) = K` 的性质。K的取值上限是10^9

思路：k=x/5+x/5^2+x/5^3... 因此搜索下限是4K。采用二分搜索，如果搜到了，结果就是5，没搜到结果就是0。注意，当k取值较大时，会超出int的上界，所以需要使用long。

```java
class Solution {
    public int preimageSizeFZF(int K) {
        long l=4*K,r=5*(long)Math.pow(10,9);
        while(l<r){
            long mid=l+(r-l)/2;
            int count=trailingZeroes(mid);
            if(count<K)  l=mid+1;
            else if(count>K) r=mid-1;
            else return 5;
        }
        return 0;
    }
    private int trailingZeroes(long n){
        if(n==0) return 0;
        int count=0;
        while(n>0){
            n/=5;
            count+=n;
        }
        return count;
    }
}
```



#### 4.字符串运算

##### 二进制求和#67

描述：给定两个二进制字符串，返回他们的和（用二进制表示）。

```java
class Solution {
    public String addBinary(String a, String b) {
        int i=a.length()-1,j=b.length()-1;
        int carry=0;//每一位相加的结果
        StringBuilder sb=new StringBuilder();
        while(carry==1 || i>=0 || j>=0){
            if(i>=0 && a.charAt(i--)=='1'){
                carry++;
            }
            if(j>=0 && b.charAt(j--)=='1'){
                carry++;
            }
            sb.append(carry%2);//本位的结果为carry%2
            carry/=2;//除以2得到的数作为进位进入下一循环计算
        }
        return sb.reverse().toString();
    }
}
```

##### 字符串相加#415

描述：给定两个字符串形式的非负整数 `num1` 和`num2` ，计算它们的和。

思路：将二进制改为十进制即可

```java
class Solution {
    public String addStrings(String num1, String num2) {
        int i=num1.length()-1,j=num2.length()-1;
        int carry=0;
        StringBuilder sb=new StringBuilder();
        while(carry==1 || i>=0 || j>=0 ){
            if(i>=0) carry+=num1.charAt(i--)-'0';
            if(j>=0) carry+=num2.charAt(j--)-'0';
            sb.append(carry%10);
            carry/=10;
        }
        return sb.reverse().toString();
    }
}
```

##### 字符串相乘#43

描述：给定两个以字符串形式表示的非负整数 `num1` 和 `num2`，返回 `num1` 和 `num2` 的乘积，它们的乘积也表示为字符串形式。

思路：用字符数组存储每一位的数，计算每一位的乘积和进位。

```java
class Solution {
    public String multiply(String num1, String num2) {
        char[] cs=new char[num1.length()+num2.length()];
        for(int i=0;i<cs.length;i++) cs[i]='0';//初始化为0
        for(int i=num1.length()-1;i>=0;i--){
            for(int j=num2.length()-1;j>=0;j--){
                int carry=cs[i+j+1]-'0'+(num1.charAt(i)-'0')*(num2.charAt(j)-'0');
                cs[i+j+1]=(char)(carry%10+'0');//当前位的结果
                cs[i+j]+=carry/10;//前一位加上进位
            }
        }
        //去除前面的0
        for(int i=0;i<cs.length;i++){
            if(cs[i]!='0') return String.valueOf(cs).substring(i);
        }
        return "0";
    }
}
```



#### 5.其他

##### 快乐数#202

描述：一个“快乐数”定义为：对于一个正整数，每一次将该数替换为它每个位置上的数字的平方和，然后重复这个过程直到这个数变为 1，也可能是无限循环但始终变不到 1。如果可以变为 1，那么这个数就是快乐数。

思路：将每次得到的和放入一个set中，如果在达到1之前出现重复的和，则return false

```java
class Solution {
    public boolean isHappy(int n) {
        Set<Integer> set=new HashSet<>();
        int sum=0;
        while(true){
            while(n!=0){
                sum+=Math.pow(n%10,2);
                n/=10;
            }
            if(sum==1) return true;
            if(set.contains(sum)) return false;
            else {
                set.add(sum);
                n=sum;
                sum=0;
            }
        }
    }
}
```

##### 最少移动次数使数组元素相等#462

描述：给定一个非空整数数组，找到使所有数组元素相等所需的最小移动数，其中每次移动可将选定的一个元素加1或减1。 您可以假设数组的长度最多为10000。

```
输入:
[1,2,3]

输出:
2
```

思路：移动次数最小的方式是将所有元素都移动到中位数。

证明：设a<b，m为中位数。欲使得a与b相等，他们总共所需移动的次数最少为b-a次。将他们都移动到中位数的次数为(m-a)+(b-m)次，等于b-a。

方法1：先排序，再统计

```java
class Solution {
    public int minMoves2(int[] nums) {
        Arrays.sort(nums);
        int moves=0;
        int l=0,r=nums.length-1;
        while(l<r){
            moves+=nums[r--]-nums[l++];
        }
        return moves;
    }
}
```

方法2：利用快速选择找到中位数（参考[数组中第k个最大元素#215](#2数组中第k个最大元素215)），时间复杂度O(N)

```java
class Solution {
    public int minMoves2(int[] nums) {
        int moves=0;
        int median=findKthSmallest(nums,nums.length/2);
        for(int num:nums){
            moves+=Math.abs(median-num);
        }
        return moves;
    }
    private int findKthSmallest(int [] nums, int k){
        int l=0,r=nums.length-1;
        while(l<r){
            int j=partition(nums,l,r);
            if(j==k) break;
            else if(j<k) l=j+1;
            else r=j-1;
        }
        return nums[k];
    }
    private int partition(int [] nums,int l,int r){
        int i=l,j=r+1;
        while(true){
            while(nums[++i]<nums[l] && i<r);
            while(nums[--j]>nums[l] && j>l);
            if(i>=j) break;
            swap(nums,i,j);
        }
        swap(nums,l,j);
        return j;
    }
    private void swap(int [] nums,int i,int j){
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
}
```

##### 3的幂#326

描述：判断一个数是否是3的幂

```java
class Solution {
    public boolean isPowerOfThree(int n) {
        while(n>0 && (n%3==0)) n/=3;
        return n==1;
    }
}
```

巧解：int类型中3的幂最大为1162261467 ，如果n能整除该数，则为3的幂

```java
class Solution {
    public boolean isPowerOfThree(int n) {
        return n>0 && (1162261467%n==0);
    }
}
```

##### 找出数组中的乘积最大的三个数#628

描述：给定一个整型数组，在数组中找出由三个数组成的最大乘积，并输出这个乘积。

思路：最大乘积只可能出现在（都为正数，最大的三个数乘积）和（一正两负）

```java
class Solution {
    public int maximumProduct(int[] nums) {
        int max1,max2,max3,min1,min2;
        max1=Integer.MIN_VALUE;
        max2=max3=max1;
        min1=min2=Integer.MAX_VALUE;
        for(int num:nums){
            if(num<min2){
                if(num<min1) {
                    min2=min1;
                    min1=num;
                }
                else min2=num;
            }
            if(num>max3){
                if(num>max2){
                    if(num>max1) {
                        max3=max2;
                        max2=max1;
                        max1=num;
                    }
                    else{
                        max3=max2;
                        max2=num;
                    }
                }else max3=num;
            }
        }
        return Math.max(max1*max2*max3,max1*min1*min2);
    }
}
```



### 七、搜索

#### 1.BFS

广度优先搜索是指对图进行逐层遍历，每一层都以上一层遍历的结果作为起点，遍历过的节点不能再被遍历。一般使用**队列**用来存储每一层遍历得到的节点，对遍历过的节点使用**标记**来防止重复遍历。

使用BFS可以求解无权图的最短路径等**最优解**问题。

#103二叉树的锯齿形层次遍历

描述：给定一个二叉树，返回其节点值的锯齿形层次遍历。（即先从左往右，再从右往左进行下一层遍历，以此类推，层与层之间交替进行）。

思路：借助双端队列存储每一层的节点。奇数层节点放后面，偶数层放前面。

```java
class Solution {
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> res=new ArrayList<>();
        if(root==null) return res;
        int layer=1;
        Queue<TreeNode> queue=new LinkedList<>();
        queue.add(root);
        while(!queue.isEmpty()){
            int len=queue.size();
            List<Integer> list=new LinkedList<>();
            while(len>0){
                TreeNode node=queue.poll();
                if((layer & 1)==1){
                    list.add(node.val);
                }else list.add(0,node.val);
                if(node.left!=null) queue.add(node.left);
                if(node.right!=null) queue.add(node.right);
                len--;
            }
            res.add(list);
            layer++;
        }
        return res;
    }
}
```

#127 单词接龙

```java
输入:
beginWord = "hit",
endWord = "cog",
wordList = ["hot","dot","dog","lot","log","cog"]

输出: 5

解释: 一个最短转换序列是 "hit" -> "hot" -> "dot" -> "dog" -> "cog",
     返回它的长度 5。
```

描述：找出一条从beginWord到endWord的最短路径，规定每次转换只能改变一个字母，且转换过程中间的单词也必须是字典中的单词。

```java
class Solution {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        wordList.add(beginWord);
        int N = wordList.size();
        int start = N - 1;
        int end = 0;
        while (end < N && !wordList.get(end).equals(endWord)) {
            end++;
        }
        if (end == N) {
            return 0;
        }
        List<Integer>[] graphic = buildGraphic(wordList);
        return getShortestPath(graphic, start, end);
    }
    private List<Integer>[] buildGraphic(List<String> wordList) {
        int N = wordList.size();
        List<Integer>[] graphic = new List[N];
        for (int i = 0; i < N; i++) {
            graphic[i] = new ArrayList<>();
            for (int j = 0; j < N; j++) {
                if (isConnect(wordList.get(i), wordList.get(j))) {
                    graphic[i].add(j);
                }
            }
        }
        return graphic;
    }

    private boolean isConnect(String s1, String s2) {
        int diffCnt = 0;
        for (int i = 0; i < s1.length() && diffCnt <= 1; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                diffCnt++;
            }
        }
        return diffCnt == 1;
    }

    private int getShortestPath(List<Integer>[] graphic, int start, int end) {
        Queue<Integer> queue = new LinkedList<>();
        boolean[] marked = new boolean[graphic.length];
        queue.add(start);
        marked[start] = true;
        int path = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            path++;
            while (size-- > 0) {
                int cur = queue.poll();
                for (int next : graphic[cur]) {
                    if (next == end) {
                        return path;
                    }
                    if (marked[next]) {
                        continue;
                    }
                    marked[next] = true;
                    queue.add(next);
                }
            }
        }
        return 0;
    }
}
```

#279 完全平方数

描述：给定正整数 *n*，找到若干个完全平方数（比如 `1, 4, 9, 16, ...`）使得它们的和等于 *n*。你需要让组成和的完全平方数的个数最少。

思路：可以将所有整数看成一张图中的节点，如果两个数之间相差一个完全平方数，则认为两数之间有一条边，那么问题就可以转化为求从节点0到n的最短路径。

```java
class Solution {
    public int numSquares(int n) {
        List<Integer> squares=generateSquares(n);
        Queue<Integer> queue=new LinkedList<>();
        boolean[] marked=new boolean[n];
        queue.add(n);
        marked[n-1]=true;
        int level=0;
        while(!queue.isEmpty()){
            int size=queue.size();
            level++;
            while(size>0){
                int cur=queue.poll();
                for(int s:squares){
                    int next=cur-s;
                    if(next<0) break;
                    if(next==0) return level;
                    if(marked[next]) continue;
                    queue.add(next);
                    marked[next]=true;
                }
                size--;
            }
        }
        return n;
    }
    private List<Integer> generateSquares(int n){
        List<Integer> res=new ArrayList<>();
        for(int i=1;i*i<=n;i++){
            res.add(i*i);
        }
        return res;
    }
}
```

#1091 二进制矩阵中的最短路径

描述：在一个NxN的网格中，每个单元格有两种状态，空(0)和阻塞(1)，求从左上角到右下角的最短路径长度，路径中相邻单元格可在八个方向之一上连通。

思路：

- 定义一个内部类，包含三个属性。横纵坐标和到该坐标最短畅通路径的长度。
- 如果起点或终点不可达，返回-1
- 用bfs遍历矩阵，找到可行路径。每次从队列中取出一个元素时，判断该坐标是否是终点，如果是，则返回其路径长度。因为每次各个方向都是走一步，这样可以保证返回值是第一个到达终点的，即最短路径。

```java
class Solution {
    private class Node{
        int x;
        int y;
        int value;
        public Node(int x,int y,int value){
            this.x=x;
            this.y=y;
            this.value=value;
        }
    }
    public int shortestPathBinaryMatrix(int[][] grid) {
        int row=grid.length;
        int col=grid[0].length;
        if(grid[0][0]==1 || grid[row-1][col-1]==1) return -1;
        return bfs(grid,row,col);
    }
    private int bfs(int[][]grid,int row,int col){
        Node root=new Node(0,0,1);
        Queue<Node> queue=new LinkedList<>();
        queue.add(root);
        int [][] direction={ {-1,-1}, {-1,1}, {1,-1}, {1,1}, {1,0}, {0,1}, {-1,0}, {0,-1}};
        while(!queue.isEmpty()){
            Node node=queue.poll();
            if(node.x==row-1 && node.y==col-1){
                return node.value;
            }
            for(int [] d:direction){
                int r=node.x+d[0];
                int c=node.y+d[1];
                if(r>=0 && c>=0 && r<row && c<col && grid[r][c]==0){
                    queue.add(new Node(r,c,node.value+1));
                    grid[r][c]=1;
                }
            }
        }
        return -1;
    }
}
```

#### 2.DFS

深度优先搜索在得到一个新节点后立即对该节点的下一个节点进行遍历，直到没有新节点出现。

使用程序实现DFS时一般需要使用栈来保存节点信息，当遍历新节点返回时能继续遍历当前节点。或者使用递归。同时需要对已经遍历过的节点进行标记。

#130被围绕的区域

```
X X X X
X O O X
X X O X
X O X X
```

运行你的函数后，矩阵变为：

```
X X X X
X X X X
X X X X
X O X X
```

描述： 找到所有被 `'X'` 围绕的区域，并将这些区域里所有的 `'O'` 用 `'X'` 填充。 边界上的O以及与边界O相连的不会被填充，任何不与边界相连的O都会被填充。

思路：从边缘开始搜索，遇到O将其换成#作为占位符，标记为与边界连通的O。待搜索结束后，再将遍历到的O填充为X，遇到#，替换回O。

```java
class Solution {
    public void solve(char[][] board) {
        if(board==null || board.length==0){
            return ;
        }
        int m=board.length,n=board[0].length;
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                boolean isEdge=i==0||j==0||i==m-1||j==n-1;
                if(isEdge && board[i][j]=='O'){
                    dfs(board,i,j);
                }
            }
        }
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                if(board[i][j]=='O'){
                    board[i][j]='X';
                }
                if(board[i][j]=='#'){
                    board[i][j]='O';
                }
            }
        }
    }
    private void dfs(char[][]board,int i,int j){
        if(i<0 || i>=board.length ||j<0||j>=board[0].length || board[i][j]!='O'){
            return;
        }
        board[i][j]='#';
        int[][]direction={ {0,1},{0,-1},{1,0},{-1,0} };
        for(int[] d:direction){
            dfs(board,i+d[0],j+d[1]);
        }
    }
}
```

#200 岛屿数量

```
输入:
11110
11010
11000
00000

输出: 1
```

描述：给定一个由 '1'（陆地）和 '0'（水）组成的的二维网格，计算岛屿的数量。一个岛被水包围，并且它是通过水平方向或垂直方向上相邻的陆地连接而成的。你可以假设网格的四个边均被水包围。

思路：题意即计算无向图连通块的数量。用dfs遍历非零节点的相邻节点，如果连通，则将其置为0，如果不连通，则遍历其他相邻节点。这样每遍历一个非零节点，其连通的所有节点都被置为0，计数+1。

```java
class Solution {
    public int numIslands(char[][] grid) {
        if(grid==null||grid.length==0){
            return 0;
        }
        int m=grid.length,n=grid[0].length;
        int islandsCount=0;
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                if(grid[i][j]=='1'){
                    dfs(grid,i,j);
                    islandsCount++;
                }
            }
        }
        return islandsCount;
    }
    private void dfs(char[][]grid,int i,int j){
        if(i<0 ||i>=grid.length || j<0 || j>=grid[0].length || grid[i][j]=='0'){
            return ;
        }
        grid[i][j]='0';
        int[][]direction={ {0,1},{0,-1},{1,0},{-1,0} };
        for(int []d:direction){
            dfs(grid,i+d[0],j+d[1]);
        }
    }
}
```

#547 朋友圈

```
输入: 
[[1,1,0],
 [1,1,0],
 [0,0,1]]
输出: 2 
```

描述：给定一个 N * N 的矩阵 M，表示班级中学生之间的朋友关系。如果M[i][j] = 1，表示已知第 i 个和 j 个学生互为朋友关系，否则为不知道。你必须输出所有学生中的已知的朋友圈总数。

思路：即找到无向图的连通块的个数。与上题类似。

```java
class Solution {
    public int findCircleNum(int[][] M) {
        if(M==null){
            return 0;
        }
        int N=M.length;
        int[] visited=new int[N];
        int count=0;
        for(int i=0;i<N;i++){
            if(visited[i]==0){
                dfs(M,visited,i);
                count++;
            }
        }
        return count;
    }
    private void dfs(int[][]M,int[] visited,int i){
        visited[i]=1;
        for(int j=0;j<M.length;j++){
            if(M[i][j]==1 && visited[j]==0){
                dfs(M,visited,j);
            }
        }
    }
}
```

#695 岛屿的最大面积

思路：即计算最大连通块的面积。用dfs遍历计算连通块的面积，每遍历到一个1，则面积+1。

```java
class Solution {
    public int maxAreaOfIsland(int[][] grid) {
        if(grid==null || grid.length==0){
            return 0;
        }
        int maxArea=0;
        for(int i=0;i<grid.length;i++){
            for(int j=0;j<grid[0].length;j++){
                maxArea=Math.max(maxArea,dfs(grid,i,j));
            }
        }
        return maxArea;
    }
    private int dfs(int[][]grid,int i,int j){
        if(i<0||i>=grid.length||j<0||j>=grid[0].length||grid[i][j]==0){
            return 0;
        }
        grid[i][j]=1;
        int area=1;
        int[][]direction={ {0,1},{0,-1},{1,0},{-1,0} };
        for(int[] d:direction){
            area+=dfs(grid,i+d[0],j+d[1]);
        }
        return area;
    }
}
```

#### 3.回溯

回溯法属于DFS，但与一般DFS的区别在于回溯法的目的是为了求出问题的解，而DFS的目的在于遍历。回溯法在遍历过程中发现该路径不是问题的解，那么久回溯到上一层或者上一个节点，而DFS是遍历搜索整个空间。

回溯法需要注意：在一个递归链中，访问过的元素需要进行标记避免重复访问；但在递归返回后，需要将之前访问过的元素都标记为未访问，这样才不影响其他递归链的访问。

##### 3.1 排列

#46 全排列

描述：给定一个**没有重复**数字的序列，返回其所有可能的全排列。

```java
class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> res=new ArrayList<>();
        List<Integer> permuteList=new ArrayList<>();
        boolean[] visited=new boolean[nums.length];
        doPermute(permuteList,visited,nums,res);
        return res;
    }
    private void doPermute(List<Integer> permuteList,boolean[] visited,int[] nums,List<List<Integer>> res){
        if(permuteList.size()==nums.length){
            res.add(new ArrayList<>(permuteList));//注意new
            return;
        }
        for(int i=0;i<nums.length;i++){
            if(visited[i]){
                continue;
            }
            visited[i]=true;
            permuteList.add(nums[i]);
            doPermute(permuteList,visited,nums,res);
            permuteList.remove(permuteList.size()-1);
            visited[i]=false;
        }
    }
}
```

#47 含重复元素的全排列

思路：首先对序列进行排序，在序列中添加一个数字时，判断该数字与前一个数字是否相同，如果相同且前一个数字还未访问，那么跳过这个数字。

```java
class Solution {
    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> res=new ArrayList<>();
        List<Integer> permuteList=new ArrayList<>();
        Arrays.sort(nums);
        boolean[] visited=new boolean[nums.length];
        doPermute(permuteList,visited,nums,res);
        return res;
    }
    private void doPermute(List<Integer> permuteList,boolean[] visited,int[] nums,List<List<Integer>> res){
        if(permuteList.size()==nums.length){
            res.add(new ArrayList<>(permuteList));
            return ;
        }
        for(int i=0;i<nums.length;i++){
            if(i>0 && nums[i]==nums[i-1] && !visited[i-1]){
                continue;
            }
            if(visited[i]){
                continue;
            }
            visited[i]=true;
            permuteList.add(nums[i]);
            doPermute(permuteList,visited,nums,res);
            permuteList.remove(permuteList.size()-1);
            visited[i]=false;
        }
    }
}
```

##### 3.2 组合

#77 组合

```java
class Solution {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> res=new ArrayList<>();
        List<Integer> combineList=new ArrayList<>();
        if(k>n){
            return res;
        }
        doCombine(combineList,1,n,k,res);
        return res;
    }
    private void doCombine(List<Integer> combineList,int start,int n,int k,List<List<Integer>> res){
        if(k==0){
            res.add(new ArrayList<>(combineList));
            return;
        }
        for(int i=start;i<=n-k+1;i++){
            combineList.add(i);
            doCombine(combineList,i+1,n,k-1,res);
            combineList.remove(combineList.size()-1);
        }
    }
}
```

#39 组合总和

描述：给定一个无重复元素的数组和一个目标数，求出使数字和为目标数的组合，数组中的数可以无限重复选择。

```java
class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> res=new ArrayList<>();
        List<Integer> combineList=new ArrayList<>();
        doCombine(combineList,candidates,target,0,res);
        return res;
    }
    private void doCombine(List<Integer> combineList,int[] candidates,int target,int start,List<List<Integer>> res){
        if(target==0){
            res.add(new ArrayList<>(combineList));
            return ;
        }
        for(int i=start;i<candidates.length;i++){
            if(target-candidates[i]<0){
                continue;
            }
            combineList.add(candidates[i]);
            doCombine(combineList,candidates,target-candidates[i],i,res);
            combineList.remove(combineList.size()-1);
        }
    }
}
```

#40 组合总和II

描述：数组中的每个数字在组合中都只能使用一次。

思路：添加visited数组用于记录当前位置的数是否访问过。1表示访问过。

```java
class Solution {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> res=new ArrayList<>();
        List<Integer> combineList=new ArrayList<>();
        Arrays.sort(candidates);
        doCombine(combineList,candidates,target,0,new int[candidates.length],res);
        return res;
    }
    private void doCombine(List<Integer> combineList,int[] candidates, int target,int start,int[] visited, List<List<Integer>> res){
        if(target==0){
            res.add(new ArrayList<>(combineList));
            return;
        }
        for(int i=start;i<candidates.length;i++){
            if(i>0  && candidates[i]==candidates[i-1] && visited[i-1]==0 ){
                continue;
            }
            if(candidates[i]<=target){
                visited[i]=1;
                combineList.add(candidates[i]);
                doCombine(combineList,candidates,target-candidates[i],i+1,visited,res);
                combineList.remove(combineList.size()-1);
                visited[i]=0;
            }
        }
    }
}
```

#216 组合总和III

描述：从1-9中选出k个不重复的数，使他们的和为n。

```java
class Solution {
    public List<List<Integer>> combinationSum3(int k, int n){
        List<List<Integer>> res=new ArrayList<>();
        List<Integer> combineList=new ArrayList<>();
        doCombine(combineList,k,n,1,res);
        return res;
    }
    private void doCombine(List<Integer> combineList,int k,int n,int start, List<List<Integer>> res){
        if(k==0 && n==0){
            res.add(new ArraList<>(combineList));
            return;
        }
        if(k==0 || n<=0){
            return;
        }
        for(int i=start;i<=9;i++){
            combineList.add(i);
            doCombine(combineList,k-1,n-1,i+1,res);
            combineList.remove(combineList.size()-1);
        }
    }
}
```

#17 电话号码的字母组合

描述：给定一个仅包含数字 `2-9` 的字符串，返回所有它能表示的字母组合。

思路：用StringBuilder的长度标识字符串当前位置。

```java
class Solution {
    private static final String[] LETTERS={ "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz" };

    public List<String> letterCombinations(String digits) {
        List<String> res=new ArrayList<>();
        if(digits!=null && digits.length()!=0){
            doCombine(new StringBuilder(),digits,res);
        }
        return res;
    }
    private void doCombine(StringBuilder sb,String digits,List<String> res){
        if(sb.length()==digits.length()){
            res.add(sb.toString());
            return;
        }
        int curDigits=digits.charAt(sb.length())-'2';
        String letters=LETTERS[curDigits];
        for(char c:letters.toCharArray()){
            sb.append(c);
            doCombine(sb,digits,res);
            sb.deleteCharAt(sb.length()-1);
        }
    }
}
```

**求子集**：求子集的问题本质上也是一个组合问题，只不过组合中元素的个数没有限定。

#78 子集

描述：给定一组**不含重复元素**的整数数组 *nums*，返回该数组所有可能的子集（幂集）。

思路：即求size=0~n的组合问题，注意空集不要遗漏。

```java
class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res=new ArrayList<>();
        List<Integer> subset=new ArrayList<>();
        for(int size=0;size<=nums.length;size++){
            findSubsets(subset,nums,0,size,res);
        }
        return res;
    }
    private void findSubsets(List<Integer> subset,int[] nums, int start,int size,List<List<Integer>> res){
        if(subset.size()==size){
            res.add(new ArrayList<>(subset));
            return;
        }
        for(int i=start;i<nums.length;i++){
            subset.add(nums[i]);
            findSubsets(subset,nums,i+1,size,res);
            subset.remove(subset.size()-1);
        }
    }
}
```









### 八、动态规划

动态规划与贪心法的区别：

- 贪心法的每一步贪心决策都无法改变，每次根据上一步的最优解推导下一步的最优解。之前的最优解不做保留。贪心法所求的结果不一定是全局最优解，因此在使用贪心法时必须确保策略具有无后效性，只与当前状态有关。
- 动态规划求的是全局最优解，是从局部最优解推导出全局最优解的过程，其中边界条件是最易推导出的局部最优解。关键是求出状态转移方程。

#### 1.连续子数组最大的和#53

描述：给定一个整数数组 nums ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。

思路：动态规划。设以第i个数为结尾的最大子序列的和为F（i）。则有：

- F（i）=max（F（i-1）+array[i]，array[i]）
- 最大连续子序列res = max（F（i），F（i-1）...F（0））

```java
class Solution {
    public int maxSubArray(int[] nums) {
        int res,max;
        res=max=nums[0];
        for(int i=1;i<nums.length;i++){
            max=Math.max(max+nums[i],nums[i]);
            res=Math.max(max,res);
        } 
        return res;
    }
}
```



