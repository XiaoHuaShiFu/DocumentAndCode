/****************************ͷ�ļ���*********************************/
#include <iostream>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <malloc.h>
#include <sstream>
#include <conio.h>
#include <windows.h> //Sleep()����,������ɫ
#include <time.h>
#include <ctime>
using namespace std;
/****************************ͷ�ļ���*********************************/



/****************************�궨����*********************************/
#define ERROR 0
#define OK 1
#define ElemType int
typedef struct auctionItem{
    long Id;//���
    char Category[20];//���
    char Name[30];//����
    char BiddingMode[5];//���ĺͰ���
    long TheHighestPrice;//��߼�
    char TheHighestBidder[30];//��߳�����
    long Evaluation;//����
    long StartingPrice;//���ļ�
    char OldAndNew[3];//�¾�
    char Description[101];//��Ʒ����
    char State[10];//��Ʒ״̬ �����У��ѳɽ�
    char Owner[30];//����
    long long EndTime;//����ʱ��
    struct auctionItem *next;
} auctionItem,*auctionItems;
typedef struct User{
    char Account[30];//�˺�
    char Password[20];//����
    char Nickname[20];//�ǳ�
    struct User *next;
}User,*Users;
/****************************�궨����*********************************/



/****************************ȫ�ֱ�����*******************************/
char Empty[6] = "Empty";
char a1[30] = "��������Ʒ���:";
char a2[30] = "��������Ʒ���:";
char a3[30] = "��������Ʒ����:";
char a4[30] = "��������Ʒ�۸�:";
char a5[30] = "��������Ʒ����:";
char a6[30] = "��������Ʒ�¾�:";
char a7[30] = "��������Ʒ����:";
char a8[30] = "��������Ʒ״̬:";
char a9[30] = "��������Ʒӵ����:";
char a10[30] = "��������Ʒ���ļ�:";
char a11[30] = "1:����      2:����";
char a12[30] = "��������Ʒ��߳�����:";
char a13[30] = "��������Ʒ��ǰ��߼۸�:";
char a14[30] = "��������Ʒ����ʱ��(��λСʱ):";
char b1[30] = "�������˺�:";
char b2[30] = "�������ǳ�:";
char b3[30] = "����������:";
char b4[30] = "���ٴ���������:";
char b5[30] = "������������:";
char b6[30] = "���ٴ�����������:";
char Traded[7] = "�ѳɽ�";
char Auctioning[7] = "������";
char Light[5] = "����";
char Manager[5] = "root";
char Not[3] = "��";
char Auctioneer[13] = "�����й���Ա";
char Help[30] = "              H.����";
char EMPTY[2] = " ";
char dash[2] = "-";
char colon[2] = ":";
/****************************ȫ�ֱ�����*******************************/



/**************************ͨ�ú���������*****************************/
/*�Ƚ������ַ����Ƿ���ȣ���ȷ���1������ȷ���0*/
int Equal_Str(char str1[],char str2[]);

/*�����ַ������ȣ������ַ�������*/
int Len_Str(char str[]);

/*��ʱ������������λΪ����*/
void Delay(int seconds);

/*����ʱ�������ַ���*/
void CreateTime(long long seconds,char *Time);

/*��ȡ��ǰ�·ݵ�����*/
int GetDay(int month);

/*��ȡ��ǰʱ��*/
long long GetTheCurrentTime();

/*��ȡ��ǰ�·�*/
int GetMonth(long Seconds);

/*�����ʾ��������������*/
void Input_Int(long &Int,char prompt[]);

/*�����ʾ�������ַ���������*/
void Input_Str(char *Char,char prompt[]);

/*�����ʾ����������,�ɹ�¼�뷵��1��ʧ�ܷ���0*/
int Input_Password(char *Password,char prompt1[],char prompt2[]);

/*���θ�ֵ*/
void Assign_Int(long &Int,long Number);

/*�ַ����͸�ֵ*/
void Assign_Char(char *Char,char Str[]);

/*�����Ļ*/
void ClearScreen();
/**************************ͨ�ú���������*****************************/



/**************************��Ʒ����������*****************************/
/*��������Ʒ����*/
int AuctionItemQuantity(auctionItems Items);

/*����Ʒ��Ϣ�����ļ���*/
void AddAuctionItemToFile();

/*�����Ʒ��Ϣ*/
void AddAuctionItem(auctionItems &Items,auctionItem &Item,User UserItem);

/*��ʼ������Ʒ����*/
void CreateAuctionList(auctionItems &Items);

/*��ӡ����Ʒ*/
int PrintAuctionItem(auctionItem Item,User UserItem);

/*��ӡ����Ʒ�б�*/
int PrintAuctionItems(auctionItems Items,User UserItem);

/*ɾ������Ʒ��ĳ����Ʒ������*/
int DeleteItem(auctionItems Items,long Id,auctionItem &Item);

/*ɾ����Ʒ��Ϣ����ӡɾ�����*/
void DeleteItemAndPrint(auctionItems &Items,long Id,auctionItem &Item,User UserItem);

/*ɾ����Ʒ��Ϣҳ��*/
int DeleteItemPage(auctionItems &Items,User UserItem);

/*�޸�ĳ������Ʒ����Ϣ*/
int ModifyItem(auctionItems &Items,long Id,auctionItem &PreItem,auctionItem &PostItem);

/*�޸�ĳ������Ʒ����Ϣ����ӡ*/
int ModifyItemAndPage(auctionItems &Items,User UserItem);

/*ͨ��IdѰ����Ʒ���Ҳ�������0���ҵ�����1*/
int SearchById(auctionItems Items,long Id,auctionItem &Item);

/*ͨ��NameѰ����Ʒ���Ҳ�������0���ҵ�����1*/
int SearchByName(auctionItems Items,char Name[],auctionItem &Item);

/*ͨ��CategoryѰ����Ʒ���Ҳ�������0���ҵ������ҵ��ĸ���*/
int SearchByCategory(auctionItems Items,char Category[],User UserItem);

/*ͨ��StateѰ����Ʒ���Ҳ�������0���ҵ������ҵ��ĸ���*/
int SearchByState(auctionItems Items,char State[],User UserItem);

/*ͨ��TheHighestBidderѰ����Ʒ���Ҳ�������0���ҵ������ҵ��ĸ���*/
int SearchByTheHighestBidder(auctionItems Items,char TheHighestBidder[],User UserItem);

/*ͨ��OwnerѰ����Ʒ���Ҳ�������0���ҵ������ҵ��ĸ���*/
int SearchByOwner(auctionItems Items,char Owner[],User UserItem);

/*���ص�ǰ������������Ʒ����������۸�*/
long TheHighestPriceInAuctionHouse(auctionItems Items,auctionItem &Item);

/*���ص�ǰ������������Ʒ����������۸�*/
long TheLowestPriceInAuctionHouse(auctionItems Items,auctionItem &Item);

/*���ص�ǰ������������Ʒ���ܳɽ���*/
long TurnoverInAuctionHouse(auctionItems Items);

/*���ص�ǰ������������Ʒ���ܳɽ���or����������*/
int TurnoverNumberInAuctionHouse(auctionItems Items,char State[]);

/*��ʼ�������ɹ�����1��ʧ�ܷ���0*/
int StartAuction(auctionItem &Item);

/*����Ƿ���Խ�������������������������*/
void CheckAuction(auctionItems &Items,auctionItem &Item);

/*��ʼ����ҳ��*/
int StartAuctionPage(auctionItems &Items,User UserItem);

/*��Ʒ����ҳ��*/
int SearchPage(auctionItems &Items,auctionItem &Item,User UserItem);

/*��Ʒ�嵥ҳ��*/
int ItemsPage(auctionItems &Items,auctionItem &Item,User UserItem);

/*�û�����*/
int UserBid(auctionItems &Items,auctionItem &Item,User UserItem);

/*�����д�����ҳ��*/
int BigDataPage(auctionItems Items,Users UserItems,auctionItem Item);

/**************************��Ʒ����������*****************************/



/**************************�û�����������*****************************/
/*���ļ�����û���Ϣ*/
void AddUserToFile(User &UserItem);

/*����û���Ϣ*/
void AddUser(User &UserItem);

/*���û�ע��*/
void Register(Users &UserItems,User &UserItem);

/*ɾ���û���Ϣ*/
int DeleteUser(Users UserItems,char *UserAccount,User &UserItem);

/*ɾ���û���Ϣ�����´�������*/
int DeleteUserAndCreateList(Users &UserItems,char *UserAccount,User &UserItem);

/*��ʼ���û�����*/
void CreateUserList(Users &UserItems);

/*ͨ��AccountѰ���û����Ҳ�������0���ҵ�����1*/
int SearchByAccount(Users UserItems,char Account[],User &UserItem);

/*ͨ��NicknameѰ���û����Ҳ�������0���ҵ�����1*/
int SearchByNickname(Users UserItems,char Nickname[],User &UserItem);

/*�û���¼���ɹ�����1��������󷵻�-1���˺Ų����ڷ���0*/
int UserLog(Users UserItems,User &UserItem);

/*�û���¼����ӡUIҳ��,�ɹ�����1��ʧ�ܷ���0*/
int UserLogPrint(Users UserItems,User &UserItem);

/*��ӡ�û���Ϣ*/
void PrintUser(User UserItem);

/*��ӡ�û���Ϣ�б�*/
int PrintUsers(Users UserItems);

/*���ص�ǰ�������е��û���*/
int UserNumberInAuctionHouse(Users UserItems);

