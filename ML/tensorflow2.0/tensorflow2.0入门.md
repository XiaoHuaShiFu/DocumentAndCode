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

5. 批标准化（Batcch Normalization， BN）

   - ![image-20200413004454295](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413004454295.png)

   - 为每个卷积核引入可训练参数γ和β，使得标准化后特征数据还是具有非线性能力。

     ![image-20200413004835702](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413004835702.png)

   - BN层编程

     ![image-20200413004920067](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413004920067.png)

6. 池化（pooling）

   - ![image-20200413005153774](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413005153774.png)

7. 舍弃（Dropout）

   - ![image-20200413005254236](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413005254236.png)

8. 卷积网络模块

   ![image-20200413005614957](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413005614957.png)

9. Cifar10数据集

   - ![image-20200413005727635](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413005727635.png)
   - ![image-20200413005808892](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413005808892.png)

10. 搭建卷积神经网络

    - ![image-20200413012244433](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413012244433.png)

    - 代码

      ```python
      class Baseline(Model):
          def __init__(self):
              super(Baseline, self).__init__()
              self.c1 = Conv2D(filters=6, kernel_size=(5, 5), padding='same')  # 卷积层
              self.b1 = BatchNormalization()  # BN层
              self.a1 = Activation('relu')  # 激活层
              self.p1 = MaxPool2D(pool_size=(2, 2), strides=2, padding='same')  # 池化层
              self.d1 = Dropout(0.2)  # dropout层
      
              self.flatten = Flatten()
              self.f1 = Dense(128, activation='relu')
              self.d2 = Dropout(0.2)
              self.f2 = Dense(10, activation='softmax')
      
          def call(self, x):
              x = self.c1(x)
              x = self.b1(x)
              x = self.a1(x)
              x = self.p1(x)
              x = self.d1(x)
      
              x = self.flatten(x)
              x = self.f1(x)
              x = self.d2(x)
              y = self.f2(x)
              return y
      ```

11. LeNet

    - ![image-20200413013920757](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413013920757.png)

    - 实现

      ```python
      class LeNet5(Model):
          def __init__(self):
              super(LeNet5, self).__init__()
              self.c1 = Conv2D(filters=6, kernel_size=(5, 5),
                               activation='sigmoid')
              self.p1 = MaxPool2D(pool_size=(2, 2), strides=2)
      
              self.c2 = Conv2D(filters=16, kernel_size=(5, 5),
                               activation='sigmoid')
              self.p2 = MaxPool2D(pool_size=(2, 2), strides=2)
      
              self.flatten = Flatten()
              self.f1 = Dense(120, activation='sigmoid')
              self.f2 = Dense(84, activation='sigmoid')
              self.f3 = Dense(10, activation='softmax')
      
          def call(self, x):
              x = self.c1(x)
              x = self.p1(x)
      
              x = self.c2(x)
              x = self.p2(x)
      
              x = self.flatten(x)
              x = self.f1(x)
              x = self.f2(x)
              y = self.f3(x)
              return y
      ```

12. AlexNet

    - ![image-20200413014348127](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413014348127.png)

    - 实现

      ```python
      class AlexNet8(Model):
          def __init__(self):
              super(AlexNet8, self).__init__()
              self.c1 = Conv2D(filters=96, kernel_size=(3, 3))
              self.b1 = BatchNormalization()
              self.a1 = Activation('relu')
              self.p1 = MaxPool2D(pool_size=(3, 3), strides=2)
      
              self.c2 = Conv2D(filters=256, kernel_size=(3, 3))
              self.b2 = BatchNormalization()
              self.a2 = Activation('relu')
              self.p2 = MaxPool2D(pool_size=(3, 3), strides=2)
      
              self.c3 = Conv2D(filters=384, kernel_size=(3, 3), padding='same',
                               activation='relu')
                               
              self.c4 = Conv2D(filters=384, kernel_size=(3, 3), padding='same',
                               activation='relu')
                               
              self.c5 = Conv2D(filters=256, kernel_size=(3, 3), padding='same',
                               activation='relu')
              self.p3 = MaxPool2D(pool_size=(3, 3), strides=2)
      
              self.flatten = Flatten()
              self.f1 = Dense(2048, activation='relu')
              self.d1 = Dropout(0.5)
              self.f2 = Dense(2048, activation='relu')
              self.d2 = Dropout(0.5)
              self.f3 = Dense(10, activation='softmax')
      
          def call(self, x):
              x = self.c1(x)
              x = self.b1(x)
              x = self.a1(x)
              x = self.p1(x)
      
              x = self.c2(x)
              x = self.b2(x)
              x = self.a2(x)
              x = self.p2(x)
      
              x = self.c3(x)
      
              x = self.c4(x)
      
              x = self.c5(x)
              x = self.p3(x)
      
              x = self.flatten(x)
              x = self.f1(x)
              x = self.d1(x)
              x = self.f2(x)
              x = self.d2(x)
              y = self.f3(x)
              return y
      ```

