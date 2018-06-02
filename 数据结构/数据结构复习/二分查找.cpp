#include<stdio.h>
#include<malloc.h>

void Search_Bin(int *arr,int n,int e){
    int l,m,h;
    l = 0;h = n - 1;
    while(l <= h){
        m = (l + h) / 2;
        if(arr[m] == e){
            printf("The element position is %d.",m);
            return ;
        }
        else if(arr[m] > e) h = m - 1;
        else l = m + 1;
    }
    printf("The element is not exist.");
}

int main(){
    int n,*arr,e;
    scanf("%d",&n);
    arr = (int *)malloc(sizeof(int)*n);
    for(int i = 0;i < n;i++) scanf("%d",&arr[i]);
    scanf("%d",&e);
    Search_Bin(arr,n,e);
}