/*�޸����뺯��*/
int ModifyUserPassword(Users &UserItems,char *Account,char *Password);

/*�޸�����ҳ��*/
int ModifyPasswordPage(Users &UserItems,User &UserItem);

/*¼����Ʒ��Ϣҳ��*/
void AddAuctionItemPage(auctionItems &Items,auctionItem &Item);

/**************************�û�����������*****************************/



/*****************************UI������********************************/
/*��ӡҳ��ͷ*/
void Page_Head(char *Title,char *Help);

/*��ҳ��ӡ*/
void PrintHomePage();

/*��ҳ��¼��*/
void LogPrintHomePage();

/*���û�ע��ҳ��*/
void RegisterPage();

/*��ҳ*/
void PrintIndexPage();

/*��ҳ��Ʒ����ҳ*/
void PrintIndex_SearchPage();

/*��Ʒ�嵥ҳ��*/
void PrintIndex_ItemsPage();

/*��ӡ�����д�����ҳ��*/
void PrintIndex_BigDataPage();

/*��ӡ�ҵ�ҳ��*/
void PrintMyPage();

/*��ӡ�ҵľ���ҳ��*/
void PrintMyAuctions(auctionItems Items,User UserItem);

/*����ҳ��*/
void PrintHelpPage();

/*���塢������ʼ��*/
void InitBackgroundAndFont();
/*****************************UI������********************************/



/****************************ͨ�ú�����*******************************/
/*�Ƚ������ַ����Ƿ���ȣ���ȷ���1������ȷ���0*/
int Equal_Str(char str1[],char str2[]){
    if(strcmp(str1,str2) == 0) return 1;
    return 0;
}

/*�����ַ������ȣ������ַ�������*/
int Len_Str(char str[]){
    return strlen(str);
}

/*��ʱ������������λΪ����*/
void Delay(int seconds){
    Sleep(seconds);
}

/*��ȡ��ǰ�·ݵ�����*/
int GetDay(int month){
    switch(month){
        case 1:return 0;
        case 2:return 31;
        case 3:return 59;
        case 4:return 90;
        case 5:return 120;
        case 6:return 151;
        case 7:return 181;
        case 8:return 212;
        case 9:return 243;
        case 10:return 273;
        case 11:return 304;
        case 12:return 334;
        default:return -1;
    }
}

/*��ȡ��ǰʱ��*/
long long GetTheCurrentTime(){
    char CurrentTime[20];
    time_t Time = time(0);
    strftime(CurrentTime, sizeof(CurrentTime),"%Y-%m-%d %X",localtime(&Time));
    int year,month,day,hour,min,sec;
    sscanf(CurrentTime,"%d-%d-%d %d:%d:%d",&year,&month,&day,&hour,&min,&sec);
    long long seconds = (long long )year*31536000 +
                        (long long )(GetDay(month)+day)*86400 + (long long )hour * 3600 + (long long )min * 60 + (long long )sec;
    return seconds;
}

/*��ȡ��ǰ�·�*/
int GetMonth(long Seconds){
    if(Seconds > 334*86400){
        return 12;
    }else if(Seconds > 304*86400){
        return 11;
    }else if(Seconds > 273*86400){
        return 10;
    }else if(Seconds > 243*86400){
        return 9;
    }else if(Seconds > 212*86400){
        return 8;
    }else if(Seconds > 181*86400){
        return 7;
    }else if(Seconds > 151*86400){
        return 6;
    }else if(Seconds > 120*86400){
        return 5;
    }else if(Seconds > 90*86400){
        return 4;
    }else if(Seconds > 59*86400){
        return 3;
    }else if(Seconds > 31*86400){
        return 2;
    }else return 1;
}

/*����ʱ�������ַ���*/
void CreateTime(long long seconds,char *Time){
    int year = seconds / 31536000;
    long month_seconds = seconds % 31536000;
    int month = GetMonth(month_seconds);
    int day_seconds = month_seconds - GetDay(month)*86400;
    int day = day_seconds / 86400;
    int hour_seconds = day_seconds - day * 86400;
    int hour = hour_seconds / 3600;
    int min_seconds = hour_seconds - hour * 3600;
    int min = min_seconds / 60;
    int sec = min_seconds - min * 60;
    char time[20] = "";
    char Year[5],Month[3],Day[3],Hour[3],Min[3],Sec[3];
    itoa(year,Year,10);
    itoa(month,Month,10);
    itoa(day,Day,10);
    itoa(hour,Hour,10);
    itoa(min,Min,10);
    itoa(sec,Sec,10);
    strcat(time,Year);
    strcat(time,dash);
    strcat(time,Month);
    strcat(time,dash);
    strcat(time,Day);
    strcat(time,EMPTY);
    strcat(time,Hour);
    strcat(time,colon);
    strcat(time,Min);
    strcat(time,colon);
    strcat(time,Sec);
    strcpy(Time,time);

}

/*�����ʾ��������������*/
void Input_Int(long &Int,char prompt[]){
    if(Equal_Str(prompt,Empty)){
        scanf("%ld",&Int);
    }
    else{
        cout << prompt;
        scanf("%ld",&Int);
    }
}

/*�����ʾ�������ַ���������*/
void Input_Str(char *Char,char prompt[]){
    if(Equal_Str(prompt,Empty)){
        scanf("%s",Char);
    }
    else{
        cout << prompt;
        scanf("%s",Char);
    }
}

/*�����ʾ����������,�ɹ�¼�뷵��1��ʧ�ܷ���0*/
int Input_Password(char *Password,char prompt1[],char prompt2[]){
    int idx = 0;
    char c,FirstInput[20],SecondInput[20];
    cout << prompt1;
    while((c = getch()) != '\r'){
//        if(c == '\b'){
//             if(idx != 0){
//                cout << '\b';
//                idx--;
//                continue;
//             }
//             else{
//                continue;
//             }
//        }
        FirstInput[idx++] = c;
        cout << '*';
    }
    FirstInput[idx] = '\0';
    cout << endl << prompt2;
    idx = 0;
    while((c = getch()) != '\r'){
//        if(c == '\b'){
//             if(idx != 0){
//                cout << '\b';
//                idx--;
//                continue;
//             }
//             else{
//                continue;
//             }
//        }
        SecondInput[idx++] = c;
        cout << '*';
    }
    SecondInput[idx] = '\0';
    cout << endl;
    if(Equal_Str(FirstInput,SecondInput)){
        Assign_Char(Password,FirstInput);
        return 1;
    }
    else return 0;
}

/*���θ�ֵ*/
void Assign_Int(long &Int,long Number){
    Int = Number;
}

/*�ַ����͸�ֵ*/
void Assign_Char(char *Char,char *Str){
    strcpy(Char,Str);
}

/*�����Ļ*/
void ClearScreen(){
    system("cls");
}
/****************************ͨ�ú�����*******************************/



/****************************����Ʒ���������*******************************/

/*��������Ʒ����*/
int AuctionItemQuantity(auctionItems Items){
    int Quantity = 0;
    while(Items -> next){
        Items = Items -> next;
        Quantity++;
    }
    return Quantity;
}

/*����Ʒ��Ϣ�����ļ���*/
void AddAuctionItemToFile(auctionItem &Item){
    FILE *fp;
    fp=fopen("auctionItems_DataBase.txt","a+");
    fwrite(&Item,sizeof(auctionItem),1,fp);
    fclose(fp);
}

/*�����Ʒ��Ϣ*/
void AddAuctionItem(auctionItems &Items,auctionItem &Item,User UserItem){
    int Flag = 0;
    char selection;
    long long hour;
    char Bright[5] = "����",Dark[5] = "����";
    char New[3] = "��",Old[3] = "��";
    cout << "                     ";
    do{
        if(Flag == 1){
            cout << endl  << "                     " << "����Ʒ����Ѵ��ڣ�����������"<< endl << endl << "                     ";
        }
        Input_Int(Item.Id,a1);
        Flag = SearchById(Items,Item.Id,Item);
    }while(Flag == 1);
    cout << endl << "                     ";
    Input_Str(Item.Category,a2);
    cout << endl << "                     ";
    Input_Str(Item.Name,a3);
    cout << endl << "                     " << "��ѡ����Ʒ����ģʽ:" << endl << endl << "                     " << a11;
    do{
        if(Flag == 1){
            cout << endl << endl << "                     ";
            cout << "��ѡ����ȷ������ģʽ" << endl << endl << "                     " << a11;
        }
        selection = getch();
        if(selection == '1'){
            Assign_Char(Item.BiddingMode,Bright);
            Flag = 0;
        }else if(selection =='2'){
            Assign_Char(Item.BiddingMode,Dark);
            Flag = 0;
        }else{
            Flag = 1;
        }
    }while(Flag);
    cout << endl << endl << "                     ";
    Assign_Int(Item.TheHighestPrice,0);
    Assign_Char(Item.TheHighestBidder,Not);
    Input_Int(Item.Evaluation,a5);
    cout << endl << "                     ";
    Input_Int(Item.StartingPrice,a10);
    cout << endl << "                     " << "��ѡ����Ʒ�¾�״̬:" << endl << endl << "                     " << "1:��          2:��";
    do{
        if(Flag == 1){
            cout << endl << endl << "                     ";
            cout << "��ѡ����ȷ���¾�״̬" << endl << endl << "                     " << "1:��          2:��";
        }
        selection = getch();
        if(selection == '1'){
            Assign_Char(Item.OldAndNew,New);
            Flag = 0;
        }else if(selection =='2'){
            Assign_Char(Item.OldAndNew,Old);
            Flag = 0;
        }else{
            Flag = 1;
        }
    }while(Flag);
    cout << endl << endl << "                     ";
    Input_Str(Item.Description,a7);
    cout << endl << "                     ";
    Assign_Char(Item.State,Auctioning);
    Assign_Char(Item.Owner,UserItem.Nickname);
    cout << a14;
    scanf("%lld",&hour);
    Item.EndTime = (long long)hour*3600 + GetTheCurrentTime();
}

