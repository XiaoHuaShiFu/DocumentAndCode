#include <malloc.h>
#include <iostream>
using namespace std;
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
    int bf;
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
    Q.front = Q.rear = new QNode;
    if(!Q.front) return 0;
    Q.front->next = NULL;
    return 1;
}

//入队，成功返回1，失败返回0
Status EnQueue( LinkQueue &Q , BiTree e ) {
    QueuePtr P;
    P = new QNode;
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
    S.base = new BiTree[100];
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

//初始化二叉树，成功返回1，失败返回0
Status InitBST( BiTree &T , int e ) {
    T = new BiTNode;
    T->data = e;
    T->lchild=T->rchild=NULL;
    return TRUE;
}

//输出元素e的值
Status PrintElement( ElemType e ) {
    cout << e << " ";
    return OK;
}

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
}

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
    Visit(P -> data);
    if(P -> lchild) {
        EnQueue(Q,P -> lchild);
    }
    if(P -> rchild) {
        EnQueue(Q,P -> rchild);
    }
    while(DeQueue(Q,P) != 0) {
        Visit(P -> data);
        if(P -> lchild) {
            EnQueue(Q,P -> lchild);
        }
        if(P -> rchild) {
            EnQueue(Q,P -> rchild);
        }
    }
}

//交换左右结点
void ExchangeBiTNode( BiTree &T ) {
    BiTree P;
    P = T -> lchild;
    T -> lchild = T -> rchild;
    T -> rchild = P;
    if(T -> lchild) {
        ExchangeBiTNode(T -> lchild);
    }
    if(T -> rchild) {
        ExchangeBiTNode(T -> rchild);
    }
}

//树深，返回树深
Status BiTreeHigh( BiTree T ) {
    int High = 0;
    if(T != NULL) {
        int LH = BiTreeHigh(T -> lchild);
        int RH = BiTreeHigh(T -> rchild);
        High = LH >= RH ? LH + 1 : RH + 1;
    }
    return High;
}

//叶子节点数，返回叶子结点树
Status BiTNodeNumber( BiTree T , int &Node ) {
    if(T != NULL) {
        if(!(T -> lchild || T -> rchild)) {
            Node++;
        }
        BiTNodeNumber(T -> lchild,Node);
        BiTNodeNumber(T -> rchild,Node);
    }
    return Node;
}

//右旋
void RR(BiTree &T) {
    BiTree T1;
    T1 = T -> lchild;
    T -> lchild = T1 -> rchild;
    T1 -> rchild = T;
    T = T1;
}

//左旋
void LR(BiTree &T) {
    BiTree T1;
    T1 = T -> rchild;
    T -> rchild = T1 -> lchild;
    T1 -> lchild = T;
    T = T1;
}

//右平衡
void RightBalance(BiTree &T) {
    BiTree R,rl;
    R = T -> rchild;
    switch (R -> bf) {
        case -1:
            T -> bf = R -> bf = 0;
            LR(T);
            break;
        case 0:
            T -> bf = -1;
            R -> bf = 1;
            LR(T);
            break;
        case 1:
            rl = R -> lchild;
            switch (rl -> bf) {
                case 0:
                    T -> bf = R -> bf = 0;
                    break;
                case -1:
                    R -> bf = 0;
                    T -> bf = 1;
                    break;
                case 1:
                    R -> bf = -1;
                    T -> bf = 0;
                    break;
                default:
                    break;
            }
            rl -> bf = 0;
            RR(T -> rchild);
            LR(T);
            break;
    }
}

//左平衡
void LeftBalance(BiTree &T) {
    BiTree L,lr;
    L = T -> lchild;
    switch (L -> bf) {
        case 0:
            L -> bf = -1;
            T -> bf = 1;
            RR(T);
            break;
        case 1:
            L -> bf = T -> bf = 0;
            RR(T);
            break;
        case -1:
            lr = L -> rchild;
            switch (lr -> bf) {
                case 0:
                    L -> bf = L -> bf = 0;
                case -1:
                    T -> bf = 0;
                    L -> bf = 1;
                    break;
                case 1:
                    L -> bf = 0;
                    T -> bf = -1;
                    break;
                default:
                    break;
            }
            lr -> bf = 0;
            LR(T -> lchild);
            RR(T);
            break;
        default:
            break;
    }
}

//插入结点
bool InsertNode(BiTree &T,int key,bool &taller) {
    if(!T) {
        T = new BiTNode;
        T -> bf = 0;
        T -> lchild = T -> rchild = NULL;
        T -> data = key;
        taller = true;
        return true;
    } else {
        if(key == T -> data) {
            taller = false;
            return false;
        }
        if(key < T -> data) {
            if(!InsertNode(T -> lchild,key,taller))
                return false;
            if(taller) {
                switch (T -> bf) {
                    case 0:
                        T -> bf = 1;
                        taller = true;
                        break;
                    case 1:
                        LeftBalance(T);
                        taller = false;
                        break;
                    case -1:
                        T -> bf = 0;
                        taller = false;
                        break;
                    default:
                        break;
                }
            }
        } else {
            if(!InsertNode(T -> rchild,key,taller))
                return false;
            if(taller) {
                switch (T -> bf) {
                    case 0:
                        T -> bf = -1;
                        taller = true;
                        break;
                    case 1:
                        T -> bf = 0;
                        taller = false;
                        break;
                    case -1:
                        RightBalance(T);
                        taller = false;
                        break;
                    default:
                        break;
                }
            }
        }


    }
}

