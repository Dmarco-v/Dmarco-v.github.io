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
    private LazySingleton(){
    }    //private 避免类在外部被实例化
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

3.双重校验锁

instance只需要被实例化一次，之后就可以直接使用了。加锁操作只需要对实例化那部分的代码进行，只有当 instance没有被实例化时，才需要进行加锁。

双重校验锁先判断instance是否已被实例化，如果没有，则对实例化语句加锁。

```java
public class Singleton
{
    private static volatile Singleton instance=null;    
    private Singleton(){
    }    
    public static synchronized Singleton getInstance()
    {
        if(instance==null){
            synchronized(Singleton.class){
                if(instance==null){
                    instance=new Singleton();
                }
            }
        }
        return instance;
    }
}
```

设计双重校验锁的原因：

如果只有外层的if语句，即如下代码。此时如果instance==null，可能会有两个线程同时进入内层，从而使得两个线程都会执行实例化操作。而如果使用双重校验锁，进入内层后只能有一个线程执行实例化操作。

```java
if(instance==null){
    synchronized(Singleton.class){
        instance=new Singleton();
    }
}
```

同时，使用volatile关键字修饰可以禁止JVM指令重排，从而保证多线程环境下instance=new Singleton();可以正常运行。

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

创建一个对象时不向客户暴露内部细节，并提供一个创建对象的通用接口。

结构：简单工厂将实例化的操作单独放到一个类中，该类即为简单工厂类，让简单工厂来决定应使用哪个具体的子类来进行实例化。

**实现**

抽象产品：

```java
public interface Product {
}
```

具体产品类：

```java
public class Product1 implements Product {
}
public class Product2 implements Product {
}
```

简单工厂类：

```java
public class SimpleFactory{
    public Product createProduct(int type){
        if(type==1){
            return new Product1();
        }else if(type==2){
            return new Product2();
        }
    }
}
```

客户类：

```java
public class Client{
    public static void main(String[] args) {
        SimpleFactory simpleFactory = new SimpleFactory();
        Product product = simpleFactory.createProduct(1);
    }
}
```

**优点**：只需要传入一个类别参数即可完成不同类型对象的创建。

**缺点**：

- 简单工厂类中集中了所有产品的创建逻辑，一旦这个工厂出现问题，整个系统都会受到影响。
- 违背了开闭原则。一旦添加了新产品就不得不修改工厂类的逻辑，造成工厂类逻辑过于复杂。



### 4.工厂方法

定义一个用于创建产品的接口，由子类决定生产什么产品。

结构：

- 抽象工厂：提供创建产品的接口，调用者通过它访问具体工厂的具体方法。
- 具体工厂：实现抽象工厂中的抽象方法，完成具体产品的创建。
- 抽象产品：定义产品规范，描述了产品的主要特性和功能。
- 具体产品：实现抽象产品定义的接口，由具体工厂来创建。

**实现**

抽象工厂

```java
public interface Factory {
    public Product factoryMethod();
    public default void doSth(){
        Product product=factoryMethod();
    }
}
```

具体工厂

```java
public class ConcreteFactory1 implements Factory {
    @Override
    public Product factoryMethod() {
        System.out.println("Create ConcreteProduct1");
        return new ConcreteProduct1();
    }
}
public class ConcreteFactory2 implements Factory {
    @Override
    public Product factoryMethod() {
        System.out.println("Create ConcreteProduct2");
        return new ConcreteProduct2();
    }
}
```

抽象产品

```java
public interface Product {
    public void show();
}
```

具体产品

```java
public class ConcreteProduct1 implements Product {
    @Override
    public void show() {
        System.out.println("This is concreteProduct1");
    }
}
public class ConcreteProduct2 implements Product {
    @Override
    public void show() {
        System.out.println("This is concreteProduct2");
    }
}
```

客户类

```java
public class Client {
    public static void main(String[] args) {
        //客户需要产品1
        Factory factory=new ConcreteFactory1();
        Product product=factory.factoryMethod();
        product.show();
        //客户需要产品2
        Factory factory=new ConcreteFactory1();
        Product product=factory.factoryMethod();
        product.show();
    }
}
```

**优点**：

