# Spring Security中Authority、Role的区别

Role是角色，使用hasRole来鉴权，初始化时需要添加ROLE_前缀

使用hasRole()方法鉴权

![image-20201113182128962](C:\Users\82703\AppData\Roaming\Typora\typora-user-images\image-20201113182128962.png)

![image-20201113182140605](C:\Users\82703\AppData\Roaming\Typora\typora-user-images\image-20201113182140605.png)

authority是权限，不用添加ROLE_前缀

使用hasAuthrity()方法鉴权