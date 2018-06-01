#include<stdio.h>
#include<malloc.h>

typedef struct TNode{
    int data;
    struct Tree lchild,rchild;
}TNode,*Tree;

int searchNode(Tree &T,int e){
    if(!T){

    }
    if(T -> data > e){
        searchNode(T -> lchild);
    }else {
        searchNode(T -> rchild);
    }
}

int CreateTree(Tree &T,int n){
    T = (Tree)malloc(sizeof(TNode));
    scanf("%d",T->data);
    T -> lchild = T -> rchild = NULL;
    for(int i = 1,e;i < n;i++){
        scanf("%d",&e);
        Tree Node; Node = (Tree)malloc(sizeof(TNode)); Node->data = e;
        searchNode(T,e);
    }
}


int main(){
    int n;
    scanf("%d",&n);
}
