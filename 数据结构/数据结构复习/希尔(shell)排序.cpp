#include<stdio.h>
#include<malloc.h>

void ShellSort(int *arr,int n){
    for(int d = n / 2;d > 0;d /= 2){
        for(int i = d,j;i < n;i++){
            int e = arr[i];
            for(j = i;j >= d;j -= d){
                if(arr[j - d] > e) arr[j] = arr[j - d];
                else break;
            }
            arr[j] = e;
        }
        for(int i = 0;i < n;i++) printf("%d ",arr[i]);
        printf("\n");
    }
}

int main() {
    int n,*arr;
    scanf("%d",&n);
    arr = (int *)malloc(sizeof(int)*n);
    for(int i = 0; i < n; i++) scanf("%d",&arr[i]);
    ShellSort(arr,n);
}
