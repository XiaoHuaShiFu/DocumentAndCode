#include<stdio.h>
#include<malloc.h>

int main(){
    int n,*arr;
    scanf("%d",&n);
    arr = (int *)malloc(sizeof(int)*(n + 1));
    for(int i = 1;i <= n;i++) scanf("%d",&arr[i]);
    for(int i = 2;i <= n;i++){
        arr[0] = arr[i];
        int l,m,h;
        l = 1;h = i;
        while(l <= h){
            m = (l + h) / 2;
            if(arr[m] >= arr[0]) h = m - 1;
            else l = m + 1;
        }
        for(int j = i; j > l;j--) arr[j] = arr[j - 1];
        arr[l] = arr[0];
        for(int j = 1; j <= n;j++) printf("%d ",arr[j]);
        printf("\n");
    }
}
