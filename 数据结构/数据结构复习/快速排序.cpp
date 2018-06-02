#include<stdio.h>
#include<malloc.h>

int Partition(int *arr,int n,int l,int h){
    int e = arr[l];
    while(l < h){
        while(l < h && arr[h] >= e) h--;
        arr[l] = arr[h];
        while(l < h && arr[l] <= e) l++;
        arr[h] = arr[l];
    }
    arr[l] = e;
    for(int i = 0;i < n;i++) printf("%d ",arr[i]);
    printf("\n");
    return l;
}

void QuickSort(int *arr,int n,int l,int h){
    if(l < h){
        int position = Partition(arr,n,l,h);
        QuickSort(arr,n,l,position - 1);
        QuickSort(arr,n,position + 1,h);
    }
}


int main(){
    int n,*arr;
    scanf("%d",&n);
    arr = (int *)malloc(sizeof(int)*n);
    for(int i = 0;i < n;i++) scanf("%d",&arr[i]);
    QuickSort(arr,n,0,n-1);
}
