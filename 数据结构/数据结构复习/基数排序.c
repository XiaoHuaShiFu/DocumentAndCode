#include<stdio.h>
#include<malloc.h>

int GetPos(int num,int pos){
    for(int i = 1;i < pos;i++) num /= 10;
    return num % 10;
}

void BaseSort(int *arr,int n,int weishu){
    int **arr1;
    arr1 = (int **)malloc(sizeof(int *) * 10);
    for(int i = 0 ;i < 10;i++){
         arr1[i] = (int *)malloc(sizeof(int) * (n + 1));
         arr1[i][0] = 0;
    }
    for(int i = 0;i < weishu;i++){
        for(int j = 0;j < n;j++){
            int pos = GetPos(arr[j],i + 1);
            arr1[pos][++arr1[pos][0]] = arr[j];
        }
        int k = 0;
        for(int j = 0;j < 10;j++){
            for(int i = 1;i <= arr1[j][0];i++){
                arr[k++] = arr1[j][i];
                if(weishu == 1) printf("%d ",arr[k - 1]);
                else if(weishu == 2) printf("%02d ",arr[k - 1]);
                else if(weishu == 3) printf("%03d ",arr[k - 1]);
                else if(weishu == 4) printf("%04d ",arr[k - 1]);
                else printf("%05d ",arr[k - 1]);
            }
            arr1[j][0] = 0;
        }
        printf("\n");
    }
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
    BaseSort(arr,n,weishu);
}
