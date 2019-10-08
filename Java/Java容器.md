<!-- GFM-TOC -->

- [一、概述](#一概述)
  - [1.Collection](#1.Collection)
  - [2.Map](#2.Map)
- [二、源码](#二源码)

<!-- GFM-TOC -->



## 一、概述

Java中主要有Collection和Map两种容器，Collection是存储某种对象的集合，Map是存储两个对象形成的键值对（映射表）。

### 1.Collection

<div align="center"> <img src="./pics/2Collection.png" width="600"/> </div><br>
Collection继承自Iterable接口。

Collection下有Set、List和Queue三个接口。

**Set**

Set中存放的数据是唯一的。可以存放null，但只有一个。

- HashSet。无序，底层的数据结构是哈希表，支持快速查找O(1)，使用Iterator遍历得到的结果不确定。保证唯一性的两个方法（hashCode()和equals()方法）。
- LinkedHashSet。有序，底层数据结构是链表+哈希表，具有HashSet的查找效率。
- TreeSet。有序（排序），底层数据结构是红黑树，查找效率为O(logN)。实现了SortedSet接口，元素排序的方法有默认排序和比较器排序。

**List**

List中存放的数据是有序且可重复的。可存放多个null值。

- ArrayList。底层数据结构是数组，查询快，增删慢。线程不安全，效率高。
- Vector。底层数据结构是数组，查询快，增删慢。线程安全，效率低。Stack类继承自Vector类。
- LinkedList。底层数据结构是链表（双向链表），查询慢，增删块。线程不安全，效率高。

**Queue**

- LinkedList。可以用来实现双端队列。
- PriorityQueue。基于堆结构实现，可以用来实现优先队列。java中默认是小顶堆，使用大顶堆需要重写其compare方法。

Collection集合类的选用：

- 先判断是否需要元素唯一。唯一则用set，否则用list
- 使用set判断是否需要有序。有序用LinkedHashSet/TreeSet，否则HashSet
- 使用list判断是否需要线程安全。需要用vector；否则查询多用ArrayList，增删多用LinkedList。
- 需要用到堆或者优先队列时使用PriorityQueue。

### 2.Map

Map接口主要的实现类有HashMap,HashTable,TreeMap和LinkedHashMap。其中TreeMap实现了SortedMap接口。

- HashMap。底层数据结构是哈希表。
- HashTable。与HashMap类似，但它是线程安全的。属于遗留类，不建议使用。可以使用来支持线程安全，并且ConcurrentHashMap 的效率更高一些，因为HashTable是整个加锁，而ConcurrentHashMap 引入了分段锁。
- TreeMap。有序（排序），底层数据结构是红黑树。
- LinkedHashMap。有序，链表+哈希表，顺序为插入顺序或者LRU（最近最少使用）顺序。



## 二、源码分析

