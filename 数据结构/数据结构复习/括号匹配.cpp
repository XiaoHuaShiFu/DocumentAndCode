typedef char SElemType;
#include"malloc.h"
#include"stdio.h"
#include"math.h"
#include"stdlib.h" // exit()
#include <iostream>
using namespace std;
#define OK 1
#define ERROR 0
#define TRUE 1
#define FALSE 0
typedef int Status; // Status是函数的类型,其值是函数结果状态代码，如OK等
#define STACK_INIT_SIZE 10 // 存储空间初始分配量
#define STACKINCREMENT 2 // 存储空间分配增量
struct SqStack {
    SElemType *base; // 在栈构造之前和销毁之后，base的值为NULL
    SElemType *top; // 栈顶指针
    int stacksize; // 当前已分配的存储空间，以元素为单位
}; // 顺序栈
Status InitStack(SqStack &S) {
    S.base = new char[STACK_INIT_SIZE];
    S.top = S.base;
    S.stacksize = 10;
    return 1;
}

Status StackEmpty(SqStack S) {
    return S.base == S.top ? 1 : 0;
}
Status Push(SqStack &S,SElemType e) {
    *S.top = e;
    S.top++;
    return 1;
}
Status Pop(SqStack &S,SElemType &e) {
    if(S.top == S.base ) return 0;
    e = *(S.top - 1);
    S.top--;
    return 1;
}
void check() {
    // 对于输入的任意一个字符串，检验括号是否配对
    SqStack s;
    SElemType ch[80],*p,e;
    if(InitStack(s)) { // 初始化栈成功
        //printf("请输入表达式\n");
        gets(ch);
        p=ch;
        while(*p) // 没到串尾
            switch(*p) {
            case '(':
            case '[':
                Push(s,*p);
                p++;
                break; // 左括号入栈，且p++
            case ')':
            case ']':
                if(!StackEmpty(s)) { // 栈不空
                    Pop(s,e); // 弹出栈顶元素
                    if(*p==')'&&e!='('||*p == ']'&&e!= '[')
                        // 弹出的栈顶元素与*p不配对
                    {
                        printf("isn't matched pairs\n");
                        exit(ERROR);
                    } else {
                        p++;
                        break; // 跳出switch语句
                    }
                } else { // 栈空
                    printf("lack of left parenthesis\n");
                    exit(ERROR);
                }
            default:
                p++; // 其它字符不处理，指针向后移
            }
        if(StackEmpty(s)) // 字符串结束时栈空
            printf("matching\n");
        else
            printf("lack of right parenthesis\n");
    }
}
int main() {
    check();
}
