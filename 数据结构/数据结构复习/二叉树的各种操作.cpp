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
    struct BiTNode *lchild,*rchild;//���Һ���ָ��
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

//��ʼ�����У��ɹ�����1��ʧ�ܷ���0
Status InitQueue( LinkQueue &Q ) {
    Q.front = Q.rear = new QNode;
    if(!Q.front) return 0;
    Q.front->next = NULL;
    return 1;
}

//��ӣ��ɹ�����1��ʧ�ܷ���0
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

//���ӣ��ɹ�����1��ʧ�ܷ���0
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

//��ʼ��ջ���ɹ�����1��ʧ�ܷ���0
Status InitStack( Stack &S ) {
    S.base = new BiTree[100];
    if(!S.base) return 0;
    S.top = S.base;
    S.stacksize = 100;
    return 1;
}

//��ջ���ɹ�����1��ʧ�ܷ���0
Status Push( Stack &S , BiTree e ) {
    *S.top++ = e;
    return 1;
}

//��ջ���ɹ�����1��ʧ�ܷ���0
Status Pop( Stack &S , BiTree &e ) {
    if(S.top == S.base) return 0;
    e = *--S.top;
    return 1;
}

//�ж����Ƿ�Ϊ�գ��շ���1���ǿշ���0
Status StackEmpty( Stack S ) {
    if(S.top == S.base) return 1;
    else return 0;
}

//������㣬�ɹ�����1��ʧ�ܷ���0
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

//��ʼ�����������ɹ�����1��ʧ�ܷ���0
Status InitBST( BiTree &T , int e ) {
    T = new BiTNode;
    T->data = e;
    T->lchild=T->rchild=NULL;
    return TRUE;
}

//���Ԫ��e��ֵ
Status PrintElement( ElemType e ) {
    cout << e << " ";
    return OK;
}

//�ǵݹ�ǰ��
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

//�ݹ�ǰ��
void PreOrderTraverse( BiTree T, Status(*Visit)(ElemType) ) {
    if(!T) return ;
    else {
        Visit(T -> data);
        PreOrderTraverse(T -> lchild,Visit);
        PreOrderTraverse(T -> rchild,Visit);
    }
}

//�ǵݹ����
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

//�ݹ����
void PostOrderTraverse( BiTree T , Status(*Visit)(ElemType) ) {
    if(T) {
        PostOrderTraverse(T -> lchild,Visit);
        PostOrderTraverse(T -> rchild,Visit);
        Visit(T -> data);
    }
}

//�ǵݹ�����
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

//�ݹ�����
void InOrder( BiTree T , Status (*Visit)(int e) ) {
    if(!T) {
        return ;
    } else {
        InOrder(T -> lchild,Visit);
        Visit(T -> data);
        InOrder(T -> rchild,Visit);
    }
}

//��α���������
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

//�������ҽ��
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

//�����������
Status BiTreeHigh( BiTree T ) {
    int High = 0;
    if(T != NULL) {
        int LH = BiTreeHigh(T -> lchild);
        int RH = BiTreeHigh(T -> rchild);
        High = LH >= RH ? LH + 1 : RH + 1;
    }
    return High;
}

//Ҷ�ӽڵ���������Ҷ�ӽ����
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

//����
void RR(BiTree &T) {
    BiTree T1;
    T1 = T -> lchild;
    T -> lchild = T1 -> rchild;
    T1 -> rchild = T;
    T = T1;
}

//����
void LR(BiTree &T) {
    BiTree T1;
    T1 = T -> rchild;
    T -> rchild = T1 -> lchild;
    T1 -> lchild = T;
    T = T1;
}

//��ƽ��
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

//��ƽ��
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

//������
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

//ɾ�����
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


    cout << "�������" << endl;
    PreOrderTraverse(T,PrintElement);
    cout << endl << "�������" << endl;
    InOrderTraverse(T,PrintElement);
    cout << endl << "�������" << endl;
    PostOrderTraverse(T,PrintElement);
    cout << endl;

    cout << "�������" << endl;
    cin >> e;
    cout << SearchBST(T,e,NULL,P) << endl;


    cout << "�������ǰ�к������" << endl;
    cin >> e;
    InsertNode(T,e,taller);
    cout << "�������" << endl;
    PreOrderTraverse(T,PrintElement);
    cout << endl << "��������ǵݹ�" << endl;
    PreOrder(T,PrintElement);
    cout << endl << "�������" << endl;
    InOrderTraverse(T,PrintElement);
    cout << endl << "��������ǵݹ�" << endl;
    InOrder(T,PrintElement);
    cout << endl << "�������" << endl;
    PostOrderTraverse(T,PrintElement);
    cout << endl << "��������ǵݹ�" << endl;
    PostOrder(T,PrintElement);


    cout << endl << "ɾ������ǰ�к������" << endl;
    cin >> e;
    DeleteNode(T,e,taller);
    cout << "�������" << endl;
    PreOrderTraverse(T,PrintElement);
    cout << endl << "�������" << endl;
    InOrderTraverse(T,PrintElement);
    cout << endl << "�������" << endl;
    PostOrderTraverse(T,PrintElement);


    cout << endl << "��α���" << endl;
    ccTraverse(T,PrintElement);


    cout << endl << "�������ҽ���ǰ�к��ȱ���" << endl;
    ExchangeBiTNode(T);
    cout << "�������" << endl;
    PreOrderTraverse(T,PrintElement);
    cout << endl << "�������" << endl;
    InOrderTraverse(T,PrintElement);
    cout << endl << "�������" << endl;
    PostOrderTraverse(T,PrintElement);


    cout << endl << "�ٴν������ҽ���ǰ�к��ȱ���" << endl;
    ExchangeBiTNode(T);
    cout << "�������" << endl;
    PreOrderTraverse(T,PrintElement);
    cout << endl << "�������" << endl;
    InOrderTraverse(T,PrintElement);
    cout << endl << "�������" << endl;
    PostOrderTraverse(T,PrintElement);
    cout << endl;


    cout << "����" << endl << BiTreeHigh(T) << endl;
    cout << "Ҷ�ӽ����" << endl << BiTNodeNumber(T,Node) << endl;

}