/*��ʼ������Ʒ����*/
void CreateAuctionList(auctionItems &Items){
    FILE *fp;
    fp = fopen("auctionItems_DataBase.txt","rb");
    auctionItems Pointer,PointerBridge;
    auctionItem temp;//����ṹ�����
    Items = (auctionItems)malloc(sizeof(auctionItem));
    Items->next = NULL;
    Pointer = (auctionItems)malloc(sizeof(auctionItem));
    Pointer = Items;
    while(fread(&temp,sizeof(auctionItem),1,fp)!=0){//���ļ��ж��ṹ���
        PointerBridge = (auctionItems)malloc(sizeof(auctionItem));
        Assign_Int(PointerBridge -> Id,temp.Id);
        Assign_Char(PointerBridge -> Category,temp.Category);
        Assign_Char(PointerBridge -> Name,temp.Name);
        Assign_Char(PointerBridge -> BiddingMode,temp.BiddingMode);
        Assign_Int(PointerBridge -> TheHighestPrice,temp.TheHighestPrice);
        Assign_Char(PointerBridge -> TheHighestBidder,temp.TheHighestBidder);
        Assign_Int(PointerBridge -> Evaluation,temp.Evaluation);
        Assign_Int(PointerBridge -> StartingPrice,temp.StartingPrice);
        Assign_Char(PointerBridge -> OldAndNew,temp.OldAndNew);
        Assign_Char(PointerBridge -> Description,temp.Description);
        Assign_Char(PointerBridge -> State,temp.State);
        Assign_Char(PointerBridge -> Owner,temp.Owner);
        PointerBridge -> EndTime = temp.EndTime;
        PointerBridge->next = Pointer -> next;
        Pointer->next = PointerBridge;
        Pointer = PointerBridge;
    }
    fclose(fp);
}

/*��ӡ����Ʒ������1*/
int PrintAuctionItem(auctionItem Item,User UserItem){
    char Time[64];
    CreateTime(Item.EndTime,Time);
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "------��Ʒ��Ϣ����-------" << endl ;
    cout << "                     ";
    cout << " ��ţ�" << Item.Id << endl ;
    cout << "                     ";
    cout << " ���ͣ�" << Item.Category << endl ;
    cout << "                     ";
    cout << " ���ƣ�" << Item.Name << endl ;
    cout << "                     ";
    cout << " ������ʽ��" << Item.BiddingMode << endl ;
    if(Equal_Str(Item.BiddingMode,Light) || Equal_Str(UserItem.Account,Manager) || Equal_Str(Item.State,Traded)){
        cout << "                     ";
        cout << " ��߼ۣ�" << Item.TheHighestPrice << endl ;
        cout << "                     ";
        cout << " ��߳����ߣ�" << Item.TheHighestBidder << endl ;
        cout << "                     ";
        cout << " ���ۣ�" << Item.Evaluation << endl ;
        cout << "                     ";
        cout << " ���ļۣ�" << Item.StartingPrice << endl ;
    }
    cout << "                     ";
    cout << " �¾ɣ�" << Item.OldAndNew << endl ;
    cout << "                     ";
    cout << " ������" << Item.Description << endl ;
    cout << "                     ";
    cout << " ״̬��" << Item.State << endl ;
    cout << "                     ";
    cout << " ӵ���ߣ�" << Item.Owner << endl ;
    cout << "                     ";
    cout << " ��������ʱ�䣺" << Time << endl ;
    cout << "                     ";
    cout << "---From Auction By XHSF--" << endl ;
    cout << "                     ";
    cout << "-------------------------" << endl;
    return 1;
}

/*��ӡ����Ʒ�б��ձ���0����ӡ�ɹ�����1*/
int PrintAuctionItems(auctionItems Items,User UserItem){
    if(Items->next == NULL) return 0;
    while(Items->next != NULL){
        Items = Items->next;
        PrintAuctionItem(*Items,UserItem);
    }
    return 1;
}

/*ɾ������Ʒ��ĳ����Ʒ�����ݣ�ԭ�ļ�Ϊ�շ���-2��
�Ҳ���Ҫɾ������Ʒ����-1���ļ���ʧ�ܷ���0���޸ĳɹ�����1*/
int DeleteItem(auctionItems Items,long Id,auctionItem &Item){
    int Flag = 0;
    FILE *Prototype,*Temporary;
    Prototype = fopen("auctionItems_DataBase.txt","rb");
    Temporary = fopen("auctionItems_DataBase_Temporary.txt","a+");
    if(Prototype == NULL || Temporary == NULL){
        fclose(Prototype);
        fclose(Temporary);
        return 0; //�ų����ļ�ʧ��
    }
    if(Items->next == NULL){
        fclose(Prototype);
        fclose(Temporary);
        return -2;//�ų�ԭ����Ϊ��
    }
    while(Items->next != NULL){
        Items = Items->next;
        if(Items -> Id != Id) fwrite(&*Items,sizeof(auctionItem),1,Temporary);//�Ѳ���Ҫɾ���Ľڵ�д����ʱ�ļ�
        else{
            Item = *Items;//����Ҫɾ���ڵ����Ϣ
            Flag = 1;
        }
    }
    fclose(Prototype);
    fclose(Temporary);
    remove("auctionItems_DataBase.txt");//ɾ��ԭ�ļ�
    rename("auctionItems_DataBase_Temporary.txt","auctionItems_DataBase.txt");//����ʱ�ļ�����Ϊԭ�ļ���
    if(Flag == 0) return -1; //���û���ҵ���Ҫ�ı�� �򷵻�1
    return 1;//�ҵ��򷵻�1
}

/*ɾ����Ʒ��Ϣ����ӡɾ�������ɾ��ʧ�ܷ���0��ɾ���ɹ�����1*/
void DeleteItemAndPrint(auctionItems &Items,long Id,auctionItem &Item,User UserItem){
    int ReturnValue = DeleteItem(Items,Id,Item);
    if(ReturnValue == -2){
        cout << "ԭ�ļ�Ϊ��" << endl;
//        return 0;
    }
    else if(ReturnValue == -1){
        cout << "�Ҳ������Ϊ" << Id << "����Ʒ" << endl;
//        return 0;
    }
    else if(ReturnValue == 0){
        cout << "�ļ���ʧ��" << endl;
//        return 0;
    }
    else{
        CreateAuctionList(Items);
        cout << "-----------ɾ�����ļ��б�-----------" << endl;
        PrintAuctionItems(Items,UserItem);
        cout << "-----------ɾ������Ʒ��Ϣ-----------" << endl;
        PrintAuctionItem(Item,UserItem);
//        return 1;
    }
}

/*ɾ����Ʒ��Ϣҳ��*/
int DeleteItemPage(auctionItems &Items,User UserItem){
    long Id;
    int Flag = 1;
    auctionItem Item;
    char TITLE[20] = "ɾ����Ʒ��Ϣ����";
    char selection;
    Page_Head(TITLE,EMPTY);
    cout << endl << "                     " << "������Ҫɾ����Ʒ�ı��:" ;
    do{
        if(!Flag){
            cout << endl << "                     " << "��������ȷ����Ʒ���:" ;
        }
        scanf("%ld",&Id);
        if(SearchById(Items,Id,Item)&&Equal_Str(Item.Owner,UserItem.Nickname)){
            PrintAuctionItem(Item,UserItem);
            cout << endl << "                     " << "1��ȷ��ɾ�� 2������ɾ��" ;
            do{
                if(!Flag){
                    cout << endl << endl << "                     " << "    ��ѡ����ȷ�Ĳ���" ;
                    cout << endl << endl << "                     " << "1��ȷ��ɾ�� 2������ɾ��" ;
                }
                selection = getch();
                switch(selection){
                    case '1':
                            DeleteItem(Items,Id,Item);
                            cout << endl << endl << "                     " << "ɾ���ɹ���1���Ż���һҳ" ;
                            Delay(1000);
                            return 1;
                    case '2':
                            cout << endl << endl << "                     " << "ɾ��ʧ�ܣ�1���Ż���һҳ" ;
                            Delay(1000);
                            return 1;
                    default:Flag = 0;
                }
            }while(!Flag);
        }else{
            Flag = 0;
        }
    }while(!Flag);
}

/*�޸�ĳ������Ʒ����Ϣ������ɹ��޸ķ���1���Ҳ���Ҫ�޸ĵ���Ʒ����0*/
int ModifyItem(auctionItems &Items,long Id,auctionItem &PreItem,auctionItem &PostItem){
    if(DeleteItem(Items,Id,PreItem) == -2){//ɾ��ԭ��Ʒ��Ϣ
        cout << "----------------�Ҳ���Ҫ�޸ĵ���Ʒ-----------------" << endl;
        return 0;
    }
    AddAuctionItemToFile(PostItem);//�����޸ĺ����Ϣ
    CreateAuctionList(Items);//���³�ʼ������
    return 1;
}

