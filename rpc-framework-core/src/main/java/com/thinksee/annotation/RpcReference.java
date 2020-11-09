package com.thinksee.annotation;

import java.lang.annotation.*;

/**
 * Retention:  标识这个注解怎么保存，是只在代码中，还是编入class文件中，或者是在运行时可以通过反射访问。
 * Documented: 标记这些注解是否包含在用户文档中
 * Target:
 * Inherited:  标记这个注解是继承于哪个注解类（默认注解并没有继承于任何子类）
 *
 * 0. 1个annotation和1个retention policy关联
 * 1. 1个annotation和1-n个element type关联
 * 2. annotation有许多实现类，包括deprecated, documented, inherited, override
 *
 * ElementType
 * TYPE:            类，接口（包括注释类型）或者枚举声明
 * FIELD:           字段声明（包括枚举常量）
 * METHOD:          方法声明
 * PARAMETER:       参数声明
 * CONSTRUCTOR:     构造方法声明
 * LOCAL_VARIABLE:  局部变量声明
 * ANNOTATION_TYPE: 注释类型声明
 * PACKAGE:         包声明
 *
 * RetentionPolicy
 * SOURCE:          Annotation信息仅存在于编译器处理期间，编译器处理完之后就没有该Annotation信息了
 *      若 Annotation 的类型为 SOURCE，则意味着：Annotation 仅存在于编译器处理期间，编译器处理完之后，该 Annotation 就没用了。
 *      例如，" @Override" 标志就是一个 Annotation。当它修饰一个方法的时候，就意味着该方法覆盖父类的方法；并且在编译期间会进行语法检查！
 *      编译器处理完后，"@Override" 就没有任何作用了。
 * CLASS:           编译器将Annotation存储于类对应的.class文件中，默认行为
 *      若 Annotation 的类型为 CLASS，则意味着：编译器将 Annotation 存储于类对应的 .class 文件中，它是 Annotation 的默认行为。
 * RUNTIME:         编译器将Annotation存储于class文件中，并且可由jvm读入
 *      若 Annotation 的类型为 RUNTIME，则意味着：编译器将 Annotation 存储于 class 文件中，并且可由JVM读入。
 *
 * JAVA中常用的Annotation
 * @Deprecated  -- @Deprecated 所标注内容，不再被建议使用。
 * @Override    -- @Override 只能标注方法，表示该方法覆盖父类中的方法。
 * @Documented  -- @Documented 所标注内容，可以出现在javadoc中。
 * @Inherited   -- @Inherited只能被用来标注“Annotation类型”，它所标注的Annotation具有继承性。
 * @Retention   -- @Retention只能被用来标注“Annotation类型”，而且它被用来指定Annotation的RetentionPolicy属性。
 * @Target      -- @Target只能被用来标注“Annotation类型”，而且它被用来指定Annotation的ElementType属性。
 * @SuppressWarnings -- @SuppressWarnings 所标注内容产生的警告，编译器会对这些警告保持静默。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {
    String version() default "";
    String group() default "";
}
