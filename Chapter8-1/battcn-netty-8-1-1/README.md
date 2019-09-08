# HttpFileServerByNetty
基于netty构建的http文件服务器。利用netty的零拷贝与NIO特性，很好的解决内存溢出等问题，适合大文件上传与下载。不过这份代码只有文件下载。
注意需要设置下文件服务器的根目录与ip，如果设置成127.0.0.1可能会其他设备无法访问服务，因此建议设置成公网IP。
![](./doc/img/root.png)
![](./doc/img/ip.png)