/*�޸���Ʒ��Ϣҳ��*/
int ModifyItemAndPage(auctionItems &Items,User UserItem){
    char TITLE[20] = "�޸���Ʒ��Ϣ����";
    Page_Head(TITLE,EMPTY);
    auctionItem PostItem,PreItem;
    int Flag = 1;
    char selection;
    char Bright[5] = "����",Dark[5] = "����";
    char New[3] = "��",Old[3] = "��";
    char Auctioning[7] = "������",Dealt[7] = "�ѳɽ�";
    cout << "                     ";
    do{
        if(Flag == 0){
            cout << endl  << "                     " << "����Ʒ��Ų����ڣ�����������"<< endl << endl << "                     ";
        }
        Input_Int(PostItem.Id,a1);
        Flag = SearchById(Items,PostItem.Id,PostItem);
        Flag = Equal_Str(PostItem.Owner,UserItem.Nickname);
    }while(Flag == 0);
    cout << endl << "                     " << "         �޸�ǰ" << endl;
    PrintAuctionItem(PostItem,UserItem);
    Flag = 0;
    cout << endl << "                     ";
    Input_Str(PostItem.Category,a2);
    cout << endl << "                     ";
    Input_Str(PostItem.Name,a3);
    cout << endl << "                     " << "��ѡ����Ʒ����ģʽ:" << endl << endl << "                     " << a11;
    do{
        if(Flag == 1){
            cout << endl << endl << "                     ";
            cout << "��ѡ����ȷ������ģʽ" << endl << endl << "                     " << a11;
        }
        selection = getch();
        if(selection == '1'){
            Assign_Char(PostItem.BiddingMode,Bright);
            Flag = 0;
        }else if(selection =='2'){
            Assign_Char(PostItem.BiddingMode,Dark);
            Flag = 0;
        }else{
            Flag = 1;
        }
    }while(Flag);
    cout << endl << endl << "                     ";
    Input_Int(PostItem.TheHighestPrice,a13);
    cout <<endl << "                     ";
    Input_Str(PostItem.TheHighestBidder,a12);
    cout <<endl << "                     ";
    Input_Int(PostItem.Evaluation,a5);
    cout << endl << "                     ";
    Input_Int(PostItem.StartingPrice,a10);
    cout << endl << "                     " << "��ѡ����Ʒ�¾�״̬:" << endl << endl << "                     " << "1:��          2:��";
    do{
        if(Flag == 1){
            cout << endl << endl << "                     ";
            cout << "��ѡ����ȷ���¾�״̬" << endl << endl << "                     " << "1:��          2:��";
        }
        selection = getch();
        if(selection == '1'){
            Assign_Char(PostItem.OldAndNew,New);
            Flag = 0;
        }else if(selection =='2'){
            Assign_Char(PostItem.OldAndNew,Old);
            Flag = 0;
        }else{
            Flag = 1;
        }
    }while(Flag);
    cout << endl << endl << "                     ";
    Input_Str(PostItem.Description,a7);
    cout << endl << endl << "                     " << "��ѡ����Ʒ����״̬:" << endl << endl << "                     " << "1:������  2:�ѳɽ�";
    do{
        if(Flag == 1){
            cout << endl << endl << "                     ";
            cout << "��ѡ����ȷ������״̬" << endl << endl << "                     " << "1:������  2:�ѳɽ�";
        }
        selection = getch();
        if(selection == '1'){
            Assign_Char(PostItem.State,Auctioning);
            Flag = 0;
        }else if(selection =='2'){
            Assign_Char(PostItem.State,Dealt);
            Flag = 0;
        }else{
            Flag = 1;
        }
    }while(Flag);
    cout << endl << endl << "                     ";
    Input_Str(PostItem.Owner,a9);
    cout << endl << "                     " << "         �޸ĺ�" << endl;
    PrintAuctionItem(PostItem,UserItem);
    cout << endl << "                     " << "    ��ȷ���Ƿ�Ҫ�޸�" << endl;
    cout << endl << "                     " << "1��ȷ���޸� 2�������޸�" << endl;
    selection = getch();
    do{
        if(Flag == 1){
            cout << endl << "                     ";
            cout << "    ��ѡ����ȷ�Ĳ���  " << endl << endl << "                     " << "1��ȷ���޸� 2�������޸�";
        }
        selection = getch();
        if(selection == '1'){
            DeleteItem(Items,PostItem.Id,PreItem);
            AddAuctionItemToFile(PostItem);//�����޸ĺ����Ϣ
            CreateAuctionList(Items);//���³�ʼ������
            cout << endl << endl << "                  " << "�޸ĳɹ���1��󷵻���һ��ҳ��" << endl;
            Delay(1000);
            return 1;
        }else if(selection =='2'){
            cout << endl << endl << "                  " << "�޸�ʧ�ܣ�1��󷵻���һ��ҳ��" << endl;
            Delay(1000);
            return 1;
        }else{
            Flag = 1;
        }
    }while(Flag);
}

/*ͨ��IdѰ����Ʒ���Ҳ�������0���ҵ�����1*/
int SearchById(auctionItems Items,long Id,auctionItem &Item){
    while(Items -> next != NULL){
        Items = Items -> next;
        if(Items -> Id == Id){
            Item = *Items;
            return 1;
        }
    }
    return 0;
}

/*ͨ��NameѰ����Ʒ���Ҳ�������0���ҵ�����1*/
int SearchByName(auctionItems Items,char Name[],auctionItem &Item){
    while(Items -> next != NULL){
        Items = Items -> next;
        if(Equal_Str(Items->Name,Name)){
            Item = *Items;
            return 1;
        }
    }
    return 0;
}

/*ͨ��CategoryѰ����Ʒ���Ҳ�������0���ҵ������ҵ��ĸ���*/
int SearchByCategory(auctionItems Items,char Category[],User UserItem){
    int Quantity = 0;
    while(Items -> next != NULL){
        Items = Items -> next;
        if(Equal_Str(Items->Category,Category)){
            Quantity++;
            PrintAuctionItem(*Items,UserItem);
        }
    }
    return Quantity;
}

/*ͨ��StateѰ����Ʒ���Ҳ�������0���ҵ������ҵ��ĸ���*/
int SearchByState(auctionItems Items,char State[],User UserItem){
    int Quantity = 0;
    while(Items -> next != NULL){
        Items = Items -> next;
        if(Equal_Str(Items->State,State)){
            Quantity++;
            PrintAuctionItem(*Items,UserItem);
        }
    }
    return Quantity;
}

/*ͨ��TheHighestBidderѰ����Ʒ���Ҳ�������0���ҵ������ҵ��ĸ���*/
int SearchByTheHighestBidder(auctionItems Items,char TheHighestBidder[],User UserItem){
    int Quantity = 0;
    while(Items -> next != NULL){
        Items = Items -> next;
        if(Equal_Str(Items->TheHighestBidder,TheHighestBidder)){
            Quantity++;
            PrintAuctionItem(*Items,UserItem);
        }
    }
    return Quantity;
}

/*ͨ��OwnerѰ����Ʒ���Ҳ�������0���ҵ������ҵ��ĸ���*/
int SearchByOwner(auctionItems Items,char Owner[],User UserItem){
    int Quantity = 0;
    while(Items -> next != NULL){
        Items = Items -> next;
        if(Equal_Str(Items->Owner,Owner)){
            Quantity++;
            PrintAuctionItem(*Items,UserItem);
        }
    }
    return Quantity;
}

/*���ص�ǰ������������Ʒ����������۸�*/
long TheHighestPriceInAuctionHouse(auctionItems Items,auctionItem &Item){
    long HighestPrice = 0;
    while(Items -> next != NULL){
        Items = Items -> next;
        if(Items -> TheHighestPrice >= HighestPrice && Equal_Str(Items -> State,Traded)){
            HighestPrice = Items -> TheHighestPrice;
            Item = *Items;
        }
    }
    return HighestPrice;
}

/*���ص�ǰ������������Ʒ����������۸�*/
long TheLowestPriceInAuctionHouse(auctionItems Items,auctionItem &Item){
    long LowestPrice = 0;
    while(Items -> next != NULL){
        Items = Items -> next;
        if(Items -> TheHighestPrice <= LowestPrice && Equal_Str(Items -> State,Traded)){
            LowestPrice = Items -> TheHighestPrice;
            Item = *Items;
        }
    }
    return LowestPrice;
}

/*���ص�ǰ������������Ʒ���ܳɽ���*/
long TurnoverInAuctionHouse(auctionItems Items){
    long Turnover = 0;
    while(Items -> next != NULL){
        Items = Items -> next;
        if(Equal_Str(Items -> State,Traded)) Turnover += (Items -> TheHighestPrice);
    }
    return Turnover;
}

/*���ص�ǰ������������Ʒ���ܳɽ���or����������*/
int TurnoverNumberInAuctionHouse(auctionItems Items,char State[]){
    long TurnoverNumber = 0;
    while(Items -> next != NULL){
        Items = Items -> next;
        if(Equal_Str(Items -> State,State)) TurnoverNumber++;
    }
    return TurnoverNumber;
}

/*��ʼ�������ɹ�����1��ʧ�ܷ���0*/
int StartAuction(auctionItems &Items,auctionItem &Item){
    if(Item.TheHighestPrice >= Item.StartingPrice){
        DeleteItem(Items,Item.Id,Item);
        Assign_Char(Item.Owner,Item.TheHighestBidder);
        Assign_Char(Item.State,Traded);
        FILE *fp;
        fp=fopen("auctionItems_DataBase.txt","a+");
        fwrite(&Item,sizeof(auctionItem),1,fp);
        fclose(fp);
        CreateAuctionList(Items);//���³�ʼ������
        return 1;//�����ɹ�����1
    }
    else{
        return 0;//����ʧ��
    }
}

