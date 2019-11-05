 <!-- GFM-TOC -->

- [一、数组](#一数组)
- [二、栈和队列](#二栈和队列)
- [三、链表](#三链表)
- [四、哈希表](#四哈希表)
- [五、字符串](#五字符串)
- [六、树](#六树)
- [七、图](#七图)

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

#### 1.最小栈#155

```java
class MinStack {
    private Stack<Integer> s;
    private Stack<Integer> min;
    public MinStack() {
        s=new Stack<>();
        min=new Stack<>();
    }
    public void push(int x) {
        s.push(x);
        if(min.isEmpty() || min.peek()>=x){
            min.push(x);
        }
    }
    public void pop() {
        if(!s.isEmpty()){
            int top=s.pop();//注意这个地方将s.pop()的值先赋给另一个数，比较时会自动装箱；而直接使用s.pop()==min.peek()比较的是两个Integer类型的对象，会返回false，需要改成使用equals()方法。
            if(top==min.peek()){
                min.pop();
            }
        }
    }
    public int top() {
        if(!s.isEmpty()){
            return s.peek();
        }
        throw new RuntimeException("栈中元素为空");
    }
    public int getMin() {
        if(!min.isEmpty()){
            return min.peek();
        }
        throw new RuntimeException("栈中元素为空");
    }
}
```



#### 2.用队列实现栈#225

```java
class MyStack {
    private Queue<Integer>  queue;
    public MyStack() {
        queue=new LinkedList<>();
    }
    public void push(int x) {
        queue.add(x);
        int cnt=queue.size();
        while(cnt>1){
            queue.add(queue.poll());
            cnt--;
        }
    }
    public int pop() {
        return queue.poll();
    }
    public int top() {
        return queue.peek();
    }
    public boolean empty() {
        return queue.isEmpty();
    }
}
```



#### 3.用栈实现队列#232

```java
class MyQueue {
    private Stack<Integer> s1;
    private Stack<Integer> s2;
    public MyQueue() {
        s1=new Stack<>();
        s2=new Stack<>();
    }
    public void push(int x) {
        while(!s2.isEmpty()){
            s1.push(s2.pop());
        }
        s1.push(x);
    }
    public int pop() {
        while(!s1.isEmpty()){
            s2.push(s1.pop());
        }
        return s2.pop();
    }
    public int peek() {
        while(!s1.isEmpty()){
            s2.push(s1.pop());
        }
        return s2.peek();
    }
    public boolean empty() {
        return s1.isEmpty() && s2.isEmpty();
    }
}

```

#### 4.有效的括号#20

描述：给定一个只包括 `'('`，`')'`，`'{'`，`'}'`，`'['`，`']'` 的字符串，判断字符串是否有效。

##### 括号相关题目：#22，#32，#301

```java
class Solution {
    public boolean isValid(String s) {
        Stack<Character> stack=new Stack<>();
        for(char c:s.toCharArray()){
            if(c =='('|| c=='{'||c=='['){
                stack.push(c);
            }else{
                if(stack.isEmpty()) return false;
                char peek=stack.pop();
                if((c==')'&& peek!='(')|| (c=='}'&& peek!='{')|| (c==']'&& peek!='[') ) return false;
            }
        }
        return stack.isEmpty();
    }
}
```

#### 5.移掉K位数字#402

描述：给定一个以字符串表示的非负整数 *num*，移除这个数中的 *k* 位数字，使得剩下的数字最小。

```java
class Solution {
    public String removeKdigits(String num, int k) {
        //维护一个单调递增的栈
        Stack <Character> stack=new Stack<>();
        char[] cs=num.toCharArray();
        int n=cs.length,m=n-k;
        //遇到比栈顶元素小的，则将栈顶元素移除，直到k为0为止
        for(char c:cs){
            while(k>0 && !stack.isEmpty() && stack.peek()>c){
                stack.pop();
                k--;
            }
            stack.push(c);
        }
        //如果k不为空，则说明是一个单调递增的序列，将最后几位移除即可
        while(k>0){
            stack.pop();
            k--;
        }
        int i=m-1;
        //如果i<0说明栈中没有元素
        if(i>=0){
            while(i>=0){
                cs[i--]=stack.pop();
            }
            //去除首部的0
            for(i=0;i<m;i++){
                if(cs[i]!='0') return String.valueOf(cs).substring(i,m);
            }
        }
        return "0";
    }
}
```

#### 6.单调栈的应用：下一个更大元素#496，503，739

#496 描述：给定两个没有重复元素的数组 nums1 和 nums2 ，其中nums1 是 nums2 的子集。找到 nums1 中每个元素在 nums2 中的下一个比其大的值。nums1 中数字 x 的下一个更大元素是指 x 在 nums2 中对应位置的右边的第一个比 x 大的元素。如果不存在，对应位置输出-1。

思路：维护一个单调递减的栈，从数组的后面开始入栈（倒序入栈，出栈顺序为正序），如果栈顶元素比数组元素小，则出栈。对应位置上判断此时的栈是否为空，若为空，说明当前数的后面没有比他大的数；非空则返回栈顶元素。

```java
class Solution {
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Stack <Integer> s=new Stack<>();
        int [] res=new int [nums1.length];
        HashMap<Integer,Integer> map=new HashMap<>();
        for(int i=nums2.length-1;i>=0;i--){
            while(!s.isEmpty() && s.peek()<=nums2[i]){
                s.pop();
            }
            int t=s.isEmpty() ? -1:s.peek();
            map.put(nums2[i],t);
            s.push(nums2[i]);
        }
        for(int i=0;i<nums1.length;i++){
            res[i]=map.get(nums1[i]);
        }
        return res;
    }
}
```

#503 描述：将上题数组改为循环数组（即最后一个元素的下一个元素是数组的第一个元素）

思路：因为是循环数组，因此需要遍历数组两次。

```java
class Solution {
    public int[] nextGreaterElements(int[] nums) {
        Stack<Integer> s=new Stack<>();
        int n=nums.length;
        int [] res=new int[n];
        for(int i=2*n-1;i>=0;i--){
            int num=nums[i%n];
            while(!s.isEmpty() && s.peek()<=num){
                s.pop();
            }
            int t=s.isEmpty()? -1:s.peek();
            res[i%n]=t;
            s.push(num);
        }
        return res;
    }
}
```

#739 描述：根据每日气温列表，请重新生成一个列表，对应位置的输入是你需要再等待多久温度才会升高超过该日的天数。如果之后都不会升高，请在该位置用 `0` 来代替。

思路：本题求解的是下一个更大元素与当前元素的距离

```java
class Solution {
    public int[] dailyTemperatures(int[] T) {
        Stack<Integer> s=new Stack<>();
        int n=T.length;
        int[] res=new int[n];
        for(int i=n-1;i>=0;i--){
            while(!s.isEmpty() && T[s.peek()]<=T[i]){
                s.pop();
            }
            int d=s.isEmpty()? 0:s.peek()-i;
            res[i]=d;
            s.push(i);
        }
        return res;
    }
}
```

#### 7.优先队列（堆）的应用#215，#347

#215 数组中的第k大元素

思路：维护一个包含最大的k个元素的小顶堆。时间复杂度为O(Nlogk)

```java
class Solution {
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap=new PriorityQueue<>();
        for(int num:nums){
            minHeap.offer(num);
            if(minHeap.size()>k){
                minHeap.poll();
            }
        }
        return minHeap.peek();
    }
}
```

#347 前k个高频元素

描述：给定一个非空整数数组，返回其中出现频率前k高的元素。

思路：map存放元素出现的频率，对再出现频率进行堆排序。

```java
class Solution {
    public List<Integer> topKFrequent(int[] nums, int k) {
        HashMap<Integer,Integer> map=new HashMap<>();
        for(int i=0;i<nums.length;i++){
            if(map.containsKey(nums[i])){
                map.put(nums[i],map.get(nums[i])+1);
            }else map.put(nums[i],1);
        }
        PriorityQueue<Integer> minHeap=new PriorityQueue<>(new Comparator<Integer>(){
            @Override
            public int compare(Integer a,Integer b){
                return map.get(a).compareTo(map.get(b));
            }
        });
        for(int key:map.keySet()){
            minHeap.offer(key);
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

#### 8.双端队列的应用#239

描述：滑动窗口的最大值。给定一个数组 *nums*，有一个大小为 *k* 的滑动窗口从数组的最左侧移动到数组的最右侧。你只可以看到在滑动窗口内的 *k* 个数字。滑动窗口每次只向右移动一位。返回滑动窗口中的最大值。

思路：使用双端队列保存滑动窗口中值的索引，队列中的索引对应的值时单调递减的。注意检查队首索引是否过期（在窗口外）。

```java
class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        if(k==0) return new int[]{};
        Deque<Integer> queue=new LinkedList<>();
        //初始窗口
        for(int i=0;i<k;i++){
            while(!queue.isEmpty() && nums[i]>nums[queue.peekLast()]){
                queue.pollLast();
            }
            queue.add(i);
        }
        int [] res=new int [nums.length-k+1];
        res[0]=nums[queue.peekFirst()];
        for(int i=k;i<nums.length;i++){
            //检查队首
            if(!queue.isEmpty() && queue.peekFirst()<=i-k){
                queue.pollFirst();
            }
            while(!queue.isEmpty() && nums[i]>nums[queue.peekLast()]){
                queue.pollLast();
            }
            queue.add(i);
            res[i-k+1]=nums[queue.peekFirst()];
        }
        return res;
    }
}
```



### 三、链表

#### 1.链表反转

#206翻转一个单链表

递归：

```java
class Solution {
    public ListNode reverseList(ListNode head) {
        if(head==null ||head.next==null) return head;
        ListNode next=head.next;
        ListNode newHead=reverseList(next);
        next.next=head;
        head.next=null;
        return newHead;
    }
}
```

迭代：

```java
class Solution {
    public ListNode reverseList(ListNode head) {
        if(head==null) return null;
        ListNode pre=null;
        ListNode next=null;
        while(head!=null){
            next=head.next;
            head.next=pre;
            pre=head;
            head=next;
        }
        return pre;
    }
}
```

#92一趟扫描反转位置m到n的链表

```java
class Solution {
    public ListNode reverseBetween(ListNode head, int m, int n) {
        ListNode curNode=head,start=null;
        while(m>1 ){
            start=curNode;
            curNode=curNode.next;
            m--;
            n--;
        }
        ListNode tail=curNode;
        ListNode pre=null,next=null;
        while(n>0){
            next=curNode.next;
            curNode.next=pre;
            pre=curNode;
            curNode=next;
            n--;
        }
        //此时start为第m-1个节点，tail为第m个节点，pre为第n个节点，curNode为第n+1个节点
        if(start==null){
            head=pre;
        }else start.next=pre;
        tail.next=curNode;
        return head;
    }
}
```

#61旋转链表

描述：给定一个链表，旋转链表，将链表每个节点向右移动 *k* 个位置，其中 *k* 是非负数。

```java
class Solution {
    public ListNode rotateRight(ListNode head, int k) {
        if(head==null) return null;
        //找链表长度和尾部节点
        int n=1;
        ListNode tail=head;
        while(tail.next!=null){
            tail=tail.next;
            n++;
        }
        k=k%n;
        if(head.next==null || k==0) return head;
        //找到要断开的节点
        ListNode newTail=head;
        for(int i=0;i<n-k-1;i++){
            newTail=newTail.next;
        }
        ListNode newHead=newTail.next;
        tail.next=head;
        newTail.next=null;
        return newHead;
    }
}
```



#### 2.快慢指针法

快慢指针常用于找链表中的环、找中点、某个节点等。

#19删除链表的倒数第N个节点

思路：快指针先走N步，然后慢指针从头开始和快指针一起走，快指针到头，此时的慢指针即为要删除的节点。

```java
class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode fast=head;
        while(n>0){
            fast=fast.next;
            n--;
        }
        if(fast==null) return head.next;
        ListNode slow=head;
        while(fast.next!=null){
            fast=fast.next;
            slow=slow.next;
        }
        slow.next=slow.next.next;
        return head;
    }
}
```

#141判断链表是否有环

思路：快指针2倍速，慢指针1倍，如果相遇，则有环。

```java
public class Solution {
    public boolean hasCycle(ListNode head) {
        if(head==null) return false;
        ListNode fast=head;
        ListNode slow=head;
        while(fast!=null && fast.next!=null){
            fast=fast.next.next;
            slow=slow.next;
            if(fast==slow) return true;
        }
        return false;
    }
}
```

#142找到环形链表入环的节点

思路：找到快慢指针的汇合点，再让快指针回到起点，与慢指针同速走。

```java
public class Solution {
    public ListNode detectCycle(ListNode head) {
        if(head==null) return null;
        ListNode fast=head;
        ListNode slow=head;
        while(true){
            if(fast==null || fast.next==null) return null;
            slow=slow.next;
            fast=fast.next.next;
            if(fast==slow) break;
        }
        fast=head;
        while(fast!=slow){
            fast=fast.next;
            slow=slow.next;
        }
        return fast;
    }
}
```

#234判断回文链表

思路：题目要求O（1）空间复杂度解决此题。三个步骤：

- 快慢指针找到链表的中点
- 翻转链表前半部分
- 比较两部分是否相同

```java
class Solution {
    public boolean isPalindrome(ListNode head) {
        if(head==null) return true;
        //快慢指针找中点
        ListNode fast=head;
        ListNode slow=head;
        while(fast!=null && fast.next!=null){
            fast=fast.next.next;
            slow=slow.next;
        }
        //翻转链表前半部分
        ListNode pre=null;
        ListNode next=null;
        while(head!=slow){
            next=head.next;
            head.next=pre;
            pre=head;
            head=next;
        }
        if(fast!=null) slow=slow.next;//如果是奇数个节点，则去掉中间的节点
        //回文校验
        while(pre!=null){
            if(pre.val!=slow.val) return false;
            pre=pre.next;
            slow=slow.next;
        }
        return true;
    }
}
```

#### 3.链表的交点#160

思路：要求时间复杂度O(N)，空间复杂度O(1)，如果没有交点则返回null。当链表A到达尾部时令其访问链表B，链表B到达尾部时令其访问链表A。有两种结果：存在交点，返回交点；不存在交点，两个指针最后都为null，返回null。

```java
public class Solution {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode l1=headA,l2=headB;
        while(l1!=l2){
            l1=(l1==null)? headB:l1.next;
            l2=(l2==null)? headA:l2.next;
        }
        return l1;
    }
}
```

如果只是判断链表是否有交点，只需比较两个链表的尾结点是否相等即可。

#### 4.有序链表

#21归并两个有序链表

```java
class Solution {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if(l1==null) return l2;
        if(l2==null) return l1;
        if(l1.val<l2.val){
            l1.next=mergeTwoLists(l1.next,l2);
            return l1;
        }else{
            l2.next=mergeTwoLists(l1,l2.next);
            return l2;
        }
    }
}
```

#83删除有序列表中的重复元素

```java
class Solution {
    public ListNode deleteDuplicates(ListNode head) {
        ListNode curNode=head;
        while(curNode!=null && curNode.next!=null){
            if(curNode.next.val==curNode.val){
                curNode.next=curNode.next.next;
            }else curNode=curNode.next;
        }
        return head;
    }
}
```

#### 5.交换链表中的相邻节点#24

描述：给定一个链表，两两交换其中相邻的节点，并返回交换后的链表。要求不能修改节点的值，空间复杂度O(1)

思路：头插法。在头部新增一个null节点。

```java
class Solution {
    public ListNode swapPairs(ListNode head) {
        ListNode node=new ListNode(-1);
        node.next=head;
        ListNode pre=node;
        while(pre.next!=null && pre.next.next!=null){
            ListNode l1=pre.next,l2=pre.next.next;
            ListNode next=l2.next;
            l1.next=next;
            l2.next=l1;
            pre.next=l2;
            pre=l1;
        }
        return node.next;
    }
}
```

#### 6.链表两数相加#445

描述：给定两个**非空**链表来代表两个非负整数。数字最高位位于链表开始位置。它们的每个节点只存储单个数字。将这两数相加会返回一个新的链表。

错误思路：将链表转成数字然后相加，再转成链表。测试发现Long型整数都不足够存储链表转成的数，因此不通过。

正解：用栈保存链表中的数字，逐位相加，用一个整型变量存储进位。

```java
class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2){
        Stack<Integer> s1=listToStack(l1);
        Stack<Integer> s2=listToStack(l2);
        ListNode head=new ListNode(-1);
        int carry=0;
        while(!s1.isEmpty()|| !s2.isEmpty()|| carry!=0){
            int i1=s1.isEmpty()? 0:s1.pop();
            int i2=s2.isEmpty()? 0:s2.pop();
            int sum=i1+i2+carry;
            ListNode node=new ListNode(sum%10);
            node.next=head.next;
            head.next=node;
            carry=sum/10;
        }
        return head.next;
    }
    private Stack<Integer> listToStack(ListNode head){
        Stack<Integer> s=new Stack<>();
        while(head!=null){
            s.push(head.val);
            head=head.next;
        }
        return s;
    }
}
```

#### 7.分割链表#725

描述：给定一个头结点为 `root` 的链表, 编写一个函数以将链表分隔为 `k` 个连续的部分。每部分的长度相差不超过1。前面部分的长度大于等于后面部分的长度。

思路：n%k个长度为n/k+1链表，其余长度为n/k。

```java
class Solution {
    public ListNode[] splitListToParts(ListNode root, int k) {
        int n=0;
        ListNode cur=root;
        while(cur!=null){
            cur=cur.next;
            n++;
        }
        int mod=n%k,size=n/k;
        ListNode[] ret=new ListNode[k];
        cur=root;
        for(int i=0;cur!=null && i<k;i++){
            ret[i]=cur;
            int curSize=mod>0 ? size+1:size;
            mod--;
            while(curSize>1){
                cur=cur.next;
                curSize--;
            }
            ListNode next=cur.next;
            cur.next=null;
            cur=next;
        }
        return ret;
    }
}
```

#### 8.奇偶链表#328

描述：将一个单链表的所有奇数节点和偶数节点分别排在一起。

```java
class Solution {
    public ListNode oddEvenList(ListNode head) {
        if(head==null) return null;
        ListNode odd=head,even=head.next,evenHead=even;
        while(even!=null && even.next!=null){
            odd.next=odd.next.next;
            odd=odd.next;
            even.next=even.next.next;
            even=even.next;
        }
        odd.next=evenHead;
        return head;
    }
}
```



### 四、哈希表

哈希表使用方法总结：

- 使用哈希表的时间复杂度为O(1)，效率很高；空间复杂度为O(n)
- HashSet用于存储一个集合，可以用于判断一个元素是否在集合中。如果元素有限，例如26个字母，可以用一个布尔数组来存储元素是否存在的信息，这样可以将空间复杂度下降到O(1)
- HashMap用于建立映射关系。在对一个数据转换成另一种数据时，利用HashMap可以建立转换后的数据与之前的数据之间的关系。同样的，如果元素数量有限（例如数字范围为0-n），可以用数组来存储元素信息。

#### 1.HashSet-判断重复元素#217

```java
class Solution {
    public boolean containsDuplicate(int[] nums) {
        HashSet<Integer> set=new HashSet<>();
        for(int num:nums){
            if(!set.contains(num)){
                set.add(num);
            }else return true;
        }
        return false;
    }
}
```

#### 2.HashMap-两数之和#1

```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer,Integer>  map=new HashMap<>();
        for(int i=0;i<nums.length;i++){
            if(map.containsKey(target-nums[i])){
                return new int[]{map.get(target-nums[i]),i};
            }else{
                map.put(nums[i],i);
            }
        }
        return null;
    }
}
```

也可以先对数组排序，然后用双指针或二分查找的方式，时间复杂度为O(NlogN)，空间复杂度为O(1)

#### 3.整型数组-有效的字母异位词#242

```java
class Solution {
    public boolean isAnagram(String s, String t) {
        if(s.length()!=t.length()) return false;
        int[] count=new int[26];
        for(int i=0;i<s.length();i++){
            count[s.charAt(i)-'a']++;
            count[t.charAt(i)-'a']--;
        }
        for(int i=0;i<26;i++){
            if(count[i]!=0){
                return false;
            }
        }
        return true;
    }
}
```

#### 4.最长连续序列#128

```java
class Solution {
    public int longestConsecutive(int[] nums) {
        HashSet<Integer> set=new HashSet<>();
        for(int num:nums){
            set.add(num);
        }
        int longest=0;
        for(int num:nums){
            if(set.contains(num-1)) continue;
            int count=0;
            while(set.contains(num++)){
                count++;
            }
            longest=Math.max(longest,count);
        }
        return longest;
    }
}
```



### 五、字符串

#### 1.数字转换

#8 字符串转换整数

描述：请你来实现一个 `atoi` 函数，使其能将字符串转换成整数。

思路：分三步完成。

- 找到第一个非空字符。如果为数字，记录开始指针i；如果为正负号，用一个flag保存正负性，以i+1作为开始指针；如果为空格，continue，否则不是有效数组
- 找到整数的结束指针。
- 判断数字是否有溢出情况。

```java
class Solution {
    public int myAtoi(String str) {
        char[] cs=str.toCharArray();
        int flag=1;
        int i=0;
        for(;i<cs.length;i++){
            if(cs[i]>='0' && cs[i]<='9') break;
            else if(cs[i]=='+' || cs[i]=='-'){
                if(cs[i]=='-') flag=-1;
                i++;
                break;
            }
            else if(cs[i]!=' ') return 0;
        }
        int j=i;
        for(;j<cs.length;j++){
            if(cs[j]>'9'||cs[j]<'0')break;
        }
        int res=0;
        for(int k=i;k<j;k++){
            int temp=cs[k]-'0';
            if(flag==1 && (res>Integer.MAX_VALUE/10 ||  (res==Integer.MAX_VALUE/10 && temp>7))) return Integer.MAX_VALUE;
            if(flag==-1 && (flag*res<Integer.MIN_VALUE/10 ||  (flag*res==Integer.MIN_VALUE/10 && temp>8))) return Integer.MIN_VALUE;
            res=res*10+temp;
        }
        return flag*res;
    }
}
```

此题的难点在于对边界条件的考察。

#12 整数转罗马数字

建立整数和罗马数字的映射表，先用尽量大的数字表示，再依次往后面添加较小的数字。

```java
class Solution {
    public String intToRoman(int num) {
        //可以用两个数组建立映射关系
        int [] vals={1000,900,500,400,100,90,50,40,10,9,5,4,1};
        String [] romans={"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<vals.length;i++){
            int temp=num/vals[i];
            if(temp==0) continue;
            for(int j=temp;j>0;j--){
                sb.append(romans[i]);
            }
            num=num%vals[i];
        }
        return sb.toString();
    }
}
```

#### 2.回文字符串

#9判断一个整数是否是回文数

描述：要求不能将整数转为字符串来判断

```java
class Solution {
    public boolean isPalindrome(int x) {
        if(x==0) return true;
        if(x<0||x%10==0) return false;
        int right=0;
        while(x>right){
            right=right*10+x%10;
            x/=10;
        }
        return x==right || x==right/10;
    }
}
```



#409字符集合组成最长回文串

描述：给定一个包含大写字母和小写字母的字符串，找到通过这些字母构造成的最长的回文串。

思路：用一个数组统计每个字母出现的次数。回文串中间的一个字符可以单独出现。

```java
class Solution {
    public int longestPalindrome(String s) {
        int [] counts=new int[128];
        for(char c:s.toCharArray()){
            counts[c]++;
        }
        int ret=0;
        for(int count:counts){
            ret+=count/2*2;
        }
        if(ret<s.length()) ret++;
        return ret;
    }
}
```



#5最长回文子串

思路：以一字母或两字母的回文字符串作为中心，向其两侧扩展。

```java
class Solution {
    public String longestPalindrome(String s) {
        if(s==null || s.length()<1) return "";
        int start=0,end=0;
        for(int i=0;i<s.length();i++){
            int len1=expand(s,i,i);
            int len2=expand(s,i,i+1);
            int longest=Math.max(len1,len2);
            if(longest>end-start+1){
                start=i-(longest-1)/2;
                end=i+longest/2;
            }
        }
        return s.substring(start,end+1);//区间前闭后开
    }
    private int expand(String s,int left,int right){
        while(left>=0 && right<s.length() && s.charAt(left)==s.charAt(right)){
            left--;
            right++;
        }
        return right-left-1;
    }
}
```



#647回文子串的个数

描述：给定一个字符串，你的任务是计算这个字符串中有多少个回文子串。不同开始和结束位置的相同内容子串，计为不同子串。

思路：类似于上一题。以某一位或两位为中心，扩展子串。

```java
class Solution {
    private int count=0;
    public int countSubstrings(String s) {
        if(s==null||s.length()<1) return 0;
        for(int i=0;i<s.length();i++){
            expand(s,i,i);
            expand(s,i,i+1);
        }
        return count;
    }
    private void expand(String s,int left, int right){
        while(left>=0 && right<s.length() && s.charAt(left)==s.charAt(right)){
            left--;
            right++;
            count++;
        }
    }
}  
```



#516最长回文子序列的长度

描述：给定一个字符串`s`，找到其中最长的回文子序列。可以假设`s`的最大长度为`1000`。

思路：动态规划方法。四要素：

- 状态：用f [i] [j]表示第i到j个字符组成的子串中，最长的回文子序列长度是多少。
- 转移方程：如果字符i和j相同：f [i] [j] = f [i-1] [j+1]+2；如果不同：f [i] [j] = max（f [i+1] [j]，f [i] [j-1]）。遍历顺序i从后往前，j从i+1往后。
- 初始化：f [i] [i] =1 单个字符
- 结果：i=0，j=n-1

```java
class Solution {
    public int longestPalindromeSubseq(String s) {
        int n=s.length();
        int[][] f=new int[n][n];
        for(int i=n-1;i>=0;i--){
            f[i][i]=1;
            for(int j=i+1;j<n;j++){
                if(s.charAt(i)==s.charAt(j)){
                    f[i][j]=f[i+1][j-1]+2;
                }else {
                    f[i][j] = Math.max(f[i+1] [j],f[i][j-1]);
                }
            }
        }
        return f[0][n-1];
    }
}
```

#### 3.字符串同构#205

描述：给定两个字符串 ***s*** 和 **t**，判断它们是否是同构的。

思路：用长度128的整型数组统计s和t中每个字符上次出现的位置，如果位置相同，则属于同构。

```java
class Solution {
    public boolean isIsomorphic(String s, String t) {
        int [] indexOfS=new int[128];
        int [] indexOfT=new int[128];
        for(int i=0;i<s.length();i++){
            int si=s.charAt(i),ti=t.charAt(i);
            if(indexOfS[si]!=indexOfT[ti]) return false;
            indexOfS[si]=i+1;//索引要从1开始计算，0为初始值
            indexOfT[ti]=i+1;
        }
        return true;
    }
}
```

#### 4.计数二进制子串#696

描述：给定一个字符串 `s`，计算具有相同数量0和1的非空(连续)子字符串的数量，并且这些子字符串中的所有0和所有1都是组合在一起的。

思路：将字符串的连续字符分割成几组，相邻两组能组成的满足条件的子串数量为相邻两组长度的较小值。

```java
class Solution {
    public int countBinarySubstrings(String s) {
        int preLen=0,curLen=1,count=0;
        for(int i=1;i<s.length();i++){
            if(s.charAt(i)!=s.charAt(i-1)){
                count+=Math.min(preLen,curLen);
                preLen=curLen;
                curLen=1;
            }else {
                curLen++;
            }
        }
        return count+Math.min(preLen,curLen);
    }
}
```



###   六、树

#### 6-1 递归

##### 1.树的高度

#104树的深度

```java
class Solution {
    public int maxDepth(TreeNode root) {
        if(root==null)return 0;
        return Math.max(maxDepth(root.left),maxDepth(root.right))+1;
    }
}
```

#111二叉树的最小深度

```java
class Solution {
    public int minDepth(TreeNode root) {
        if(root==null) return 0;
        int leftDepth=minDepth(root.left),rightDepth=minDepth(root.right);
        if(root.left==null) return rightDepth+1;
        if(root.right==null) return leftDepth+1;
        return Math.min(rightDepth,leftDepth)+1;
    }
}
```

#110平衡树

平衡树的左右子树高度差小于等于1

```java
class Solution {
    public boolean isBalanced(TreeNode root) {
        if(root==null)return true;
        return Math.abs(maxDepth(root.left)-maxDepth(root.right))<=1 && isBalanced(root.left)&& isBalanced(root.right);
    }
    private int maxDepth(TreeNode root){
        if(root==null)return 0;
        return Math.max(maxDepth(root.left),maxDepth(root.right))+1;
    }
}
```

但这样自顶向下访问，会产生大量重复的节点访问和计算，最差的情况下时间复杂度为O(n^2)。可以改为自底向上的访问，只用遍历一次所有节点。如果从底部发现有子树不是平衡树的情况，则高度返回-1，从而节省了时间。

```java
class Solution {
    public boolean isBalanced(TreeNode root) {
        return maxDepth(root)!=-1;
    }
    private int maxDepth(TreeNode root){
        if(root==null) return 0;
        int left=maxDepth(root.left);
        if(left==-1) return -1;
        int right=maxDepth(root.right);
        if(right==-1) return -1;
        return Math.abs(left-right)<=1 ? Math.max(left,right)+1:-1;
    }
}
```

##### 2.相同、对称、翻转

#100相同的树

```java
class Solution {
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if(p==null) return q==null;
        if(q==null) return false;
        if(p.val!=q.val) return false;
        return isSameTree(p.left,q.left) && isSameTree(p.right,q.right);
    }
}
```

#101对称二叉树

```java
class Solution {
    public boolean isSymmetric(TreeNode root) {
        return isMirror(root,root);
    }
    public boolean isMirror(TreeNode t1, TreeNode t2) {
        if (t1 == null && t2 == null) return true;
        if (t1 == null || t2 == null) return false;
        return (t1.val == t2.val)
            && isMirror(t1.right, t2.left)
            && isMirror(t1.left, t2.right);
    }
}
```

迭代方法。用队列每次读取两个节点比较他们的值。每次入队时注意左右节点入队顺序相反

```java
class Solution {
    public boolean isSymmetric(TreeNode root) {
        Queue<TreeNode> queue=new LinkedList<>();
        queue.add(root);
        queue.add(root);
        while(!queue.isEmpty()){
            TreeNode t1=queue.poll();
            TreeNode t2=queue.poll();
            if(t1==null && t2==null) continue;
            if(t1 == null || t2 == null ) return false;
            if(t1.val!=t2.val) return false;
            queue.add(t1.left);
            queue.add(t2.right);
            queue.add(t1.right);
            queue.add(t2.left);
        }
        return true;
    }
}
```

#226翻转树

```java
class Solution {
    public TreeNode invertTree(TreeNode root) {
        if(root==null) return root;
        TreeNode left=invertTree(root.right);
        TreeNode right=invertTree(root.left);
        root.right=right;
        root.left=left;
        return root;
    }
}
```

##### 3.树的路径

#112路径总和

判断是否存在二叉树的路径和等于目标值

```java
class Solution {
    public boolean hasPathSum(TreeNode root, int sum) {
        if(root==null) return false;
        if(root.left==null && root.right==null && root.val==sum) return true;
        return hasPathSum(root.left,sum-root.val) || hasPathSum(root.right,sum-root.val);
    }
}
```

#113路径总和II

找到所有路径总和等于目标值的路径。

思路：DFS。

```java
class Solution {
    public List<List<Integer>> pathSum(TreeNode root, int sum) {
        List<List<Integer>> res=new ArrayList<>();
        List<Integer> path=new ArrayList<>();
        if(root!=null){
            dfs(root,sum,res,path);
        }
        return res;
    }
    private void dfs(TreeNode root,int sum,List<List<Integer>> res,List<Integer> path){
        path.add(root.val);
        if(root.left==null && root.right==null && sum-root.val==0){
            res.add(new ArrayList<Integer> (path));
        }
        if(root.left!=null){
            dfs(root.left,sum-root.val,res,path);
        }
        if(root.right!=null){
            dfs(root.right,sum-root.val,res,path);
        }
        path.remove(path.size()-1);
    }
}
```

#437路径总和III

找出路径和等于给定数值的路径总数。路径方向必须向下，路径不需要从根节点开始，也不需要再叶节点结束。

思路：写一个方法统计从某一节点开始的路径的数量。

```java
class Solution {
    public int pathSum(TreeNode root, int sum) {
        if(root==null) return 0;
        return pathSumStartWithRoot(root,sum)
            +pathSum(root.left,sum)+pathSum(root.right,sum);
    }
    private int pathSumStartWithRoot(TreeNode root,int sum){
        if(root==null) return 0;
        int res=0;
        if(root.val==sum) res++;
        res+=pathSumStartWithRoot(root.left,sum-root.val)
            +pathSumStartWithRoot(root.right,sum-root.val);
        return res;
    }
}
```

#543两节点的最长路径

描述：给定一棵二叉树，你需要计算它的直径长度。一棵二叉树的直径长度是任意两个结点路径长度中的最大值。这条路径可能穿过根结点。

思路：即求出左右子树深度相加的最大值

```java
class Solution {
    private int max=0;
    public int diameterOfBinaryTree(TreeNode root) {
        depth(root);
        return max;
    }
    private int depth(TreeNode root){
        if(root==null) return 0;
        int leftDepth=depth(root.left);
        int rightDepth=depth(root.right);
        max=Math.max(max,leftDepth+rightDepth);
        return Math.max(leftDepth,rightDepth)+1;
    }
}
```

##### 4. House Robber

不能同一晚上偷相邻的两间房屋。

I.数组。动态规划。

- 状态。f(k)表示从前k个房屋中能抢到的最大金额。Ai为第i个房屋的金钱。
- 转移方程。f(k)=max( f(k-2)+Ak , f(k-1) )
- 初始条件。f(1)=A1
- 结果。f(n)

```java
class Solution {
    public int rob(int[] nums) {
        int preMax=0,curMax=0;
        for(int a:nums){
            int temp=curMax;
            curMax=Math.max(preMax+a,curMax);
            preMax=temp;
        }
        return curMax;
    }
}
```

II环形数组。第一个和最后一个不能同时偷，因此可分解为(0,n-1)和(1,n)两个子问题

```java
class Solution {
    public int rob(int[] nums) {
        int n=nums.length;
        if(n==0) return 0;
        if(n==1) return nums[0];
        return Math.max(robCount(nums,0,n-1),robCount(nums,1,n));
    }
    private int robCount(int[] nums,int l,int r){
        int preMax=0,curMax=0;
        for(int i=l;i<r;i++){
            int temp=curMax;
            curMax=Math.max(preMax+nums[i],curMax);
            preMax=temp;
        }
        return curMax;
    }
}
```

III二叉树。对于每个节点，有抢和不抢两种选择。如果抢了，那么就加上以其子节点的子节点为根抢劫得到的总金额；如果不抢，则总金额等于其两个子节点为根所抢到的总金额。比较两者的大小即可。

```java
class Solution {
    public int rob(TreeNode root) {
        if(root==null) return 0;
        int val1=root.val;
        if(root.left!=null) 
            val1+=rob(root.left.left)+rob(root.left.right);
        if(root.right!=null)
            val1+=rob(root.right.left)+rob(root.right.right);
        int val2=rob(root.left)+rob(root.right);
        return Math.max(val1,val2);
    }
}
```

##### 5.左叶子之和#404

```java
class Solution {
    public int sumOfLeftLeaves(TreeNode root) {
        if(root==null) return 0;
        if(isLeaf(root.left)) 
            return root.left.val+sumOfLeftLeaves(root.right);
        return sumOfLeftLeaves(root.left)
            +sumOfLeftLeaves(root.right);
    }
    private boolean isLeaf(TreeNode root){
        if(root==null) return false;
        return root.left==null && root.right==null;
    }
}
```

##### 6.子树#572

描述：判断一个树t是否是另一个树s的子树

```java
class Solution {
    public boolean isSubtree(TreeNode s, TreeNode t) {
        if(s==null) return false;
        return isSubtreeWithRoot(s,t)
            ||isSubtree(s.left,t)
            ||isSubtree(s.right,t);
    }
    private boolean isSubTreeWithRoot(TreeNode s,TreeNode t){
        if(t==null && s==null) return true;
        if(t==null || s==null) return false;
        if(t.val!=s.val) return false;
        return isSubTreeWithRoot(s.left,t.left)
            && isSubTreeWithRoot(s.right,t.right);
    }
}
```

##### 7.合并两个二叉树#617

```java
class Solution {
    public TreeNode mergeTrees(TreeNode t1, TreeNode t2) {
        if(t1==null) return t2;
        if(t2==null) return t1;
        TreeNode root=new TreeNode(t1.val+t2.val);
        root.left=mergeTrees(t1.left,t2.left);
        root.right=mergeTrees(t1.right,t2.right);
        return root;
    }
}
```

##### 8.二叉树中第二小的节点#671

描述：一个二叉树每个节点都是正数，且子节点数量只能为0或2，且此节点的值不大于其子节点的值。找出第二小的值，不存在返回-1

思路：根节点一定是值最小的节点，只需要在子树中找到最小的值即可。因此该函数可以理解为求以root为根的子树中的最小值的函数。

```java
class Solution {
    public int findSecondMinimumValue(TreeNode root) {
        if(root==null) return -1;
        if(root.left==null && root.right==null) return -1;
        int leftMin=root.left.val;
        if(leftMin==root.val) 
            leftMin=findSecondMinimumValue(root.left);
        int rightMin=root.right.val;
        if(rightMin==root.val)
            rightMin=findSecondMinimumValue(root.right);
        if(leftMin!=-1 && rightMin!=-1) 
            return Math.min(leftMin,rightMin);
        if(leftMin!=-1) return leftMin;
        return rightMin;
    }
}
```

#### 6-2 层序遍历

#102 二叉树的层次遍历

```java
class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> res=new ArrayList<>();
        if(root==null) return res;
        Queue<TreeNode> queue=new LinkedList<>();
        queue.add(root);
        while(!queue.isEmpty()){
            int len=queue.size();
            List<Integer> layer=new ArrayList<>();
            while(len>0){
                TreeNode node=queue.poll();
                layer.add(node.val);
                len--;
                if(node.left!=null) queue.offer(node.left);
                if(node.right!=null) queue.offer(node.right);
            }
            res.add(layer);
        }
        return res;
    }
}
```

#103 锯齿形层次遍历

```java
class Solution {
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> res=new ArrayList<>();
        if(root==null) return res;
        Queue<TreeNode> queue=new LinkedList<>();
        queue.add(root);
        int level=1;
        while(!queue.isEmpty()){
            int len=queue.size();
            List<Integer> layer=new LinkedList<>();
            while(len>0){
                TreeNode node=queue.poll();
                if((level&1)==1){
                    layer.add(node.val);
                }else layer.add(0,node.val);
                if(node.left!=null) queue.offer(node.left);
                if(node.right!=null) queue.offer(node.right);
                len--;
            }
            level++;
            res.add(layer);
        }
        return res;
    }
}
```

#513左下角的节点

找到最下层最左边的节点。root不为null

```java
class Solution {
    public int findBottomLeftValue(TreeNode root) {
        Queue<TreeNode> queue=new LinkedList<>();
        queue.add(root);
        while(!queue.isEmpty()){
            root=queue.poll();
            if(root.right!=null) queue.add(root.right);
            if(root.left!=null) queue.add(root.left);
        }
        return root.val;
    }
}
```

#637二叉树每一层的平均值

```java
class Solution {
    public List<Double> averageOfLevels(TreeNode root) {
        List<Double> res=new ArrayList<>();
        if(root==null) return res;
        Queue<TreeNode> queue=new LinkedList<>();
        queue.add(root);
        while(!queue.isEmpty()){
            int len=queue.size();
            double sum=0;
            for(int i=0;i<len;i++){
                TreeNode node=queue.poll();
                sum+=node.val;
                if(node.left!=null)queue.add(node.left);
                if(node.right!=null) queue.add(node.right);
            }
            res.add(sum/len);
        }
        return res;
    }
}
```

#### 6-3 前中后序遍历

递归版本：以前序遍历为例。中序和后序变更三个语句顺序即可。

```java
class Solution {
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> res=new ArrayList<>();
        preorder(root,res);
        return res;
    }
    private void preorder(TreeNode root,List<Integer> res){
        if(root==null) return ;
        res.add(root.val);//1
        preorder(root.left,res);//2
        preorder(root.right,res);//3
    }
}
```

#144 二叉树的前序遍历

借助栈存储节点，每有一个节点出栈，就将其子节点入栈。注意出栈顺序与入栈相反，因此入栈顺序先右后左。

```java
class Solution {
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> res=new ArrayList<>();
        if(root==null) return res;
        Stack<TreeNode> s=new Stack<>();
        s.push(root);
        while(!s.isEmpty()){
            TreeNode node=s.pop();
            res.add(node.val);
            if(node.right!=null) s.push(node.right);
            if(node.left!=null) s.push(node.left);
        }
        return res;
    }
}
```

#145 二叉树的后序遍历

前序遍历的顺序是根左右，将左右顺序调换就是根右左，而后序是左右根，因此可以参考按照前序的思路，结束后翻转一下结果即可。

```java
class Solution {
    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> res=new ArrayList<>();
        if(root==null) return res;
        Stack<TreeNode> s=new Stack<>();
        s.push(root);
        while(!s.isEmpty()){
            TreeNode node=s.pop();
            res.add(node.val);
            if(node.left!=null) s.push(node.left);
            if(node.right!=null) s.push(node.right);
        }
        Collections.reverse(res);
        return res;
    }
}
```

#94 二叉树的中序遍历

```java
class Solution {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> res=new ArrayList<>();
        if(root==null) return res;
        Stack<TreeNode> s=new Stack<>();
        TreeNode cur=root;
        while(cur!=null || !s.isEmpty()){
            while(cur!=null){
                s.push(cur);
                cur=cur.left;
            }
            TreeNode node=s.pop();
            res.add(node.val);
            cur=node.right;
        }
        return res;
    }
}
```

#### 6-4 二叉搜索树BST

二叉搜索树是指树的每个节点大于等于其左子树的所有节点，小于等于其右子树的所有节点。

性质：二叉搜索树的中序遍历结果是一个有序数组

##### 1.构建二叉搜索树

#108将有序数组转换为二叉搜索树

转换为一个高度平衡的BST。高度平衡是指左右子树高度差绝对值不超过1。

思路：取数组的中间值为根节点

```java
class Solution {
    public TreeNode sortedArrayToBST(int[] nums) {
        return toBST(nums,0,nums.length-1);
    }
    private TreeNode toBST(int[] nums,int l,int r){
        if(l>r) return null;
        int mid=l+(r-l)/2;
        TreeNode root=new TreeNode(nums[mid]);
        root.left=toBST(nums,l,mid-1);
        root.right=toBST(nums,mid+1,r);
        return root;
    }
}
```

#109将有序链表转换为二叉搜索树

思路：快慢指针找到链表的中点的前一个节点，并断开链表

```java
class Solution {
    public TreeNode sortedListToBST(ListNode head) {
        if(head==null) return null;
        if(head.next==null) return new TreeNode(head.val);
        ListNode preMid=findPreMid(head);
        ListNode mid=preMid.next;
        preMid.next=null;
        TreeNode root=new TreeNode(mid.val);
        root.left=sortedListToBST(head);
        root.right=sortedListToBST(mid.next);
        return root;
    }
    private ListNode findPreMid(ListNode head){
        ListNode slow=head,fast=head;
        ListNode pre=slow;
        while(fast!=null && fast.next!=null){
            pre=slow;
            slow=slow.next;
            fast=fast.next.next;
        }
        return pre;
    }
}
```

#669修剪二叉搜索树

将二叉搜索树修剪为只保留值在L-R之间的节点。

思路：三种情况

- 如果root值比R大，则剪去根节点和右子树。
- 如果root值比L小，则剪去根节点和左子树。
- 如果root值在两者之间，则递归对左右子树进行修剪。

```java
class Solution {
    public TreeNode trimBST(TreeNode root, int L, int R) {
        if(root==null) return null;
        if(root.val>R) return trimBST(root.left,L,R);
        if(root.val<L) return trimBST(root.right,L,R);
        root.left=trimBST(root.left,L,R);
        root.right=trimBST(root.right,L,R);
        return root;
    }
}
```

##### 2.最近公共祖先

#235 BST的最近公共祖先

一个节点也可以是自己的祖先。

思路：最近公共祖先一定满足p和q两节点的值分居两侧。

```java
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if(root.val>p.val && root.val>q.val){
            return lowestCommonAncestor(root.left,p,q);
        }
        if(root.val<p.val && root.val<q.val){
            return lowestCommonAncestor(root.right,p,q);
        }
        return root;
    }
}
```

#236 二叉树的最近公共祖先

```java
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if(root==null || root==p || root==q) return root;
        TreeNode left=lowestCommonAncestor(root.left,p,q);
        TreeNode right=lowestCommonAncestor(root.right,p,q);
        if(left==null) return right;
        if(right==null) return left;
        return root;
    }
}
```

##### 3.查找

#230 BST中第k小的元素

```java
class Solution {
    private int cnt=0;
    private int val;
    public int kthSmallest(TreeNode root, int k) {
        inorder(root,k);
        return val;
    }
    private void inorder(TreeNode node,int k){
        if(node==null) return;
        inorder(node.left,k);
        cnt++l
        if(cnt==k){
            val=node.val;
            return;
        }
        inorder(node.right,k);
    }
}
```







### 七、图











