#include "stdio.h"
#include "malloc.h"
#define TRUE 1
#define FALSE 0
#define OK  1
#define ERROR  0
#define INFEASIBLE -1
#define OVERFLOW -2
typedef int  Status;

typedef char  ElemType;
typedef struct BiTNode{
  ElemType data;
  struct BiTNode *lchild,*rchild;//左右孩子指针
} BiTNode,*BiTree;

Status CreateBiTree(BiTree &T) {  // 算法6.4
  // 按先序次序输入二叉树中结点的值（一个字符），’#’字符表示空树，
  // 构造二叉链表表示的二叉树T。
  char ch;
  scanf("%c",&ch);
  if (ch=='#') T = NULL;
  else {
    if (!(T = (BiTNode *)malloc(sizeof(BiTNode)))) return ERROR;
    T->data = ch; // 生成根结点
     CreateBiTree(T->lchild);  // 构造左子树
    CreateBiTree(T->rchild);  // 构造右子树
  }
  return OK;
} // CreateBiTree


Status PrintElement( ElemType e ) {  // 输出元素e的值
printf("%c", e );
return OK;
}// PrintElement


Status PreOrderTraverse( BiTree T, Status(*Visit)(ElemType) ) {
   // 前序遍历二叉树T的递归算法，对每个数据元素调用函数Visit。
   //补全代码,可用多个语句
  if(!T) return 0;
  Visit(T->data);
  PreOrderTraverse(T->lchild,Visit);
  PreOrderTraverse(T->rchild,Visit);
} // PreOrderTraverse

Status InOrderTraverse( BiTree T, Status(*Visit)(ElemType) ) {
     // 中序遍历二叉树T的递归算法，对每个数据元素调用函数Visit。
    //补全代码,可用多个语句
    if(!T) return 0;
  InOrderTraverse(T->lchild,Visit);
  Visit(T->data);
  InOrderTraverse(T->rchild,Visit);

} // InOrderTraverse

Status PostOrderTraverse( BiTree T, Status(*Visit)(ElemType) ) {
     // 后序遍历二叉树T的递归算法，对每个数据元素调用函数Visit。
     //补全代码,可用多个语句
    if(!T) return 0;
  PostOrderTraverse(T->lchild,Visit);
  PostOrderTraverse(T->rchild,Visit);
  Visit(T->data);
} // PostOrderTraverse



int main()   //主函数
{
                      //补充代码
                      BiTree T;
                      CreateBiTree(T);
                      PreOrderTraverse(T,PrintElement);
                      printf("\n");
                      InOrderTraverse(T,PrintElement);
                      printf("\n");
                      PostOrderTraverse(T,PrintElement);
 }//main