/*����Ƿ���Խ�������������������������*/
void CheckAuction(auctionItems &Items,auctionItem &Item){
    long long CurrentTime = GetTheCurrentTime();
    auctionItems TemporaryItems = Items;
    while(TemporaryItems -> next != NULL){
        TemporaryItems = TemporaryItems -> next;
        if(TemporaryItems -> EndTime >= CurrentTime){
            Item = *TemporaryItems;
            StartAuction(Items,Item);
        }
    }
}

/*��ʼ����ҳ��*/
int StartAuctionPage(auctionItems &Items,User UserItem){
    char root[5] = "root";
    long Id;
    int Flag = 1;
    auctionItem Item;
    char TITLE[20] = "   ��ʼ���Ľ���";
    char selection;
    Page_Head(TITLE,EMPTY);
    if(!Equal_Str(UserItem.Nickname,root)){
        cout << endl << "                     " << "�ǹ���Ա�޷����о���" ;
        cout << endl << endl << "                       " << "1��󷵻���һҳ" ;
        Delay(1000);
        return 1;
    }
    cout << endl << "                     " << "������Ҫ������Ʒ�ı��:" ;
    do{
        if(!Flag){
            cout << endl << "                     " << "��������ȷ����Ʒ���:" ;
        }
        scanf("%ld",&Id);
        if(SearchById(Items,Id,Item)){
            if(!Equal_Str(Item.Owner,UserItem.Nickname)){
                cout << "               ";
                cout <<"�����Ǹ���Ʒ�����ң�1��󷵻���һҳ";
                Delay(1000);
                return 1;
            }
            PrintAuctionItem(Item,UserItem);
            if(Equal_Str(Item.State,Traded)){
                cout << endl << endl << "             " << "����ʧ�ܣ���Ʒ�ѽ��ף�1��󷵻���һҳ" ;
                Delay(1000);
                return 1;
            }
            cout << endl << "                     " << "1��ȷ�Ͼ��� 2����������" ;
            do{
                if(!Flag){
                    cout << endl << endl << "                     " << "    ��ѡ����ȷ�Ĳ���" ;
                    cout << endl << endl << "                     " << "1��ȷ�Ͼ��� 2����������" ;
                }
                selection = getch();
                switch(selection){
                    case '1':
                            StartAuction(Items,Item);
                            cout << endl << endl << "                     " << "���ĳɹ�����Ʒ�ĵ����ǣ�" << Item.TheHighestBidder;
                            cout << endl << endl << "                     " << "  �������������һҳ" ;
                            getch();
                            Delay(1000);
                            return 1;
                    case '2':
                            cout << endl << endl << "                     " << "����ʧ�ܣ�1���Ż���һҳ" ;
                            Delay(1000);
                            return 1;
                    default:Flag = 0;
                }
            }while(!Flag);
        }else{
            Flag = 0;
        }
    }while(!Flag);
}

/*�û����ģ����۳ɹ�����1��ʧ�ܷ���0*/
int UserBid(auctionItems &Items,auctionItem &Item,User UserItem){
    long Price;
    char Light[5] = "����";
    if(Equal_Str(Item.BiddingMode,Light)){
        cout << endl << endl << "                     ";
        cout << "------��Ʒ��Ϣ����-------" << endl << endl ;
        cout << "                     ";
        cout << " ��ǰ��Ʒ��߼ۣ�" << Item.TheHighestPrice << endl << endl ;
        cout << "                     ";
        cout << " ��ǰ��Ʒ���ļۣ�" << Item.StartingPrice << endl << endl ;
        cout << "                     ";
        cout << " ��ǰ��Ʒ���ۣ�" << Item.Evaluation << endl << endl ;
    }else{
        cout << endl << endl << "                     ";
        cout << "------��Ʒ���а���-------" << endl << endl;
    }
    cout << "                ";
    cout << "ע�������е�ǰ��֧�ֳ���2��Ԫ�Ľ���" << endl << endl;
    cout << "                     ";
    cout << " ���������ĳ��ۣ�";
    scanf("%ld",&Price);
    if(Price > Item.TheHighestPrice && Price > Item.StartingPrice && Price <= 2000000000){
        DeleteItem(Items,Item.Id,Item);
        Assign_Char(Item.TheHighestBidder,UserItem.Nickname);
        Assign_Int(Item.TheHighestPrice,Price);
        FILE *fp;
        fp=fopen("auctionItems_DataBase.txt","a+");
        fwrite(&Item,sizeof(auctionItem),1,fp);
        fclose(fp);
        CreateAuctionList(Items);//���³�ʼ������
        return 1;//���۳ɹ�����1
    }
    else{
        return 0;//
    }
}

/*��Ʒ����ҳ��*/
int SearchPage(auctionItems &Items,auctionItem &Item,User UserItem){
    PrintIndex_SearchPage();
    char Selection;
    char TITLE[20] = "    ��Ʒ����";
    int Id,Flag = 0;
    char Name[30],Category[20];
    do{
        if(Flag == 1){
            cout << "                     ";
            cout << "     ��ѡ����ȷ����" << endl;
        }
        Selection = getch();
        switch(Selection){
        case '1':
                Page_Head(TITLE,EMPTY);
                cout << "                     ";
                cout << "   ��������Ʒ��ţ�";
                scanf("%ld",&Id);
                if(SearchById(Items,Id,Item)){
                    if(Equal_Str(Item.Owner,UserItem.Nickname)){
                        cout << "                 ";
                        cout <<"�޷������Լ�����Ʒ��1��󷵻���ҳ";
                        Delay(1000);
                        return 1;
                    }
                    PrintAuctionItem(Item,UserItem);
                    cout << "                     ";
                    cout <<"��1���о��ģ���2������ҳ";
                    Selection = getch();
                    switch(Selection){
                        case '1':
                                if(UserBid(Items,Item,UserItem)){
                                    cout << endl << "                     ";
                                    cout <<"���ĳɹ���1��󷵻���ҳ";
                                    Delay(1000);
                                    return 1;
                                }else{
                                    cout << endl << "                     ";
                                    cout <<"����ʧ�ܣ�1��󷵻���ҳ";
                                    Delay(1000);
                                    return 1;
                                }
                                break;
                        case '2':return 1;break;
                    }

                }else{
                    cout << "                     ";
                    cout <<"����ʧ�ܣ�1��󷵻���ҳ";
                    Delay(1000);
                    return 1;
                }
                Flag = 0;
                break;
        case '2':
                Page_Head(TITLE,EMPTY);
                cout << "                     ";
                cout << "   ��������Ʒ���";
                scanf("%s",Category);
                if(SearchByCategory(Items,Category,UserItem)){
                    cout << "                     ";
                    cout <<"��1���о��ģ���2������ҳ";
                    Selection = getch();
                    switch(Selection){
                        case '1':
                                cout << endl << "                     ";
                                cout << "   ��������Ʒ��ţ�";
                                scanf("%ld",&Id);
                                SearchById(Items,Id,Item);
                                if(Equal_Str(Item.Owner,UserItem.Nickname)){
                                    cout << "                 ";
                                    cout <<"�޷������Լ�����Ʒ��1��󷵻���ҳ";
                                    Delay(1000);
                                    return 1;
                                }
                                if(UserBid(Items,Item,UserItem)){
                                    cout << "                     ";
                                    cout <<"���ĳɹ���1��󷵻���ҳ";
                                    Delay(1000);
                                    return 1;
                                }else{
                                    cout << "                     ";
                                    cout <<"����ʧ�ܣ�1��󷵻���ҳ";
                                    Delay(1000);
                                    return 1;
                                }
                                break;
                        case '2':return 1;break;
                    }

                }else{
                    cout << "                     ";
                    cout <<"����ʧ�ܣ�1��󷵻���ҳ";
                    Delay(1000);
                    return 1;
                }
                Flag = 0;
                break;
        case '3':
                Page_Head(TITLE,EMPTY);
                cout << "                     ";
                cout << "   ��������Ʒ���ƣ�";
                scanf("%s",Name);
                if(SearchByName(Items,Name,Item)){
                    if(Equal_Str(Item.Owner,UserItem.Nickname)){
                        cout << "                 ";
                        cout <<"�޷������Լ�����Ʒ��1��󷵻���ҳ";
                        Delay(1000);
                        return 1;
                    }
                    PrintAuctionItem(Item,UserItem);
                    cout << "                     ";
                    cout <<"��1���о��ģ���2������ҳ";
                    Selection = getch();
                    switch(Selection){
                        case '1':
                                if(UserBid(Items,Item,UserItem)){
                                    cout << "                     ";
                                    cout <<"���ĳɹ���1��󷵻���ҳ";
                                    Delay(1000);
                                    return 1;
                                }else{
                                    cout << "                     ";
                                    cout <<"����ʧ�ܣ�1��󷵻���ҳ";
                                    Delay(1000);
                                    return 1;
                                }
                                break;
                        case '2':return 1;break;
                    }

                }else{
                    cout << "                     ";
                    cout <<"����ʧ�ܣ�1��󷵻���ҳ";
                    Delay(1000);
                    return 1;
                }
                Flag = 0;
                break;
        case '4':
                return 1;
                break;
        case 'h':
                PrintHelpPage();
                Flag = 2;
                break;
        case 'H':
                PrintHelpPage();
                Flag = 2;
                break;
        default:
                Flag = 1;
        }
    }while(Flag);
}

