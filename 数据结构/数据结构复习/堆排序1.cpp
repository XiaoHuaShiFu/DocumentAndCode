#include <iostream>
#include <algorithm>
using namespace std;

void Adjust(int *arr,int i,int n) {
    int j = i * 2 + 1;
    while(j < n) {
        if(arr[j] < arr[j+1] && j + 1 < n) j++;
        if(arr[i] > arr[j]) break;
        swap(arr[i],arr[j]);
        i = j;
        j = i * 2 + 1;
    }
}

void HeapSort(int *arr,int n) {
    for(int i = n / 2 - 1; i >= 0; i--) Adjust(arr,i,n);
    for(int j = 0; j < n; j++) cout << arr[j] << " ";
    cout << endl;
    for(int i = n - 1; i > 0 ; i--) {
        swap(arr[i],arr[0]);
        Adjust(arr,0,i);
        for(int j = 0; j < n; j++) cout << arr[j] << " ";
        cout << endl;
    }
}


int main() {
    int n,*arr;
    cin >> n;
    arr = new int[n];
    for(int i = 0; i < n; i++) cin >> arr[i];
    HeapSort(arr,n);
}
