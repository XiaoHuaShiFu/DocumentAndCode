/****************************头文件区*********************************/
#include <iostream>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <malloc.h>
#include <sstream>
#include <conio.h>
#include <windows.h> //Sleep()函数,字体颜色
#include <time.h>
#include <ctime>
using namespace std;
/****************************头文件区*********************************/



/****************************宏定义区*********************************/
#define ERROR 0
#define OK 1
#define ElemType int
typedef struct auctionItem{
    long Id;//编号
    char Category[20];//类别
    char Name[30];//名称
    char BiddingMode[5];//明拍和暗拍
    long TheHighestPrice;//最高价
    char TheHighestBidder[30];//最高出价者
    long Evaluation;//估价
    long StartingPrice;//起拍价
    char OldAndNew[3];//新旧
    char Description[101];//物品描述
    char State[10];//物品状态 竞拍中，已成交
    char Owner[30];//物主
    long long EndTime;//结束时间
    struct auctionItem *next;
} auctionItem,*auctionItems;
typedef struct User{
    char Account[30];//账号
    char Password[20];//密码
    char Nickname[20];//昵称
    struct User *next;
}User,*Users;
/****************************宏定义区*********************************/



/****************************全局变量区*******************************/
char Empty[6] = "Empty";
char a1[30] = "请输入物品编号:";
char a2[30] = "请输入物品类别:";
char a3[30] = "请输入物品名称:";
char a4[30] = "请输入物品价格:";
char a5[30] = "请输入物品估价:";
char a6[30] = "请输入物品新旧:";
char a7[30] = "请输入物品描述:";
char a8[30] = "请输入物品状态:";
char a9[30] = "请输入物品拥有者:";
char a10[30] = "请输入物品起拍价:";
char a11[30] = "1:明拍      2:暗拍";
char a12[30] = "请输入物品最高出价者:";
char a13[30] = "请输入物品当前最高价格:";
char a14[30] = "请输入物品竞拍时长(单位小时):";
char b1[30] = "请输入账号:";
char b2[30] = "请输入昵称:";
char b3[30] = "请输入密码:";
char b4[30] = "请再次输入密码:";
char b5[30] = "请输入新密码:";
char b6[30] = "请再次输入新密码:";
char Traded[7] = "已成交";
char Auctioning[7] = "拍卖中";
char Light[5] = "明拍";
char Manager[5] = "root";
char Not[3] = "无";
char Auctioneer[13] = "拍卖行管理员";
char Help[30] = "              H.帮助";
char EMPTY[2] = " ";
char dash[2] = "-";
char colon[2] = ":";
/****************************全局变量区*******************************/



/**************************通用函数声明区*****************************/
/*比较两个字符串是否相等，相等返回1，不相等返回0*/
int Equal_Str(char str1[],char str2[]);

/*计算字符串长度，返回字符串长度*/
int Len_Str(char str[]);

/*延时函数，参数单位为毫秒*/
void Delay(int seconds);

/*生成时间类型字符串*/
void CreateTime(long long seconds,char *Time);

/*获取当前月份的天数*/
int GetDay(int month);

/*获取当前时间*/
long long GetTheCurrentTime();

/*获取当前月份*/
int GetMonth(long Seconds);

/*输出提示并输入整形数据*/
void Input_Int(long &Int,char prompt[]);

/*输出提示并输入字符串型数据*/
void Input_Str(char *Char,char prompt[]);

/*输出提示并输入密码,成功录入返回1，失败返回0*/
int Input_Password(char *Password,char prompt1[],char prompt2[]);

/*整形赋值*/
void Assign_Int(long &Int,long Number);

/*字符串型赋值*/
void Assign_Char(char *Char,char Str[]);

/*清空屏幕*/
void ClearScreen();
/**************************通用函数声明区*****************************/



/**************************物品函数声明区*****************************/
/*返回拍卖品数量*/
int AuctionItemQuantity(auctionItems Items);

/*把物品信息输入文件中*/
void AddAuctionItemToFile();

/*添加物品信息*/
void AddAuctionItem(auctionItems &Items,auctionItem &Item,User UserItem);

/*初始化拍卖品链表*/
void CreateAuctionList(auctionItems &Items);

/*打印拍卖品*/
int PrintAuctionItem(auctionItem Item,User UserItem);

/*打印拍卖品列表*/
int PrintAuctionItems(auctionItems Items,User UserItem);

/*删除拍卖品中某个物品的数据*/
int DeleteItem(auctionItems Items,long Id,auctionItem &Item);

/*删除物品信息并打印删除结果*/
void DeleteItemAndPrint(auctionItems &Items,long Id,auctionItem &Item,User UserItem);

/*删除物品信息页面*/
int DeleteItemPage(auctionItems &Items,User UserItem);

/*修改某个拍卖品的信息*/
int ModifyItem(auctionItems &Items,long Id,auctionItem &PreItem,auctionItem &PostItem);

/*修改某个拍卖品的信息并打印*/
int ModifyItemAndPage(auctionItems &Items,User UserItem);

/*通过Id寻找物品，找不到返回0，找到返回1*/
int SearchById(auctionItems Items,long Id,auctionItem &Item);

/*通过Name寻找物品，找不到返回0，找到返回1*/
int SearchByName(auctionItems Items,char Name[],auctionItem &Item);

/*通过Category寻找物品，找不到返回0，找到返回找到的个数*/
int SearchByCategory(auctionItems Items,char Category[],User UserItem);

/*通过State寻找物品，找不到返回0，找到返回找到的个数*/
int SearchByState(auctionItems Items,char State[],User UserItem);

/*通过TheHighestBidder寻找物品，找不到返回0，找到返回找到的个数*/
int SearchByTheHighestBidder(auctionItems Items,char TheHighestBidder[],User UserItem);

/*通过Owner寻找物品，找不到返回0，找到返回找到的个数*/
int SearchByOwner(auctionItems Items,char Owner[],User UserItem);

/*返回当前拍卖行中拍卖品的最高拍卖价格*/
long TheHighestPriceInAuctionHouse(auctionItems Items,auctionItem &Item);

/*返回当前拍卖行中拍卖品的最低拍卖价格*/
long TheLowestPriceInAuctionHouse(auctionItems Items,auctionItem &Item);

/*返回当前拍卖行中拍卖品的总成交额*/
long TurnoverInAuctionHouse(auctionItems Items);

/*返回当前拍卖行中拍卖品的总成交数or正在拍卖数*/
int TurnoverNumberInAuctionHouse(auctionItems Items,char State[]);

/*开始拍卖，成功返回1，失败返回0*/
int StartAuction(auctionItem &Item);

/*检查是否可以进行拍卖，如果可以则进行拍卖*/
void CheckAuction(auctionItems &Items,auctionItem &Item);

/*开始竞拍页面*/
int StartAuctionPage(auctionItems &Items,User UserItem);