13. VGGNet

    - ![image-20200413014719711](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413014719711.png)

    - 实现

      ```python
      class VGG16(Model):
          def __init__(self):
              super(VGG16, self).__init__()
              self.c1 = Conv2D(filters=64, kernel_size=(3, 3), padding='same')  # 卷积层1
              self.b1 = BatchNormalization()  # BN层1
              self.a1 = Activation('relu')  # 激活层1
              self.c2 = Conv2D(filters=64, kernel_size=(3, 3), padding='same', )
              self.b2 = BatchNormalization()  # BN层1
              self.a2 = Activation('relu')  # 激活层1
              self.p1 = MaxPool2D(pool_size=(2, 2), strides=2, padding='same')
              self.d1 = Dropout(0.2)  # dropout层
      
              self.c3 = Conv2D(filters=128, kernel_size=(3, 3), padding='same')
              self.b3 = BatchNormalization()  # BN层1
              self.a3 = Activation('relu')  # 激活层1
              self.c4 = Conv2D(filters=128, kernel_size=(3, 3), padding='same')
              self.b4 = BatchNormalization()  # BN层1
              self.a4 = Activation('relu')  # 激活层1
              self.p2 = MaxPool2D(pool_size=(2, 2), strides=2, padding='same')
              self.d2 = Dropout(0.2)  # dropout层
      
              self.c5 = Conv2D(filters=256, kernel_size=(3, 3), padding='same')
              self.b5 = BatchNormalization()  # BN层1
              self.a5 = Activation('relu')  # 激活层1
              self.c6 = Conv2D(filters=256, kernel_size=(3, 3), padding='same')
              self.b6 = BatchNormalization()  # BN层1
              self.a6 = Activation('relu')  # 激活层1
              self.c7 = Conv2D(filters=256, kernel_size=(3, 3), padding='same')
              self.b7 = BatchNormalization()
              self.a7 = Activation('relu')
              self.p3 = MaxPool2D(pool_size=(2, 2), strides=2, padding='same')
              self.d3 = Dropout(0.2)
      
              self.c8 = Conv2D(filters=512, kernel_size=(3, 3), padding='same')
              self.b8 = BatchNormalization()  # BN层1
              self.a8 = Activation('relu')  # 激活层1
              self.c9 = Conv2D(filters=512, kernel_size=(3, 3), padding='same')
              self.b9 = BatchNormalization()  # BN层1
              self.a9 = Activation('relu')  # 激活层1
              self.c10 = Conv2D(filters=512, kernel_size=(3, 3), padding='same')
              self.b10 = BatchNormalization()
              self.a10 = Activation('relu')
              self.p4 = MaxPool2D(pool_size=(2, 2), strides=2, padding='same')
              self.d4 = Dropout(0.2)
      
              self.c11 = Conv2D(filters=512, kernel_size=(3, 3), padding='same')
              self.b11 = BatchNormalization()  # BN层1
              self.a11 = Activation('relu')  # 激活层1
              self.c12 = Conv2D(filters=512, kernel_size=(3, 3), padding='same')
              self.b12 = BatchNormalization()  # BN层1
              self.a12 = Activation('relu')  # 激活层1
              self.c13 = Conv2D(filters=512, kernel_size=(3, 3), padding='same')
              self.b13 = BatchNormalization()
              self.a13 = Activation('relu')
              self.p5 = MaxPool2D(pool_size=(2, 2), strides=2, padding='same')
              self.d5 = Dropout(0.2)
      
              self.flatten = Flatten()
              self.f1 = Dense(512, activation='relu')
              self.d6 = Dropout(0.2)
              self.f2 = Dense(512, activation='relu')
              self.d7 = Dropout(0.2)
              self.f3 = Dense(10, activation='softmax')
      
          def call(self, x):
              x = self.c1(x)
              x = self.b1(x)
              x = self.a1(x)
              x = self.c2(x)
              x = self.b2(x)
              x = self.a2(x)
              x = self.p1(x)
              x = self.d1(x)
      
              x = self.c3(x)
              x = self.b3(x)
              x = self.a3(x)
              x = self.c4(x)
              x = self.b4(x)
              x = self.a4(x)
              x = self.p2(x)
              x = self.d2(x)
      
              x = self.c5(x)
              x = self.b5(x)
              x = self.a5(x)
              x = self.c6(x)
              x = self.b6(x)
              x = self.a6(x)
              x = self.c7(x)
              x = self.b7(x)
              x = self.a7(x)
              x = self.p3(x)
              x = self.d3(x)
      
              x = self.c8(x)
              x = self.b8(x)
              x = self.a8(x)
              x = self.c9(x)
              x = self.b9(x)
              x = self.a9(x)
              x = self.c10(x)
              x = self.b10(x)
              x = self.a10(x)
              x = self.p4(x)
              x = self.d4(x)
      
              x = self.c11(x)
              x = self.b11(x)
              x = self.a11(x)
              x = self.c12(x)
              x = self.b12(x)
              x = self.a12(x)
              x = self.c13(x)
              x = self.b13(x)
              x = self.a13(x)
              x = self.p5(x)
              x = self.d5(x)
      
              x = self.flatten(x)
              x = self.f1(x)
              x = self.d6(x)
              x = self.f2(x)
              x = self.d7(x)
              y = self.f3(x)
              return y
      ```

