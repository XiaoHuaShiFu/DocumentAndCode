#include <iostream>
using namespace std;

void adjust(int *arr,int l,int h) {
    int j = l * 2 + 1;
    while(j <= h) {
        if(arr[j] < arr[j + 1] && j + 1 <= h) j++;
        if(arr[j] > arr[l]) {
            int temp = arr[j];
            arr[j] = arr[l];
            arr[l] = temp;

        }
        l = j;
        j = l * 2 + 1;
    }
}

int main() {
    int n;
    cin >> n;
    int *arr = new int[n];
    for(int i = 0; i < n; i++) {
        cin >> arr[i];
    }
    for(int i = n / 2; i >= 0; i--) {
        adjust(arr,i,n - 1);
    }
    for(int i = 0; i < n; i++) {
        cout << arr[i] << " ";
    }
    cout << endl;
    for(int i = 1; i < n; i++) {
        int temp = arr[0];
        arr[0] = arr[n - i];
        arr[n - i] = temp;
        adjust(arr,0,n - i - 1);
        for(int j = 0; j < n; j++) {
            cout << arr[j] << " ";
        }
        cout << endl;
    }
}