/*物品搜索页面*/
int SearchPage(auctionItems &Items,auctionItem &Item,User UserItem);

/*物品清单页面*/
int ItemsPage(auctionItems &Items,auctionItem &Item,User UserItem);

/*用户竞拍*/
int UserBid(auctionItems &Items,auctionItem &Item,User UserItem);

/*拍卖行大数据页面*/
int BigDataPage(auctionItems Items,Users UserItems,auctionItem Item);

/**************************物品函数声明区*****************************/



/**************************用户函数声明区*****************************/
/*向文件添加用户信息*/
void AddUserToFile(User &UserItem);

/*添加用户信息*/
void AddUser(User &UserItem);

/*新用户注册*/
void Register(Users &UserItems,User &UserItem);

/*删除用户信息*/
int DeleteUser(Users UserItems,char *UserAccount,User &UserItem);

/*删除用户信息后并重新创建链表*/
int DeleteUserAndCreateList(Users &UserItems,char *UserAccount,User &UserItem);

/*初始化用户链表*/
void CreateUserList(Users &UserItems);

/*通过Account寻找用户，找不到返回0，找到返回1*/
int SearchByAccount(Users UserItems,char Account[],User &UserItem);

/*通过Nickname寻找用户，找不到返回0，找到返回1*/
int SearchByNickname(Users UserItems,char Nickname[],User &UserItem);

/*用户登录，成功返回1，密码错误返回-1，账号不存在返回0*/
int UserLog(Users UserItems,User &UserItem);

/*用户登录并打印UI页面,成功返回1，失败返回0*/
int UserLogPrint(Users UserItems,User &UserItem);

/*打印用户信息*/
void PrintUser(User UserItem);

/*打印用户信息列表*/
int PrintUsers(Users UserItems);

/*返回当前拍卖行中的用户数*/
int UserNumberInAuctionHouse(Users UserItems);

/*修改密码函数*/
int ModifyUserPassword(Users &UserItems,char *Account,char *Password);

/*修改密码页面*/
int ModifyPasswordPage(Users &UserItems,User &UserItem);

/*录入物品信息页面*/
void AddAuctionItemPage(auctionItems &Items,auctionItem &Item);

/**************************用户函数声明区*****************************/



/*****************************UI声明区********************************/
/*打印页面头*/
void Page_Head(char *Title,char *Help);

/*首页打印*/
void PrintHomePage();

/*首页登录框*/
void LogPrintHomePage();

/*新用户注册页面*/
void RegisterPage();

/*主页*/
void PrintIndexPage();

/*主页物品搜索页*/
void PrintIndex_SearchPage();

/*物品清单页面*/
void PrintIndex_ItemsPage();

/*打印拍卖行大数据页面*/
void PrintIndex_BigDataPage();

/*打印我的页面*/
void PrintMyPage();

/*打印我的竞拍页面*/
void PrintMyAuctions(auctionItems Items,User UserItem);

/*帮助页面*/
void PrintHelpPage();

/*字体、背景初始化*/
void InitBackgroundAndFont();
/*****************************UI声明区********************************/



/****************************通用函数区*******************************/
/*比较两个字符串是否相等，相等返回1，不相等返回0*/
int Equal_Str(char str1[],char str2[]){
    if(strcmp(str1,str2) == 0) return 1;
    return 0;
}

/*计算字符串长度，返回字符串长度*/
int Len_Str(char str[]){
    return strlen(str);
}

/*延时函数，参数单位为毫秒*/
void Delay(int seconds){
    Sleep(seconds);
}

/*获取当前月份的天数*/
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

/*获取当前时间*/
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

/*获取当前月份*/
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

/*生成时间类型字符串*/
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

/*输出提示并输入整形数据*/
void Input_Int(long &Int,char prompt[]){
    if(Equal_Str(prompt,Empty)){
        scanf("%ld",&Int);
    }
    else{
        cout << prompt;
        scanf("%ld",&Int);
    }
}

/*输出提示并输入字符串型数据*/
void Input_Str(char *Char,char prompt[]){
    if(Equal_Str(prompt,Empty)){
        scanf("%s",Char);
    }
    else{
        cout << prompt;
        scanf("%s",Char);
    }
}

/*输出提示并输入密码,成功录入返回1，失败返回0*/
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

/*整形赋值*/
void Assign_Int(long &Int,long Number){
    Int = Number;
}

/*字符串型赋值*/
void Assign_Char(char *Char,char *Str){
    strcpy(Char,Str);
}

/*清空屏幕*/
void ClearScreen(){
    system("cls");
}
/****************************通用函数区*******************************/



/****************************拍卖品链表操作区*******************************/

/*返回拍卖品数量*/
int AuctionItemQuantity(auctionItems Items){
    int Quantity = 0;
    while(Items -> next){
        Items = Items -> next;
        Quantity++;
    }
    return Quantity;
}

/*把物品信息输入文件中*/
void AddAuctionItemToFile(auctionItem &Item){
    FILE *fp;
    fp=fopen("auctionItems_DataBase.txt","a+");
    fwrite(&Item,sizeof(auctionItem),1,fp);
    fclose(fp);
}