14. InceptionNet

    - InceptionNet结构块

      ![image-20200413015247657](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413015247657.png)

      - 实现

        ```python
        class ConvBNRelu(Model):
            def __init__(self, ch, kernelsz=3, strides=1, padding='same'):
                super(ConvBNRelu, self).__init__()
                self.model = tf.keras.models.Sequential([
                    Conv2D(ch, kernelsz, strides=strides, padding=padding),
                    BatchNormalization(),
                    Activation('relu')
                ])
        
            def call(self, x):
                x = self.model(x, training=False) #在training=False时，BN通过整个训练集计算均值、方差去做批归一化，training=True时，通过当前batch的均值、方差去做批归一化。推理时 training=False效果好
                return x
            
        class InceptionBlk(Model):
            def __init__(self, ch, strides=1):
                super(InceptionBlk, self).__init__()
                self.ch = ch
                self.strides = strides
                self.c1 = ConvBNRelu(ch, kernelsz=1, strides=strides)
                self.c2_1 = ConvBNRelu(ch, kernelsz=1, strides=strides)
                self.c2_2 = ConvBNRelu(ch, kernelsz=3, strides=1)
                self.c3_1 = ConvBNRelu(ch, kernelsz=1, strides=strides)
                self.c3_2 = ConvBNRelu(ch, kernelsz=5, strides=1)
                self.p4_1 = MaxPool2D(3, strides=1, padding='same')
                self.c4_2 = ConvBNRelu(ch, kernelsz=1, strides=strides)
        
            def call(self, x):
                x1 = self.c1(x)
                x2_1 = self.c2_1(x)
                x2_2 = self.c2_2(x2_1)
                x3_1 = self.c3_1(x)
                x3_2 = self.c3_2(x3_1)
                x4_1 = self.p4_1(x)
                x4_2 = self.c4_2(x4_1)
                # concat along axis=channel
                x = tf.concat([x1, x2_2, x3_2, x4_2], axis=3)
                return x
        ```

    - 实现InceptionNet

      ![image-20200413015735900](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413015735900.png)

      ```python
      class Inception10(Model):
          def __init__(self, num_blocks, num_classes, init_ch=16, **kwargs):
              super(Inception10, self).__init__(**kwargs)
              self.in_channels = init_ch
              self.out_channels = init_ch
              self.num_blocks = num_blocks
              self.init_ch = init_ch
              self.c1 = ConvBNRelu(init_ch)
              self.blocks = tf.keras.models.Sequential()
              for block_id in range(num_blocks):
                  for layer_id in range(2):
                      if layer_id == 0:
                          block = InceptionBlk(self.out_channels, strides=2)
                      else:
                          block = InceptionBlk(self.out_channels, strides=1)
                      self.blocks.add(block)
                  # enlarger out_channels per block
                  self.out_channels *= 2
              self.p1 = GlobalAveragePooling2D()
              self.f1 = Dense(num_classes, activation='softmax')
      
          def call(self, x):
              x = self.c1(x)
              x = self.blocks(x)
              x = self.p1(x)
              y = self.f1(x)
              return y
      ```

