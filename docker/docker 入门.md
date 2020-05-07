# 1、基础命令

1. docker pull [OPTIONS]  NAME[:TAG] 拉取镜像
2. docker images [OPTIONS] [REPOSITORY[:TAG]] 查看镜像
3. docker run [OPTIONS] IMAGE[:TAG] [COMMAND] [ARG...]
   - docker run -d hub.c.163.com/library/nginx 后台运行
   - docker run -d -p 8080:80 hub.c.163.com/library/nginx 后台运行，主机的8080端口映射到容器的80端口
   - docker run -d --name nginx -v /usr/share/nginx/html nginx： 后台运行，指定名字，挂载存储
4. docker ps 查看有什么镜像在运行
5. docker stop CONTAINER ID 停止一个容器
6. docker exec [OPTIONS] CONTAINER COMMAND [ARG...]
   - docker exec -it 2f bash 进入容器的bash
7. docker build
   - docker build -t jpress:latest . 指定镜像名字和版本
8. docker restart 
   - docker restart 243350eec3e0 重启docker容器
9. docker rm 删除容器
10. docker rmi 删除镜像
11. docker cp 在host和container之间拷贝文件
12. docker commit 保存改动为新的image
13. docker inspect
    - docker inspect nginx 通过名字检查docker信息

# 2、

1. ![image-20200508015944686](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200508015944686.png)
2. docker网络
   - ![image-20200508022800277](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200508022800277.png)
   - ![image-20200508022911836](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200508022911836.png)
3. 

# 3、构建自己的镜像

1. Dockerfile

   ![image-20200508025016915](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200508025016915.png)

2. docker build

   ![image-20200508025059643](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200508025059643.png)

3. Dockerfile语法

   ![image-20200508035333020](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200508035333020.png)

   ![image-20200508035417364](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200508035417364.png)

# 4、Volume

1. docker run -d --name nginx -v /usr/share/nginx/html nginx
   - 把容器的存储挂载到host上
2. docker run -p 80:80 -d -v $PWD/html:/usr/share/nginx/html nginx
   - 把当前目录挂载到容器的对应目录上
3.  docker create -v $PWD/data:/var/mydata --name data_container ubuntu 
   - 创建一个只有数据的容器
   - docker run -it --volumes-from data_container ubuntu /bin/bash 把只有数据的容器挂载到另外一个容器上

# 5、registry 

1. ![image-20200508041507509](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200508041507509.png)
2. docker login 登录
3. docker push imageName 推送镜像

# 6、docker-compose安装

1. ![image-20200508052024547](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200508052024547.png)
2. ![image-20200508052033197](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200508052033197.png)

