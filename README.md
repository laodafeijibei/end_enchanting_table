# End Enchanting Table

Minecraft 1.20.1 Forge Mod，作者：laodafeijibei。

## 安装

1. 安装 Minecraft 1.20.1 与 Forge 47.3.20。
2. 客户端和服务端都将 `End Enchanting Table.jar` 放入 `mods` 目录。
3. 使用 Java 17 启动游戏。

## PCL 双语名称与封面

交付 JAR 使用纯英文文件名 `End Enchanting Table.jar`；Forge `mods.toml` 的 `displayName` 与 PCL 本地项目名均配置为 `终焉附魔台 | End Enchanting Table`，并内嵌封面。PCL 2.13 对没有发布到 Modrinth/CurseForge 的私人 JAR 默认不读取 Forge 封面，可在本机运行以下脚本安装 PCL 本地显示数据：

```powershell
.\tools\install_pcl_display.ps1
```

脚本会安装双语 JAR、从 JAR 提取封面、写入 PCL 本地项目缓存，并在首次修改前备份原缓存。本项目所用 PCL 实例已经执行并验证完成。

## 合成

```text
下界合金块  下界合金块  下界合金块
下界合金块  附魔台      下界合金块
黑曜石      黑曜石      黑曜石
```

## 使用

1. 放置终焉附魔台并右键打开 GUI。
2. 将待附魔物品放入右侧物品槽。
3. 在左栏搜索并勾选附魔；列表来自 Forge 附魔注册表，因此包含其他 Mod 已注册的附魔。
4. 点击附魔名称，在右侧输入 `0` 到 `2147483647` 的目标等级。输入低于当前等级的值会祛除对应等级，输入 `0` 会完全移除该附魔。
5. 可以同时选择多项升级和祛魔；确认右下角显示的净消耗或净返还后，点击“结算附魔”。

经验等级按“目标等级 - 当前等级”的差值结算：

- 普通附魔：每级 2 级经验。
- 宝藏附魔：每级 5 级经验。
- 诅咒附魔：每级 1 级经验。
- 创造模式不扣经验。
- 降低等级时按同一倍率返还经验；多项升级与降低先累加，最终只结算净额。

普通书会转换为附魔书；已有附魔会保留，同 ID 的附魔会被目标等级覆盖。附魔书移除最后一项附魔后恢复为普通书。GUI 与数据包均允许完整的 32 位非负整数目标等级，服务端重新校验容器、物品、附魔 ID、当前等级、目标等级和经验净额。

## 高等级说明

本 Mod 将标准 `Enchantments` / `StoredEnchantments` NBT 的 `lvl` 改为 32 位整数，并修正 Forge 1.20.1 的 255 级读取钳制。原版和 Forge 通过 `EnchantmentHelper` 查询时会获得完整等级；超高等级 Tooltip 使用十进制显示。`Integer.MAX_VALUE` 的耐久附魔另有溢出保护。

其他 Mod 的附魔会被列出并写入完整等级。如果某个第三方附魔在自己的实现中主动钳制等级或使用会溢出的自定义数学，本 Mod无法通用改写该第三方代码。

## 构建与验证

```powershell
.\gradlew.bat test --no-daemon
.\gradlew.bat build --no-daemon
```

构建输出位于 `build\libs\end_enchanting_table-1.1.2.jar`。目标双语显示来自 Forge/PCL 元数据，不依赖文件名解析；Forge 内部兼容 ID 仍为合法的 `end_enchanting_table`。资源可通过 `tools\generate_assets.ps1` 重新生成；首次运行前需先执行一次 `gradlew compileJava` 以准备 Minecraft 1.20.1 的 UV 参考缓存。
