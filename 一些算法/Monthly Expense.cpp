#include <cstdio>
#include <malloc.h>
int main(){
    int n,m,*arr,left = 0,right = 0;
    scanf("%d %d",&n,&m);
    arr = (int *)malloc(sizeof(int)*n);
    for(int i = 0;i < n;i++){
        scanf("%d",&arr[i]);
        left = left >= arr[i] ? left : arr[i];
        right += arr[i];
    }
    while(left < right){
        int mid = (left + right) / 2;
        int count = 0;
        for(int i = 0,SUM = 0;i < n;i++){
            SUM += arr[i];
            if(SUM > mid){
                SUM = arr[i];
                count++;
            }
        }
        if(count < m) right = mid;
        else left = mid + 1;
    }
    printf("%d",left);
}
