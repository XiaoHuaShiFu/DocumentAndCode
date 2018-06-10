#include<malloc.h>
#include<stdio.h>
#include<iostream>
using namespace std;
#define OK 1
#define ERROR 0
#define STACK_INIT_SIZE 100 // 存储空间初始分配量
#define STACKINCREMENT 10 // 存储空间分配增量

typedef int SElemType; // 定义栈元素类型
typedef int Status; // Status是函数的类型,其值是函数结果状态代码，如OK等

struct SqStack {
    SElemType *base; // 在栈构造之前和销毁之后，base的值为NULL
    SElemType *top; // 栈顶指针
    int stacksize; // 当前已分配的存储空间，以元素为单位
}; // 顺序栈

Status InitStack(SqStack &S) {
// 构造一个空栈S，该栈预定义大小为STACK_INIT_SIZE
// 请补全代码
    S.base = new int[STACK_INIT_SIZE];
    S.stacksize = STACK_INIT_SIZE;
    S.top = S.base;
    return 1;
}

Status Push(SqStack &S,SElemType e) {
// 在栈S中插入元素e为新的栈顶元素
// 请补全代码
    *S.top = e;
    S.top++;
    return 1;
}

Status Pop(SqStack &S,SElemType &e) {
// 若栈不空，则删除S的栈顶元素，用e返回其值，并返回OK；否则返回ERROR
// 请补全代码
    if(S.top == S.base) return 0;
    e =   *(S.top - 1);
    S.top--;
    return 1;
}

Status GetTop(SqStack S,SElemType &e) {
// 若栈不空，则用e返回S的栈顶元素，并返回OK；否则返回ERROR
// 请补全代码
    e = * (S.top - 1);
    return 1;
}

int StackLength(SqStack S) {
// 返回栈S的元素个数
// 请补全代码
    return S.top - S.base;
}

Status StackTraverse(SqStack S) {
// 从栈顶到栈底依次输出栈中的每个元素
    SElemType *p = (SElemType *)malloc(sizeof(SElemType));
    p = S.top;       //请填空
    if(!StackLength(S))printf("The Stack is Empty!"); //请填空
    else {
        printf("The Stack is: ");
        p--;
        while(p >= S.base) {          //请填空
            printf("%d ", *p);
            p--;               //请填空
        }
    }
    printf("\n");
    return OK;
}

int main() {
    int a;
    SqStack S;
    SElemType x, e;
    if(InitStack(S)) {  // 判断顺序表是否创建成功，请填空
        printf("A Stack Has Created.\n");
    }
    while(1) {
        printf("1:Push \n2:Pop \n3:Get the Top \n4:Return the Length of the Stack\n5:Load the Stack\n0:Exit\nPlease choose:\n");
        scanf("%d",&a);
        switch(a) {
        case 1:
            scanf("%d", &x);
            if(!Push(S,x)) printf("Push Error!\n"); // 判断Push是否合法，请填空
            else printf("The Element %d is Successfully Pushed!\n", x);
            break;
        case 2:
            if(!Pop(S,e)) printf("Pop Error!\n"); // 判断Pop是否合法，请填空
            else printf("The Element %d is Successfully Poped!\n", e);
            break;
        case 3:
            if(!GetTop(S,e))printf("Get Top Error!\n"); // 判断Get Top是否合法，请填空
            else printf("The Top Element is %d!\n", e);
            break;
        case 4:
            printf("The Length of the Stack is %d!\n",StackLength(S)); //请填空
            break;
        case 5:
            StackTraverse(S);  //请填空
            break;
        case 0:
            return 1;
        }
    }
}
