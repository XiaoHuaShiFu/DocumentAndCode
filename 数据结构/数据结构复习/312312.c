#include <stdio.h>

int* search(int a[][4], int n) { //接收二维数组用a[][5]
    int* p = a[n];
    return p;
}

void main() {
    int a[][4]= {
        {91,92,93,94},
        {74,83,92,81},
        {34,56,77,88},
        {78,87,66,85}
    };
    int *p; //第n个学生在数组中的地址
    int n; //取出第n个学生
    scanf("%d", &n); //输入要取出第几个学生
    p = search(a, n);
    printf("the stduent is %d, %d, %d, %d", p[0], p[1], p[2], p[3]);
}
