# -2、tf的基本操作

1. tf的数据类型

   ![image-20200407205335090](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407205335090.png)

2. 创建张量

   ![image-20200407205448549](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407205448549.png)

3. 转换numpy格式数据到tf格式数据

   ![image-20200407205721397](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407205721397.png)

4. 创建tensor的函数

   ![image-20200407205821057](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407205821057.png)

5. 生成正态分布的随机数

   ![image-20200407205923282](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407205923282.png)

   ![image-20200407205941255](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407205941255.png)

6. 生成均匀分布的随机数

   ![image-20200407210051664](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407210051664.png)

7. 常用函数

   ![image-20200407210144867](C:\Users\lenovo\Desktop\Github\DocumentAndCode\ML\tensorflow2.0\image-20200407210144867.png)

   ![image-20200407210302980](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407210302980.png)

   ![image-20200407213555884](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407213555884.png)

   ![image-20200407213646363](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407213646363.png)

   ![image-20200407213710139](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407213710139.png)

   ![image-20200407213935067](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407213935067.png)

8. tf.Variable 可训练参数

   ![image-20200407210428645](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407210428645.png)

9. 常用运算方式

   

   ![image-20200407210455133](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407210455133.png)![image-20200407210533391](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407210533391.png)

10. 生成特征/标签对

    ![image-20200407210820592](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407210820592.png)

    ![image-20200407210846468](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407210846468.png)

11. 求导函数

    ![image-20200407211247086](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407211247086.png)

12. enumerate 枚举元素

    ![image-20200407211322564](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407211322564.png)

13. 热编码 tf.one_hot

    ![image-20200407211427884](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407211427884.png)

    ![image-20200407211441867](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407211441867.png)

14. tf.nn.softmax : 概率分布转换

    ![image-20200407211657738](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407211657738.png)

15. assign_sub 自减操作

    ![image-20200407211915161](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407211915161.png)

16. tf.argmax

    ![image-20200407211959800](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407211959800.png)

17. 格式化表格操作

    ```python
    x_data = DataFrame(x_data, columns=['花萼长度', '花萼宽度', '花瓣长度', '花瓣宽度']) # 为表格增加行索引（左侧）和列标签（上方）
    pd.set_option('display.unicode.east_asian_width', True)  # 设置列名对齐
    print("x_data add index: \n", x_data)
    
    x_data['类别'] = y_data  # 新加一列，列标签为‘类别’，数据为y_data
    print("x_data add a column: \n", x_data)
    ```

18. 

# -1、一些库的操作

1. plt.scatter(data.radio, data.sales)：画图
2. data = pd.read_csv('./dataset/Advertising.csv')：读文件
3. data.iloc[:, 1:-1]：取矩阵数据
4. 

# 0、notebook

1. shitf + enter：运行换行
2. Ctrl+Enter ：运行所选cell
3. Alt+Enter ：运行当前单元格，在下面插入
4. tap：智能补全
5. np.random.random**?**：显示说明文档或者用shift+tab+tab
6. 魔术命令：
   - %matplotlib inline：画图显示
   - %pwd：当前路径
   - %timeit [x**3 for x in range(1000)]：看代码要运行多少时间

# 1、线性回归

# 2、激活函数

1. relu

   ![image-20200407013832740](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407013832740.png)

2. sigmoid

   ![image-20200407013845411](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407013845411.png)

3. tanh

   ![image-20200407013904429](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407013904429.png)

4. leak relu

   ![image-20200407013944219](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407013944219.png)

# 3、多层感知器

# 4、Adam优化器参数

1. ![image-20200407161401751](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407161401751.png)

2. ![image-20200407162710670](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407162710670.png)
3. ![image-20200407162745050](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407162745050.png)

# 5、解决过拟合Dropout

1. ![image-20200407164035575](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407164035575.png)
2. ![image-20200407164044278](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407164044278.png)
3. ![image-20200407164215552](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407164215552.png)
4. ![image-20200407164223793](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407164223793.png)
5. ![image-20200407164314354](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407164314354.png)

# 6、神经网络实现鸟尾草分类

1. 实现步骤

   ![image-20200407212655100](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407212655100.png)

2. 实现步骤

   1. ![image-20200407212727061](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407212727061.png)
   2. ![image-20200407213117220](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407213117220.png)
   3. ![image-20200407213239987](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407213239987.png)

# 7、神经网络优化

1. 指数衰减学习率

   ![image-20200407214427755](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407214427755.png)

2. 激活函数

   1. sigmoid函数

      ![image-20200407214605957](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407214605957.png)

   2. tanh函数

      ![image-20200407214724222](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407214724222.png)

   3. relu函数

      ![image-20200407214906091](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407214906091.png)

   4. leaky relu函数

      ![image-20200407214940975](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407214940975.png)

   5. 激活函数建议

      ![image-20200407215009496](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407215009496.png)

