#include<stdio.h>
#include<malloc.h>
#include<stdlib.h>
#define OK 1
#define ERROR 0
#define LIST_INIT_SIZE 1000
#define LISTINCREMENT 10
#define ElemType int
#define overflow -2
typedef struct
{
    int *elem;
    int length;
    int listsize;
} SqList;

int Load_Sq(SqList L) //print
{
    int i;
    if(L.length==0)
        printf("The List is empty!");
    else
    {
        printf("The List is: ");
        for(i=0; i<L.length; i++)
            printf("%d ",L.elem[i]);
    }
    printf("\n");
    return OK;
}

int main()
{
    SqList T;
    int a, i,*q,*p,*n;
    ElemType e, x;//initialization
    T.elem=(ElemType*)malloc(LIST_INIT_SIZE*sizeof(ElemType));
    if(!T.elem)
        exit(overflow);
    T.length=0;
    T.listsize=LIST_INIT_SIZE;
    printf("A Sequence List Has Created.\n");
     while(1)
    {
        printf("1:Insert element\n2:Delete element\n3:Load all elements\n0:Exit\nPlease choose:\n");
        scanf("%d",&a);
        switch(a)
        {
        case 1:// insert
            scanf("%d%d",&i,&x);
            if((i<1)||(i>T.length+1))
                printf("Insert Error!\n");
            else
            {
                if(T.length>=T.listsize)
                {
                    n=(ElemType*)realloc(T.elem,(T.listsize+LISTINCREMENT)*sizeof(ElemType));
                    if(!n)
                        exit(overflow);
                    T.elem=n;
                    T.listsize+=LISTINCREMENT;
                }
                q=&(T.elem[i-1]);
                if(T.length>0)
                for(p=&(T.elem[T.length-1]); p>=q; p--)
                    *(p+1)=*p;
                *q=x;
                T.length++;
                printf("The Element %d is Successfully Inserted!\n", x);
            }
            break;
        case 2:// delete
            scanf("%d",&i);
            if((i<1)||(i>T.length))
                printf("Delete Error!\n");
            else
            {
                p=&(T.elem[i-1]);
                e=*p;
                q=T.elem+T.length-1;
                for(p++; p<=q; p++)
                    *(p-1)=*p;
                T.length--;
                printf("The Element %d is Successfully Deleted!\n", e);
            }
            break;
        case 3:
            Load_Sq(T);
            break;
        case 0:
            return 1;
        }
    }
}
