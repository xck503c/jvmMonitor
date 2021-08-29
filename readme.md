#项目说明

服务主要用于白盒测试，由于目前项目处于单元测试覆盖初期，测试也处于黑盒测试，模仿arthas写了一个服务端，利用agent在目标jvm中启动http服务，对外暴露接口。便于调试。

##使用说明

将几个jar放到同个目录下面即可使用

``nohup java -cp '.:jvmMonitor-boot-V1.0.0.jar:/usr/java/jdk1.7.0_80/lib/tools.jar' com.xck.boot.BootStrap pid &``

服务接口采用showdoc生成接口文档，具体可以参考：https://www.showdoc.com.cn/page/741656402509783

接口逻辑采用插件形式提供，@RequestMapping注解标识提供逻辑的类，采用注解扫描形式来注册和reload。

使用还不是很方便，后面优化
