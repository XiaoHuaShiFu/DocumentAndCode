#include<iostream>
using namespace std;

void insertSort(int *arr,int n){
    for(int gap = n / 2;gap > 0;gap /= 2){
        for(int i = gap,j;i < n;i++){
            int e = arr[i];
            for(j = i;j >= gap;j -= gap){
                if(arr[j - gap] > e) arr[j] = arr[j - gap];
                else break;
            }
            arr[j] = e;
        }
        for(int i = 0;i < n;i++) cout << arr[i] << " ";
        cout << endl;
    }
}

int main() {
    int n,*arr;
    cin >> n;
    arr = new int[n];
    for(int i = 0; i < n; i++) cin >> arr[i];
    insertSort(arr,n);
}
