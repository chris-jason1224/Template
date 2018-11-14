# Template

##更新日志##



#20181114

    1、引入MMKV，替代原有SharedPreferences，支持跨进程访问本地Key-Value类型缓存
    
    
#20181023

    1、优化网络变化监听逻辑
    2、基类加入沉浸式效果

#20181018

    1、优化了系统崩溃拦截处理逻辑，发生崩溃后交由用户自己选择是直接关闭系统还是重启app

#20181017

    1、引入greenDao3，完成基本配置项


#20180916

    1、完成EasyCompressor本地图片压缩库并上传到GitHub
    @link「 https://github.com/chris-jason1224/EasyCompressor 」


#20180907
    
    1、添加全局java层崩溃捕获机制，崩溃发生后，由用户选择关闭或者重启app
    2、自动记录崩溃信息到磁盘
    3、崩溃后自动重启


#20180830
    
    1、core-ui添加RoundShape系列控件
    2、core-ui添加CircleImageView
    3、core-ui添加消息提示简单对话框QMUITipDialog
    4、添加基于VLayout的万能适配器


#20180829
    
    1、添加自定义注解@ModuleRegister来自动为需要的module生成组件信息，以供framework组件使用
    2、添加BaseApplication对各个组件的管理功能和生命周期回调


#20180827

    1、引入google easyPermissions动态权限处理框架


#20180823

    1、base-common组件中增加DataBus数据总线


#20180822

    1、BaseMVPActivity和BaseMVPFragment添加了Lifecycle来管理Presenter层


#20180821

    1、添加Http常见异常处理


#20180820

    1、搭建MVP架构
    2、封装Retrofit2配合RxJava2实现网络层
