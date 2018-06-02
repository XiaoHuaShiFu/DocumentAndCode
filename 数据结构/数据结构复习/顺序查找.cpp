#include<stdio.h>
#include<malloc.h>

int main(){
    int n,*arr,e;
    scanf("%d",&n);
    arr = (int*)malloc(sizeof(int)*n);
    for(int i = 0;i < n;i++) scanf("%d",&arr[i]);
    scanf("%d",&e);
    int idx;
    for(idx = 0;idx < n;idx++) if(arr[idx] == e) {
        printf("The element position is %d.",idx + 1);
        break;
    }
    if(idx == n) printf("The element is not exist.\n");
}
