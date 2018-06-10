#include<malloc.h>
#include<stdio.h>
#include<stdlib.h>
#include <iostream>
using namespace std;
#define OK 1
#define ERROR 0
typedef int Status; // Status�Ǻ���������,��ֵ�Ǻ������״̬���룬��OK��
typedef int QElemType;
#define MAXQSIZE 100 // �����г���(����ѭ�����У������г���Ҫ��1)

typedef struct
{
   QElemType *base; // ��ʼ���Ķ�̬����洢�ռ�
   int front; // ͷָ��,�����в���,ָ�����ͷԪ��
   int rear; // βָ��,�����в���,ָ�����βԪ�ص���һ��λ��
 }SqQueue;

Status InitQueue(SqQueue &Q)
{
// ����һ���ն���Q���ö���Ԥ�����СΪMAXQSIZE
	Q.base=(QElemType*)malloc(MAXQSIZE*sizeof(QElemType));
	if(!Q.base) exit(1);
	Q.rear=Q.front=0;
	return OK;
}

Status EnQueue(SqQueue &Q,QElemType e)
{
// ����Ԫ��eΪQ���µĶ�βԪ��
	 if((Q.rear+1)%MAXQSIZE==Q.front) return ERROR;
	 Q.base[Q.rear]=e;
	 Q.rear=(Q.rear+1)%MAXQSIZE;
	 return OK;
}

Status DeQueue(SqQueue &Q, QElemType &e)
{
// �����в���, ��ɾ��Q�Ķ�ͷԪ��, ��e������ֵ, ������OK; ���򷵻�ERROR
	 if(Q.front==Q.rear) return ERROR;
	 e=Q.base[Q.front];
	 Q.front=(Q.front+1)%MAXQSIZE;
	 return OK;
}

Status GetHead(SqQueue Q, QElemType &e)
{
// �����в��գ�����e���ض�ͷԪ�أ�������OK�����򷵻�ERROR
	if(Q.front==Q.rear) return ERROR;
	e=Q.base[Q.front];
	return OK;
}

int QueueLength(SqQueue Q)
{
// ����Q��Ԫ�ظ���
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
