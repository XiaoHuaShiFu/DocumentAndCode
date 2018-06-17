#include<iostream>
using namespace std;
#include<algorithm>

void AdjustHeap(int *arr,int n,int i){
    int j = i * 2 + 1;
    while(j < n){
        if(arr[j] < arr[j + 1] && j + 1 < n) j++;
        if(arr[j] < arr[i]) break;
        swap(arr[i],arr[j]);
        i = j;
        j = i * 2 + 1;
    }
}

void HeapSort(int *arr,int n){
    for(int i = n / 2 - 1;i >= 0;i--) AdjustHeap(arr,n,i);
    for(int j = 0; j < n; j++) cout << arr[j] << " ";
    cout << endl;
    for(int i = n - 1;i > 0;i--){
        swap(arr[0],arr[i]);
        AdjustHeap(arr,i,0);
        for(int j = 0; j < n; j++) cout << arr[j] << " ";
        cout << endl;
    }
}

int main(){
    int n;
    cin >> n;
    int arr[n];
    for(int i = 0; i < n; i++) cin >> arr[i];
    HeapSort(arr,n);
}
