#include<stdio.h>
#include<malloc.h>
#include<stdlib.h>
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

int LayerTraverse(Tree T,int Level){
    if(!T || Level < 0) return 0;
    else printf("%d ",T -> data);
    LayerTraverse(T -> left,Level - 1);
    LayerTraverse(T -> right,Level - 1);
}


int search(Tree T,int &e){
    if(!T) return 0;
    else if(e == T -> data) return 1;
    else if(e < T -> data) search(T -> left,e);
    else search(T -> right,e);
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
    printf("%d\n",GetLevel(T));
    LayerTraverse(T,GetLevel(T));
    printf("\n");
}
