#include <iostream>
#include <algorithm>
using namespace std;

int Merge(int *arr,int l,int m,int r){
    int i = l,k = m + 1,*arr1 = new int[r - l + 1],j = 0;
    while(i <= m && k <= r){
        if(arr[i] < arr[k]) arr1[j++] = arr[i++];
        else arr1[j++] = arr[k++];
    }
    while(i <= m){
        arr1[j++] = arr[i++];
    }
    while(k <= r){
        arr1[j++] = arr[k++];
    }
    for(int i = l,j = 0;i <= r;i++,j++) arr[i] = arr1[j];

}

void MSort(int *arr,int n){
    int z = 2,i;
    while(z < n){
        for(int i = 0,j = z;j <= n;i += z,j += z){
            int m = (i + j - 1) / 2;
            Merge(arr,i,m,j - 1);
        }
        z *= 2;
        for(int j = 0 ;j < n ;j++ ) cout << arr[j] << " ";
        cout << endl;
    }
    if(z >= n){
        Merge(arr,0,z / 2 - 1,n-1);
        for(int j = 0 ;j < n ;j++ ) cout << arr[j] << " ";
        cout << endl;
    }
}

int main(){
    int n,*arr;
    cin >> n;
    arr = new int[n];
    for(int i = 0;i < n ;i++ ) cin >> arr[i];
    MSort(arr,n);
}
