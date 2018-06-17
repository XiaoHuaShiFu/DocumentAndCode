#include<iostream>
using namespace std;

int Partition(int *arr,int n,int l,int h){
    int e = arr[l];
    while(l < h){
        while(l < h && arr[h] >= e) h--;
        arr[l] = arr[h];
        while(l < h && arr[l] <= e) l++;
        arr[h] = arr[l];
    }
    arr[l] = e;
    for(int i = 0;i < n;i++) cout << arr[i] << " ";
    cout << endl;
    return l;
}

void QuickSort(int *arr,int n,int l,int h){
    if(l < h){
        int Position = Partition(arr,n,l,h);
        QuickSort(arr,n,l,Position - 1);
        QuickSort(arr,n,Position + 1,h);
    }
}

int main(){
    int n;
    cin >> n;
    int arr[n];
    for(int i = 0;i < n;i++) cin >> arr[i];
    QuickSort(arr,n,0,n-1);
}