//删除结点
bool DeleteNode(BiTree &T,int key,bool &lower) {
    bool L,R;
    L = R = false;
    if(T == NULL)
        return false;
    if(key == T -> data) {
        BiTNode* p,*s;
        p = T -> rchild;
        s = p;
        lower = true;
        if(T -> rchild == NULL) {
            p = T;
            T = T -> lchild;
            free(p);
            lower = true;
            return true;
        } else {
            while (s) {
                p = s;
                s = s -> lchild;
            }
            T -> data = p -> data;
            DeleteNode(T -> rchild,p -> data,lower);
            R = true;
        }
    } else if(key < T -> data) {
        DeleteNode(T -> lchild,key,lower);
        L = true;
    } else {
        DeleteNode(T -> rchild,key,lower);
        R = true;
    }
    if(lower) {
        if(L) {
            switch (T -> bf) {
                case 1:
                    T -> bf = 0;
                    lower = true;
                    break;
                case -1:
                    RightBalance(T);
                    lower = false;
                    break;
                case 0:
                    T -> bf = -1;
                    lower = false;
                    break;
                default:
                    break;
            }
        } else {
            switch (T -> bf) {
                case 0:
                    T -> bf = 1;
                    lower = false;
                    break;
                case -1:
                    T -> bf = 0;
                    lower = true;
                    break;
                case 1:
                    LeftBalance(T);
                    lower = false;
                    break;
                default:
                    break;
            }
        }
    }
}

int main() {
    int n,e,Node = 0;
    bool taller;
    BiTree T;
    BiTree P;
    cin >> n;
    cin >> e;
    InitBST(T,e);


    for(int i = 1; i < n; i++) {
        cin >> e;
        InsertNode(T,e,taller);
    }


    cout << "先序遍历" << endl;
    PreOrderTraverse(T,PrintElement);
    cout << endl << "中序遍历" << endl;
    InOrderTraverse(T,PrintElement);
    cout << endl << "后序遍历" << endl;
    PostOrderTraverse(T,PrintElement);
    cout << endl;

    cout << "搜索结点" << endl;
    cin >> e;
    cout << SearchBST(T,e,NULL,P) << endl;


    cout << "插入结点后前中后序遍历" << endl;
    cin >> e;
    InsertNode(T,e,taller);
    cout << "先序遍历" << endl;
    PreOrderTraverse(T,PrintElement);
    cout << endl << "先序遍历非递归" << endl;
    PreOrder(T,PrintElement);
    cout << endl << "中序遍历" << endl;
    InOrderTraverse(T,PrintElement);
    cout << endl << "中序遍历非递归" << endl;
    InOrder(T,PrintElement);
    cout << endl << "后序遍历" << endl;
    PostOrderTraverse(T,PrintElement);
    cout << endl << "后序遍历非递归" << endl;
    PostOrder(T,PrintElement);


    cout << endl << "删除结点后前中后序遍历" << endl;
    cin >> e;
    DeleteNode(T,e,taller);
    cout << "先序遍历" << endl;
    PreOrderTraverse(T,PrintElement);
    cout << endl << "中序遍历" << endl;
    InOrderTraverse(T,PrintElement);
    cout << endl << "后序遍历" << endl;
    PostOrderTraverse(T,PrintElement);


    cout << endl << "层次遍历" << endl;
    ccTraverse(T,PrintElement);


    cout << endl << "交换左右结点后前中后先遍历" << endl;
    ExchangeBiTNode(T);
    cout << "先序遍历" << endl;
    PreOrderTraverse(T,PrintElement);
    cout << endl << "中序遍历" << endl;
    InOrderTraverse(T,PrintElement);
    cout << endl << "后序遍历" << endl;
    PostOrderTraverse(T,PrintElement);


    cout << endl << "再次交换左右结点后前中后先遍历" << endl;
    ExchangeBiTNode(T);
    cout << "先序遍历" << endl;
    PreOrderTraverse(T,PrintElement);
    cout << endl << "中序遍历" << endl;
    InOrderTraverse(T,PrintElement);
    cout << endl << "后序遍历" << endl;
    PostOrderTraverse(T,PrintElement);
    cout << endl;


    cout << "树高" << endl << BiTreeHigh(T) << endl;
    cout << "叶子结点数" << endl << BiTNodeNumber(T,Node) << endl;

}
