#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>

void maopao (int a[], int n) {
    int i;
    int j;
    int t;
    for (i = 1; i < n; i++) {
        for (j = 0; j < n - i; j++) {
            if (a[j] > a[j + 1]) {
                t = a[j];
                a[j] = a[j + 1];
                a[j + 1] = t;
            }
        }
    }
}

void swap(int *a, int *b) {
    int t = *a;
    *a = *b;
    *b = t;
}

//    FILE *fp;
//    char c;
//    if ((fp = fopen("case.in", "r")) == NULL) {
//        return ;
//    }
//    while ((c = fgetc(fp)) != EOF)

struct student *reverse(struct student *head) {
    struct student *p1, *p2, *p3;
    head = NULL;

}

void main() {

}