/*��Ʒ�嵥ҳ��*/
int ItemsPage(auctionItems &Items,auctionItem &Item,User UserItem){
    PrintIndex_ItemsPage();
    PrintAuctionItems(Items,UserItem);
    char Selection;
    int Id;
    cout << endl << "                     ";
    cout << "  ��ѡ��Ҫ���еĲ�����" << endl << endl;
    cout << "                     ";
    cout << "      1.���о���"    << endl << endl;
    cout << "                     ";
    cout << "      2.������ҳ"    << endl << endl;
    Selection = getch();
    switch(Selection){
        case '1':
                cout << "                     ";
                cout << "   ��������Ʒ��ţ�";
                scanf("%ld",&Id);
                if(SearchById(Items,Id,Item)){
                    if(Equal_Str(Item.Owner,UserItem.Nickname)){
                        cout << "                 ";
                        cout <<"�޷������Լ�����Ʒ��1��󷵻���ҳ";
                        Delay(1000);
                        return 1;
                    }
                    if(UserBid(Items,Item,UserItem)){
                        cout << "                     ";
                        cout <<"���ĳɹ���1��󷵻���ҳ";
                        Delay(1000);
                        return 1;
                    }else{
                        cout << "                     ";
                        cout <<"����ʧ�ܣ�1��󷵻���ҳ";
                        Delay(1000);
                        return 1;
                    }
                }else{
                    cout << "                     ";
                    cout <<"��Ʒ�����ڣ�1��󷵻���ҳ";
                    Delay(1000);
                    return 1;
                }
                break;
        case '2':
                return 1;
                break;
        default:
                cout << "                     ";
                cout << "ѡ�����1��󷵻���ҳ"    << endl;
                Delay(1000);
                return 1;
    }
}

/*�����д�����ҳ��*/
int BigDataPage(auctionItems Items,Users UserItems,auctionItem Item){
    PrintIndex_BigDataPage();
    char Selection;
    char State1[7] = "�ѳɽ�";
    char State2[7] = "������";
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "                       " << endl ;
    cout << "                     ";
    cout << " Welcome to �����д����� " << endl << endl;
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "��ǰ�������û������ǣ� " << UserNumberInAuctionHouse(UserItems) << endl;
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    TheHighestPriceInAuctionHouse(Items,Item);
    cout << "  ��ǰ��߼۸������Ʒ��"  << endl;
    cout << "                     ";
    cout << "*********" << Item.Name <<"**********" << endl;
    cout << "                     ";
    cout << "*****�ɽ���Ϊ��" << Item.TheHighestPrice << "*****"<< endl;
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    TheLowestPriceInAuctionHouse(Items,Item);
    cout << "  ��ǰ��ͼ۸������Ʒ�� " << endl;
    cout << "                     ";
    cout << "*********" << Item.Name <<"**********" << endl;
    cout << "                     ";
    cout << "*****�ɽ���Ϊ��" << Item.TheHighestPrice << "*****" << endl;
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "��ǰ����������Ʒ���ǣ�" << TurnoverNumberInAuctionHouse(Items,State1) << endl;
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "��ǰ�����е���Ʒ���ǣ�" << TurnoverNumberInAuctionHouse(Items,State2) << endl;
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "��ǰ�����е��ܽ��׶��ǣ�" << TurnoverInAuctionHouse(Items) << endl;
    cout << "                     ";
    cout << "-------------------------" << endl << endl << endl;
    cout << endl << "                     ";
    cout << "    �������������ҳ     " << endl << endl;
    cout << "____________________________________________________________________" << endl ;
    cout << "                     ";
    cout << "    2018.5.3 By XHSF      " << endl << endl << endl<< endl;
    Selection = getch();
    return 1;
}

/*¼����Ʒ��Ϣҳ��*/
void AddAuctionItemPage(auctionItems &Items,auctionItem &Item,User UserItem){
    char TITLE[20] = "¼����Ʒ��Ϣ����";
    Page_Head(TITLE,EMPTY);
    AddAuctionItem(Items,Item,UserItem);//�����Ʒ
    AddAuctionItemToFile(Item);//�����Ʒ���ļ�
    CreateAuctionList(Items);//��ʼ����Ʒ����
    cout << "                     ";
    cout << "                       " << endl ;
    cout << "                ";
    cout << "�����Ʒ�ɹ���1��󷵻���һ��ҳ��" << endl << endl;
    Delay(1000);
}

/****************************����Ʒ���������*******************************/



/****************************�û����������*********************************/
/*���ļ�����û���Ϣ*/
void AddUserToFile(User &UserItem){
    FILE *fp;
    fp = fopen("Users_DataBase.txt","a+");
    fwrite(&UserItem,sizeof(User),1,fp);
    fclose(fp);
}

/*����û���Ϣ*/
void AddUser(User &UserItem){
    Input_Str(UserItem.Account,b1);
    Input_Str(UserItem.Nickname,b2);
    Input_Password(UserItem.Password,b3,b4);
}

/*���û�ע��*/
void Register(Users &UserItems,User &UserItem){
    int FlagAccount = 0,FlagPassword = 6,FlagNickname = 4,idx = 0;
    char Password[20],Account[30],Nickname[30],c;
    do{
        RegisterPage();
        if(FlagAccount){
            cout << "                     ";
            cout << "   ���˺��ѱ�ע��    " << endl << endl;
            memset(Account,'\0',sizeof(Account));
            memset(Password,'\0',sizeof(Password));
            memset(Nickname,'\0',sizeof(Nickname));
        }
        if(FlagPassword < 6){
            cout << "                     ";
            cout << "����Ӧ���ڵ���6���ַ�" << endl << endl;
            memset(Account,'\0',sizeof(Account));
            memset(Password,'\0',sizeof(Password));
            memset(Nickname,'\0',sizeof(Nickname));
        }
        if(FlagNickname < 4){
            cout << "                     ";
            cout << "�ǳ�Ӧ���ڵ���4���ַ�" << endl << endl;
            memset(Account,'\0',sizeof(Account));
            memset(Password,'\0',sizeof(Password));
            memset(Nickname,'\0',sizeof(Nickname));
        }
        cout << "                     ";
        cout << "�ǳƣ�" ;
        scanf("%s",Nickname);
        cout<< endl << endl  << "                     ";
        cout << "�˺ţ�" ;
        scanf("%s",Account);
        cout<< endl << endl  << "                     ";
        cout  << "���룺" ;
        while((c = getch()) != '\r'){
        if(c == '\b'){
            if(idx != 0){
                cout << '\b';
                idx--;
                continue;
                }
                else{
                    continue;
                }
            }
            Password[idx++] = c;
            cout << '*';
        }
        Password[idx] = '\0';
        cout <<  endl ;
        FlagNickname = Len_Str(Nickname);
        FlagAccount = SearchByAccount(UserItems,Account,UserItem);
        FlagPassword = Len_Str(Password);
    }while(FlagAccount || FlagPassword < 6 || FlagNickname < 4);
    Assign_Char(UserItem.Nickname,Nickname);
    Assign_Char(UserItem.Account,Account);
    Assign_Char(UserItem.Password,Password);
    AddUserToFile(UserItem);
    CreateUserList(UserItems);
    cout << "                     ";
    cout << "ע���У���Ⱥ򡣡��� " << endl ;
    Delay(1000);
}

/*ɾ���û���Ϣ*/
int DeleteUser(Users UserItems,char *UserAccount,User &UserItem){
    int Flag = 0;
    FILE *Prototype,*Temporary;
    Prototype = fopen("Users_DataBase.txt","rb");
    Temporary = fopen("Users_DataBase_Temporary.txt","a+");
    if(Prototype == NULL || Temporary == NULL){
        fclose(Prototype);
        fclose(Temporary);
        return 0; //�ų����ļ�ʧ��
    }
    if(UserItems->next == NULL){
        fclose(Prototype);
        fclose(Temporary);
        return -2;//�ų�ԭ����Ϊ��
    }
    while(UserItems->next != NULL){
        UserItems = UserItems->next;
        if(!Equal_Str(UserItems->Account,UserAccount)) fwrite(&*UserItems,sizeof(User),1,Temporary);//�Ѳ���Ҫɾ���Ľڵ�д����ʱ�ļ�
        else{
            UserItem = *UserItems;//����Ҫɾ���ڵ����Ϣ
            Flag = 1;
        }
    }
    fclose(Prototype);
    fclose(Temporary);
    remove("Users_DataBase.txt");//ɾ��ԭ�ļ�
    rename("Users_DataBase_Temporary.txt","Users_DataBase.txt");//����ʱ�ļ�����Ϊԭ�ļ���
    if(Flag == 0) return -1; //���û���ҵ���Ҫɾ���ı�� �򷵻�1
    return 1;//�ҵ��򷵻�1
}

/*ɾ���û���Ϣ�����´�������*/
int DeleteUserAndCreateList(Users &UserItems,char *UserAccount,User &UserItem){
    int ReturnValue = DeleteUser(UserItems,UserAccount,UserItem);
    CreateUserList(UserItems);
    return ReturnValue;
}

