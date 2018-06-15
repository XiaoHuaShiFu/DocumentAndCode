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
    Q.front = Q.rear = (QueuePtr)malloc(sizeof(QNode));
    if(!Q.front) return 0;
    Q.front->next = NULL;
    return 1;
}

//��ӣ��ɹ�����1��ʧ�ܷ���0
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
    S.base = (BiTree *)malloc(100 * sizeof(BiTree));
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

//�����㣬�ɹ�����1��ʧ�ܷ���0
Status InserBST( BiTree &T , ElemType e ) {
    BiTree p;
    BiTree s;
    if(!SearchBST(T,e,NULL,p)) {
        s = (BiTree)malloc(sizeof(BiTNode));
        s -> data = e;
        s -> lchild = NULL;
        s -> rchild = NULL;
        if(!p)T = s;//��һ���ڵ�
        else if(e < (p -> data)) {
            p -> lchild = s;
        } else {
            p -> rchild = s;
        }
        return TRUE;
    } else return FALSE;
}

//��ʼ�����������ɹ�����1��ʧ�ܷ���0
Status InitBST( BiTree &T , int e ) {
    T = (BiTree)malloc(sizeof(BiTNode));
    T->data = e;
    T->lchild=T->rchild=NULL;
    return TRUE;
}

//���Ԫ��e��ֵ
Status PrintElement( ElemType e ) {
    printf("%d ", e );
    return OK;
}// PrintElement

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
} // PreOrderTrav

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

//�������ҽ��
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

//�����������
Status BiTreeHigh( BiTree T ) {
    int High = 0;
    if(T!=NULL) {
        int LH = BiTreeHigh(T->lchild);
        int RH = BiTreeHigh(T->rchild);
        High = LH >= RH ? LH+1 : RH+1;
    }
    return High;
}

//Ҷ�ӽڵ���������Ҷ�ӽ����
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

//ɾ�����
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

//ɾ�����
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


    printf("�������\n");
    PreOrderTraverse(T,PrintElement);
    printf("\n");
    printf("�������\n");
    InOrderTraverse(T,PrintElement);
    printf("\n");
    printf("�������\n");
    PostOrderTraverse(T,PrintElement);
    printf("\n");


    printf("�������\n");
    scanf("%d",&e);
    printf("%d\n",SearchBST(T,e,NULL,P));


    printf("�������ǰ�к������\n");
    scanf("%d",&e);
    InserBST(T,e);
    printf("�������\n");
    PreOrderTraverse(T,PrintElement);
    printf("\n");
    printf("��������ǵݹ�\n");
    PreOrder(T,PrintElement);
    printf("\n");
    printf("�������\n");
    InOrderTraverse(T,PrintElement);
    printf("\n");
    printf("��������ǵݹ�\n");
    InOrder(T,PrintElement);
    printf("\n");
    printf("�������\n");
    PostOrderTraverse(T,PrintElement);
    printf("\n");
    printf("��������ǵݹ�\n");
    PostOrder(T,PrintElement);
    printf("\n");


    printf("ɾ������ǰ�к������\n");
    scanf("%d",&e);
    DeleteBST(T,e);
    printf("�������\n");
    PreOrderTraverse(T,PrintElement);
    printf("\n");
    printf("�������\n");
    InOrderTraverse(T,PrintElement);
    printf("\n");
    printf("�������\n");
    PostOrderTraverse(T,PrintElement);
    printf("\n");


    printf("��α���\n");
    ccTraverse(T,PrintElement);
    printf("\n");


    printf("�������ҽ���ǰ�к��ȱ���\n");
    ExchangeBiTNode(T);
    printf("�������\n");
    PreOrderTraverse(T,PrintElement);
    printf("\n");
    printf("�������\n");
    InOrderTraverse(T,PrintElement);
    printf("\n");
    printf("�������\n");
    PostOrderTraverse(T,PrintElement);
    printf("\n");


    printf("�ٴν������ҽ���ǰ�к��ȱ���\n");
    ExchangeBiTNode(T);
    printf("�������\n");
    PreOrderTraverse(T,PrintElement);
    printf("\n");
    printf("�������\n");
    InOrderTraverse(T,PrintElement);
    printf("\n");
    printf("�������\n");
    PostOrderTraverse(T,PrintElement);
    printf("\n");


    printf("����\n");
    printf("%d",BiTreeHigh(T));
    printf("\n");
    printf("Ҷ�ӽ����\n");
    printf("%d",BiTNodeNumber(T,Node));
    printf("\n");

}