/*添加物品信息*/
void AddAuctionItem(auctionItems &Items,auctionItem &Item,User UserItem){
    int Flag = 0;
    char selection;
    long long hour;
    char Bright[5] = "明拍",Dark[5] = "暗拍";
    char New[3] = "新",Old[3] = "旧";
    cout << "                     ";
    do{
        if(Flag == 1){
            cout << endl  << "                     " << "该物品编号已存在，请重新输入"<< endl << endl << "                     ";
        }
        Input_Int(Item.Id,a1);
        Flag = SearchById(Items,Item.Id,Item);
    }while(Flag == 1);
    cout << endl << "                     ";
    Input_Str(Item.Category,a2);
    cout << endl << "                     ";
    Input_Str(Item.Name,a3);
    cout << endl << "                     " << "请选择物品拍卖模式:" << endl << endl << "                     " << a11;
    do{
        if(Flag == 1){
            cout << endl << endl << "                     ";
            cout << "请选择正确的拍卖模式" << endl << endl << "                     " << a11;
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
    cout << endl << "                     " << "请选择物品新旧状态:" << endl << endl << "                     " << "1:新          2:旧";
    do{
        if(Flag == 1){
            cout << endl << endl << "                     ";
            cout << "请选择正确的新旧状态" << endl << endl << "                     " << "1:新          2:旧";
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

/*初始化拍卖品链表*/
void CreateAuctionList(auctionItems &Items){
    FILE *fp;
    fp = fopen("auctionItems_DataBase.txt","rb");
    auctionItems Pointer,PointerBridge;
    auctionItem temp;//定义结构体别名
    Items = (auctionItems)malloc(sizeof(auctionItem));
    Items->next = NULL;
    Pointer = (auctionItems)malloc(sizeof(auctionItem));
    Pointer = Items;
    while(fread(&temp,sizeof(auctionItem),1,fp)!=0){//从文件中读结构体块
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

/*打印拍卖品，返回1*/
int PrintAuctionItem(auctionItem Item,User UserItem){
    char Time[64];
    CreateTime(Item.EndTime,Time);
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "------物品信息如下-------" << endl ;
    cout << "                     ";
    cout << " 编号：" << Item.Id << endl ;
    cout << "                     ";
    cout << " 类型：" << Item.Category << endl ;
    cout << "                     ";
    cout << " 名称：" << Item.Name << endl ;
    cout << "                     ";
    cout << " 拍卖方式：" << Item.BiddingMode << endl ;
    if(Equal_Str(Item.BiddingMode,Light) || Equal_Str(UserItem.Account,Manager) || Equal_Str(Item.State,Traded)){
        cout << "                     ";
        cout << " 最高价：" << Item.TheHighestPrice << endl ;
        cout << "                     ";
        cout << " 最高出价者：" << Item.TheHighestBidder << endl ;
        cout << "                     ";
        cout << " 估价：" << Item.Evaluation << endl ;
        cout << "                     ";
        cout << " 起拍价：" << Item.StartingPrice << endl ;
    }
    cout << "                     ";
    cout << " 新旧：" << Item.OldAndNew << endl ;
    cout << "                     ";
    cout << " 描述：" << Item.Description << endl ;
    cout << "                     ";
    cout << " 状态：" << Item.State << endl ;
    cout << "                     ";
    cout << " 拥有者：" << Item.Owner << endl ;
    cout << "                     ";
    cout << " 拍卖结束时间：" << Time << endl ;
    cout << "                     ";
    cout << "---From Auction By XHSF--" << endl ;
    cout << "                     ";
    cout << "-------------------------" << endl;
    return 1;
}

/*打印拍卖品列表，空表返回0，打印成功返回1*/
int PrintAuctionItems(auctionItems Items,User UserItem){
    if(Items->next == NULL) return 0;
    while(Items->next != NULL){
        Items = Items->next;
        PrintAuctionItem(*Items,UserItem);
    }
    return 1;
}

/*删除拍卖品中某个物品的数据，原文件为空返回-2，
找不到要删除的物品返回-1，文件打开失败返回0，修改成功返回1*/
int DeleteItem(auctionItems Items,long Id,auctionItem &Item){
    int Flag = 0;
    FILE *Prototype,*Temporary;
    Prototype = fopen("auctionItems_DataBase.txt","rb");
    Temporary = fopen("auctionItems_DataBase_Temporary.txt","a+");
    if(Prototype == NULL || Temporary == NULL){
        fclose(Prototype);
        fclose(Temporary);
        return 0; //排除打开文件失败
    }
    if(Items->next == NULL){
        fclose(Prototype);
        fclose(Temporary);
        return -2;//排除原链表为空
    }
    while(Items->next != NULL){
        Items = Items->next;
        if(Items -> Id != Id) fwrite(&*Items,sizeof(auctionItem),1,Temporary);//把不是要删除的节点写入临时文件
        else{
            Item = *Items;//返回要删除节点的信息
            Flag = 1;
        }
    }
    fclose(Prototype);
    fclose(Temporary);
    remove("auctionItems_DataBase.txt");//删除原文件
    rename("auctionItems_DataBase_Temporary.txt","auctionItems_DataBase.txt");//把临时文件名改为原文件名
    if(Flag == 0) return -1; //如果没有找到所要的编号 则返回1
    return 1;//找到则返回1
}

/*删除物品信息并打印删除结果，删除失败返回0，删除成功返回1*/
void DeleteItemAndPrint(auctionItems &Items,long Id,auctionItem &Item,User UserItem){
    int ReturnValue = DeleteItem(Items,Id,Item);
    if(ReturnValue == -2){
        cout << "原文件为空" << endl;
//        return 0;
    }
    else if(ReturnValue == -1){
        cout << "找不到编号为" << Id << "的物品" << endl;
//        return 0;
    }
    else if(ReturnValue == 0){
        cout << "文件打开失败" << endl;
//        return 0;
    }
    else{
        CreateAuctionList(Items);
        cout << "-----------删除后文件列表-----------" << endl;
        PrintAuctionItems(Items,UserItem);
        cout << "-----------删除的物品信息-----------" << endl;
        PrintAuctionItem(Item,UserItem);
//        return 1;
    }
}

/*删除物品信息页面*/
int DeleteItemPage(auctionItems &Items,User UserItem){
    long Id;
    int Flag = 1;
    auctionItem Item;
    char TITLE[20] = "删除物品信息界面";
    char selection;
    Page_Head(TITLE,EMPTY);
    cout << endl << "                     " << "请输入要删除物品的编号:" ;
    do{
        if(!Flag){
            cout << endl << "                     " << "请输入正确的物品编号:" ;
        }
        scanf("%ld",&Id);
        if(SearchById(Items,Id,Item)&&Equal_Str(Item.Owner,UserItem.Nickname)){
            PrintAuctionItem(Item,UserItem);
            cout << endl << "                     " << "1：确认删除 2：放弃删除" ;
            do{
                if(!Flag){
                    cout << endl << endl << "                     " << "    请选择正确的操作" ;
                    cout << endl << endl << "                     " << "1：确认删除 2：放弃删除" ;
                }
                selection = getch();
                switch(selection){
                    case '1':
                            DeleteItem(Items,Id,Item);
                            cout << endl << endl << "                     " << "删除成功，1秒后放回上一页" ;
                            Delay(1000);
                            return 1;
                    case '2':
                            cout << endl << endl << "                     " << "删除失败，1秒后放回上一页" ;
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

/*修改某个拍卖品的信息，如果成功修改返回1，找不到要修改的物品返回0*/
int ModifyItem(auctionItems &Items,long Id,auctionItem &PreItem,auctionItem &PostItem){
    if(DeleteItem(Items,Id,PreItem) == -2){//删除原物品信息
        cout << "----------------找不到要修改的物品-----------------" << endl;
        return 0;
    }
    AddAuctionItemToFile(PostItem);//输入修改后的信息
    CreateAuctionList(Items);//重新初始化链表
    return 1;
}

/*修改物品信息页面*/
int ModifyItemAndPage(auctionItems &Items,User UserItem){
    char TITLE[20] = "修改物品信息界面";
    Page_Head(TITLE,EMPTY);
    auctionItem PostItem,PreItem;
    int Flag = 1;
    char selection;
    char Bright[5] = "明拍",Dark[5] = "暗拍";
    char New[3] = "新",Old[3] = "旧";
    char Auctioning[7] = "拍卖中",Dealt[7] = "已成交";
    cout << "                     ";
    do{
        if(Flag == 0){
            cout << endl  << "                     " << "该物品编号不存在，请重新输入"<< endl << endl << "                     ";
        }
        Input_Int(PostItem.Id,a1);
        Flag = SearchById(Items,PostItem.Id,PostItem);
        Flag = Equal_Str(PostItem.Owner,UserItem.Nickname);
    }while(Flag == 0);
    cout << endl << "                     " << "         修改前" << endl;
    PrintAuctionItem(PostItem,UserItem);
    Flag = 0;
    cout << endl << "                     ";
    Input_Str(PostItem.Category,a2);
    cout << endl << "                     ";
    Input_Str(PostItem.Name,a3);
    cout << endl << "                     " << "请选择物品拍卖模式:" << endl << endl << "                     " << a11;
    do{
        if(Flag == 1){
            cout << endl << endl << "                     ";
            cout << "请选择正确的拍卖模式" << endl << endl << "                     " << a11;
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
    cout << endl << "                     " << "请选择物品新旧状态:" << endl << endl << "                     " << "1:新          2:旧";
    do{
        if(Flag == 1){
            cout << endl << endl << "                     ";
            cout << "请选择正确的新旧状态" << endl << endl << "                     " << "1:新          2:旧";
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
    cout << endl << endl << "                     " << "请选择物品拍卖状态:" << endl << endl << "                     " << "1:拍卖中  2:已成交";
    do{
        if(Flag == 1){
            cout << endl << endl << "                     ";
            cout << "请选择正确的拍卖状态" << endl << endl << "                     " << "1:拍卖中  2:已成交";
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
    cout << endl << "                     " << "         修改后" << endl;
    PrintAuctionItem(PostItem,UserItem);
    cout << endl << "                     " << "    请确认是否要修改" << endl;
    cout << endl << "                     " << "1：确认修改 2：放弃修改" << endl;
    selection = getch();
    do{
        if(Flag == 1){
            cout << endl << "                     ";
            cout << "    请选择正确的操作  " << endl << endl << "                     " << "1：确认修改 2：放弃修改";
        }
        selection = getch();
        if(selection == '1'){
            DeleteItem(Items,PostItem.Id,PreItem);
            AddAuctionItemToFile(PostItem);//输入修改后的信息
            CreateAuctionList(Items);//重新初始化链表
            cout << endl << endl << "                  " << "修改成功，1秒后返回上一级页面" << endl;
            Delay(1000);
            return 1;
        }else if(selection =='2'){
            cout << endl << endl << "                  " << "修改失败，1秒后返回上一级页面" << endl;
            Delay(1000);
            return 1;
        }else{
            Flag = 1;
        }
    }while(Flag);
}

/*通过Id寻找物品，找不到返回0，找到返回1*/
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

/*通过Name寻找物品，找不到返回0，找到返回1*/
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

/*通过Category寻找物品，找不到返回0，找到返回找到的个数*/
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

/*通过State寻找物品，找不到返回0，找到返回找到的个数*/
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

/*通过TheHighestBidder寻找物品，找不到返回0，找到返回找到的个数*/
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

/*通过Owner寻找物品，找不到返回0，找到返回找到的个数*/
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

/*返回当前拍卖行中拍卖品的最高拍卖价格*/
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

/*返回当前拍卖行中拍卖品的最低拍卖价格*/
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

/*返回当前拍卖行中拍卖品的总成交额*/
long TurnoverInAuctionHouse(auctionItems Items){
    long Turnover = 0;
    while(Items -> next != NULL){
        Items = Items -> next;
        if(Equal_Str(Items -> State,Traded)) Turnover += (Items -> TheHighestPrice);
    }
    return Turnover;
}

/*返回当前拍卖行中拍卖品的总成交数or正在拍卖数*/
int TurnoverNumberInAuctionHouse(auctionItems Items,char State[]){
    long TurnoverNumber = 0;
    while(Items -> next != NULL){
        Items = Items -> next;
        if(Equal_Str(Items -> State,State)) TurnoverNumber++;
    }
    return TurnoverNumber;
}

/*开始拍卖，成功返回1，失败返回0*/
int StartAuction(auctionItems &Items,auctionItem &Item){
    if(Item.TheHighestPrice >= Item.StartingPrice){
        DeleteItem(Items,Item.Id,Item);
        Assign_Char(Item.Owner,Item.TheHighestBidder);
        Assign_Char(Item.State,Traded);
        FILE *fp;
        fp=fopen("auctionItems_DataBase.txt","a+");
        fwrite(&Item,sizeof(auctionItem),1,fp);
        fclose(fp);
        CreateAuctionList(Items);//重新初始化链表
        return 1;//拍卖成功返回1
    }
    else{
        return 0;//拍卖失败
    }
}

/*检查是否可以进行拍卖，如果可以则进行拍卖*/
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

/*开始竞拍页面*/
int StartAuctionPage(auctionItems &Items,User UserItem){
    char root[5] = "root";
    long Id;
    int Flag = 1;
    auctionItem Item;
    char TITLE[20] = "   开始竞拍界面";
    char selection;
    Page_Head(TITLE,EMPTY);
    if(!Equal_Str(UserItem.Nickname,root)){
        cout << endl << "                     " << "非管理员无法进行竞拍" ;
        cout << endl << endl << "                       " << "1秒后返回上一页" ;
        Delay(1000);
        return 1;
    }
    cout << endl << "                     " << "请输入要竞拍物品的编号:" ;
    do{
        if(!Flag){
            cout << endl << "                     " << "请输入正确的物品编号:" ;
        }
        scanf("%ld",&Id);
        if(SearchById(Items,Id,Item)){
            if(!Equal_Str(Item.Owner,UserItem.Nickname)){
                cout << "               ";
                cout <<"您不是该物品的卖家，1秒后返回上一页";
                Delay(1000);
                return 1;
            }
            PrintAuctionItem(Item,UserItem);
            if(Equal_Str(Item.State,Traded)){
                cout << endl << endl << "             " << "竞拍失败，物品已交易，1秒后返回上一页" ;
                Delay(1000);
                return 1;
            }
            cout << endl << "                     " << "1：确认竞拍 2：放弃竞拍" ;
            do{
                if(!Flag){
                    cout << endl << endl << "                     " << "    请选择正确的操作" ;
                    cout << endl << endl << "                     " << "1：确认竞拍 2：放弃竞拍" ;
                }
                selection = getch();
                switch(selection){
                    case '1':
                            StartAuction(Items,Item);
                            cout << endl << endl << "                     " << "竞拍成功，物品的得主是：" << Item.TheHighestBidder;
                            cout << endl << endl << "                     " << "  按任意键返回上一页" ;
                            getch();
                            Delay(1000);
                            return 1;
                    case '2':
                            cout << endl << endl << "                     " << "竞拍失败，1秒后放回上一页" ;
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

/*用户竞拍，出价成功返回1，失败返回0*/
int UserBid(auctionItems &Items,auctionItem &Item,User UserItem){
    long Price;
    char Light[5] = "明拍";
    if(Equal_Str(Item.BiddingMode,Light)){
        cout << endl << endl << "                     ";
        cout << "------物品信息如下-------" << endl << endl ;
        cout << "                     ";
        cout << " 当前物品最高价：" << Item.TheHighestPrice << endl << endl ;
        cout << "                     ";
        cout << " 当前物品起拍价：" << Item.StartingPrice << endl << endl ;
        cout << "                     ";
        cout << " 当前物品估价：" << Item.Evaluation << endl << endl ;
    }else{
        cout << endl << endl << "                     ";
        cout << "------物品进行暗拍-------" << endl << endl;
    }
    cout << "                ";
    cout << "注：拍卖行当前不支持超过2亿元的交易" << endl << endl;
    cout << "                     ";
    cout << " 请输入您的出价：";
    scanf("%ld",&Price);
    if(Price > Item.TheHighestPrice && Price > Item.StartingPrice && Price <= 2000000000){
        DeleteItem(Items,Item.Id,Item);
        Assign_Char(Item.TheHighestBidder,UserItem.Nickname);
        Assign_Int(Item.TheHighestPrice,Price);
        FILE *fp;
        fp=fopen("auctionItems_DataBase.txt","a+");
        fwrite(&Item,sizeof(auctionItem),1,fp);
        fclose(fp);
        CreateAuctionList(Items);//重新初始化链表
        return 1;//出价成功返回1
    }
    else{
        return 0;//
    }
}

/*物品搜索页面*/
int SearchPage(auctionItems &Items,auctionItem &Item,User UserItem){
    PrintIndex_SearchPage();
    char Selection;
    char TITLE[20] = "    物品搜索";
    int Id,Flag = 0;
    char Name[30],Category[20];
    do{
        if(Flag == 1){
            cout << "                     ";
            cout << "     请选择正确操作" << endl;
        }
        Selection = getch();
        switch(Selection){
        case '1':
                Page_Head(TITLE,EMPTY);
                cout << "                     ";
                cout << "   请输入物品编号：";
                scanf("%ld",&Id);
                if(SearchById(Items,Id,Item)){
                    if(Equal_Str(Item.Owner,UserItem.Nickname)){
                        cout << "                 ";
                        cout <<"无法竞拍自己的物品，1秒后返回主页";
                        Delay(1000);
                        return 1;
                    }
                    PrintAuctionItem(Item,UserItem);
                    cout << "                     ";
                    cout <<"按1进行竞拍，按2返回主页";
                    Selection = getch();
                    switch(Selection){
                        case '1':
                                if(UserBid(Items,Item,UserItem)){
                                    cout << endl << "                     ";
                                    cout <<"竞拍成功，1秒后返回主页";
                                    Delay(1000);
                                    return 1;
                                }else{
                                    cout << endl << "                     ";
                                    cout <<"竞拍失败，1秒后返回主页";
                                    Delay(1000);
                                    return 1;
                                }
                                break;
                        case '2':return 1;break;
                    }

                }else{
                    cout << "                     ";
                    cout <<"搜索失败，1秒后返回主页";
                    Delay(1000);
                    return 1;
                }
                Flag = 0;
                break;
        case '2':
                Page_Head(TITLE,EMPTY);
                cout << "                     ";
                cout << "   请输入物品类别：";
                scanf("%s",Category);
                if(SearchByCategory(Items,Category,UserItem)){
                    cout << "                     ";
                    cout <<"按1进行竞拍，按2返回主页";
                    Selection = getch();
                    switch(Selection){
                        case '1':
                                cout << endl << "                     ";
                                cout << "   请输入物品编号：";
                                scanf("%ld",&Id);
                                SearchById(Items,Id,Item);
                                if(Equal_Str(Item.Owner,UserItem.Nickname)){
                                    cout << "                 ";
                                    cout <<"无法竞拍自己的物品，1秒后返回主页";
                                    Delay(1000);
                                    return 1;
                                }
                                if(UserBid(Items,Item,UserItem)){
                                    cout << "                     ";
                                    cout <<"竞拍成功，1秒后返回主页";
                                    Delay(1000);
                                    return 1;
                                }else{
                                    cout << "                     ";
                                    cout <<"竞拍失败，1秒后返回主页";
                                    Delay(1000);
                                    return 1;
                                }
                                break;
                        case '2':return 1;break;
                    }

                }else{
                    cout << "                     ";
                    cout <<"搜索失败，1秒后返回主页";
                    Delay(1000);
                    return 1;
                }
                Flag = 0;
                break;
        case '3':
                Page_Head(TITLE,EMPTY);
                cout << "                     ";
                cout << "   请输入物品名称：";
                scanf("%s",Name);
                if(SearchByName(Items,Name,Item)){
                    if(Equal_Str(Item.Owner,UserItem.Nickname)){
                        cout << "                 ";
                        cout <<"无法竞拍自己的物品，1秒后返回主页";
                        Delay(1000);
                        return 1;
                    }
                    PrintAuctionItem(Item,UserItem);
                    cout << "                     ";
                    cout <<"按1进行竞拍，按2返回主页";
                    Selection = getch();
                    switch(Selection){
                        case '1':
                                if(UserBid(Items,Item,UserItem)){
                                    cout << "                     ";
                                    cout <<"竞拍成功，1秒后返回主页";
                                    Delay(1000);
                                    return 1;
                                }else{
                                    cout << "                     ";
                                    cout <<"竞拍失败，1秒后返回主页";
                                    Delay(1000);
                                    return 1;
                                }
                                break;
                        case '2':return 1;break;
                    }

                }else{
                    cout << "                     ";
                    cout <<"搜索失败，1秒后返回主页";
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

/*物品清单页面*/
int ItemsPage(auctionItems &Items,auctionItem &Item,User UserItem){
    PrintIndex_ItemsPage();
    PrintAuctionItems(Items,UserItem);
    char Selection;
    int Id;
    cout << endl << "                     ";
    cout << "  请选择要进行的操作：" << endl << endl;
    cout << "                     ";
    cout << "      1.进行竞拍"    << endl << endl;
    cout << "                     ";
    cout << "      2.返回主页"    << endl << endl;
    Selection = getch();
    switch(Selection){
        case '1':
                cout << "                     ";
                cout << "   请输入物品编号：";
                scanf("%ld",&Id);
                if(SearchById(Items,Id,Item)){
                    if(Equal_Str(Item.Owner,UserItem.Nickname)){
                        cout << "                 ";
                        cout <<"无法竞拍自己的物品，1秒后返回主页";
                        Delay(1000);
                        return 1;
                    }
                    if(UserBid(Items,Item,UserItem)){
                        cout << "                     ";
                        cout <<"竞拍成功，1秒后返回主页";
                        Delay(1000);
                        return 1;
                    }else{
                        cout << "                     ";
                        cout <<"竞拍失败，1秒后返回主页";
                        Delay(1000);
                        return 1;
                    }
                }else{
                    cout << "                     ";
                    cout <<"物品不存在，1秒后返回主页";
                    Delay(1000);
                    return 1;
                }
                break;
        case '2':
                return 1;
                break;
        default:
                cout << "                     ";
                cout << "选择错误，1秒后返回主页"    << endl;
                Delay(1000);
                return 1;
    }
}

/*拍卖行大数据页面*/
int BigDataPage(auctionItems Items,Users UserItems,auctionItem Item){
    PrintIndex_BigDataPage();
    char Selection;
    char State1[7] = "已成交";
    char State2[7] = "拍卖中";
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "                       " << endl ;
    cout << "                     ";
    cout << " Welcome to 拍卖行大数据 " << endl << endl;
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "当前拍卖行用户总数是： " << UserNumberInAuctionHouse(UserItems) << endl;
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    TheHighestPriceInAuctionHouse(Items,Item);
    cout << "  当前最高价格的拍卖品是"  << endl;
    cout << "                     ";
    cout << "*********" << Item.Name <<"**********" << endl;
    cout << "                     ";
    cout << "*****成交价为：" << Item.TheHighestPrice << "*****"<< endl;
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    TheLowestPriceInAuctionHouse(Items,Item);
    cout << "  当前最低价格的拍卖品是 " << endl;
    cout << "                     ";
    cout << "*********" << Item.Name <<"**********" << endl;
    cout << "                     ";
    cout << "*****成交价为：" << Item.TheHighestPrice << "*****" << endl;
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "当前已拍卖的物品数是：" << TurnoverNumberInAuctionHouse(Items,State1) << endl;
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "当前拍卖中的物品数是：" << TurnoverNumberInAuctionHouse(Items,State2) << endl;
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "当前拍卖行的总交易额是：" << TurnoverInAuctionHouse(Items) << endl;
    cout << "                     ";
    cout << "-------------------------" << endl << endl << endl;
    cout << endl << "                     ";
    cout << "    按任意键返回主页     " << endl << endl;
    cout << "____________________________________________________________________" << endl ;
    cout << "                     ";
    cout << "    2018.5.3 By XHSF      " << endl << endl << endl<< endl;
    Selection = getch();
    return 1;
}

/*录入物品信息页面*/
void AddAuctionItemPage(auctionItems &Items,auctionItem &Item,User UserItem){
    char TITLE[20] = "录入物品信息界面";
    Page_Head(TITLE,EMPTY);
    AddAuctionItem(Items,Item,UserItem);//添加物品
    AddAuctionItemToFile(Item);//添加物品到文件
    CreateAuctionList(Items);//初始化物品链表
    cout << "                     ";
    cout << "                       " << endl ;
    cout << "                ";
    cout << "添加物品成功，1秒后返回上一级页面" << endl << endl;
    Delay(1000);
}

/****************************拍卖品链表操作区*******************************/



/****************************用户链表操作区*********************************/
/*向文件添加用户信息*/
void AddUserToFile(User &UserItem){
    FILE *fp;
    fp = fopen("Users_DataBase.txt","a+");
    fwrite(&UserItem,sizeof(User),1,fp);
    fclose(fp);
}

/*添加用户信息*/
void AddUser(User &UserItem){
    Input_Str(UserItem.Account,b1);
    Input_Str(UserItem.Nickname,b2);
    Input_Password(UserItem.Password,b3,b4);
}

/*新用户注册*/
void Register(Users &UserItems,User &UserItem){
    int FlagAccount = 0,FlagPassword = 6,FlagNickname = 4,idx = 0;
    char Password[20],Account[30],Nickname[30],c;
    do{
        RegisterPage();
        if(FlagAccount){
            cout << "                     ";
            cout << "   该账号已被注册    " << endl << endl;
            memset(Account,'\0',sizeof(Account));
            memset(Password,'\0',sizeof(Password));
            memset(Nickname,'\0',sizeof(Nickname));
        }
        if(FlagPassword < 6){
            cout << "                     ";
            cout << "密码应大于等于6个字符" << endl << endl;
            memset(Account,'\0',sizeof(Account));
            memset(Password,'\0',sizeof(Password));
            memset(Nickname,'\0',sizeof(Nickname));
        }
        if(FlagNickname < 4){
            cout << "                     ";
            cout << "昵称应大于等于4个字符" << endl << endl;
            memset(Account,'\0',sizeof(Account));
            memset(Password,'\0',sizeof(Password));
            memset(Nickname,'\0',sizeof(Nickname));
        }
        cout << "                     ";
        cout << "昵称：" ;
        scanf("%s",Nickname);
        cout<< endl << endl  << "                     ";
        cout << "账号：" ;
        scanf("%s",Account);
        cout<< endl << endl  << "                     ";
        cout  << "密码：" ;
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
    cout << "注册中，请等候。。。 " << endl ;
    Delay(1000);
}

/*删除用户信息*/
int DeleteUser(Users UserItems,char *UserAccount,User &UserItem){
    int Flag = 0;
    FILE *Prototype,*Temporary;
    Prototype = fopen("Users_DataBase.txt","rb");
    Temporary = fopen("Users_DataBase_Temporary.txt","a+");
    if(Prototype == NULL || Temporary == NULL){
        fclose(Prototype);
        fclose(Temporary);
        return 0; //排除打开文件失败
    }
    if(UserItems->next == NULL){
        fclose(Prototype);
        fclose(Temporary);
        return -2;//排除原链表为空
    }
    while(UserItems->next != NULL){
        UserItems = UserItems->next;
        if(!Equal_Str(UserItems->Account,UserAccount)) fwrite(&*UserItems,sizeof(User),1,Temporary);//把不是要删除的节点写入临时文件
        else{
            UserItem = *UserItems;//返回要删除节点的信息
            Flag = 1;
        }
    }
    fclose(Prototype);
    fclose(Temporary);
    remove("Users_DataBase.txt");//删除原文件
    rename("Users_DataBase_Temporary.txt","Users_DataBase.txt");//把临时文件名改为原文件名
    if(Flag == 0) return -1; //如果没有找到所要删除的编号 则返回1
    return 1;//找到则返回1
}

/*删除用户信息后并重新创建链表*/
int DeleteUserAndCreateList(Users &UserItems,char *UserAccount,User &UserItem){
    int ReturnValue = DeleteUser(UserItems,UserAccount,UserItem);
    CreateUserList(UserItems);
    return ReturnValue;
}

/*初始化用户链表*/
void CreateUserList(Users &UserItems){
    FILE *fp;
    fp = fopen("Users_DataBase.txt","rb");
    Users Pointer,PointerBridge ;
    User temp;//定义结构体别名
    UserItems = (Users)malloc(sizeof(User));
    UserItems->next = NULL;
    Pointer = (Users)malloc(sizeof(User));  // Pointer指向新开辟的节点内存
    Pointer = UserItems;
    while(fread(&temp,sizeof(User),1,fp)!=0){//从文件中读结构体块
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

/*通过Account寻找用户，找不到返回0，找到返回1*/
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

/*通过Nickname寻找用户，找不到返回0，找到返回1*/
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

/*返回当前拍卖行中的用户数*/
int UserNumberInAuctionHouse(Users UserItems){
    long UserNumber = 0;
    while(UserItems -> next != NULL){
        UserItems = UserItems -> next;
        UserNumber++;
    }
    return UserNumber;
}

/*打印用户信息*/
void PrintUser(User UserItem){
    cout << "------------------------" << endl;
    cout << UserItem.Account << endl;
    cout << UserItem.Nickname << endl;
    cout << UserItem.Password << endl;
    cout << "------------------------" << endl;
}

/*打印用户信息列表*/
int PrintUsers(Users UserItems){
    if(UserItems->next == NULL) return 0;
    while(UserItems->next != NULL){
        UserItems = UserItems->next;
        PrintUser(*UserItems);
    }
    return 1;
}

/*用户登录，成功返回1，密码错误返回-1，账号不存在返回0*/
int UserLog(Users UserItems,User &UserItem,char *Account,char *Password){
    if(SearchByAccount(UserItems,Account,UserItem)){
        if(Equal_Str(Password,UserItem.Password)) return 1;//登录成功
        else return -1;//密码错误
    }
    return 0;//账号不存在
}

/*用户登录并打印UI页面,成功返回1，失败返回0*/
int UserLogPrint(Users UserItems,User &UserItem){
    int FlagLog = 1,idx = 0;
        char Password[20],Account[30],c;
        do{
            LogPrintHomePage();
            if(FlagLog!=1){
                cout << "                        ";
                cout << "密码或账号错误，请重新登录" << endl ;
                idx = 0;//置零
            }
            cout<< endl << endl  << "                     ";
            cout << "账号：" ;
            scanf("%s",Account);
            cout<< endl << endl  << "                     ";
            cout  << "密码：" ;
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
        cout << "  登录中，请等待...  " << endl;
        Delay(500);
}

/*修改密码函数*/
int ModifyUserPassword(Users &UserItems,char *Account,char *Password){

    User UserItem;
    DeleteUser(UserItems,Account,UserItem);
    Assign_Char(UserItem.Password,Password);
    FILE *fp;
    fp = fopen("Users_DataBase.txt","a+");
    fwrite(&UserItem,sizeof(User),1,fp);
    CreateUserList(UserItems);//重新初始化链表
    fclose(fp);
    return 1;
}

/*修改密码页面*/
int ModifyPasswordPage(Users &UserItems,User &UserItem){
    char TITLE[20] = "    修改密码";
    char Password[20];
    Page_Head(TITLE,EMPTY);
    int idx = 0;
    char c,FirstInput[20],SecondInput[20];
    cout << "                     ";
    cout << "   请输入原密码：" ;
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
    cout << "   请输入新密码：";
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
        cout << "密码修改失败，密码长度应大于6位";
        cout << endl << endl << "                         ";
        cout << "1秒后返回我的界面";
        Delay(1000);
        return 0;
    }
    if(Equal_Str(FirstInput,UserItem.Password)){
        Assign_Char(Password,SecondInput);
        ModifyUserPassword(UserItems,UserItem.Account,Password);
        cout << endl << endl << "                  ";
        cout << "密码修改成功，1秒后返回登录界面";
        Delay(1000);
        return 1;
    }
    else{
        cout << "                     ";
        cout << "原密码输入错误，1秒后返回我的界面：";
        Delay(1000);
        return 0;
    }
}

/****************************用户链表操作区*********************************/



/*******************************UI操作区************************************/
/*打印页面头*/
void Page_Head(char *Title,char *Help){
    ClearScreen();
    cout << endl << "                     ";
    cout << "Auction management system" << Help << endl;
    cout << "                         ";
    cout << Title << endl;
    cout << "____________________________________________________________________" << endl << endl << endl<< endl;
}

/*首页打印*/
void PrintHomePage(){
    char TITLE[20] = "物品竞拍管理系统";
    Page_Head(TITLE,Help);
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "Welcome to 竞拍管理系统" << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  请选择要进行的操作： " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "        1.登录         " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "     2.新用户注册      " << "|" << endl ;
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

/*首页登录框*/
void LogPrintHomePage(){
    char TITLE[20] = "    登陆界面";
    Page_Head(TITLE,EMPTY);
}

/*新用户注册页面*/
void RegisterPage(){
    char TITLE[20] = "    注册页面";
    Page_Head(TITLE,EMPTY);
}

/*主页*/
void PrintIndexPage(){
    char TITLE[20] = "      主页";
    Page_Head(TITLE,Help);
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "    Welcome to 主页    " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  请选择要进行的操作： " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      1.物品搜索       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      2.物品清单       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "    3.拍卖行大数据     " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "    4.进入管理页面     " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "    5.进入我的页面     " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "-------------------------" << endl << endl << endl;
    cout << "____________________________________________________________________" << endl ;
    cout << "                     ";
    cout << "    2018.5.3 By XHSF      " << endl << endl << endl<< endl;
}

/*打印物品搜索页*/
void PrintIndex_SearchPage(){
    char TITLE[20] = "    物品搜索";
    Page_Head(TITLE,Help);
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  Welcome to 搜索页面  " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "   请选择搜索的类型：  " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "       1.编号搜索      " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "       2.类别搜索      " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "     3.物品名称搜索    " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "       4.返回主页      " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "-------------------------" << endl << endl << endl;
    cout << "____________________________________________________________________" << endl ;
    cout << "                     ";
    cout << "    2018.5.3 By XHSF      " << endl << endl << endl<< endl;
}

/*打印物品清单页面*/
void PrintIndex_ItemsPage(){
    char TITLE[20] = "    物品清单";
    Page_Head(TITLE,EMPTY);
}

/*打印拍卖行大数据页面*/
void PrintIndex_BigDataPage(){
    char TITLE[20] = "   拍卖行大数据";
    Page_Head(TITLE,EMPTY);
}

/*打印我的页面*/
void PrintMyPage(){
    char TITLE[20] = "     我的页面";
    Page_Head(TITLE,Help);
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  Welcome to 我的页面  " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  请选择要进行的操作： " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      1.修改密码       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      2.我的竞拍       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      3.退出登录       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      4.返回主页       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "-------------------------" << endl << endl << endl;
    cout << "____________________________________________________________________" << endl ;
    cout << "                     ";
    cout << "    2018.5.3 By XHSF      " << endl << endl << endl<< endl;
}

/*打印我的竞拍页面*/
void PrintMyAuctions(auctionItems Items,User UserItem){
    char TITLE[20] = "     我的竞拍";
    Page_Head(TITLE,EMPTY);
    if(!SearchByTheHighestBidder(Items,UserItem.Nickname,UserItem)){
        cout << endl << "                     ";
        cout << "---您暂时还没有竞拍物品---"<< endl ;
    }
    cout << endl << "                     ";
    cout << "---按任意键返回我的页面---"<< endl ;
    getch();
}

/*打印管理员页面*/
void PrintManagerPage(){
    char TITLE[20] = "    管理页面";
    Page_Head(TITLE,Help);
    cout << "                     ";
    cout << "-------------------------" << endl;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  Welcome to 管理页面  " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "  请选择要进行的操作： " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      1.录入物品       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      2.修改物品       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      3.删除物品       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      4.开始竞拍       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "      5.返回主页       " << "|" << endl ;
    cout << "                     ";
    cout << "|" << "                       " << "|" << endl ;
    cout << "                     ";
    cout << "-------------------------" << endl << endl << endl;
    cout << "____________________________________________________________________" << endl ;
    cout << "                     ";
    cout << "    2018.5.3 By XHSF      " << endl << endl << endl<< endl;
}

/*帮助页面*/
void PrintHelpPage(){
    ClearScreen();
    cout << endl << "                     ";
    cout << "Auction management system              " << endl;
    cout << "                         ";
    cout << "     帮助页面" << endl;
    cout << "____________________________________________________________________" << endl << endl << endl<< endl;
    cout << "           ";
    cout << "1.这是一个提供用户进行物品竞拍的系统，用户可在"<< endl << "             " << "此系统上对物品进行出价，价高者得。";
    cout << endl << endl << "           ";
    cout << "2.竞拍方式有明拍和暗拍两种。在暗拍方式下，用用"<< endl << "             " << "户无法看到物品当前的最高价。";
    cout << endl << endl << "           ";
    cout << "3.当前拍卖行仅支持2亿元以下的交易。";
    cout << endl << endl << "           ";
    cout << "4.此系统可以全程使用键盘进行操作，无需鼠标。";
    cout << endl << endl << "           ";
    cout << "5.用户密码长度应该大于等于6位。";
    cout << endl << endl << "           ";
    cout << "6.系统自动检测拍卖结束时间，时间到自动确认得主。";
    cout << endl << endl << endl << "                         ";
    cout << "按任意键返回上一页";
    getch();
}

/*字体、背景初始化*/
void InitBackgroundAndFont(){
    SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE),15 | 8 | 128 | 64);//初始化字体
    system("color F8");//初始化背景
}
/*******************************UI操作区************************************/



int main()
{
    /*******************************变量定义区************************************/
    auctionItems Items;//用户表
    auctionItem Item;//用户
    auctionItem PreItem,PostItem;//用户信息修改前后
    User UserItem;//用户
    Users UserItems;//用户表
    char selection;//操作选择
    int Flag = 0;//提示标志
    /*******************************变量定义区************************************/


    /********************************初始化区*************************************/
    InitBackgroundAndFont();//初始化字体和背景
    CreateUserList(UserItems);//初始化用户链表
    CreateAuctionList(Items);//初始化物品链表
    CheckAuction(Items,Item);//检查是否可以进行拍卖，如果可以则进行拍卖
    /********************************初始化区*************************************/





    /*******************************页面操作区************************************/


    /*************登录页面*************/
    LogPage:
        PrintHomePage();
        if(Flag == 1){
            cout << "                     ";
            cout << " 选择错误，请重新选择操作" << endl ;
            Flag = 0;
        }
        selection = getch();
        switch(selection){
            case '1':
                    //登录页面
                    UserLogPrint(UserItems,UserItem);
                    Flag = 0;
                    goto IndexPage;
                    break;
            case '2':
                    //注册页面
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
                    //错误，重新选择
                    Flag = 1;
                    goto LogPage;
        }

    /*************登录页面*************/
    /***************主页***************/
    IndexPage:
        PrintIndexPage();
        if(Flag == 1){
            cout << "                     ";
            cout << " 选择错误，请重新选择操作" << endl ;
            Flag = 0;
        }
        selection = getch();
        switch(selection){
            case '1':
                    //搜索页面
                    SearchPage(Items,Item,UserItem);
                    Flag = 0;
                    goto IndexPage;
                    break;
            case '2':
                    //物品清单页面
                    ItemsPage(Items,Item,UserItem);
                    Flag = 0;
                    goto IndexPage;
                    break;
            case '3':
                    //大数据页面
                    BigDataPage(Items,UserItems,Item);
                    Flag = 0;
                    goto IndexPage;
                    break;
            case '4':
                    //跳转到管理员页面
                    Flag = 0;
                    goto ManagerPage;
                    break;
            case '5':
                    //跳转到我的页面
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
                    //错误，重新选择
                    Flag = 1;
                    goto IndexPage;
        }
    /***************主页***************/
    /***************管理页面***************/
    ManagerPage:
        PrintManagerPage();
        if(Flag == 1){
            cout << "                     ";
            cout << " 选择错误，请重新选择操作" << endl ;
            Flag = 0;
        }
        selection = getch();
        switch(selection){
            case '1':
                    //增加物品
                    AddAuctionItemPage(Items,Item,UserItem);
                    Flag = 0;
                    goto ManagerPage;
                    break;
            case '2':
                    //修改物品
                    ModifyItemAndPage(Items,UserItem);
                    Flag = 0;
                    goto ManagerPage;
                    break;
            case '3':
                    //删除物品
                    DeleteItemPage(Items,UserItem);
                    Flag = 0;
                    goto ManagerPage;
                    break;
            case '4':
                    //开始竞拍
                    StartAuctionPage(Items,UserItem);
                    Flag = 0;
                    goto ManagerPage;
                    break;
            case '5':
                    //跳转到主页
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
                    //错误，重新选择
                    Flag = 1;
                    goto ManagerPage;
        }

    /***************管理页面***************/
    /***************我的页面***************/
    MyPage:
        PrintMyPage();
        if(Flag == 1){
            cout << "                     ";
            cout << " 选择错误，请重新选择操作" << endl ;
            Flag = 0;
        }
        selection = getch();
        switch(selection){
            case '1':
                    //修改密码
                    if(ModifyPasswordPage(UserItems,UserItem)){//成功
                        Flag = 0;
                        CreateUserList(UserItems);//初始化用户链表
                        CreateAuctionList(Items);//初始化物品链表
                        goto LogPage;
                    }else{//失败
                        Flag = 0;
                        goto MyPage;
                    }
                    break;
            case '2':
                    //我的竞拍
                    PrintMyAuctions(Items,UserItem);
                    Flag = 0;
                    goto MyPage;
                    break;
            case '3':
                    //退出登录
                    CreateUserList(UserItems);//初始化用户链表
                    CreateAuctionList(Items);//初始化物品链表
                    cout << "                     ";
                    cout << "  退出登录中，请等候..." << endl ;
                    Delay(1000);
                    Flag = 0;
                    goto LogPage;
                    break;
            case '4':
                    //跳转到主页
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
                    //错误，重新选择
                    Flag = 1;
                    goto MyPage;
        }
    /***************我的页面***************/


    /*******************************页面操作区************************************/
}
