转载

# 一篇实用的Latex的入门教程

2018-08-07 16:26:39  更多



## 转载

## LaTeX新人教程，一小时从完全陌生到基本入门

作者 : [董楠](http://blog.renren.com/GetEntry.do?id=892726074&owner=237934623)

 

LaTeX新人教程，一小时从完全陌生到基本入门

by Nan

 

**对于真心渴望迅速上手LaTeX的人，可以只看正文。**

**曾经在缝缝补补中变得长长的“前言”被我丢到了正文后面当“后记”。**

**后记部分可以跳过不看或只看粗体。**

**原本这个版本的流程内容是30分钟。**

**后来应小伙伴呼声，加入了图片引用、公式引用和参考文献内容。**

**因此如果要走完这个流程的话，大致需要一个小时。**

 

=============================================

下面是教程正文。（正文最后应大家要求增添了附录）

**注：文章里的代码我自己测试是可以直接复制进winedt编译成功的。但是有朋友反馈说编译不成功，检查后的结果大概是在人人网发帖的时候，人人网自己神奇的排版系统自主“智能”增添了一些换行符制表符类似物。因此如果直接复制编译不成功的时候，请尝试手打一遍。代码内容已经十分精简，相信手打起来也不会有太大压力。**

 

**1.LaTeX软件的安装和使用** 
方法A（自助）：在MikTeX的官网下载免费的MikTeX编译包（150Mb）并安装。下载WinEdt（9.5Mb）（收费）或TexMaker（32Mb)（免费）等编辑界面软件并安装。 
方法B（打包）：在ctex.org下载ctex套装(203Mb或1.3Gb)（含MikTeX及WinEdt）

哈哈这一部分当然不包含在标题的一小时里。

新人不必纠结软件问题，随便什么软件随便什么版本只要下载下来能编译出pdf来就可以，先下载了装上来试试再说。我推荐winedt也是方便我在介绍按钮样子的时候可以统一描述。在真正开始跑步之前没有必要纠结该买NIKE跑鞋还是ADI跑鞋，跑起来再说。不要瞻前顾后，just try it.



**2.第一个文档** 
打开WinEdt，建立一个新文档，将以下内容复制进入文档中，保存，保存类型选择为UTF-8。 

```latex
\documentclass{article} 

\begin{document}  
   hello, world  
\end{document}
```

**在下拉菜单中选择XeLaTeX**

```latex
\documentclass{article}    
\author{My Name}    
\title{The Title} 

\begin{document}  
   \maketitle  
   hello, world % This is comment  
\end{document}
```

- %为本行右边所有内容被注释掉，在生成的pdf中不会显示。

- 块注释也有专门的语句，不过更方便的方式是选中一块区域点鼠标右键，点comment 

- maketitle使标题显示出来



**4.章节和段落** 
建立一个新文档，将以下内容复制进入文档中，保存，保存类型选择为UTF-8，编译并观察现象。 

```latex
\documentclass{article} 
   \title{Hello World} 

\begin{document}  
   \maketitle  
   \section{Hello China} China is in East Asia.  
     \subsection{Hello Beijing} Beijing is the capital of China.  
       \subsubsection{Hello Dongcheng District}  
         \paragraph{Tian'anmen Square}is in the center of Beijing  
           \subparagraph{Chairman Mao} is in the center of Tian'anmen Square  
       \subsection{Hello Guangzhou}  
         \paragraph{Sun Yat-sen University} is the best university in Guangzhou.  
\end{document}
```



**5.加入目录** 

```latex
\documentclass{article} 

\begin{document}  
   \tableofcontents  
   \section{Hello China} China is in East Asia.  
     \subsection{Hello Beijing} Beijing is the capital of China.  
       \subsubsection{Hello Dongcheng District}  
         \paragraph{Hello Tian'anmen Square}is in the center of Beijing  
           \subparagraph{Hello Chairman Mao} is in the center of Tian'anmen Square  
\end{document}
```



**7.数学公式** 
建立一个新文档，将以下内容复制进入文档中，保存，保存类型选择为UTF-8，编译并观察对比现象。 

