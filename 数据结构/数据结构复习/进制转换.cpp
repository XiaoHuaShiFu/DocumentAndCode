#include <iostream>
using namespace std;

int main(){
    int a,b[10],i = 0;
    cin >> a;
    while(a){
        b[i++] = a % 8;
        a = a / 8;
    }
    for(i--;i >= 0;i--) cout << b[i];
}