- 只需通过创建不同的具体工厂类，调用相同的方法就可以创建不同的具体产品类实例。
- 工厂方法模式是对简单工厂的进一步抽象和扩展。在简单工厂的基础上，增加了其扩展性。

**JDK中的应用**

- java.util.Calendar
- java.util.ResourceBundle
- java.text.NumberFormat
- java.nio.charset.Charset
- java.net.URLStreamHandlerFactory
- java.util.EnumSet
- javax.xml.bind.JAXBContext

### 5.抽象工厂

提供一个创建产品族的接口，每个子类可以生产一系列相关产品。

与工厂方法的区别：

- 抽象工厂创建的是对象族，是多个等级的产品（相关联的产品）。而工厂方法只创建一个产品。
- 抽象工厂创建的对象族中的的单个对象是采用工厂方法模式来进行。
- 抽象工厂用到了组合关系，而工厂方法是实现关系。

结构：结构与工厂方法相近，除了工厂与产品的关系由1对1变成了1对多。

**实现**

抽象工厂

```java
public interface Factory{
    public Product1 createProduct1();
    public Product2 createProduct2();
}
```

具体工厂

```java
public class ConcreteFactory1 implements Factory{
    @Override
    public Product1 createProduct1(){
        System.out.println("具体工厂1创建具体产品 11");
        return new ConcreteProduct11();
    }
    @Override
    public Product2 createProduct2(){
        System.out.println("具体工厂1创建具体产品 21");
        return new ConcreteProduct21();
    }
}
```

**JDK中的应用**

- javax.xml.parsers.DocumentBuilderFactory
- javax.xml.transform.TransformerFactory
- javax.xml.xpath.XPathFactory



### 6.建造者

将一个复杂对象分解成多个相对简单的部分，然后根据不同需要分别创建他们，最后构成该复杂对象。

结构：

- 产品：产品是由多个部件组成的复杂对象，由具体的建造者来创建各个部件。
- 抽象建造者：包含创建各个子部件的抽象方法的接口，以及一个返回复杂产品的方法。
- 具体建造者：实现抽象建造者接口，完成各部件的具体创建方法。
- 指挥者：调用建造者对象中的部件构造与装配方法完成复杂对象的创建。

**实现**

产品：

```java
public class Product{
    private String partA;
    private String partB;
    private String partC;
    public void setPartA(String partA){
        this.partA=partA;
    }
    public void setPartB(String partB){
        this.partB=partB;
    }
    public void setPartC(String partC){
        this.partC=partC;
    }
    public void show(){
        System.out.println("show the product");
    }
}
```

抽象建造者：

```java
public abstract class Builder{
    protected Product product=new Product();
    public abstract void buildPartA();
    public abstract void buildPartB();
    public abstract void buildPartC();
    public Product getProduct(){
        return product;
    }
}
```

具体建造者：

```java
public class ConcreteBuilder extends Builder
{
    //各种零部件的生产过程可以不尽相同
    public void buildPartA(){
        product.setPartA("建造 PartA");
    }
    public void buildPartB(){
        product.setPartB("建造 PartB");
    }
    public void buildPartC(){
        product.setPartC("建造 PartC");
    }
}
```

指挥者：

```java
class Director{
    private Builder builder;
    public Director(Builder builder){
        this.builder=builder;
    }
    public Product construct(){
        builder.buildPartA();
        builder.buildPartB();
        builder.buildPartC();
        return builder.getProduct();
    }
}
```

访问类

```java
public class Client{
    public static void main(String[] args){
        Builder builder=new ConcreteBuilder();
        Director director=new Director(builder);
        Product product=director.construct();
        product.show();
    }
}
```

**优点：**

- 各具体建造者相互独立，增加了系统扩展性。
- 客户端不必知道产品内部组成细节。

**缺点：**

- 产品的组成部分最好相同，不适用于产品内部变化较大的。



## 二、结构型





## 三、行为型





### 参考资料

- [Java设计模式全面解析](http://c.biancheng.net/design_pattern/ )
- 弗里曼. HeadFirst设计模式 [M]. 中国电力出版社，2007
-  Bloch J. Effective java[M]. Addison-Wesley Professional, 2017. 