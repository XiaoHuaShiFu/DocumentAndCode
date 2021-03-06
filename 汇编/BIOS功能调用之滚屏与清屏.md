# BIOS功能调用之滚屏与清屏

每次启动Bochs的时候，显示出来的那些内容有没有干扰你的眼球呢？其实我早就想清屏了，今天就来探索一下怎样利用BIOS中断来清屏。

清屏都有哪些方法？
1.空格填充法
如果单就”清屏幕”这个问题而言，解决办法有很多，最最“勤劳”的方法就是在屏幕上显示25*80的空格。

除了这个，还有什么方法呢？对了，BIOS中断。它山之石，可以攻玉。

2.滚屏与清除窗口调用

```
功能号：06H/07H 
用　途：窗口内容向上/向下滚动 
参　数：AL＝要滚动的行数（若是0将清窗口） 
BH＝填入新行的属性 
CH＝滚动窗口的左上角行号 
CL＝滚动窗口的左上角列号 
DH＝滚动窗口的右下角行号 
DL＝滚动窗口的右下角列号 
调　用：INT 10H 
返　回：无
```

以上是从网上搜出来的。到底好用不好用呢？试试便知。

### 实验一 清除窗口

我们先不考虑滚屏的事情，先看看当AL=0的时候怎么清窗口。 
写个代码验证一下。

```assembly
    mov ax,cs
    mov ss,ax
    mov sp,0x7c00  ;set stack and sp

    mov ah,0x06
    mov al,0   ;清窗口

    mov ch,5   ;左上角的行号
    mov cl,5   ;左上角的列号
    mov dh,20  ;右下角的行号
    mov dl,74  ;右下角的行号
    mov bh,0x17;属性为蓝底白字
    int 0x10


@1:  
    jmp @1

    times 510-($-$$) db 0
    db 0x55,0xaa
```



上面这段代码的功能清除窗口。窗口的位置由左上角和右下角的坐标指定。 
运行结果如图： 

注意，因为没有可见字符，所以看不到白色的字，但是可以看到蓝底。

### 实验二 窗口滚动

为了展示窗口滚动的效果，刚才我们绘制的那个蓝色窗口肯定不行，一片蓝色，不利于观察。先写个代码，让每一行有不同的颜色。

```assembly
    mov ax,cs
    mov ss,ax
    mov sp,0x7c00  ;set stack and sp

    mov ah,0x06
    mov al,0

    mov ch,5  ;(5,5)
    mov cl,5
    mov dh,5  ;(5,74)
    mov dl,74
    mov bh,0x17 ;蓝底白字
    int 0x10

    mov ch,6  ;(6,5)
    mov cl,5
    mov dh,6  ;(6,74)
    mov dl,74
    mov bh,0x27 ;绿底白字
    int 0x10

    mov ch,7  ;(7,5)
    mov cl,5
    mov dh,7  ;(7,74)
    mov dl,74
    mov bh,0x37  ;青底白字
    int 0x10    

    mov ch,8  ;(8,5)
    mov cl,5
    mov dh,8  ;(8,74)
    mov dl,74
    mov bh,0x47  ;红底白字
    int 0x10

@1: jmp @1

    times 510-($-$$) db 0
    db 0x55,0xaa
```


上面的代码，相当于绘制了4道条纹。效果图如下： 


接下来，我们要添加代码，让这个窗口向上滚动。怎样能看到动态的滚动效果呢？我想到了键盘中断。 
以下指令用于从键盘读取一个按键：

```assembly
mov ah,0x00
int 0x16
```

当ah的内容是0x00时，执行int 0x16后，中断服务例程会监视键盘动作，当它返回时，会在寄存器al中存放按键的ASCII码。 
本实验我们不关心按下哪个键，只关心按键这个动作。当中断返回时，说明用户按键了，这时候我们让窗口上滚一行，这样4行就变成3行了，最底下这行我们用白色填充。然后再次调用键盘中断，当用户按键后继续上滚，依然用白色填充最下面一行……当按键4次后，窗口被完全滚出，看到的是4行白色。 
代码如下：

```assembly
    mov ax,cs
    mov ss,ax
    mov sp,0x7c00  ;set stack and sp

    mov ah,0x06
    mov al,0

    mov ch,5  ;(5,5)
    mov cl,5
    mov dh,5  ;(5,74)
    mov dl,74
    mov bh,0x17 ;蓝底白字
    int 0x10

    mov ch,6  ;(6,5)
    mov cl,5
    mov dh,6  ;(6,74)
    mov dl,74
    mov bh,0x27 ;绿底白字
    int 0x10

    mov ch,7  ;(7,5)
    mov cl,5
    mov dh,7  ;(7,74)
    mov dl,74
    mov bh,0x37  ;青底白字
    int 0x10    

    mov ch,8  ;(8,5)
    mov cl,5
    mov dh,8  ;(8,74)
    mov dl,74
    mov bh,0x47  ;红底白字
    int 0x10

@1:
    mov ah,0x00
    int 0x16   ; wait for press key

    mov ah,0x06
    mov al,1

    mov ch,5
    mov cl,5
    mov dh,8
    mov dl,74
    mov bh,0x77
    int 0x10

    jmp @1

    times 510-($-$$) db 0
    db 0x55,0xaa
```


当按键2次后，执行效果如下图： 

### 实验三 清屏

搞明白了以上的实验，我们终于可以明明白白地清屏了。只需要令AL=0，并且把区域的坐标设置为（0，0）（24，79）即可。

```assembly
    mov ax,cs
    mov ss,ax
    mov sp,0x7c00  ;set stack and sp

    mov ah,0x06
    mov al,0

    mov ch,0  ;(0,0)
    mov cl,0
    mov dh,24  ;(24,79)
    mov dl,79
    mov bh,0x07 ;黑底白字
    int 0x10


@1:

    jmp @1

    times 510-($-$$) db 0
    db 0x55,0xaa
```


执行效果如下图 

果然一片漆黑啊。

### 3.重置显示模式

一般的显卡能支持多种显示模式（比如大体上可以分为文本模式和图形模式），程序需要使用何种显示模式是可以选择的。这里需要再补充一点，那就是当程序设定显示模式后，BIOS中断服务程序会自动的清除整个屏幕，这样看来重置显示模式也是一种清屏方法。 
设置显示模式可以使用10H中断的0号功能：

```
功能号：00H 
用　途：设置显示模式 
参　数：AL = 显示模式号 
调　用：INT 10H 
返　回：无
```

对于AL的取值，我搜到的一部分是

```
AL=00H：40x25黑白文本方式 
AL=01H：40x25彩色文本方式 
AL=02H：80x25黑白文本方式 
AL=03H：80x25彩色文本方式 
……
```

由于历史原因，所有在个人计算机上使用的显卡，在加电自检之后都会把自己显示模式设为3，也就是80*25的彩色文本模式。所以如果编程重新选择显示模式3，那么就会有”清屏”的效果而且显示模式没有改变。 
代码如下，哈哈，只需要3行代码哦。

### 实验四 利用重置显示模式来清屏

```assembly
    mov ax,cs
    mov ss,ax
    mov sp,0x7c00  ;set stack and sp

    mov ah,0x00
    mov al,0x03
    int 0x10

@1: 
    jmp @1

    times 510-($-$$) db 0
    db 0x55,0xaa
```

结果如下 

如果仔细观察，我们会发现，和实验三不同的是光标被设置到了0行0列。







