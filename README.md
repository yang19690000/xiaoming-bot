# XiaomingBot
太学小明机器人

## 小明插件开发
小明机器人可加载自己开发的插件。如需编写插件，请先导入 `xiaomingbot-api`。下面演示一个空的小明插件的开发方式：

### 快速开始
#### 插件主类
小明插件的主类应该是 `com.taixue.xiaomingbot.api.plugin.XiaomingPlugin` 的子类。因此，你的插件中至少应该含有这样一个类：
```java
package com.taixue.xiaominglexicons;

import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;

// 插件主类
public class XiaomingLexicons extends XiaomingPlugin {
}
```
这就是一个最简单的小明插件，可以被小明加载，但它没有任何功能。
#### 打包导出
小明加载插件时会先读取资源文件 `plugin.json`，所以你应该在 `jar` 中加入该文件，其内容应为：
```json
{
  "name": "Lexicons",   // 插件名（选填），默认值为 jar 文件名
  "version": "1.0",     // 版本号（选填），默认值为 (unknown version)
  "authors": [          // 多个作者（选填）。如果只有一个作者，也可以 "author": "Chuanwise",
    "Chuanwise"
  ],
  "fronts": [           // 前置插件（选填）。若前置插件未全部加载，则你的插件不会被加载。
  ],
  "main": "com.taixue.xiaominglexicons.XiaomingLexicons"    // 插件主类（必填）
}
```
打包插件后将插件放在小明根目录的 `plugins` 文件夹，重启小明即可。
#### 更进一步
你可以在插件主类通过重写 `onEnable` 方法，让插件在刚加载时执行一些操作。它们通常是读取你的相关文件、创建和注册群聊交互器、指令执行器等。例如：
```java
package com.taixue.xiaominglexicons;

import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;

/**
 * @author Chuanwise
 */
public class XiaomingLexicons extends XiaomingPlugin {
    @Override
    public void onEnable() {
        logger.info("我的第一个小明插件加载啦 (๑•̀ㅂ•́)و✧");
    }
}
```
类似的，你还可以重写很多方法，它们被执行的时机如下：
方法名|形参列表|返回值|执行时机
---|---|---|---
onEnable|无|无|插件刚被加载时
onDisable|无|无|插件被卸载前

#### 插件间通信
小明的插件间通信采用基于 `API` 的的通信方式。方式主要有两种：直接访问插件和通过事先设计好的 API。我们推荐你采用第二种方法。

#### 直接访问插件
通过 `getXiaomingBot().getPluginManager().getPlugin("插件名")` 便可获得一个被加载在小明的插件。其类型为 `XiaomingPlugin`，需手动转换为其主类类型。例如：
```java
package wiki.chuanwise.myxiaomingplugin;

import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaominglexicons.XiaomingLexicons;

/**
 * @author Chuanwise
 */
public class MyXiaomingPlugin extends XiaomingPlugin {
    @Override
    public void onEnable() {
        XiaomingLexicons plugin = (XiaomingLexicons) getXiaomingBot().getPluginManager().getPlugin("XiaomingLexicons");
    }
}
```

#### 更为推荐的 hook 通信方式
如果你希望其他插件主动与你通信，你需要写一个通信所用的类，其继承自 `com.taixue.xiaomingbot.api.plugin.HookHolder`。例如：
```java
package com.taixue.xiaominglexicons.hook;

import com.taixue.xiaomingbot.api.plugin.HookHolder;
import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaominglexicons.XiaomingLexicons;

/**
 * 插件通信所用类
 */
public class LexiconsHookHolder extends HookHolder {
    /**
     * 构造一个通信所用类
     * @param sponsor Hook 行为的发起插件，一般为 XiaomingPlugin。如果需要指定该类的使用者，也可以使用其他 XiaomingPlugin 的子类。
     * @param recipient Hook 行为的接受插件，一般是本插件主类类型
     */
    public LexiconsHookHolder(XiaomingPlugin sponsor, XiaomingLexicons recipient) {
        super(sponsor, recipient);
    }

    /**
     * 插件通信所用的一些方法
     */
    public void action() {}
}

```
其他插件需要与你的类通信时，需要 `hook` 你的插件。例如：

```java
package wiki.chuanwise.myxiaomingplugin;

import com.taixue.xiaomingbot.api.plugin.XiaomingPlugin;
import com.taixue.xiaominglexicons.hook.LexiconsHookHolder;

/**
 * @author Chuanwise
 */
public class MyXiaomingPlugin extends XiaomingPlugin {
    @Override
    public void onEnable() {
        LexiconsHookHolder lexicons = null;
        try {
            lexicons = hook("Lexicons", LexiconsHookHolder.class);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (Objects.isNull(lexicons)) {
            // hook failure
        }
        else {
            // hooked successfully
            lexicons.action();
        }
    }
}
```