/*��ʼ���û�����*/
void CreateUserList(Users &UserItems){
    FILE *fp;
    fp = fopen("Users_DataBase.txt","rb");
    Users Pointer,PointerBridge ;
    User temp;//����ṹ�����
    UserItems = (Users)malloc(sizeof(User));
    UserItems->next = NULL;
    Pointer = (Users)malloc(sizeof(User));  // Pointerָ���¿��ٵĽڵ��ڴ�
    Pointer = UserItems;
    while(fread(&temp,sizeof(User),1,fp)!=0){//���ļ��ж��ṹ���
        PointerBridge = (Users)malloc(sizeof(User));
        Assign_Char(PointerBridge -> Account,temp.Account);
        Assign_Char(PointerBridge -> Nickname,temp.Nickname);
        Assign_Char(PointerBridge -> Password,temp.Password);
        PointerBridge->next = Pointer -> next;
        Pointer->next = PointerBridge;
        Pointer = PointerBridge;
    }
    fclose(fp);
}

/*ͨ��AccountѰ���û����Ҳ�������0���ҵ�����1*/
int SearchByAccount(Users UserItems,char Account[],User &UserItem){
    while(UserItems -> next != NULL){
        UserItems = UserItems -> next;
        if(Equal_Str(UserItems->Account,Account)){
            UserItem = *UserItems;
            return 1;
        }
    }
    return 0;
}

/*ͨ��NicknameѰ���û����Ҳ�������0���ҵ�����1*/
int SearchByNickname(Users UserItems,char Nickname[],User &UserItem){
    while(UserItems -> next != NULL){
        UserItems = UserItems -> next;
        if(Equal_Str(UserItems->Nickname,Nickname)){
            UserItem = *UserItems;
            return 1;
        }
    }
    return 0;
}

/*���ص�ǰ�������е��û���*/
int UserNumberInAuctionHouse(Users UserItems){
    long UserNumber = 0;
    while(UserItems -> next != NULL){
        UserItems = UserItems -> next;
        UserNumber++;
    }
    return UserNumber;
}

/*��ӡ�û���Ϣ*/
void PrintUser(User UserItem){
    cout << "------------------------" << endl;
    cout << UserItem.Account << endl;
    cout << UserItem.Nickname << endl;
    cout << UserItem.Password << endl;
    cout << "------------------------" << endl;
}

/*��ӡ�û���Ϣ�б�*/
int PrintUsers(Users UserItems){
    if(UserItems->next == NULL) return 0;
    while(UserItems->next != NULL){
        UserItems = UserItems->next;
        PrintUser(*UserItems);
    }
    return 1;
}

/*�û���¼���ɹ�����1��������󷵻�-1���˺Ų����ڷ���0*/
int UserLog(Users UserItems,User &UserItem,char *Account,char *Password){
    if(SearchByAccount(UserItems,Account,UserItem)){
        if(Equal_Str(Password,UserItem.Password)) return 1;//��¼�ɹ�
        else return -1;//�������
    }
    return 0;//�˺Ų�����
}

/*�û���¼����ӡUIҳ��,�ɹ�����1��ʧ�ܷ���0*/
int UserLogPrint(Users UserItems,User &UserItem){
    int FlagLog = 1,idx = 0;
        char Password[20],Account[30],c;
        do{
            LogPrintHomePage();
            if(FlagLog!=1){
                cout << "                        ";
                cout << "������˺Ŵ��������µ�¼" << endl ;
                idx = 0;//����
            }
            cout<< endl << endl  << "                     ";
            cout << "�˺ţ�" ;
            scanf("%s",Account);
            cout<< endl << endl  << "                     ";
            cout  << "���룺" ;
            while((c = getch()) != '\r'){
                if(c == '\b'){
                    if(idx != 0){
                        cout << '\b';
                        idx--;
                        continue;
                        }
                        else{
                            continue;
                        }
                }
                    Password[idx++] = c;
                    cout << '*';
            }
            Password[idx] = '\0';
            cout <<  endl ;
            FlagLog = UserLog(UserItems,UserItem,Account,Password);
        }while(FlagLog != 1);
        cout << endl << endl << endl ;
        cout << "                     ";
        cout << "  ��¼�У���ȴ�...  " << endl;
        Delay(500);
}

/*�޸����뺯��*/
int ModifyUserPassword(Users &UserItems,char *Account,char *Password){

    User UserItem;
    DeleteUser(UserItems,Account,UserItem);
    Assign_Char(UserItem.Password,Password);
    FILE *fp;
    fp = fopen("Users_DataBase.txt","a+");
    fwrite(&UserItem,sizeof(User),1,fp);
    CreateUserList(UserItems);//���³�ʼ������
    fclose(fp);
    return 1;
}

/*�޸�����ҳ��*/
int ModifyPasswordPage(Users &UserItems,User &UserItem){
    char TITLE[20] = "    �޸�����";
    char Password[20];
    Page_Head(TITLE,EMPTY);
    int idx = 0;
    char c,FirstInput[20],SecondInput[20];
    cout << "                     ";
    cout << "   ������ԭ���룺" ;
    while((c = getch()) != '\r'){
        if(c == '\b'){
            if(idx != 0){
                cout << '\b';
                idx--;
                continue;
            }
            else{
                continue;
            }
        }
        FirstInput[idx++] = c;
        cout << '*';
    }
    FirstInput[idx] = '\0';
    idx = 0;
    getchar();
    cout << endl << endl << "                     ";
    cout << "   �����������룺";
    while((c = getch()) != '\r'){
        if(c == '\b'){
            if(idx != 0){
                cout << '\b';
                idx--;
                continue;
            }
            else{
                continue;
            }
        }
        SecondInput[idx++] = c;
        cout << '*';
    }
    SecondInput[idx] = '\0';
    cout << endl;
    if(Len_Str(SecondInput)<6){
        cout << endl << endl << "                  ";
        cout << "�����޸�ʧ�ܣ����볤��Ӧ����6λ";
        cout << endl << endl << "                         ";
        cout << "1��󷵻��ҵĽ���";
        Delay(1000);
        return 0;
    }
    if(Equal_Str(FirstInput,UserItem.Password)){
        Assign_Char(Password,SecondInput);
        ModifyUserPassword(UserItems,UserItem.Account,Password);
        cout << endl << endl << "                  ";
        cout << "�����޸ĳɹ���1��󷵻ص�¼����";
        Delay(1000);
        return 1;
    }
    else{
        cout << "                     ";
        cout << "ԭ�����������1��󷵻��ҵĽ��棺";
        Delay(1000);
        return 0;
    }
}

/****************************�û����������*********************************/



/*******************************UI������************************************/
/*��ӡҳ��ͷ*/
void Page_Head(char *Title,char *Help){
    ClearScreen();
    cout << endl << "                     ";
    cout << "Auction management system" << Help << endl;
    cout << "                         ";
    cout << Title << endl;
    cout << "____________________________________________________________________" << endl << endl << endl<< endl;
}

/*��ҳ��ӡ*/
void PrintHomePage(){
    char TITLE[20] = "��Ʒ���Ĺ���ϵͳ";
    Page_Head(TITLE,Help);
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "Welcome to ���Ĺ���ϵͳ" << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  ��ѡ��Ҫ���еĲ����� " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "        1.��¼         " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "     2.���û�ע��      " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout <<"-------------------------" << endl << endl << endl ;
    cout << "____________________________________________________________________" << endl ;
    cout << "                     ";
    cout << "    2018.5.3 By XHSF      " << endl << endl << endl<< endl;
}

/*��ҳ��¼��*/
void LogPrintHomePage(){
    char TITLE[20] = "    ��½����";
    Page_Head(TITLE,EMPTY);
}

/*���û�ע��ҳ��*/
void RegisterPage(){
    char TITLE[20] = "    ע��ҳ��";
    Page_Head(TITLE,EMPTY);
}

/*��ҳ*/
void PrintIndexPage(){
    char TITLE[20] = "      ��ҳ";
    Page_Head(TITLE,Help);
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "    Welcome to ��ҳ    " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  ��ѡ��Ҫ���еĲ����� " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      1.��Ʒ����       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      2.��Ʒ�嵥       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "    3.�����д�����     " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "    4.�������ҳ��     " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "    5.�����ҵ�ҳ��     " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "-------------------------" << endl << endl << endl;
    cout << "____________________________________________________________________" << endl ;
    cout << "                     ";
    cout << "    2018.5.3 By XHSF      " << endl << endl << endl<< endl;
}

/*��ӡ��Ʒ����ҳ*/
void PrintIndex_SearchPage(){
    char TITLE[20] = "    ��Ʒ����";
    Page_Head(TITLE,Help);
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  Welcome to ����ҳ��  " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "   ��ѡ�����������ͣ�  " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "       1.�������      " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "       2.�������      " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "     3.��Ʒ��������    " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "       4.������ҳ      " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "-------------------------" << endl << endl << endl;
    cout << "____________________________________________________________________" << endl ;
    cout << "                     ";
    cout << "    2018.5.3 By XHSF      " << endl << endl << endl<< endl;
}

/*��ӡ��Ʒ�嵥ҳ��*/
void PrintIndex_ItemsPage(){
    char TITLE[20] = "    ��Ʒ�嵥";
    Page_Head(TITLE,EMPTY);
}

/*��ӡ�����д�����ҳ��*/
void PrintIndex_BigDataPage(){
    char TITLE[20] = "   �����д�����";
    Page_Head(TITLE,EMPTY);
}

