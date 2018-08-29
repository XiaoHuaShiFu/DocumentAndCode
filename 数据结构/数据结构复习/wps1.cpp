#include <stdio.h>



int main() {
   int a = 1,b = 2,c = 3;
   float s,v;
   s = (a * b + a * c + b * c) * 2;
   v = a * b * c;
   printf("%.2f",s / v);
}
