#include<iostream>
#include<stdio.h>
using namespace std;

int GetGaowei(int e,int weishu,int m){
    for(int i = 1;i < weishu;i++) e /= 10;
    return e % 10;
}

void JishuSort(int *arr,int n,int m){
    int arr1[10][n + 1];
    for(int i = 0;i < 10;i++) arr1[i][0] = 0;
    for(int i = 1;i <= m;i++){
        for(int j = 0;j < n;j++){
            int gaoshu = GetGaowei(arr[j],i,m);
            arr1[gaoshu][0]++;
            arr1[gaoshu][arr1[gaoshu][0]] = arr[j];
        }
        for(int j = 0,k = 0;j < 10;j++){
            for(int z = 1;z <= arr1[j][0];z++) arr[k++] = arr1[j][z];

            arr1[j][0] = 0;
        }
        for(int j = 0;j < n;j++){
            if(m == 1) printf("%d ",arr[j]);
            else if(m == 2) printf("%02d ",arr[j]);
            else if(m == 3) printf("%03d ",arr[j]);
            else if(m == 4) printf("%04d ",arr[j]);
            else printf("%05d ",arr[j]);
        }
        cout << endl;
    }
}

int main(){
    int n,m = 1,max = 0;
    cin >> n;
    int arr[n];
    for(int i = 0;i < n;i++){
         cin >> arr[i];
         if(arr[i] > max) max = arr[i];
    }
    while(max/=10) m++;
    JishuSort(arr,n,m);
}
