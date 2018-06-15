#include <stdio.h>
#include <malloc.h>
#define TRUE 1
#define FALSE 0
#define OK  1
#define ERROR  0
#define INFEASIBLE -1
#define OVERFLOW -2
typedef int  Status;
typedef int  ElemType;


typedef struct BiTNode {
    ElemType data;
    struct BiTNode *lchild,*rchild;//左右孩子指针
} BiTNode,*BiTree;

typedef struct {
    BiTree *base;
    BiTree *top;
    int stacksize;
} Stack;

typedef struct QNode {
    BiTree data;
    struct QNode *next;
} QNode,*QueuePtr;
typedef struct {
    QueuePtr front;
    QueuePtr rear;
} LinkQueue;

//初始化队列，成功返回1，失败返回0
Status InitQueue( LinkQueue &Q ) {
    Q.front = Q.rear = (QueuePtr)malloc(sizeof(QNode));
    if(!Q.front) return 0;
    Q.front->next = NULL;
    return 1;
}

//入队，成功返回1，失败返回0
Status EnQueue( LinkQueue &Q , BiTree e ) {
    QueuePtr P;
    P = (QueuePtr)malloc(sizeof(QNode));
    if(!P) return 0;
    P->data = e;
    P->next = NULL;
    Q.rear->next = P;
    Q.rear = P;
    return 1;
}

//出队，成功返回1，失败返回0
Status DeQueue( LinkQueue &Q , BiTree &e ) {
    QueuePtr P;
    if(Q.front == Q.rear) return 0;
    P = Q.front->next;
    e = P->data;
    Q.front->next = P->next;
    if(Q.rear == P) Q.rear = Q.front;
    free(P);
    return 1;
}

//初始化栈，成功返回1，失败返回0
Status InitStack( Stack &S ) {
    S.base = (BiTree *)malloc(100 * sizeof(BiTree));
    if(!S.base) return 0;
    S.top = S.base;
    S.stacksize = 100;
    return 1;
}

//入栈，成功返回1，失败返回0
Status Push( Stack &S , BiTree e ) {
    *S.top++ = e;
    return 1;
}

//出栈，成功返回1，失败返回0
Status Pop( Stack &S , BiTree &e ) {
    if(S.top == S.base) return 0;
    e = *--S.top;
    return 1;
}

//判断树是否为空，空返回1，非空返回0
Status StackEmpty( Stack S ) {
    if(S.top == S.base) return 1;
    else return 0;
}

//搜索结点，成功返回1，失败返回0
Status SearchBST( BiTree T , int key , BiTree f , BiTree &p ) {
    if(!T) {
        p = f;
        return FALSE;
    } else if(key == (T->data)) {
        p = T;
        return TRUE;
    } else if(key < (T -> data)) {
        return SearchBST(T -> lchild,key,T,p);
    } else {
        return SearchBST(T -> rchild,key,T,p);
    }
}

//插入结点，成功返回1，失败返回0
Status InserBST( BiTree &T , ElemType e ) {
    BiTree p;
    BiTree s;
    if(!SearchBST(T,e,NULL,p)) {
        s = (BiTree)malloc(sizeof(BiTNode));
        s -> data = e;
        s -> lchild = NULL;
        s -> rchild = NULL;
        if(!p)T = s;//第一个节点
        else if(e < (p -> data)) {
            p -> lchild = s;
        } else {
            p -> rchild = s;
        }
        return TRUE;
    } else return FALSE;
}

//初始化二叉树，成功返回1，失败返回0
Status InitBST( BiTree &T , int e ) {
    T = (BiTree)malloc(sizeof(BiTNode));
    T->data = e;
    T->lchild=T->rchild=NULL;
    return TRUE;
}

//输出元素e的值
Status PrintElement( ElemType e ) {
    printf("%d ", e );
    return OK;
}// PrintElement

//非递归前序
void PreOrder( BiTree T , Status(*Visit)(ElemType) ) {
    Stack S;
    InitStack(S);
    while(T || !StackEmpty(S)) {
        if(T) {
            Visit(T -> data);
            Push(S,T -> rchild);
            Push(S,T -> lchild);
        }
        Pop(S,T);
    }
}

//递归前序
void PreOrderTraverse( BiTree T, Status(*Visit)(ElemType) ) {
    if(!T) return ;
    else {
        Visit(T -> data);
        PreOrderTraverse(T -> lchild,Visit);
        PreOrderTraverse(T -> rchild,Visit);
    }
} // PreOrderTrav

//非递归后序
void PostOrder( BiTree T, Status(*Visit)(ElemType) ) {
    Stack S;
    InitStack(S);
    BiTree p = T;
    Push(S,T);
    while(!StackEmpty(S)) {
        Pop(S,T);
        if( (!(T -> rchild) && !(T -> lchild)) || (T -> rchild == p)
                || (T -> lchild == p && T -> rchild == NULL) ) {
            Visit(T -> data);
            p = T;
        } else {
            if(T -> rchild) Push(S,T -> rchild);
            if(T -> lchild) Push(S,T -> lchild);
        }
    }
}

//递归后序
void PostOrderTraverse( BiTree T , Status(*Visit)(ElemType) ) {
    if(T) {
        PostOrderTraverse(T -> lchild,Visit);
        PostOrderTraverse(T -> rchild,Visit);
        Visit(T -> data);
    }
}

//非递归中序
void InOrderTraverse( BiTree T,Status (*Visit)(int e) ) {
    Stack S;
    InitStack(S);
    BiTree p = T;
    while(p || !StackEmpty(S)) {
        if(p) {
            Push(S,p);
            p = p -> lchild;
        } else {
            Pop(S,p);
            Visit(p -> data);
            p = p -> rchild;
        }
    }
}

