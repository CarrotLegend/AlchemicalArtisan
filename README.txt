Alchemical Artisan（炼金工匠）
===========================

这是面向 Minecraft Java Edition 1.20.1 的 Forge 模组，Mod ID 为
alchemicalartisan，Java 根包为 com.carrot123.alchemicalartisan。

开发环境
--------
Minecraft：1.20.1，仅声明支持此版本
Forge：47.4.20；元数据声明的 Forge 版本下限为 47.4.0
Java：17（Gradle toolchain）
Gradle Wrapper：8.8
ForgeGradle：6.0.54
映射：official 1.20.1
JEI：开发依赖 15.62.0.219；可选客户端依赖，声明兼容 15.62.0.219 至 16.0.0 之前
运行端：Forge 客户端与专用服务端；JEI 集成仅在客户端启用
生成资源：src/generated/resources 由 Gradle 资源源集接入；本次未运行 datagen
发布产物：Gradle build 的重映射 JAR，实际发布前仍需核对 JAR 内容与运行加载

代码职责
--------
AlchemicalArtisan 是 @Mod 主类，调用 registry 包中的物品、方块、方块实体、
创造模式物品栏、菜单、配方类型、序列化器和掉落修改器注册。
block 与 blockentity 负责合金炉、炽烈熔炉的交互和服务端加工。
recipe 负责合金配方匹配及 JSON、网络序列化。
menu 提供容器槽位与数据同步；client/screen 提供客户端界面。
compat/jei 提供可选的 JEI 配方展示。

构建检查
--------
使用 gradlew.bat compileJava、gradlew.bat processResources、gradlew.bat build。
这些检查不等于游戏内、专服或发布包验证。不要运行 datagen 或游戏来代替静态审查。

许可
----
本模组源码以根目录 LICENSE 中的 MIT License 授权。
根目录 LICENSE.txt 是随 Forge MDK 保留的 Forge/LGPL 许可文本，不是本模组的许可声明。
