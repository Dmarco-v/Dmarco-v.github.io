<!-- GFM-TOC -->

- [](#)
- [](#)
- [](#)
- [](#)

<!-- GFM-TOC -->

### 一、数组

#### 1.删除有序数组中的重复项#26

描述：给定一个排序数组，你需要在**原地**删除重复出现的元素，使得每个元素只出现一次，返回移除后数组的新长度。

思路：双指针，快指针遍历数组，每有一个不同的数，慢指针+1

```java
class Solution {
    public int removeDuplicates(int[] nums) {
        if(nums.length==0) return 0;
        int i=0;
        for(int j=1;j<nums.length;j++){
            if(nums[j]!=nums[i]){
                i++;
                nums[i]=nums[j];
            }
        }
        return i+1;
    }
}
```

#### 2.有序矩阵中第k小的元素#378

描述：给定一个 *n x n* 矩阵，其中每行和每列元素均按升序排序，找到矩阵中第k小的元素。

思路：二分查找。

```java
class Solution {
    public int kthSmallest(int[][] matrix, int k) {
        int m=matrix.length,n=matrix[0].length;
        int lo=matrix[0][0],hi=matrix[m-1][n-1];
        while(lo<hi){
            int mid=lo+(hi-lo)/2;
            int count=countMid(matrix,mid,m,n);
            if(count<k) lo=mid+1;
            else hi=mid;//可能包括mid，因此high需要设置为mid
        }
        return lo;
    }
    private int countMid(int [][] matrix, int mid,int m,int n){
        int r=0,c=n-1;
        int count=0;
        while(r<m && c>=0){
            if(matrix[r][c]>mid){
                c--;
            }else{
                count+=c+1;
                r++;
            }
        }
        return count;
    }
}
```

#### 3.错误的集合#645

描述：集合 S 包含从1到 n 的整数。不幸的是，因为数据错误，导致集合里面某一个元素复制了成了集合里面的另外一个元素的值，导致集合丢失了一个整数并且有一个元素重复。

思路：由于集合中的整数只有1到n，因此可以交换数组元素，使每个元素处在索引与值对应的位置上（值=索引+1）。交换完成后，如果nums[i] !=i+1，则该为位置上的数即为重复的数，其索引对应的值就是缺失值。

```java
class Solution {
    public int[] findErrorNums(int[] nums) {
        for(int i=0;i<nums.length;i++){
            while(nums[i] != i+1 && nums[i] != nums[nums[i]-1]){
                swap(nums,i,nums[i]-1);
            }
        }
        for(int i=0;i<nums.length;i++){
            if(nums[i]!=i+1){
                return new int[]{nums[i],i+1};
            }
        }
        return null;
    }
    private void swap(int[] nums,int i,int j){
        int tmp=nums[i];
        nums[i]=nums[j];
        nums[j]=tmp;
    }
}
```

#### 4.有效三角形的个数#611

描述：给定一个包含非负整数的数组，你的任务是统计其中可以组成三角形三条边的三元组个数。

思路：类似三数之和。先排序，然后双指针。先定位最长的边，然后双指针搜索其左边的区间，如果双指针之和大于该边，则满足条件的数量为r-l个，r左移；否则l右移继续搜索

```java
class Solution {
    public int triangleNumber(int[] nums) {
        Arrays.sort(nums);
        int count=0;
        for(int i=nums.length-1;i>=0;i--){
            int l=0,r=i-1;
            while(l<r){
                if(nums[l]+nums[r]>nums[i]){
                    count+=r-l;
                    r--;
                }else l++;
            }
        }
        return count;
    }
}
```

#### 5.寻找重复的数#287

描述：长度n+1的数组取值范围在1到n之间，找出重复的数。要求不能修改数组也不能使用额外的空间。

方法1.排序。O(NlogN)

```java
class Solution {
    public int findDuplicate(int[] nums) {
        Arrays.sort(nums);
        for(int i=1;i<nums.length;i++){
            if(nums[i]==nums[i-1]) return nums[i];
        }
        return -1;
    }
}
```

方法2.二分查找不断搜索。O(NlogN)

```java
class Solution {
    public int findDuplicate(int[] nums) {
        int l=1,r=nums.length-1;
        while(l<=r){
            int mid=l+(r-l)/2;
            int cnt=0;
            for(int i=0;i<nums.length;i++){
                if(nums[i]<=mid) cnt++;
            }
            if(cnt>mid) r=mid-1;
            else l=mid+1;
        }
        return l;
    }
}
```

方法3.快慢指针检测循环是否存在。O(n)。

```java
class Solution {
    public int findDuplicate(int[] nums) {
        int slow = nums[0], fast = nums[nums[0]];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[nums[fast]];
        }
        fast = 0;
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }
        return slow;
    }
}
```

#### 6.数组相邻元素差值有k个#667

描述：给定两个整数 n 和 k，你需要实现一个数组，这个数组包含从 1 到 n 的 n 个不同整数，同时满足以下条件：

① 如果这个数组是 [a1, a2, a3, ... , an] ，那么数组 [|a1 - a2|, |a2 - a3|, |a3 - a4|, ... , |an-1 - an|] 中应该有且仅有 k 个不同整数；.

② 如果存在多种答案，你只需实现并返回其中任意一种.

思路：令前k+1个元素构建出k个不相同的差值，后面顺序填充。1,k+1,2,k,3,k-1,...k/2,k/2+1, k+2,k+3...n

```java
class Solution {
    public int[] constructArray(int n, int k) {
        int [] ret=new int[n];
        ret[0]=1;
        for(int i=1,interval=k;i<=k;i++,interval--){
            ret[i]= i%2==1 ? ret[i-1]+interval:ret[i-1]-interval;
        }
        for(int i=k+1;i<n;i++){
            ret[i]=i+1;
        }
        return ret;
    }
}
```

#### 7.数组的度#697

描述：数组的度是指重复元素出现的最高次数。要求找到一个最小的子数组，这个子数组的度与原数组一样，返回子数组的长度。

思路：三个hashmap分别存放每个数的count，起点和终点。

```java
class Solution {
    public int findShortestSubArray(int[] nums) {
        Map<Integer,Integer> count=new HashMap<>();
        Map<Integer,Integer> left=new HashMap<>();
        Map<Integer,Integer> right=new HashMap<>();
        for(int i=0;i<nums.length;i++){
            if(!count.containsKey(nums[i])){
                count.put(nums[i],1);
                left.put(nums[i],i);
            }else {
                count.put(nums[i],count.get(nums[i])+1);
            }
            right.put(nums[i],i);
        }
        int degree=0;
        for(int num:nums){
            degree=Math.max(degree,count.get(num));
        }
        int ret=nums.length;
        for(int i=0;i<nums.length;i++){
            if(count.get(nums[i])==degree){
                ret=Math.min(ret,right.get(nums[i])-left.get(nums[i])+1);
            }
        }
        return ret;
    }
}
```

#### 8.数组嵌套#565

描述：长度为n的数组A包含0到n-1的所有整数。找到并返回最大的集合S，且S满足规则：S[i] = {A[i], A[A[i]], A[A[A[i]]], ... }。S中出现重复元素时，停止。返回S的长度。

```
输入: A = [5,4,0,3,1,6,2]
输出: 4
解释: 
A[0] = 5, A[1] = 4, A[2] = 0, A[3] = 3, A[4] = 1, A[5] = 6, A[6] = 2.

其中一种最长的 S[K]:
S[0] = {A[0], A[5], A[6], A[2]} = {5, 6, 2, 0}
```

思路：遍历每一个A[i]作为开头。统计每种开头的最大长度，返回最大值。

```java
class Solution {
    public int arrayNesting(int[] nums) {
        int max=0;
        for(int i=0;i<nums.length;i++){
            int cnt=0;
            int j=i;
            while(nums[j]!=-1){
                cnt++;
                int tmp=nums[j];
                nums[j]=-1;
                j=tmp;
            }
            max=Math.max(max,cnt);
        }
        return max;
    }
}
```

#### 9.分割数组为能完成排序的块#769

描述：数组arr是[0, 1, ..., arr.length - 1]的一种排列，我们将这个数组分割成几个“块”，并将这些块分别进行排序。之后再连接起来，使得连接的结果和按升序排序后的原数组相同。

```
输入: arr = [1,0,2,3,4]
输出: 4
解释:
我们可以把它分成两块，例如 [1, 0], [2, 3, 4]。
然而，分成 [1, 0], [2], [3], [4] 可以得到最多的块数。
```

思路：遍历数组，维护当前的最大值，如果最大值等于索引值，说明此最大值可以通过排序到达其应该在的位置，则其前面的可以切为1段，结果+1。

```java
class Solution {
    public int maxChunksToSorted(int[] arr) {
        int ret=0,max=arr[0];
        for(int i=0;i<arr.length;i++){
            max=Math.max(max,arr[i]);
            if(max==i) ret++;
        }
        return ret;
    }
}
```



### 二、栈和队列







### 三、链表







### 四、哈希表







### 五、字符串







### 六、树







### 七、图