/*��ӡ�ҵ�ҳ��*/
void PrintMyPage(){
    char TITLE[20] = "     �ҵ�ҳ��";
    Page_Head(TITLE,Help);
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  Welcome to �ҵ�ҳ��  " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  ��ѡ��Ҫ���еĲ����� " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      1.�޸�����       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      2.�ҵľ���       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      3.�˳���¼       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      4.������ҳ       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "-------------------------" << endl << endl << endl;
    cout << "____________________________________________________________________" << endl ;
    cout << "                     ";
    cout << "    2018.5.3 By XHSF      " << endl << endl << endl<< endl;
}

/*��ӡ�ҵľ���ҳ��*/
void PrintMyAuctions(auctionItems Items,User UserItem){
    char TITLE[20] = "     �ҵľ���";
    Page_Head(TITLE,EMPTY);
    if(!SearchByTheHighestBidder(Items,UserItem.Nickname,UserItem)){
        cout << endl << "                     ";
        cout << "---����ʱ��û�о�����Ʒ---"<< endl ;
    }
    cout << endl << "                     ";
    cout << "---������������ҵ�ҳ��---"<< endl ;
    getch();
}

/*��ӡ����Աҳ��*/
void PrintManagerPage(){
    char TITLE[20] = "    ����ҳ��";
    Page_Head(TITLE,Help);
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  Welcome to ����ҳ��  " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  ��ѡ��Ҫ���еĲ����� " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      1.¼����Ʒ       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      2.�޸���Ʒ       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      3.ɾ����Ʒ       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      4.��ʼ����       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      5.������ҳ       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "-------------------------" << endl << endl << endl;
    cout << "____________________________________________________________________" << endl ;
    cout << "                     ";
    cout << "    2018.5.3 By XHSF      " << endl << endl << endl<< endl;
}

/*����ҳ��*/
void PrintHelpPage(){
    ClearScreen();
    cout << endl << "                     ";
    cout << "Auction management system              " << endl;
    cout << "                         ";
    cout << "     ����ҳ��" << endl;
    cout << "____________________________________________________________________" << endl << endl << endl<< endl;
    cout << "           ";
    cout << "1.����һ���ṩ�û�������Ʒ���ĵ�ϵͳ���û�����"<< endl << "             " << "��ϵͳ�϶���Ʒ���г��ۣ��۸��ߵá�";
    cout << endl << endl << "           ";
    cout << "2.���ķ�ʽ�����ĺͰ������֡��ڰ��ķ�ʽ�£�����"<< endl << "             " << "���޷�������Ʒ��ǰ����߼ۡ�";
    cout << endl << endl << "           ";
    cout << "3.��ǰ�����н�֧��2��Ԫ���µĽ��ס�";
    cout << endl << endl << "           ";
    cout << "4.��ϵͳ����ȫ��ʹ�ü��̽��в�����������ꡣ";
    cout << endl << endl << "           ";
    cout << "5.�û����볤��Ӧ�ô��ڵ���6λ��";
    cout << endl << endl << "           ";
    cout << "6.ϵͳ�Զ������������ʱ�䣬ʱ�䵽�Զ�ȷ�ϵ�����";
    cout << endl << endl << endl << "                         ";
    cout << "�������������һҳ";
    getch();
}

/*���塢������ʼ��*/
void InitBackgroundAndFont(){
    SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE),15 | 8 | 128 | 64);//��ʼ������
    system("color F8");//��ʼ������
}
/*******************************UI������************************************/



int main()
{
    /*******************************����������************************************/
    auctionItems Items;//�û���
    auctionItem Item;//�û�
    auctionItem PreItem,PostItem;//�û���Ϣ�޸�ǰ��
    User UserItem;//�û�
    Users UserItems;//�û���
    char selection;//����ѡ��
    int Flag = 0;//��ʾ��־
    /*******************************����������************************************/


    /********************************��ʼ����*************************************/
    InitBackgroundAndFont();//��ʼ������ͱ���
    CreateUserList(UserItems);//��ʼ���û�����
    CreateAuctionList(Items);//��ʼ����Ʒ����
    CheckAuction(Items,Item);//����Ƿ���Խ�������������������������
    /********************************��ʼ����*************************************/





    /*******************************ҳ�������************************************/


    /*************��¼ҳ��*************/
    LogPage:
        PrintHomePage();
        if(Flag == 1){
            cout << "                     ";
            cout << " ѡ�����������ѡ�����" << endl ;
            Flag = 0;
        }
        selection = getch();
        switch(selection){
            case '1':
                    //��¼ҳ��
                    UserLogPrint(UserItems,UserItem);
                    Flag = 0;
                    goto IndexPage;
                    break;
            case '2':
                    //ע��ҳ��
                    Register(UserItems,UserItem);
                    Flag = 0;
                    goto IndexPage;
                    break;
            case 'h':
                    PrintHelpPage();
                    Flag = 0;
                    goto LogPage;
                    break;
            case 'H':
                    PrintHelpPage();
                    Flag = 0;
                    goto LogPage;
                    break;
            default:
                    //��������ѡ��
                    Flag = 1;
                    goto LogPage;
        }

    /*************��¼ҳ��*************/
    /***************��ҳ***************/
    IndexPage:
        PrintIndexPage();
        if(Flag == 1){
            cout << "                     ";
            cout << " ѡ�����������ѡ�����" << endl ;
            Flag = 0;
        }
        selection = getch();
        switch(selection){
            case '1':
                    //����ҳ��
                    SearchPage(Items,Item,UserItem);
                    Flag = 0;
                    goto IndexPage;
                    break;
            case '2':
                    //��Ʒ�嵥ҳ��
                    ItemsPage(Items,Item,UserItem);
                    Flag = 0;
                    goto IndexPage;
                    break;
            case '3':
                    //������ҳ��
                    BigDataPage(Items,UserItems,Item);
                    Flag = 0;
                    goto IndexPage;
                    break;
            case '4':
                    //��ת������Աҳ��
                    Flag = 0;
                    goto ManagerPage;
                    break;
            case '5':
                    //��ת���ҵ�ҳ��
                    Flag = 0;
                    goto MyPage;
                    break;
            case 'h':
                    PrintHelpPage();
                    Flag = 0;
                    goto IndexPage;
                    break;
            case 'H':
                    PrintHelpPage();
                    Flag = 0;
                    goto IndexPage;
                    break;
            default:
                    //��������ѡ��
                    Flag = 1;
                    goto IndexPage;
        }
    /***************��ҳ***************/
    /***************����ҳ��***************/
    ManagerPage:
        PrintManagerPage();
        if(Flag == 1){
            cout << "                     ";
            cout << " ѡ�����������ѡ�����" << endl ;
            Flag = 0;
        }
        selection = getch();
        switch(selection){
            case '1':
                    //������Ʒ
                    AddAuctionItemPage(Items,Item,UserItem);
                    Flag = 0;
                    goto ManagerPage;
                    break;
            case '2':
                    //�޸���Ʒ
                    ModifyItemAndPage(Items,UserItem);
                    Flag = 0;
                    goto ManagerPage;
                    break;
            case '3':
                    //ɾ����Ʒ
                    DeleteItemPage(Items,UserItem);
                    Flag = 0;
                    goto ManagerPage;
                    break;
            case '4':
                    //��ʼ����
                    StartAuctionPage(Items,UserItem);
                    Flag = 0;
                    goto ManagerPage;
                    break;
            case '5':
                    //��ת����ҳ
                    Flag = 0;
                    goto IndexPage;
                    break;
            case 'h':
                    PrintHelpPage();
                    Flag = 0;
                    goto ManagerPage;
                    break;
            case 'H':
                    PrintHelpPage();
                    Flag = 0;
                    goto ManagerPage;
                    break;
            default:
                    //��������ѡ��
                    Flag = 1;
                    goto ManagerPage;
        }

    /***************����ҳ��***************/
    /***************�ҵ�ҳ��***************/
    MyPage:
        PrintMyPage();
        if(Flag == 1){
            cout << "                     ";
            cout << " ѡ�����������ѡ�����" << endl ;
            Flag = 0;
        }
        selection = getch();
        switch(selection){
            case '1':
                    //�޸�����
                    if(ModifyPasswordPage(UserItems,UserItem)){//�ɹ�
                        Flag = 0;
                        CreateUserList(UserItems);//��ʼ���û�����
                        CreateAuctionList(Items);//��ʼ����Ʒ����
                        goto LogPage;
                    }else{//ʧ��
                        Flag = 0;
                        goto MyPage;
                    }
                    break;
            case '2':
                    //�ҵľ���
                    PrintMyAuctions(Items,UserItem);
                    Flag = 0;
                    goto MyPage;
                    break;
            case '3':
                    //�˳���¼
                    CreateUserList(UserItems);//��ʼ���û�����
                    CreateAuctionList(Items);//��ʼ����Ʒ����
                    cout << "                     ";
                    cout << "  �˳���¼�У���Ⱥ�..." << endl ;
                    Delay(1000);
                    Flag = 0;
                    goto LogPage;
                    break;
            case '4':
                    //��ת����ҳ
                    goto IndexPage;
                    Flag = 0;
                    break;
            case 'h':
                    PrintHelpPage();
                    Flag = 0;
                    goto MyPage;
                    break;
            case 'H':
                    PrintHelpPage();
                    Flag = 0;
                    goto MyPage;
                    break;
            default:
                    //��������ѡ��
                    Flag = 1;
                    goto MyPage;
        }
    /***************�ҵ�ҳ��***************/


    /*******************************ҳ�������************************************/
}
