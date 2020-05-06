#include <iostream>
#include <vector>
#include <string>
#include <algorithm>
using namespace std;


const int MOD = 100000007;
const int P = 1000019;

long long hash0(string s) {
    int H = 0;
    for (char c : s) {
        H = (H * P + (c - 'a')) % MOD;
    }
    return H;
}

    vector<int> ans;
int main() {
    string str;
    ans.push_back(hash0("ccc"));

    ans.push_back(hash0("aaaa"));
    ans.push_back(hash0("aaaa"));
    sort(ans.begin(), ans.end());
    int result = 0;
    for (int i = 0; i < ans.size(); i++) {
        if (i == 0 || ans[i - 1] != ans[i]) {
            result++;
        }
    }
    cout << result << endl;
    return 0;
}
