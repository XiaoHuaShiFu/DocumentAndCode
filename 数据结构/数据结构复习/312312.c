#include <stdio.h>

int* search(int a[][4], int n) { //���ն�ά������a[][5]
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
    int *p; //��n��ѧ���������еĵ�ַ
    int n; //ȡ����n��ѧ��
    scanf("%d", &n); //����Ҫȡ���ڼ���ѧ��
    p = search(a, n);
    printf("the stduent is %d, %d, %d, %d", p[0], p[1], p[2], p[3]);
}
