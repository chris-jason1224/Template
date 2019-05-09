# Template

#feature

   1、android组件化
   2、自定义注解
   3、retrofit2 + okhttp3
   4、Arouter
   5、JPush

##更新日志##


    #20190509
        1、完成fun_push组件，集成极光推送

     
    #20190410
        1、增加异步任务执行工具类

      
    #20190329
        1、优化BaseActivity和BaseFragment中多状态布局切换方式

    #20190325
        1、增加log打印文件
      
      
    #20190324
        1、完成fun_bluetooth
            a.附近蓝牙搜索
            b.蓝牙连接
            c.蓝牙自动连接
            d.蓝牙打印
            e.蓝牙各个状态监听回调


    #20190311
        1、完成fun_compressor组件，封装图片压缩服务


    #20190305
        1、完成AOP组件创建
        2、增加防止连续点击提交数据切面
        3、增加方法执行耗时统计切面


    #20190212
        1、fun_business组件增加微信授权功能

    #20190211
        1、fun_business组件增加微信支付、支付宝支付功能
        2、增加微信分享功能
    
    #20190129
        1、封装Fresco图片加载框架

    #20190118
        1、新增AES加密、解密工具类

    #20190117
        1、优化架构层次
        2、引入MultiTypeAdapter简化RecyclerView适配器工作
        3、移除vLayout二次封装
        4、实际项目中，简单的Recyclerview列表用MultiTypeAdapter，复杂效果如首页用VLayout

    #20181114
        1、引入MMKV，替代原有SharedPreferences，支持跨进程访问本地Key-Value类型缓存
        2、实际项目中，SPFUtil做底层数据存取，DiskCacheUtil做业务上隔离，调用SPFUtil

    #20181023
        1、优化网络变化监听逻辑
        2、基类加入沉浸式效果

    #20181018
        1、优化了系统崩溃拦截处理逻辑，发生崩溃后交由用户自己选择是直接关闭系统还是重启app



    #20180916
        1、完成EasyCompressor本地图片压缩库并上传到GitHub
        @link「 https://github.com/chris-jason1224/EasyCompressor 」

    #20180907
        1、添加全局java层崩溃捕获机制
        2、自动记录崩溃信息到磁盘
        3、崩溃后自动重启

    #20180830
        1、core-ui添加RoundShape系列控件
        2、core-ui添加CircleImageView
        3、core-ui天机消息提示QMUITipDialog
        4、添加基于VLayout的万能适配器

    #20180829
        1、添加自定义注解@ModuleRegister来自动为需要的module生成组件信息，以供framework组件使用
        2、添加BaseApplication对各个组件的管理功能和生命周期回调

    #20180827
        1、引入google easyPermissions动态权限处理框架

    #20180823
        1、base_common组件中增加DataBus数据总线

    #20180822
        1、BaseMVPActivity和BaseMVPFragment添加了Lifecycle来管理Presenter层

    #20180821
        1、添加Http常见异常处理

    #20180820
        1、搭建MVP架构
        2、封装Retrofit2配合RxJava2实现网络层

