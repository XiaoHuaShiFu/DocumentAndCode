#include<stdio.h>
#include<malloc.h>
#define ERROR 0
#define OK 1
#define ElemType int

typedef int Status;
typedef struct LNode
{
 int data;
 struct LNode *next;
}LNode,*LinkList;


Status ListInsert_L(LinkList &L, int i, ElemType e) {  // 算法2.9
  // 在带头结点的单链线性表L的第i个元素之前插入元素e
  LinkList p,s;
  p = L;
  int j = 0;
  while (p && j < i-1) {  // 寻找第i-1个结点
    p = p->next;
    ++j;
  }
  if (!p || j > i-1) return ERROR;      // i小于1或者大于表长
  s = (LinkList)malloc(sizeof(LNode));  // 生成新结点
  s->data = e;  s->next = p->next;      // 插入L中
  p->next = s;
  return OK;
} // LinstInsert_L

Status ListDelete_L(LinkList &L, int i, ElemType &e) {  // 算法2.10
  // 在带头结点的单链线性表L中，删除第i个元素，并由e返回其值
  LinkList p,q;
  p = L;
  int j = 0;
  while (p->next && j < i-1) {  // 寻找第i个结点，并令p指向其前趋
    p = p->next;
    ++j;
  }
  if (!(p->next) || j > i-1) return ERROR;  // 删除位置不合理
  q = p->next;
  p->next = q->next;           // 删除并释放结点
  e = q->data;
  free(q);
  return OK;
} // ListDelete_L

void print(LinkList L){
    L = L->next;
    while(L){
        printf("%d ",L -> data);
        L = L -> next;
    }
    printf("\n");
}

void hebing(LinkList L1,LinkList L2,LinkList &L3){
    L1 = L1 -> next;
    L2 = L2 -> next;
    int i = 1;
    while(L1 && L2){
        if(L1 -> data < L2 -> data){
            ListInsert_L(L3,i++,L1 -> data);
            L1 = L1 -> next;
        }else{
            ListInsert_L(L3,i++,L2 -> data);
            L2 = L2 -> next;
        }
    }
    while(L1){
        ListInsert_L(L3,i++,L1 -> data);
        L1 = L1 -> next;
    }
    while(L2){
        ListInsert_L(L3,i++,L2 -> data);
        L2 = L2 -> next;
    }
}

int main(){
    int n1,n2;
    LinkList L1,L2,L3;
    L1 = (LinkList)malloc(sizeof(LNode));
    L1 -> next = NULL;
    L2 = (LinkList)malloc(sizeof(LNode));
    L2 -> next = NULL;
    L3 = (LinkList)malloc(sizeof(LNode));
    L3 -> next = NULL;
    scanf("%d",&n1);
    for(int i = 1,e; i <= n1;i++){
        scanf("%d",&e);
        ListInsert_L(L1,i,e);
    }
    scanf("%d",&n2);
    for(int i = 1,e; i <= n2;i++){
        scanf("%d",&e);
        ListInsert_L(L2,i,e);
    }
    printf("List A:");
    print(L1);
    printf("List B:");
    print(L2);
    hebing(L1,L2,L3);
    printf("List C:");
    print(L3);
}