3. 损失函数

   1. mse

      ![image-20200407215139927](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407215139927.png)

   2. 自定义损失函数

      ![image-20200407215501877](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407215501877.png)

   3. ce

      ![image-20200407215657568](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407215657568.png)

   4. 使用softmax与ce结合

      ![image-20200407215810892](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407215810892.png)

4. 拟合问题

   1. 欠拟合和过拟合

      ![image-20200407215930967](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407215930967.png)

   2. 正则化缓解过拟合l2正则法

      ![image-20200407220118888](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407220118888.png)

      - py代码

        ![image-20200407220735110](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200407220735110.png)

5. 优化器

   1. 优化器概念：不同优化器指数mt和Vt的公式不一样

      ![image-20200408004903913](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408004903913.png)

   2. sgd优化器

      ![image-20200408004959666](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408004959666.png)

   3. sgdm优化器，有根据历史的加权

      ![image-20200408005315843](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408005315843.png)

      ![image-20200408005416886](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408005416886.png)

   4. adagrad：其实就是会自动调节学习率

      ![image-20200408005653730](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408005653730.png)

      ![image-20200408005801939](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408005801939.png)

   5. RMSProp：历史加权的Vt，也就是学习率历史加权自动调整

      ![image-20200408005846256](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408005846256.png)

      ![image-20200408005922569](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408005922569.png)

   6. adam优化器：结合sgdm一阶动量和RMSProp二阶动量的优点，并加入偏差，偏差其实就是修正历史的权重，随着训练次数t的增加，β1t和β2会逐渐接近0，也就mt和Vt随着t的增加而下降。这样，一开始mt大一点，但是学习率小一点，后面mt小，但是学习率大一点。

      ![image-20200408010931097](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408010931097.png)

      ![image-20200408010943607](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408010943607.png)

# 8、搭建网络八股

1. 搭建网络八股Sequential

   1. 六步法（步骤）

      ![image-20200408011258672](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408011258672.png)

   2. Sequential 描述各层网络

      ![image-20200408011538241](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408011538241.png)

   3. compile 配置神经网络的训练方法

      ![image-20200408011613400](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408011613400.png)

   4. fit 训练的参数

      ![image-20200408011851150](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408011851150.png)

   5. summary 网络结构

      ![image-20200408011933735](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408011933735.png)

2. 用class搭建神经网络结构

   1. 步骤

      ![image-20200408012309411](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408012309411.png)

   2. 实现代码

      ![image-20200408012454239](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408012454239.png)

3. Mnist数据集

   ![image-20200408012631818](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408012631818.png)

   ![image-20200408012755961](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408012755961.png)

4. fashion数据集

   ![image-20200408012950772](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408012950772.png)

# 9、搭建网络八股

1. 网络八股总览

   1. ![image-20200408025417168](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408025417168.png)

      ![image-20200408025501558](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408025501558.png)

2. 自制数据集

   1. 实现

      ![image-20200408030502849](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408030502849.png)

      ![image-20200408030522025](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408030522025.png)

3. 数据增强：加大数据集

   1. ![image-20200408034015738](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408034015738.png)

      ![image-20200408034506356](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408034506356.png)

4. 断点续训

   1. 读取存储模型

      ![image-20200408034620719](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408034620719.png)

      ![image-20200408034927793](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408034927793.png)

5. 参数读取，把参数存入文本

   1. ![image-20200408035301998](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408035301998.png)

6. acc曲线和loss曲线

   ![image-20200408035506498](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408035506498.png)

7. 使用前向传播计算结果

   ![image-20200408035722938](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408035722938.png)

   1. 导入模型预测结果

      ![image-20200408040015044](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408040015044.png)

# 10、卷积神经网络

1. 卷积计算过程

   1. 特征提取

      ![image-20200408041434580](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408041434580.png)

   2. 卷积核：多输入，一个输出，也就是特征提取，比如把一个3x3的正方形的像素块，提取成一个像素点

      ![image-20200408041830034](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408041830034.png)

      ![image-20200408042206168](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408042206168.png)

      ![image-20200408042314756](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408042314756.png)

      ![image-20200408042408358](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408042408358.png)

2. 感受野：也就是提取的特征映射到原始图片上的区域大小

   ![image-20200408043135968](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408043135968.png)

3. 全零填充：也就是保持原始图片的尺寸不变

   ![image-20200408043512314](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408043512314.png)

4. TF描述卷积计算层

   ![image-20200408043728823](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200408043728823.png)

5. 