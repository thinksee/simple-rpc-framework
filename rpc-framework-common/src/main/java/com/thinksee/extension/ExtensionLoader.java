package com.thinksee.extension;

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * ExtensionLoader的getExtensionLoader方法获取一个Extension实例，然后通过ExtensionLoader的getExtension方法获取拓展类对象。
 * 其中，getExtensionLoader方法用于从缓存中获取与拓展类对应的ExtensionLoader，若缓存未命中，则创建一个新的实例。
 * @param <T>
 */
@Slf4j
public final class ExtensionLoader<T> {
    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";
    // 缓存extension_loader: 其作用就是加载所有打上@SPI注解的接口，并根据配置进行实例化、封装。
    // 由于@SPI是对应@TYPE类、接口或者枚举上
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    // 缓存
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    private final Class<?> type;
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    // 从缓存中获取已加载的拓展类，线程可见（volatile关键字修饰），即Map修改之后，全线程可见
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();
    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        if(type == null) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        if(!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface");
        }
        if(type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        // 首先从缓存中获取类所对应的loader
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if(extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<>(type));
//            extensionLoader = new ExtensionLoader<>(type); // 不能使用这行语句
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }

    public T getExtension(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }
        Holder<Object> holder = cachedInstances.get(name);
        if(holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        Object instance = holder.get();
        if(instance == null) {
            // 最小化锁
            synchronized (holder) {
                instance = holder.get();
                if(instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    /**
     * 从配置文件中加载所有的拓展类。配置项名称 -> 配置类
     * 其中和原始Dubbo相比缺少依赖注入的环节
     *
     * @param name 配置名称
     * @return 配置类
     */
    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if(clazz == null) {
            throw new RuntimeException("No such extension of name " + name);
        }
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if(instance == null) {
            try {
                // 通过反射创建实例
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException("Fail to create an instance of the extension class " + clazz);
            }
        }
        return instance;
    }

    /**
     * 在通过名称获取拓展类之前，首先需要根据配置文件解析出拓展项名称到拓展类的映射关系表（Map<名称, 拓展类>），
     * 之后再根据拓展项名称从映射关系表中取出相应的拓展类即可。
     * @return 获取所有的拓展类
     */
    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if(classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if(classes == null) {
                    classes = new HashMap<>();
                    // 从文件中进行读取
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

//    /**
//     * loadExtensionClasses 方法总共做了两件事情，一是对 SPI 注解进行解析，二是调用 loadDirectory 方法加载指定文件夹配置文件。
//     * @return 扩展类
//     */
//    private Map<String, Class<?>> loadExtensionClasses() {
//        // 获取 SPI 注解，这里的 type 变量是在调用 getExtensionLoader 方法时传入的
//        final SPI defaultAnnotation = type.getAnnotation(SPI.class);
//        if (defaultAnnotation != null) {
//            String value = defaultAnnotation.value();
//            if ((value = value.trim()).length() > 0) {
//                // 对 SPI 注解内容进行切分
//                String[] names = NAME_SEPARATOR.split(value);
//                // 检测 SPI 注解内容是否合法，不合法则抛出异常
//                if (names.length > 1) {
//                    throw new IllegalStateException("more than 1 default extension name on extension...");
//                }
//
//                // 设置默认名称，参考 getDefaultExtension 方法
//                if (names.length == 1) {
//                    cachedDefaultName = names[0];
//                }
//            }
//        }
//
//        Map<String, Class<?>> extensionClasses = new HashMap<String, Class<?>>();
//        // 加载指定文件夹下的配置文件
//        loadDirectory(extensionClasses, DUBBO_INTERNAL_DIRECTORY);
//        loadDirectory(extensionClasses, DUBBO_DIRECTORY);
//        loadDirectory(extensionClasses, SERVICES_DIRECTORY);
//        return extensionClasses;
//    }

    private void loadDirectory(Map<String, Class<?>> extensionClass) {
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        try {
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            Enumeration<URL> urls = classLoader.getResources(fileName);
            if(urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    // 加载资源
                    loadResource(extensionClass, classLoader, resourceUrl);
                }
            }
        }catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        // 转化为字符流
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {
            String line;
            while((line = reader.readLine()) != null) {
                // 定位 # 字符，注释字符
                final int ci = line.indexOf('#');
                if(ci >= 0) {
                    // 截取#之前的字符串，#之后的内容为注释，需要忽略
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if(line.length() > 0) {
                    try {
                        final int ei = line.indexOf('=');
                        // 取key和value
                        String name = line.substring(0, ei).trim();
                        String clazzName = line.substring(ei + 1).trim();
                        if(name.length() > 0 && clazzName.length() > 0) {
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            extensionClasses.put(name, clazz);
                        }
                    }catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }catch (IOException e) {
            log.error(e.getMessage());
        }
    }

//    private T injectExtension(T instance) {
//        try {
//            if(objectFactory != null) {
//                // 遍历目标所有方法
//                for(Method method : instance.getClass().getMethods()) {
//                    // 检测方法是否以set开头，且方法仅有一个参数，且方法访问级别为public
//                    if(method.getName().startsWith("set")
//                    && method.getParameterTypes().length == 1
//                    && Modifier.isPublic(method.getModifiers())) {
//                        // 获取setter方法参数类型
//                        Class<?> pt = method.getParameterTypes()[0];
//                        try {
//                            // 获取属性名，比如setName 方法对应属性名name
//                            String property = method.getName().length() > 3 ?
//                                    method.getName().substring(3, 4).toLowerCase() +
//                                            method.getName().substring(4) : "";
//                            Object object = objectFactory.getExtension(pt, property);
//                            if(object != null) {
//                                method.invoke(instance, object);
//                            }
//                        } catch (Exception e) {
//                            logger.error("fail to inject via method...");
//                        }
//                    }
//                }
//        }
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//        return instance;
//    }
}
