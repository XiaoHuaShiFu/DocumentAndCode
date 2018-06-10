#include "stdio.h"
#include "malloc.h"
#include "string.h"
#include <iostream>
using namespace std;
int main(){
    int n,*arr;
    cin >> n;
    arr = new int[n];
    for(int i = 0;i < n; i++) cin >> arr[i];
    cout << "The List is:" ;
    for(int i = 0;i < n; i++) cout << arr[i] << " ";
    cout << endl << "The turned List is:" ;
    for(int i = n - 1;i >= 0;i--) cout << arr[i] << " ";
}