//递归中序
void InOrder( BiTree T , Status (*Visit)(int e) ) {
    if(!T) {
        return ;
    } else {
        InOrder(T -> lchild,Visit);
        Visit(T -> data);
        InOrder(T -> rchild,Visit);
    }
}

//层次遍历二叉树
void ccTraverse( BiTree T , Status (*Visit)(int e) ) {
    LinkQueue Q;
    InitQueue(Q);
    BiTree P = T;
    Visit(P->data);
    if(P->lchild) {
        EnQueue(Q,P->lchild);
    }
    if(P->rchild) {
        EnQueue(Q,P->rchild);
    }
    while(DeQueue(Q,P)!=0) {
        Visit(P->data);
        if(P->lchild) {
            EnQueue(Q,P->lchild);
        }
        if(P->rchild) {
            EnQueue(Q,P->rchild);
        }
    }
}

//交换左右结点
void ExchangeBiTNode( BiTree &T ) {
    BiTree P;
    P = T->lchild;
    T->lchild = T->rchild;
    T->rchild = P;
    if(T->lchild) {
        ExchangeBiTNode(T->lchild);
    }
    if(T->rchild) {
        ExchangeBiTNode(T->rchild);
    }
}

//树深，返回树深
Status BiTreeHigh( BiTree T ) {
    int High = 0;
    if(T!=NULL) {
        int LH = BiTreeHigh(T->lchild);
        int RH = BiTreeHigh(T->rchild);
        High = LH >= RH ? LH+1 : RH+1;
    }
    return High;
}

//叶子节点数，返回叶子结点树
Status BiTNodeNumber( BiTree T , int &Node ) {
    if(T!=NULL) {
        if(!(T->lchild || T->rchild)) {
            Node++;
        }
        BiTNodeNumber(T->lchild,Node);
        BiTNodeNumber(T->rchild,Node);
    }
    return Node;
}

//删除结点
void DeleteNode( BiTree &T ) {
    BiTree q, s;
    if( !T -> lchild && !T -> rchild ) T = NULL;
    else if( !T -> lchild ) {
        q = T;
        T = T->rchild;
    } else if( !T -> rchild ) {
        q = T;
        T = T -> lchild;
    } else {
        q = T;
        s = T -> lchild;
        while(s -> rchild) {
            q = s;
            s = s -> rchild;
        }
        T -> data = s -> data;
        if( q != T ) q -> rchild = s -> lchild;
        else q -> lchild = s -> lchild;
    }
}

//删除结点
void DeleteBST( BiTree &T , int key ) {
    if( !T ) return ;
    else {
        if( key == T -> data ) DeleteNode(T);
        else if( key < T -> data) return DeleteBST(T -> lchild, key);
        else return DeleteBST(T -> rchild, key);
    }
}

int main() {
    int n,i,e,Node = 0;
    BiTree T;
    BiTree P;
    scanf("%d",&n);
    scanf("%d",&e);
    InitBST(T,e);
    for(i = 1; i < n; i++) {
        scanf("%d",&e);
        InserBST(T,e);
    }


    printf("先序遍历\n");
    PreOrderTraverse(T,PrintElement);
    printf("\n");
    printf("中序遍历\n");
    InOrderTraverse(T,PrintElement);
    printf("\n");
    printf("后序遍历\n");
    PostOrderTraverse(T,PrintElement);
    printf("\n");


    printf("搜索结点\n");
    scanf("%d",&e);
    printf("%d\n",SearchBST(T,e,NULL,P));


    printf("插入结点后前中后序遍历\n");
    scanf("%d",&e);
    InserBST(T,e);
    printf("先序遍历\n");
    PreOrderTraverse(T,PrintElement);
    printf("\n");
    printf("先序遍历非递归\n");
    PreOrder(T,PrintElement);
    printf("\n");
    printf("中序遍历\n");
    InOrderTraverse(T,PrintElement);
    printf("\n");
    printf("中序遍历非递归\n");
    InOrder(T,PrintElement);
    printf("\n");
    printf("后序遍历\n");
    PostOrderTraverse(T,PrintElement);
    printf("\n");
    printf("后序遍历非递归\n");
    PostOrder(T,PrintElement);
    printf("\n");


    printf("删除结点后前中后序遍历\n");
    scanf("%d",&e);
    DeleteBST(T,e);
    printf("先序遍历\n");
    PreOrderTraverse(T,PrintElement);
    printf("\n");
    printf("中序遍历\n");
    InOrderTraverse(T,PrintElement);
    printf("\n");
    printf("后序遍历\n");
    PostOrderTraverse(T,PrintElement);
    printf("\n");


    printf("层次遍历\n");
    ccTraverse(T,PrintElement);
    printf("\n");


    printf("交换左右结点后前中后先遍历\n");
    ExchangeBiTNode(T);
    printf("先序遍历\n");
    PreOrderTraverse(T,PrintElement);
    printf("\n");
    printf("中序遍历\n");
    InOrderTraverse(T,PrintElement);
    printf("\n");
    printf("后序遍历\n");
    PostOrderTraverse(T,PrintElement);
    printf("\n");


    printf("再次交换左右结点后前中后先遍历\n");
    ExchangeBiTNode(T);
    printf("先序遍历\n");
    PreOrderTraverse(T,PrintElement);
    printf("\n");
    printf("中序遍历\n");
    InOrderTraverse(T,PrintElement);
    printf("\n");
    printf("后序遍历\n");
    PostOrderTraverse(T,PrintElement);
    printf("\n");


    printf("树高\n");
    printf("%d",BiTreeHigh(T));
    printf("\n");
    printf("叶子结点树\n");
    printf("%d",BiTNodeNumber(T,Node));
    printf("\n");

}
