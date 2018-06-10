#include<malloc.h>
#include<stdio.h>
#include<stdlib.h>
#include <iostream>
using namespace std;
#define OK 1
#define ERROR 0
typedef int Status; // Status是函数的类型,其值是函数结果状态代码，如OK等
typedef int QElemType;
#define MAXQSIZE 100 // 最大队列长度(对于循环队列，最大队列长度要减1)

typedef struct
{
   QElemType *base; // 初始化的动态分配存储空间
   int front; // 头指针,若队列不空,指向队列头元素
   int rear; // 尾指针,若队列不空,指向队列尾元素的下一个位置
 }SqQueue;

Status InitQueue(SqQueue &Q)
{
// 构造一个空队列Q，该队列预定义大小为MAXQSIZE
	Q.base=(QElemType*)malloc(MAXQSIZE*sizeof(QElemType));
	if(!Q.base) exit(1);
	Q.rear=Q.front=0;
	return OK;
}

Status EnQueue(SqQueue &Q,QElemType e)
{
// 插入元素e为Q的新的队尾元素
	 if((Q.rear+1)%MAXQSIZE==Q.front) return ERROR;
	 Q.base[Q.rear]=e;
	 Q.rear=(Q.rear+1)%MAXQSIZE;
	 return OK;
}

Status DeQueue(SqQueue &Q, QElemType &e)
{
// 若队列不空, 则删除Q的队头元素, 用e返回其值, 并返回OK; 否则返回ERROR
	 if(Q.front==Q.rear) return ERROR;
	 e=Q.base[Q.front];
	 Q.front=(Q.front+1)%MAXQSIZE;
	 return OK;
}

Status GetHead(SqQueue Q, QElemType &e)
{
// 若队列不空，则用e返回队头元素，并返回OK，否则返回ERROR
	if(Q.front==Q.rear) return ERROR;
	e=Q.base[Q.front];
	return OK;
}

int QueueLength(SqQueue Q)
{
// 返回Q的元素个数
	return Q.rear%MAXQSIZE-Q.front%MAXQSIZE;
}

int main(){
    SqQueue Q1,Q2;
    InitQueue(Q1);
    InitQueue(Q2);
    float n,sum = 1,wait = 0;
    int a,b;
    cin >> n;
    for(int i = 0;i < n;i++){
        cin >> a >> b;
        EnQueue(Q1,a);
        EnQueue(Q2,b);
    }
    for(int i = 0;i < n;i++){
        DeQueue(Q1,a);
        DeQueue(Q2,b);
        if(a <= sum){
            wait += (sum - a);
        }else{
            sum = a;
        }
        sum += b;
    }
    printf("%.2f",wait / n);
}
