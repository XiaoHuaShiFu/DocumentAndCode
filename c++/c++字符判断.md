　　但实际上，C语言系统库函数提供了这一功能，我们大可不必重复造轮子——

#include<ctype.h>
// 是数字返回非零值
int isdigit(int ch);
1
2
3
　　类似的，ctype.h库函数中还提供了很多字符判断函数，我们对常用函数做一些总结。

ctype常用字符判断函数
isalnum函数
int isalnum(int ch);
1
检查给定字符是否为当前C语言环境下的字母数字字符，包括——

数字 0123456789
大写字母ABCDEFGHIJKLMNOPQRSTUVWXYZ
小写字母abcdefghijklmnopqrstuvwxyz
如果是，返回非零值，否则返回零值。

isalpha函数
int isalpha(int ch);
1
检查给定字符是否为当前C语言环境下的字母字符，包括——

大写字母ABCDEFGHIJKLMNOPQRSTUVWXYZ
小写字母abcdefghijklmnopqrstuvwxyz
如果是，返回非零值，否则返回零值。

islower函数
int islower(int ch);
1
检查给定字符是否为当前C语言环境下的小写字母字符，包括——

小写字母abcdefghijklmnopqrstuvwxyz
如果是，返回非零值，否则返回零值。

isupper函数
int isupper(int ch);
1
检查给定字符是否为当前C语言环境下的大写字母字符，包括——

大写字母ABCDEFGHIJKLMNOPQRSTUVWXYZ
如果是，返回非零值，否则返回零值。

isdigit函数
int isdigit(int ch);
1
检查给定字符是否为当前C语言环境下的数字字符，包括——

数字 0123456789
如果是，返回非零值，否则返回零值。

isxdigit函数
int isxdigit(int ch);
1
检查给定字符是否为当前C语言环境下的十六进制数字字符，包括——

十六进制数字 0123456789abcdefABCEDF
如果是，返回非零值，否则返回零值。

isspace函数
int isspace(int ch);
1
检查给定字符是否为当前C语言环境下的空白字符，包括——

空格（0x20）
换页（0x0c）
换行（0x0a）
回车（0x0d）
水平制表符（0x09）
垂直制表符（0x0b）
如果是，返回非零值，否则返回零值。

isblank函数
int isblank(int ch);
1
检查给定字符是否为当前C语言环境下的空格字符，包括——

空格（0x20）
水平制表符（0x09）
如果是，返回非零值，否则返回零值。

ispunct函数
int ispunct(int ch);
1
检查给定字符是否为当前C语言环境下的标点字符，包括——

!"#$%&’()*+,-./:;<=>?@[]^_`{|}~
如果是，返回非零值，否则返回零值。

isprint函数
int isprint(int ch);
1
检查给定字符是否为当前C语言环境下的可打印字符，包括——

数字 0123456789
大写字母 ABCDEFGHIJKLMNOPQRSTUVWXYZ
小写字母 abcdefghijklmnopqrstuvwxyz
标点 !"#$%&’()*+,-./:;<=>?@[]^_`{|}~
空格
如果是，返回非零值，否则返回零值。

isgraph函数
int isgraph(int ch);
1
检查给定字符是否为当前C语言环境下的图形字符，包括——

数字 0123456789
大写字母 ABCDEFGHIJKLMNOPQRSTUVWXYZ
小写字母 abcdefghijklmnopqrstuvwxyz
标点 !"#$%&’()*+,-./:;<=>?@[]^_`{|}~
如果是，返回非零值，否则返回零值。不难发现，就比isprint少了一个空格，空格确实不是一种图像（not graph）。

iscntrl函数
int iscntrl(int ch);
1
检查给定字符是否为当前C语言环境下的控制字符，控制字符是拥有编码 0x00-0x1F 和 0x7F 的字符。如果是，返回非零值，否则返回零值。

tolower函数
int tolower(int ch);
1
按照当前安装的 C 本地环境所定义的规则，转换给定字符为小写。即，以分别小写字母 ABCDEFGHIJKLMNOPQRSTUVWXYZ 替换大写字母abcdefghijklmnopqrstuvwxyz 。 如果ch不存在对应的小写版本，那么返回ch值。

toupper函数
int toupper(int ch);
1
按照当前安装的 C 本地环境所定义的规则，转换给定字符为大写。即，以大写字母 ABCDEFGHIJKLMNOPQRSTUVWXYZ 替换下列小写字母 abcdefghijklmnopqrstuvwxyz 。 如果ch不存在对应的大写版本，那么返回ch值。
————————————————
版权声明：本文为CSDN博主「6号楼下的大懒喵」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/CV_Jason/article/details/86101931