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
- HashTable。与HashMap类似，但它是线程安全的。属于遗留类，不建议使用。可以使用ConcurrentHashMap 来支持线程安全，并且ConcurrentHashMap 的效率更高一些，因为HashTable是整个加锁，而ConcurrentHashMap 引入了分段锁。
- TreeMap。有序（排序），底层数据结构是红黑树。
- LinkedHashMap。有序，链表+哈希表，顺序为插入顺序或者LRU（最近最少使用）顺序。



## 二、源码分析

注：无特殊说明的情况下，以下源码是基于jdk1.8的源码分析

### 1.ArrayList

#### 1.1继承关系

```java
public class ArrayList<E> extends AbstractList<E>  
	implements List<E>, RandomAccess, Cloneable, java.io.Serializable
```

继承自AbstractList类，实现了RandomAccess接口表示该类支持快速随机访问，因为底层数据结构是数组。

#### 1.2添加与扩容

数组的默认初始容量是10。

添加元素时，使用ensureCapacityInternal方法来确保容量足够，如果容量不足，要使用grow方法进行扩容，扩容大小为原有容量的1.5倍。

```java
private static final int DEFAULT_CAPACITY = 10;

public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    elementData[size++] = e;
    return true;
}
private void ensureCapacityInternal(int minCapacity) {
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
        minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
    }
    ensureExplicitCapacity(minCapacity);
}
private void ensureExplicitCapacity(int minCapacity) {
    modCount++;
    // overflow-conscious code
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);
}
private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
    // minCapacity is usually close to size, so this is a win:
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

由于每次扩容需要使用Arrays.copyOf()方法将原数组整个复制到新数组中，这一操作的代价很高，因此最好在创建ArrayList时就指定大概的容量大小，减少扩容的次数。

#### 1.3删除

ArrayList删除元素需要调用System.arraycopy()方法将从index+1开始后面的元素全部复制到其前一个位置，时间复杂度为O(N)，代价很高。

```java
    public E remove(int index) {
        rangeCheck(index);
        modCount++;
        E oldValue = elementData(index);
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        elementData[--size] = null; // clear to let GC do its work
        return oldValue;
    }
```

#### 1.4线程安全

fail-fast（快速失败）与fail-safe（安全失败）

- 快速失败。在用迭代器遍历一个集合对象时，如果遍历过程中另一个线程对集合对象的内容进行了修改（增加、删除、修改），则会抛出Concurrent Modification Exception。
- 采用安全失败机制的集合容器，在遍历时不是直接在集合内容上访问的，而是先复制原有集合内容，在拷贝的集合上进行遍历。java.util.concurrent包下的容器都是安全失败。

modCount变量用于记录ArrayList的结构发生变化的次数，是指添加或删除了元素或是调整了数组的大小。在进行序列化或迭代等操作时，需要比较操作前后modCount的值是否发生改变，如果改变了会抛出Concurrent Modification Exception。

```java
private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException{
        // Write out element count, and any hidden stuff
        int expectedModCount = modCount;
        s.defaultWriteObject();

        // Write out size as capacity for behavioural compatibility with clone()
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (int i=0; i<size; i++) {
            s.writeObject(elementData[i]);
        }

        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
```

ArrayList是线程不安全的，可以使用Collections.synchronizedList()方法获得一个线程安全的ArrayList，也可以使用concurrent并发包下的CopyOnWriteArrayList 类。

```java
List<String> list=new ArrayList<>();
List<String> synList=Collections.synchronizedList(list);
List<String> cowList=new CopyOnWriteArrayList<>();
```

#### 1.5CopyOnWriteArrayList

读写分离的实现：

- 写操作在复制的数组上进行，读操作在原数组上进行；
- 写操作加锁，防止并发写入时导致写入数据丢失；
- 写操作结束后将原数组指向新数组。

```java
public boolean add(E e) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        Object[] elements = getArray();
        int len = elements.length;
        Object[] newElements = Arrays.copyOf(elements, len + 1);
        newElements[len] = e;
        setArray(newElements);
        return true;
    } finally {
        lock.unlock();
    }
}
final void setArray(Object[] a) {
    array = a;
}
```

CopyOnWriteArrayList可在写操作的同时允许读操作，适合读多写少的情景。但是内存开销较大，且不能实时地读取数据，不适合内存约束和有实时性要求的情景。

### 2.LinkedList

#### 2.1继承关系

```java
public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
```

#### 2.2Node类

使用Node类存储链表节点的信息，有prev和next两个指针。

```java
private static class Node<E> {
    E item;
    Node<E> next;
    Node<E> prev;

    Node(Node<E> prev, E element, Node<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
}
```

注：LinkedList类中还存储了first和last两个指针用来存储头指针和尾指针。

#### 2.3ArrayList和LinkedList的区别

- 底层实现：动态数组/双向链表
- 随机访问：支持/不支持
- 增删元素：慢/快



### 3.HashMap