15. ResNet

    - ![image-20200413020113690](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413020113690.png)

    - 实现

      ![image-20200413020518260](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200413020518260.png)

      ```python
      class ResnetBlock(Model):
      
          def __init__(self, filters, strides=1, residual_path=False):
              super(ResnetBlock, self).__init__()
              self.filters = filters
              self.strides = strides
              self.residual_path = residual_path
      
              self.c1 = Conv2D(filters, (3, 3), strides=strides, padding='same', use_bias=False)
              self.b1 = BatchNormalization()
              self.a1 = Activation('relu')
      
              self.c2 = Conv2D(filters, (3, 3), strides=1, padding='same', use_bias=False)
              self.b2 = BatchNormalization()
      
              # residual_path为True时，对输入进行下采样，即用1x1的卷积核做卷积操作，保证x能和F(x)维度相同，顺利相加
              if residual_path:
                  self.down_c1 = Conv2D(filters, (1, 1), strides=strides, padding='same', use_bias=False)
                  self.down_b1 = BatchNormalization()
              
              self.a2 = Activation('relu')
      
          def call(self, inputs):
              residual = inputs  # residual等于输入值本身，即residual=x
              # 将输入通过卷积、BN层、激活层，计算F(x)
              x = self.c1(inputs)
              x = self.b1(x)
              x = self.a1(x)
      
              x = self.c2(x)
              y = self.b2(x)
      
              if self.residual_path:
                  residual = self.down_c1(inputs)
                  residual = self.down_b1(residual)
      
              out = self.a2(y + residual)  # 最后输出的是两部分的和，即F(x)+x或F(x)+Wx,再过激活函数
              return out
      
      
      class ResNet18(Model):
      
          def __init__(self, block_list, initial_filters=64):  # block_list表示每个block有几个卷积层
              super(ResNet18, self).__init__()
              self.num_blocks = len(block_list)  # 共有几个block
              self.block_list = block_list
              self.out_filters = initial_filters
              self.c1 = Conv2D(self.out_filters, (3, 3), strides=1, padding='same', use_bias=False)
              self.b1 = BatchNormalization()
              self.a1 = Activation('relu')
              self.blocks = tf.keras.models.Sequential()
              # 构建ResNet网络结构
              for block_id in range(len(block_list)):  # 第几个resnet block
                  for layer_id in range(block_list[block_id]):  # 第几个卷积层
      
                      if block_id != 0 and layer_id == 0:  # 对除第一个block以外的每个block的输入进行下采样
                          block = ResnetBlock(self.out_filters, strides=2, residual_path=True)
                      else:
                          block = ResnetBlock(self.out_filters, residual_path=False)
                      self.blocks.add(block)  # 将构建好的block加入resnet
                  self.out_filters *= 2  # 下一个block的卷积核数是上一个block的2倍
              self.p1 = tf.keras.layers.GlobalAveragePooling2D()
              self.f1 = tf.keras.layers.Dense(10, activation='softmax', kernel_regularizer=tf.keras.regularizers.l2())
      
          def call(self, inputs):
              x = self.c1(inputs)
              x = self.b1(x)
              x = self.a1(x)
              x = self.blocks(x)
              x = self.p1(x)
              y = self.f1(x)
              return y
      ```

16. 

