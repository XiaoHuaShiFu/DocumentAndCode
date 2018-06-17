#include<iostream>
using namespace std;
#include<algorithm>

void InsertSort(int *arr,int l,int h){
    for(int i = l + 1;i <= h;i++){
        int k = i;
        for(int j = i - 1;j >= l;j--){
            if(arr[j]>arr[k]) swap(arr[j],arr[k--]);
        }
    }
}

int guibing(int *arr,int n){
    int gap = 2;
    while(gap < n){
        for(int i = gap;i <= n;i += gap){
            InsertSort(arr,i - gap,i - 1);
        }
        gap *= 2;
        for(int i = 0;i < n;i++) cout << arr[i] << " ";
        cout << endl;
    }
    InsertSort(arr,0,n - 1);
    for(int i = 0;i < n;i++) cout << arr[i] << " ";
    cout << endl;
}

int main(){
    int n;
    cin >> n;
    int arr[n];
    for(int i = 0;i < n;i++) cin >> arr[i];
    guibing(arr,n);
}