\documentclass{article} 
   \usepackage{amsmath} 
   \usepackage{amssymb} 
\begin{document} 
   The Newton's second law is F=ma. 

   The Newton's second law is $F=ma$. 

   The Newton's second law is 
  

F=maF=ma



   The Newton's second law is 
  

F=maF=ma



   Greek Letters $\eta$ and $\mu$ 

   Fraction $\frac{a}{b}$ 

   Power $a^b$ 

   Subscript $a_b$ 

   Derivate $\frac{\partial y}{\partial t} $ 

   Vector $\vec{n}$ 

   Bold $\mathbf{n}$ 

   To time differential $\dot{F}$ 

   Matrix (lcr here means left, center or right for each column) 
  

[a1d444b22e555555c333f6][a1b22c333d444e555555f6]







{a+b=cd=e+f+g{a+b=cd=e+f+g



\end{document} 

具体细节可以自行搜索LaTeX的数学符号表或别人给的例子。

 

$...$是开启行内数学模式，用于和文本合在一起使用。

 

......

和

......

是另起一行居中开启数学模式。通常用起来差别不是很大，不过$$会修改默认的公式行间距，有时可能会对文章的整体效果有影响。

但是我最推荐使用的是equation环境，在之后的第9节中介绍。

 

有一些网站可以通过画图的方式来生成公式，有的编辑器集成了各种数学功能按钮。这对于公式需求少的人来说很方便，具体信息可以自己去搜索。但是如果你的文章中要出现大段的证明过程，就会发觉还是老老实实地google一篇latex数学符号表，然后不懂的去查代码，自己用手指敲来得干脆利索。再进阶一点，可以去搜一下有关LaTeX的自定义command的内容去看一下，在敲公式时能省很多力气。


**8.插入图片** 
将待插入的图片姑且先命名为figure1.jpg 
建立一个新文档，将以下内容复制进入文档中，保存，保存类型选择为UTF-8，放在和图片文件同一个文件夹里，编译并观察现象。 

\documentclass{article} 
   \usepackage{graphicx} 

\begin{document}  
   \includegraphics[width=4.00in,height=3.00in]{figure1.jpg}  
\end{document}\begin{document}     \includegraphics[width=4.00in,height=3.00in]{figure1.jpg}  \end{document}







 

**9.可以被引用的图片和公式**

接下来进阶一点，我们来做有图片名称，可以在文中被引用的插入图片，还有可以在文中被引用的公式。建立一个新文档，将以下内容复制进入文档中，保存，保存类型选择为UTF-8，放在和图片文件同一个文件夹里。编译方式可以选择XeLaTeX或PDFTeXify。如果选择XeLaTeX，那么需要编译两次。如果选择PDFTeXify，那么编译一次就可以。

 

\documentclass{article}
\usepackage{amsmath}
\usepackage{amssymb}
\usepackage{graphicx, subfig}
\usepackage{caption}
\begin{document}
  One image [???](https://blog.csdn.net/u014803202/article/details/50410748#)???.
   

\begin{figure}[!htbp] 
      \centering 
      \includegraphics[width = .8\textwidth]{image1.jpg} 
      \caption{example of one image} \label{one-img} 
    \end{figure}\begin{figure}[!htbp]       \centering       \includegraphics[width = .8\textwidth]{image1.jpg}       \caption{example of one image} \label{one-img}     \end{figure}



[???](https://blog.csdn.net/u014803202/article/details/50410748#)
[???](https://blog.csdn.net/u014803202/article/details/50410748#)
[???](https://blog.csdn.net/u014803202/article/details/50410748#)

\begin{figure}[!htbp] 
      \centering 
      \subfloat[first sub-image]{ 
        \includegraphics[width = .45\textwidth]{image1.jpg} 
        \label{sub1} 
      } 
      \qquad 
      \subfloat[second sub-image]{ 
        \includegraphics[width = .45\textwidth]{image2.jpg} 
        \label{sub2} 
      } 
      \caption{combined image}\label{img-together} 
    \end{figure}\begin{figure}[!htbp]       \centering       \subfloat[first sub-image]{         \includegraphics[width = .45\textwidth]{image1.jpg}         \label{sub1}       }       \qquad       \subfloat[second sub-image]{         \includegraphics[width = .45\textwidth]{image2.jpg}         \label{sub2}       }       \caption{combined image}\label{img-together}     \end{figure}



[???](https://blog.csdn.net/u014803202/article/details/50410748#)

a+b+c+d+e=fa+b+c+d+e=f




**9.简单表格** 建立一个新文档，将以下内容复制进入文档中，保存，保存类型选择为UTF-8，编译并观察对比现象。 





注意观察有无\hline和有无\begin{center}的区别。注意观察\begin{tabular}后的lcr的区别，分别是left对齐，center对齐和right对齐。                          

 

**10.制作参考文献**

建立一个新文档，把以下内容复制进入文档中，保存，保存文件名为references.bib，保存类型为UTF-8。这个文档专门用来存放参考文献的信息。

@article{rivero2001resistance,
title={Resistance to cold and heat stress: accumulation of phenolic compounds in tomato and watermelon plants},
author={Rivero, Rosa M and Ruiz, Juan M and Garc{\i}a, Pablo C and L{\'o}pez-Lefebre, Luis R and S{\'a}nchez, Esteban and Romero, Luis},
journal={Plant Science},
volume={160},
number={2},
pages={315--321},
year={2001},
publisher={Elsevier}
}

@article{gostout1992clinical,
title={The clinical and endoscopic spectrum of the watermelon stomach},
author={Gostout, Christopher J and Viggiano, Thomas R and Ahlquist, David A and Wang, Kenneth K and Larson, Mark V and Balm, Rita},
journal={Journal of clinical gastroenterology},
volume={15},
number={3},
pages={256--263},
year={1992},
publisher={LWW}
}

建立一个新文档，把以下内容复制进入文档中，保存在同一个文件夹里，保存类型为UTF-8。

\documentclass{article}
\usepackage[numbers]{natbib}

\begin{document} 
          One reference about watermelon \cite{gostout1992clinical}         
          Another reference about watermelon \cite{rivero2001resistance}         
          \bibliographystyle{plain}         
          \bibliography{references}         
        \end{document}\begin{document}           One reference about watermelon \cite{gostout1992clinical}                   Another reference about watermelon \cite{rivero2001resistance}                   \bibliographystyle{plain}                   \bibliography{references}                 \end{document}



编译有两种方式可以选择，选择自己喜欢的随便一种就可以。

比较简单的方式是选择编译方式为PDFTeXify编译一下就可以了。

如果选择编译方式为XeLaTeX，那么需要多两个步骤，需要先用XeLaTeX编译一次，然后再用BibTeX编译一次（按钮就是在XeLaTeX编译右边的那个字母B），接着再用XeLaTeX编译两次。（谢谢小伙伴助攻~~）

编译成功后观察并对比现象。

​        （获得参考文献的那些信息的方式，我是觉得有谷歌学术搜索就可以了，没有必要装这个那个文献管理工具的。只要谷歌学术上搜索文献，在搜索结果下面点“引用”，在弹出窗口里点“BibTeX”，再把新窗口里的这些信息复制粘贴到自己的references.bib里去就可以了。）

 

**11.结尾** 
到目前为止，你已经可以用LaTeX自带的article模板来书写一篇基本的论文框架了，至少你已经能够借助搜索然后复制粘贴这些命令例子来开始用LaTeX编辑了。 
在论文从框架到完整的过程中，必然还存在许多的细节问题，比如字体字号，比如图片拼合，比如复杂的表格等等。 
那些问题，就请咨询google吧。通常来说我们作为初学者会提出的问题，早就已经有许多的先辈们在网络上提过同样的问题了，看看别人的回答就可以。 
LaTeX在国内的普及率并不高，因此许多时候如果搜英文关键词，会获得更好的效果。


=============================== 
附录，有关我认为不是新手急需，但是的确比较有用的信息

 

**1.中文支持**

很多朋友给我留言希望我把中文支持部分加进这个基本入门的教程里。因为没有被人要求过，所以我自己没有用LaTeX写过中文的paper，只是偶尔拿来写写小说大纲。因此对于中文方面我知道的很少，也只能最简单地提一下。

中文支持部分，在前文中提到的[《**【自制】一份其实很短的 LaTeX 入门文档**》](http://rrurl.cn/6QsD6P)中有比较详细的介绍，大家可以点进去学习浏览。

 

 

曾经的LaTeX的中文支持是比较麻烦的一件事，但是现在使用MikTeX+WinEdt的中文支持非常容易。 
只需要把开头的\documentclass{atricle}换成\documentclass{ctexart}就可以了。 
如果是第一次使用ctexart的话，会自动下载和安装宏包和模板，之后就不会再下载了。 
例子参考如下： 
打开WinEdt，建立一个新文档，将以下内容复制进入文档中，保存，保存类型选择为UTF-8。

\documentclass[UTF8]{ctexart}

\begin{document}  
你好，世界  
\end{document}\begin{document}  你好，世界  \end{document}



有同学和我反映说直接copy这些代码生成的中文是乱码，的确这样的问题偶有发生。如果这样的话，不妨在windows左下角的开始菜单的程序列表里找到MikTeX的文件夹，用maintenance里面的package manager搜索和手动安装名为CJK的package，然后再试试能否成功编译出中文。


**2.宏包** 
\package{}就是在调用宏包，对计算机实在外行的同学姑且可以理解为工具箱。 
每一个宏包里都定义了一些专门的命令，通过这些命令可以实现对于一类对象（如数学公式等）的统一排版（如字号字形），或用来实现一些功能（如插入图片或制作复杂表格）。 
通常在\documentclass之后，在\begin{document}之前，将文章所需要涉及的宏包都罗列上。 
对于新人而言比较常用的宏包有 

编辑数学公式的宏包：\usepackage{amsmath}和 \usepackage{amssymb} 
编辑数学定理和证明过程的宏包：\usepackage{amsthm} 
插入图片的宏包：\usepackage{graphicx} 
复杂表格的宏包：\usepackage{multirow} 

差不多了，对于新人来说，这五个宏包已经基本够用了。如果有其他的特殊需求，就通过google去寻找吧。 
补充说明一下，ctexart模板里已集成了CJK宏包。  


**3.模板** 
模板就是在\documentclass{}后面的大括号里的内容。 
在这一份教程中，我们使用的是LaTeX默认自带的模板article，以及中文模板ctexart。 
模板就是实现我之前所介绍的LaTeX的经验总结的第二点的实现方式。 
一篇文章，我们定义了section，定义了paragraph，就是没有定义字体字号，因为字体字号这一部分通常来说是在模板中实现的。 
一个模板可以规定，section这个层级都用什么字体什么字号怎么对齐，subsection这个层级用什么字体什么字号怎么对齐，paragraph又用什么字体什么字号怎么对齐。 
当然模板里还可以包含一些自定义的口令，以及页眉页脚页边距一类的页面设置。 
由于模板的使用，在我的使用经验里来看，绝对不可能算是基本入门级的内容，所以在正文里当然不会提及。 
如果有人实在想学，如果LaTeX已经接触到这个程度上了，那么再去翻其他厚一些的教材，也不亏了。



**4.制作幻灯片**

有关用LaTeX做幻灯片，我和几个精通LaTeX的朋友聊到最后共识趋于一点，就是除非真的对LaTeX实在太有爱，除非内容涉及到太多太多的数学公式，否则还是用powerpoint吧。毕竟有导师要求论文必须用LaTeX做，但是没导师要求presentation也必须用LaTeX做。选择LaTeX做幻灯片，对比一下其学习和使用成本，和能做出的超出powerpoint的美观程度，性价比不是很高。

 

 

=========================================

**下面是原先的“前言”，但是因为太长了太过啰嗦，所以我就把它放在教程正文的后面变成了“后记”。**

**很有闲情的可以看完，略有闲情的可以只看黑体，没有闲情的就略过不看吧。**

 

 

这是一篇面向对LaTeX完全无认知无基础的新人的入门教程。 
这一篇文章中，我追求的是极致的简短和实用。 
我希望能够帮助新人能够用最简单快捷的方式，轻松入门，能够迅速使用LaTeX完成基本的文本编辑。 
在我初学LaTeX时，我自己有着很强烈的感受，对于新人来说，LaTeX其实不缺少长篇的系统论述的manual，但是缺少简短的step by step的一个example接一个example的有操作价值的tutorial。 
我想大多数人接触LaTeX的原因都和我一样，只是论文需要，并不是有多么想去当一个杂志编辑。

**因此这一篇tutorial的起点为零，终点到满足写一个proposal就为止了。**

**同时这一篇tutorial的内容只涉及信息的撰写和录入，不涉及排版美化。**

我提倡的是新人们先开始跟着这个教程用LaTeX来写起来，在把内容放进去之后，遇到怎么让版面更加规范美观的问题的时候，可以从容地去翻manual或者问google。

这篇教程中涉及的以及被我有意过滤掉的LaTeX的功能，都是我仔细斟酌过的，我确保文章的内容对于新人来说完全够用。 
从proposal到paper当然还有一点距离，最重要的台阶是模板的应用，其次是做参考文献。 
不过有了这篇文章垫底，至少能用LaTeX编辑点东西了，也就不怕了，单独去google需要的部分的教材就可以了。

**那么我个人对于即将接触LaTeX的新人的教材建议是，先从这一篇出发，掌握这一篇里的内容之后，就可以开始着手撰写和编辑自己的LaTeX文本了，比如自己的proposal或者论文的提纲，在写的过程中遇到的大部分格式和中文支持问题，都可以在我的朋友写的《【自制】一份其实很短的 LaTeX 入门文档》中找到答案。而更多的问题，可以参考在我当初学LaTeX入门时认为最简短有指导意义有操作价值的《一份不太短的LaTeX介绍》，那一篇教材里基本就涵盖了以写paper为目的全部LaTeX功能需求了。**

另外感谢朋友留言提醒了我另外一篇当初在我入门时对我帮助非常大的教程，它名字很简单朴素叫做《LaTeX notes》。 这个note和《不太短的》都是内容合理实用，没有多余的废话，没有职业编辑才可能用到的高端内容，而且充满了清爽的examples的教程。我也要强烈推荐出来。它可以通过搜索“latex notes 包老师”获得。我写的这一篇教程，从一定意义上说，可以算是那两个简短教程的再简短的节选。因为这篇文章中的内容，就是当我在初学LaTeX的第一天，看着这两篇教程学会的，当时认为我最需要的技能。因此我将这些技能拿出来，带上我安排和精简过的例子，单独架构成精简再精简的入门教程，用来帮助新人打破在接触一个新事物最初始的“动手壁垒”。

而对于其他一些manual或厚的教材，如果只是为了写paper的话，是没有必要专门去读的，拿来当百科全书或字典，需要的时候查阅就好了。

（为什么那么多人在回复里都一定要提lshort？诚然lshort是经典教材，但是难道lshort本文没有提及吗？难道lshort不就是《The Not So Short Introduction to LaTeX》吗？难道不就是《一份不太短的LaTeX介绍》的英文版本吗？既然有中文的，也很清晰明了，毫无拙劣翻译拖后腿，却一定要拿一个英文版本来说事儿，那么这很显然除了装逼还能是什么呢？拜托实用一点好吧。）

 

 

 


**先用三句话来介绍什么是LaTeX，以下三点基于我个人写assignment report和写论文的主观经验的总结。** 
1.LaTeX是一类用于编辑和排版的软件，用于生成PDF文档。 
2.LaTeX编辑和排版的核心思想在于，通过\section和\paragraph等语句，规定了每一句话在文章中所从属的层次，从而极大方便了对各个层次批量处理。 
3.LaTeX在使用体验方面，最不易被Word替代的有四个方面：方便美观的数学公式编辑、不会乱动的退格对齐、非所见即所得因此可以在编辑的时候用退格和换行整理思路但生成PDF出来不影响美观、部分导师和刊物不接受Word排版的文章。

 

（这一段为看到部分留言后一时冲动写下的，因为很多人转，而且也的确是在说明我的态度，因此保留原样摆在这里） 
我要严厉警告和强烈声讨那些自以为是advanced LaTeX user的人。请你们不要为了自己那一点可怜可悲的虚荣心，去刻意渲染LaTeX有多么高端多么不容易学习，这和孔乙己炫耀茴香豆的茴字有四种写法有什么区别么？混账！LaTeX到底有多“难”你们自己清楚好么？同理的还有很大一部分喜欢炫耀上手难度而非实用趣味的VI user，还有Linux user，还有Fallout player。卧槽，一个软件而已，有什么好显摆的。别人想学LaTeX，好，你随手给人家丢一个几百页的英文Manual，显得自己很高端吗？你自己看过了吗？你推荐给别人的时候真的有希望别人看完吗？只是装逼的话就是混账！

 

 

我考虑了很久，最后还是决定不将这些examples的代码注释完全，而是希望读者在对比了tex代码和生成的pdf内容之后，自己得出结论。这些例子都是我自己推敲安排过的，如果想学LaTeX的话，自己总要动点脑筋才好。

 

 

写完之后很多人分享很多人回复，我很开心也很感激。文章总是在改，更正了很多信息，比如图片的插入和中文支持的问题。然而更多时候却不知不觉陷入迷途，文章开始背离面对新人说话的初衷，而开始越来越多地对一些老人去解释。居然形成了如此之长的前言，实在是有违我的本意。最终我将前言删减到现在这个样子，第一段是文章来由和闲谈，第二段是我的经验观点，第三段为看到一些留言之后一时冲动写下的，因为很多人转，而且也的确是在说明我的态度，因此保留原样，第四段是对一些朋友愿望的回应。那么前言到此结束。之后如果没有被告知文章内有重大错误，便不会再编辑修改了。诸君好运。



```latex
\usepackage{ctex} %中文宏

% 构建命令,取别名，使用degree 代替 ^ circ
\newcommand\degree{^\circ}
```



```latex1
1. 使用\ 表示空格

以及调整空格的大小

quad空格	a \qquad b		两个m的宽度
quad空格	a \quad b		一个m的宽度
大空格	a\ b		1/3m宽度
中等空格	a\;b		2/7m宽度
小空格	a\,b		1/6m宽度
没有空格	ab		 
紧贴	a\!b		缩进1/6m宽度
 

\quad、1em、em、m代表当前字体下接近字符‘M’的宽度。

2.使用\\ 表示换行
```

### 1、段落整理

通常书籍是用等长的行来排版的。为了优化整个段落的内容，LaTex在单词之间插入必要的断行点（linebreak）和间隔。如果一行的单词排不下，LaTex也会进行必要的断字。段落如何排版依赖于文档类别。通常，每一段的第一行有缩进，在两段之间没有额外的间隔。

在特殊情形下，有必要命令 LaTex断行：

![img](https://img-blog.csdn.net/20180814084831299?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0dvZF82OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

另起一行，而不另起一段。

![img](https://img-blog.csdn.net/20180814084859347?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0dvZF82OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

在强行断行后，还禁止分页。

![img](https://img-blog.csdn.net/20180814084916992?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0dvZF82OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

另起一新页。

![img](https://img-blog.csdn.net/20180814084934881?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0dvZF82OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

上述命令的效果可以从它们的名称看出来。通过可选参数 n，作者可以影响这些命令的效果。n 可以置为 0 和 4 之间的数。如果命令的效果看起来非常差，把 n 取为小于 4 的数，可以让 LaTex选择忽略这个命令。不要这些“break” 命令与 “new” 命令混淆。即使你给出了 “break” 命令，LaTex仍然试图对齐页面的右边界。如果你真想另起一行，就使用相应的命令。猜猜该是什么命令！

LaTex总是尽可能产生最好的断行效果。如果断行无法达到 LaTex 的高标准，就让这一行在段落的右侧溢出。然后在处理输入文件的同时，报告溢出的消息（“overfull hbox”）。这最可能发生在 LaTex找不到合适的地方断字时候。(注)你可以使用 **\sloppy** 命令，告诉 LaTex降低一点儿标准。虽然最终的输出结果不是最优的，它通过增加单词之间的间隔，以防止出现过长的行。在这种情况下给出警告（“underfull hbox”）。在大多数情况下得到的结果看起来不会非常好。**\fussy** 命令把 LaTex恢复为缺省状态。

注：当发生（盒子溢出）时，虽然 LaTex给出一个警告并显示溢出的那一行，但是不太容易发现溢出的行。如果你在 \documentclass 命令中使用选项 draft，LaTex就在溢出行的右边标以粗黑线。 

###  2、断字

 必要时就会出现断字。如果断字算法不能确定正确的断字点，可以使用如下命令告诉Tex如何弥补这个缺憾。

 命令：

![img](https://img-blog.csdn.net/2018081408523750?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0dvZF82OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

 使列于参量中的单词仅在注有 “-” 的点断字。命令的参量仅由正常字母构成的单词，或由激活文本中视为正常字母的符号组成。应用于（特殊）语言的已存好，当断字命令出现时，就为激活的语言储存断字可选点。这意味着如果你在文档导言中设置了断字命令，它将影响英文的断字。如果断字命令置于 \begin{document} 后面，而且你正使用类似 babel 的国际语言支持宏包，那么断字可选点在由 babel 激活的语言中就处于活动状态。

 下面的例子允许对 “hyphenation” 和 “Hyphenation” 进行断字，却根本不允许 “FORTRAN”, “Fortran” 和 “fortran” 进行断字。在参量中不允许出现特殊的字符和符号。

例子：

![img](https://img-blog.csdn.net/20180814085331629?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0dvZF82OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

命令 \- 在单词中插入一个自主的断字点。它也就成为这个单词中允许出现的唯一断字点。对于包含特殊字符（注音字符）的单词，这个命令是特别有用的，因为对于包含特殊字符的单词 LaTex不自动断字。

![img](https://img-blog.csdn.net/20180814085409114?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0dvZF82OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

 命令：

![img](https://img-blog.csdn.net/20180814085712569?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0dvZF82OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

 保证把几个单词排在同一行上。在任何情况下，这个命令把它的参量排在一起（同一行上）。

![img](https://img-blog.csdn.net/20180814085734484?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0dvZF82OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

 命令 **\fbox** 和 **\mbox** 类似，此外它还能围绕内容画一个框。



转载

# LaTeX常用表格

2018-09-08 20:44:03  更多



### 表格内自动换行

```
\begin{table}  \Large  \caption{自动换行}  \begin{center}  \begin{tabular}{|l|l|l|l| p{5cm}|}  \hline  Item & Name & Gender & Habit & Self-introduction \\ \hline  1 & Jimmy & Male & Badminton & Hi, everyone,my name is Jimmy. I come from Hamilton,  and it's my great honour to give this example. My topic is about how to use p{width} command \\ \hline  2 & Jimmy & Male & Badminton & Hi, everyone,my name is Jimmy. I come from Hamilton,  and it's my great honour to give this example. My topic is about how to use p{width} command \\  \hline  \end{tabular}  \end{center}  \end{table}
```

\begin{tabular}{|l|l|l|l| p{5cm}|}设置最后一列最大是5cm,超出部分要换行。

![img](https://img-blog.csdn.net/20141107131607487?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMjE3NjU5MQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

### 设置表格中某列的宽度

```
\begin{table}  \caption{设置宽度}  \begin{tabularx}{12cm}{lXl}  \hline  Start & End  & Character Block Name \\  \hline  3400  & 4DB5 & CJK Unified Ideographs Extension A \\  4E00  & 9FFF & CJK Unified Ideographs \\  \hline  \end{tabularx}  \end{table}
```

引入包: \usepackage{tabularx}

![img](https://img-blog.csdn.net/20141107132249016?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMjE3NjU5MQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

### 设置任一列宽

```
\begin{table}[h] %开始一个表格environment，表格的位置是h,here。\caption{改变表格任一列宽} %显示表格的标题\begin{tabular}{p{3.5cm}|p{2cm}|p{5cm}} %设置了每一列的宽度，强制转换。\hline\hlineFormat & Extension & Description \\ %用&来分隔单元格的内容 \\表示进入下一行\hline %画一个横线，下面的就都是一样了，这里一共有4行内容Bitmap & .bmp & Bitmap images are recommended because they offer the most control over the exact image and colors.\\\hlineGraphics Interchange Format (GIF) & .gif & Compressed image format used for Web pages. Animated GIFs are supported.\\\hlineJoint Photographic Experts Group (JPEG) & .jpeg, .jpg & Compressed image format used for Web pages.\\\hlinePortable Network Graphics (PNG) & .png & Compressed image format used for Web pages.\\\hline\hline\end{tabular}\end{table}
```

![img](https://img-blog.csdn.net/20141107163942296?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMjE3NjU5MQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

### 水平居中显示

```
\renewcommand{\arraystretch}{1.5} %¿ØÖÆ±í¸ñÐÐ¸ßµÄËõ·Å±ÈÀý\begin{table}[tp]   \centering  \fontsize{6.5}{8}\selectfont  \caption{Demographic Prediction performance comparison by three evaluation metrics.}  \label{tab:performance_comparison}    \begin{tabular}{|c|c|c|c|c|c|c|}    \hline    \multirow{2}{*}{Method}&    \multicolumn{3}{c|}{C}&\multicolumn{3}{c|}{ D}\cr\cline{2-7}    &Precision&Recall&F1-Measure&Precision&Recall&F1-Measure\cr    \hline    \hline    A&0.7324&0.7388&0.7301&0.6371&0.6462&0.6568\cr\hline   B&0.7321&0.7385&0.7323&0.6363&0.6462&0.6559\cr\hline    C&0.7321&0.7222&0.7311&0.6243&0.6227&0.6570\cr\hline    D&0.7654&0.7716&0.7699&0.6695&0.6684&0.6642\cr\hline    E&0.7435&0.7317&0.7343&0.6386&0.6488&0.6435\cr\hline    F&0.7667&0.7644&0.7646&0.6609&0.6687&0.6574\cr\hline    G&{\bf 0.8189}&{\bf 0.8139}&{\bf 0.8146}&{\bf 0.6971}&{\bf 0.6904}&{\bf 0.6935}\cr    \hline    \end{tabular}\end{table}
```

![img](https://img-blog.csdn.net/20151129191232744?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

### 三线法:

```
\usepackage{booktabs}\usepackage{threeparttable} \renewcommand{\arraystretch}{1.5} %控制行高\begin{table}[tp]   \centering  \fontsize{6.5}{8}\selectfont  \begin{threeparttable}  \caption{Demographic Prediction performance comparison by three evaluation metrics.}  \label{tab:performance_comparison}    \begin{tabular}{ccccccc}    \toprule    \multirow{2}{*}{Method}&    \multicolumn{3}{c}{ G}&\multicolumn{3}{c}{ G}\cr    \cmidrule(lr){2-4} \cmidrule(lr){5-7}    &Precision&Recall&F1-Measure&Precision&Recall&F1-Measure\cr    \midrule    kNN&0.7324&0.7388&0.7301&0.6371&0.6462&0.6568\cr    F&0.7321&0.7385&0.7323&0.6363&0.6462&0.6559\cr    E&0.7321&0.7222&0.7311&0.6243&0.6227&0.6570\cr    D&0.7654&0.7716&0.7699&0.6695&0.6684&0.6642\cr    C&0.7435&0.7317&0.7343&0.6386&0.6488&0.6435\cr    B&0.7667&0.7644&0.7646&0.6609&0.6687&0.6574\cr    A&{\bf 0.8189}&{\bf 0.8139}&{\bf 0.8146}&{\bf 0.6971}&{\bf 0.6904}&{\bf 0.6935}\cr    \bottomrule    \end{tabular}    \end{threeparttable}\end{table}
```

![img](https://img-blog.csdn.net/20151129201511719?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

表格生成网址:[http://www.tablesgenerator.com](http://www.tablesgenerator.com/)