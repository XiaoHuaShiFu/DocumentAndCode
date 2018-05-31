#include<stdio.h>
#include<malloc.h>


int GetPos(int num,int pos){
    for(int i = 1;i < pos;i++) num /= 10;
    return num % 10;
}

int main(){
    int n,*arr,max = 0,weishu = 1;
    scanf("%d",&n);
    arr = (int *)malloc(sizeof(int)*n);
    for(int i = 0;i < n;i++){
        scanf("%d",&arr[i]);
        if(arr[i] > max) max = arr[i];
    }
    while(max /= 10) weishu++;
    printf("%d",GetPos(123,1));
}
