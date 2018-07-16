/**
 * 打印当前月份的日历
 */

package timer;
import java.time.*;

public class GetCalendar {

	public static void main(String[] args) {
		
		 //获取当前日期
		 LocalDate date = LocalDate.now();
		 //获取当前月份
		 int month = date.getMonthValue();
		 //获取当前天数
		 int today = date.getDayOfMonth();
		 
		 //参数为当前日期要减去的天数
		 //这句话的意思是返回这个月的第一天
		 date = date.minusDays(today - 1); 
		 //将返回这一天是星期几（类型是字符串型）
		 DayOfWeek weekday = date.getDayOfWeek();
		 //把字符串型转换成整形
		 int value = weekday.getValue();
		 
		 //打印表头和第二行的缩进
		 System.out.println(" Mon Tue Wed Thu Fri Sat Sun");
		 for(int i = 1;i < value ;i++) {
			 System.out.print("    ");
		 }
		 
		 //打印日期表格
		 while (date.getMonthValue() == month) {
			 
			 //如果是是今天则前面添加星号
			 int day = date.getDayOfMonth();
			 if(day == today) {
				 System.out.printf(" *%2d",day);
			 }else {
				 System.out.printf("%4d",day);
			 }
			 
			 //日期递进加一
			 date = date.plusDays(1);
			 //如果是下一个星期则换行
			 if(date.getDayOfWeek().getValue() == 1) {
				 System.out.println();
			 }
			 
		 }

	}

}
