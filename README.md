## 项目简介

&emsp;&emsp;现代工业(ModernIndustry for MC1.12.2)是我很久以前就开始设想的项目，不过由于种种原因知道2019年才正式开工。

&emsp;&emsp;如名称一样，现代工业是工业类模组，目前网上的工业类模组已经很多了，不过我会尽可能的为玩家带来新鲜的游戏玩法，不和其他模组重合。

## 项目现状

&emsp;&emsp;目前项目并没有开始进行游玩内容的开发，仍然处于造轮子的状态，有兴趣的话可以看一看功能都是如何实现的。

&emsp;&emsp;我一个人有些顾不过来项目的开发，有能力的小伙伴想要参与项目开发的话可以通过邮箱（[minedreams@qq.com](mailto:minedreams@qq.com)）联系我。

&emsp;&emsp;在 [爱发电](https://afdian.net/a/emptydreams) 中有一些以前发的东西，里面有一些视频、截图之类的东西，不过目前我已经不再通过爱发电发布内容，想要了解项目进度的还请关注`github`以及我的博客 [山岳库博](https://kmar.top/)。

## 更新日志

&emsp;&emsp;[点击查看《现代工业更新计划》](https://kmar.top/posts/24a732ec/)

&emsp;&emsp;有能力的小伙伴也可以扫描文章下方的二维码投币支持噢~，点个`star`也好ヾ(≧▽≦*)o

## API

&emsp;&emsp;这里简单说明一下`api`包中的各个分类的作用：

+ `araw` - 实现`TileEntity`内容的自动化存储
+ `capabalities` - 各个模块用到的`cap`
+ `craftguide` - 独立于MC外的合成表
+ `electricity` - 电力系统
+ `event` - 各个模块用到的事件，部分事件是直接写在模块里面的
+ `exception` - 自定义异常
+ `fluid` - 流体管道
+ `graphics` - 实现运行期自动绘制`gui`
+ `net` - 自动化网络通信
+ `register` - 注解式注册机
+ `tools` - 各种工具
+ `utils` - 模组用到的各种操作的封装