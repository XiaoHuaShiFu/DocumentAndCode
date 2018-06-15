#include<stdio.h>
#include<malloc.h>
int main(){
    int **tu,n,m;
    scanf("%d %d",&n,&m);
    tu = (int **)malloc(sizeof(int *)*n);
    for(int i = 0;i < n;i++) tu[i] = (int *)malloc(sizeof(int) * n);
    for(int i = 0;i < n;i++) for(int j = 0; j < n;j++) tu[i][j] = 0;
    for(int i = 0;i < m;i++){
        int a,b;
        scanf("%d %d",&a,&b);
        tu[a - 1][b - 1] = 1;
    }
    for(int i = 0;i < n;i++){
        for(int j = 0; j < n;j++) printf("%d ",tu[i][j]);
        printf("\n");
    }
}
