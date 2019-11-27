<!-- GFM-TOC -->

- [创建型](#一创建型)
- [结构型](#二结构型)
- [行为型](#三行为型)

<!-- GFM-TOC -->

## 引言

设计模式是一套被反复使用、多数人知晓的、经过分类编目的、代码设计经验的总结，是对软件设计中普遍存在、反复出现的各种问题所提出的解决方案。其目的是为了提高代码的可重用性、代码的可读性和代码的可靠性。

设计模式的基本要素有四个方面：

- 名称。描述设计模式。
- 问题。描述该模式应用的环境。
- 解决方案。设计得组成部分和各部分间的关系。
- 效果。模式的优缺点。



## 一、创建型

创建型的主要特点是将对象的创建与使用进行分离，从而降低了系统耦合度，使用者不需要关心对象的创建细节，这些创建细节由相关的工厂去完成。

- 单例模式Singleton
- 原型模式Prototype
- 建造者模式Builder
- 简单工厂模式Simple Factory
- 工厂方法模式Factory Method
- 抽象工厂模式Abstract Factory

### 1.单例

某个类只能生成一个实例，且该类提供了一个全局访问点供外部获取到该实例。

结构：一个私有的构造函数、一个私有的静态实例和一个公有函数用于创建或获取该静态私有实例。 

**实现**

1.懒汉式

类加载时没有生成单例，只有当第一次调用getInstance方法时才创建这个单例。

```java
public class LazySingleton
{
    private static volatile LazySingleton instance=null;    //保证 instance 在所有线程中同步
    private LazySingleton(){}    //private 避免类在外部被实例化
    public static synchronized LazySingleton getInstance()
    {
        if(instance==null)
        {
            instance=new LazySingleton();
        }
        return instance;
    }
}
```

如果删除了volatile和synchronized关键字，那么会产生线程不安全问题。如果保留，则可以保证线程安全，但当一个线程进入该方法之后，其它试图进入该方法的线程都必须等待，这会使得线程阻塞时间过长，影响性能，这是懒汉式的缺点。

2.饿汉式

类一旦加载就会创建一个单例。

饿汉式单例在创建时就有一个静态实例供外部使用，以后不会发生改变，因此是线程安全的。但相比于懒汉式的延迟实例化，直接实例化更消耗资源。

```java
public class HungrySingleton
{
    private static final HungrySingleton instance=new HungrySingleton();
    private HungrySingleton(){}
    public static HungrySingleton getInstance()
    {
        return instance;
    }
}
```

**应用场景**

- 当某类只要求生成一个对象的时候。如每个人的身份证号。
- 当对象需要被共享的时候。共享同一对象可以节省内存，加快访问速度。如Web中的配置对象、数据库的连接池等。
- 当某类需要频繁地被实例化又要频繁地被销毁的时候，如多线程的线程池、网络连接池等。

**JDK中的应用**

-  java.lang.Runtime#getRuntime()
- java.awt.Desktop#getDesktop()
- java.lang.System#getSecurityManager()



### 2.原型

将一个对象作为原型，通过复制克隆出多个与原型类似的实例。

结构：主要由三个部分组成：

- 抽象原型类。规定了具体类型对象必须实现的接口。
- 具体原型类。实现抽象原型类的clone方法，是可被复制的对象。
- 访问类。使用具体原型类中的clone方法来复制新的对象。

**实现**

原型模式中的拷贝分为浅拷贝和深拷贝。实现Cloneable接口可以实现对象的浅拷贝。

抽象原型类（也可不写，直接让具体原型类实现Cloneable接口，也就是将Cloneable接口作为抽象原型类）：

```java
public abstract class Prototype implements Cloneable{
}
```

具体原型类：

```java
public class ConcretePrototype extends Prototype{
    ConcretePrototype(){
        System.out.println("ConcretePrototype created");
    }
    public Object clone() throws CloneNotSupportedException {
        System.out.println("ConcretePrototype cloned");
        return (ConcretePrototype)super.clone();
    }
}

```

访问类：

```java
public class Client {
    public static void main(String[] args) throws CloneNotSupportedException {
        ConcretePrototype obj1=new ConcretePrototype();
        ConcretePrototype obj2= (ConcretePrototype) obj1.clone();
        System.out.println("obj1==obj2?"+(obj1==obj2));
    }
}

```

结果：

```java
ConcretePrototype created
ConcretePrototype cloned
obj1==obj2?false
```

**应用**

原型模式可以用于生成相同对象，也可以生成相似的对象。因此通常适用于以下场景：

- 对象之间相同或相似，即只有个别的几个属性不同的时候。
- 对象的创建过程比较麻烦，而复制过程较为简单。

**JDK中的应用**

- java.lang.Object#clone()

### 3.简单工厂



### 4.工厂方法

定义一个用于创建产品的接口，由子类决定生产什么产品。



### 5.抽象工厂

提供一个创建产品族的接口，每个子类可以生产一系列相关产品。



### 6.建造者

将一个复杂对象分解成多个相对简单的部分，然后根据不同需要分别创建他们，最后构成该复杂对象。















## 二、结构型





## 三、行为型





### 参考资料

- [Java设计模式全面解析](http://c.biancheng.net/design_pattern/ )
- 弗里曼. HeadFirst设计模式 [M]. 中国电力出版社，2007
-  Bloch J. Effective java[M]. Addison-Wesley Professional, 2017. 