#include "stdio.h"
#include "stdlib.h"
#include "iostream.h"
#define  MAXSTRLEN  255                   // 用户可在255以内定义最大串长
typedef unsigned char SString[MAXSTRLEN+1];	// 0号单元存放串的长度

void get_next(SString T,int next[]){
// 算法4.7
// 求模式串T的next函数值并存入数组next
   // 请补全代码




}
void main(){
int next[MAXSTRLEN];
SString S;
int n,i,j;
char ch;
scanf("%d",&n);    // 指定要验证NEXT值的字符串个数
ch=getchar();
for(i=1;i<=n;i++)
{
ch=getchar();
for(j=1;j<=MAXSTRLEN&&(ch!='\n');j++)    // 录入字符串
{
S[j]=ch;
ch=getchar();
}
S[0]=j-1;    // S[0]用于存储字符串中字符个数
get_next(S,next);
printf("NEXT J is:");
for(j=1;j<=S[0];j++)
printf("%d",next[j]);
printf("\n");
}
}
