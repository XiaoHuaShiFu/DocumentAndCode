#include<stdio.h>
#include<malloc.h>
#include <math.h>

typedef struct TNode{
    int data;
    struct TNode *left,*right;
}TNode,*Tree;

void searchNode(Tree &T,int e){
    if(!T){
        T = (Tree)malloc(sizeof(TNode));
        T -> data = e;
        T -> left = T -> right = NULL;
        return ;
    }
    if(T -> data > e) searchNode(T -> left,e);
    else searchNode(T -> right,e);
}

void CreateTree(Tree &T,int n){
    T = (Tree)malloc(sizeof(TNode));
    scanf("%d",&T -> data);
    T -> left = T -> right = NULL;
    for(int i = 1,e;i < n;i++){
        scanf("%d",&e);
        searchNode(T,e);
    }
}

void PreTraverse(Tree T){
    if(!T) return ;
    printf("%d ",T -> data);
    PreTraverse(T -> left);
    PreTraverse(T -> right);
}

void MidTraverse(Tree T){
    if(!T) return ;
    MidTraverse(T -> left);
    printf("%d ",T -> data);
    MidTraverse(T -> right);
}

void PostTraverse(Tree T){
    if(!T) return ;
    PostTraverse(T -> left);
    PostTraverse(T -> right);
    printf("%d ",T -> data);
}

int GetLevel(Tree T){
    if(!T) return 1;
    return GetLevel(T -> left) > GetLevel(T -> right) ? GetLevel(T -> left) + 1 : GetLevel(T -> right) + 1;
}

int LayerTraverse(Tree T){//层次遍历
    Tree *ArrTree;
    int Level = GetLevel(T) - 1;
    ArrTree = (Tree*)malloc(sizeof(TNode)*pow(2,Level));//Level层最多2^Level - 1个结点
    ArrTree[0] = T;
    for(int i = 1,l = 0,r = 0,count = 1;i <= Level;i++){
        int COUNT = 0;
        for(int j = 0;j < count ;j++){
            printf("%d ",ArrTree[l] -> data);
            if(ArrTree[l] -> left){
                 ArrTree[++r] = ArrTree[l] -> left;
                 COUNT++;
            }
            if(ArrTree[l] -> right){
                 ArrTree[++r] = ArrTree[l] -> right;
                 COUNT++;
            }
            l++;
        }
        count = COUNT;
    }
}

void TurnNode(Tree &T){
    if(!T) return ;
    Tree TempNode = (Tree)malloc(sizeof(TNode));
    TempNode = T -> left;
    T -> left = T -> right;
    T -> right = TempNode;
    TurnNode(T -> left);
    TurnNode(T -> right);
}

int search(Tree T,int &e){ //搜索关键字是否存在
    if(!T) return 0;
    else if(e == T -> data) return 1;
    else if(e < T -> data) search(T -> left,e);
    else search(T -> right,e);
}

int GetNode(Tree T){
    if(!T) return 0;
    if(!T -> left && !T -> right) return 1;
    return GetNode(T -> left) + GetNode(T -> right);
}

int main(){
    int n,e;
    Tree T;
    scanf("%d",&n);
    CreateTree(T,n);
    PreTraverse(T);
    printf("\n");
    MidTraverse(T);
    printf("\n");
    PostTraverse(T);
    printf("\n");
    scanf("%d",&e);
    printf("%d\n",search(T,e));
    scanf("%d",&e);
    printf("%d\n",search(T,e));
    scanf("%d",&e);
    searchNode(T,e);
    PreTraverse(T);
    printf("\n");
    MidTraverse(T);
    printf("\n");
    PostTraverse(T);
    printf("\n");
    MidTraverse(T);
    printf("\n");
    LayerTraverse(T);
    printf("\n");
    TurnNode(T);
    PreTraverse(T);
    printf("\n");
    MidTraverse(T);
    printf("\n");
    PostTraverse(T);
    printf("\n");
    TurnNode(T);
    PreTraverse(T);
    printf("\n");
    MidTraverse(T);
    printf("\n");
    PostTraverse(T);
    printf("\n");
    printf("%d\n",GetLevel(T) - 1);
    printf("%d\n",GetNode(T));

}
