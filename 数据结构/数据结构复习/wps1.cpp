#include <iostream>
using namespace std;


int main() {
   int a,b,c;
   float s,v;
   cin >> a >> b >> c;
   s = (a * b + a * c + b * c) * 2;
   v = a * b * c;
   printf("%.2f",s / v);
}
