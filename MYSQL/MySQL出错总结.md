# MySQL出错总结

# 1.在进行optimize table t1操作时报Table does not support optimize, doing recreate + analyze instead 错误

- 错误原因：innodb的数据库不支持optimize

- 解决方案：可以用 ALTER TABLE table.name ENGINE='InnoDB'; 
  该方法会对旧表以复制的方式新建一个新表，然后删除旧表。虽然这个过程是安全的，但是在进行操作时还是先进行备份为好 

  或者You can make OPTIMIZE TABLE work on other storage engines by starting mysqld with the --skip-new or --safe-mode option. In this case, OPTIMIZE TABLE is just mapped toALTER TABLE. 

  上面是说要求我们在启动的时候指定--skip-new或者--safe-mode选项来支持optimize功能 

- 注意：优化的前提是独立表空间模式（参数innodb_file_per_table为ON)，否则没有任何效果的。