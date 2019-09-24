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







### 二、栈和队列







### 三、链表







### 四、哈希表







### 五、字符串







### 六、树







### 七、图











