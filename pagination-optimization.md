用户帖子表：

```mysql
CREATE TABLE `discuss_post` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(45) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `content` text,
  `type` int(11) DEFAULT NULL COMMENT '0-普通; 1-置顶;',
  `create_time` timestamp NULL DEFAULT NULL,
  `comment_count` int(11) DEFAULT NULL,
  `score` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3563106 DEFAULT CHARSET=utf8
```

**需求**：对用户帖子表(discuss_post)中的所有帖子进行分页展示，按照**帖子类型type **和**创建时间create_time** 倒序展示，每页展示10条。

**数据量**：3562966

```bash
mysql root@(none):nowcoder_community> select count(*) from discuss_post;
+----------+
| count(*) |
+----------+
| 3562966  |
+----------+
1 row in set
Time: 2.339s
```

> discuss_post表的数据使用 http://filldb.info/ 网站生成，总共356万左右。生成那么多数据主要是为了更直观的展示出SQL语句的性能问题。

## 版本1: 没有任何优化

查询编号为10万的记录开始的10个记录：

```mysql
mysql root@(none):nowcoder_community> select * from discuss_post order by type desc, create_time desc limit 100000, 10;
10 rows in set
Time: 5.818s
```

可以看到上述查询语句的耗时是**5.818s**，这显然是无法接受的。

使用explain查看该语句的执行计划如下：

```bash
***************************[ 1. row ]***************************
id            | 1
select_type   | SIMPLE
table         | discuss_post
partitions    | <null>
type          | ALL
possible_keys | <null>
key           | <null>
key_len       | <null>
ref           | <null>
rows          | 3246721
filtered      | 100.0
Extra         | Using filesort
```

可以看到执行计划中的type为ALL，说明上述的SQL语句会进行**全表扫描**；并且extra列为filesort，说明需要占用额外的内存或磁盘空间对数据进行排序。

**LIMIT的工作原理**如下：`limit 100000, 10`会从表中的第一条记录开始扫描，会扫描约100010条记录，并且遗弃掉前100000条记录，只取最后10条记录。所以，我们只取10条记录，却需要扫描10万条记录，并且对于InnoDB而言，全表扫描其实就是扫描主键聚簇索引的叶子结点，而叶子结点存放完整的行记录，占得体积也是相当大的，所以总体来说上述SQL语句的开销很大。

## 版本2: 在(type, create_time)上建索引

经常听到这样一句话：“在经常作为排序条件的字段上简历索引”。

显然，在该场景中，type和create_time经常被作为排序条件，因此尝试在(type, create_time)上建立一个联合索引：

```mysql
create index idx_type_ctime on discuss_post(type, create_time);
```

建立索引后，是不是就OK了呢？再次运行之前的查询语句看一下：

```mysql
mysql root@(none):nowcoder_community> select * from discuss_post order by type desc, create_time desc limit 100000, 10;
10 rows in set
Time: 5.292s
```

可以看到，执行时间是5.292s，和原来差不多，仍然是一个不可接受的时间。问题出在哪呢？先看看执行计划：

```bash
***************************[ 1. row ]***************************
id            | 1
select_type   | SIMPLE
table         | discuss_post
partitions    | <null>
type          | ALL
possible_keys | <null>
key           | <null>
key_len       | <null>
ref           | <null>
rows          | 3246721
filtered      | 100.0
Extra         | Using filesort
```

可以看到，此时的执行计划和创建索引之前一样，并没有用上我们创建的索引，这是为什么呢？

这是因为，我们的查询语句中选中的是**所有列** (`select *`)，如果走 `(type, create_time)` 这个索引的话，对于每个索引项就不得不**回表**一次获得完整的记录行。也就是说，对于上面的操作：

- 将会进行10万次左右的回表操作，带来**大量的随机IO**
- 并且每次回表也还是得取出完整的数据行

所以说，如果走`(type, create_time)`索引的话，性能甚至比版本1的全表扫描更差。

**MySQL优化器**可以对上述情况作出判断，决定使用**全表扫描**而非`(type, create_time)`索引，因此才有了上面的执行计划。

## 版本3: 延迟关联

通过对版本2的分析，我们知道了问题的症结所在——**大量的回表操作**。

那么，如何利用上我们建立的`(type, creat_time)`索引，利用上索引带来的排序特性，同时尽量避免不必要的回表操作，最终提升性能呢？

对于这一问题，《高性能MySQL》中提出了一种叫做 “**延迟关联**”的解决方案，该方案借助一个**子查询**来进行优化：

```java
mysql root@(none):nowcoder_community> select * from discuss_post dp1 inner join (select id from discuss_post order by type desc, create_time desc limit 100000, 10) dp2 on dp1.id = dp2.id;
10 rows in set
Time: 0.071s
```

可以看到，现在的查询**仅仅需要0.071s**，相比之前的5s+有大幅度的提升，这是为什么呢？还是要看一下执行计划：

![](https://raw.githubusercontent.com/lvhlvh/pictures/master/img/20200905170805.png)

- 首先执行编号为2的查询：

  - 根据**type**和**key**列可以知道该查询用到了之前建立的(type, create_time)索引，并且根据**Extra**列的“Using index”知道是用上了**覆盖索引**，无需回表。
  - 虽然根据**rows**列知道该查询仍然扫描了大约10万条记录，但是由于索引的叶子结点仅仅存放id、type、create_time这3个字段，因此这大约10万条条记录所占的总页面数应该不是太大，速度可以接受
  - 最终得到一个派生表`<derived2>`，供查询1使用

- 然后执行编号为1的查询：

  - 对派生表`<derived2>`中的每一行进行扫描，为什么这里的rows是100010呢，还需要再次扫描100010个索引项吗？难道不应该是10吗？**确实应该是10**。这里显示100010的原因是：**在评估rows列的值时，MySQL并不会考虑LIMIT语句的存在**，可以参考 [MySQL EXPLAIN limits and errors](https://www.percona.com/blog/2006/07/24/mysql-explain-limits-and-errors/)，这篇文章告诉我们，explain输出的执行计划并不总是正确的，并不总是能反映SQL语句真正执行时的情况。

  - 对于派生表中的每一行，通过其id字段`dp2.id`去表dp1的**主键索引**中进行查找，最终找到所需的10条记录。这里涉及两个表的连接，驱动表是`<derived2>`，被驱动表是`dp1`，两者根据`id`字段进行连接，由于被驱动表的`id`字段上存在索引，因此两表join时采用的**join算法**应该是**Index Nested Loop Join**，只需要根据驱动表中的id字段10次查找被驱动表的索引树即可，原理如下图所示，效率还算比较高，关于join算法可以参考[MySQL Join算法与调优白皮书](https://blog.csdn.net/orangleliu/article/details/72850659)：

    <img src="https://raw.githubusercontent.com/lvhlvh/pictures/master/img/20200905215746.png" style="zoom:80%;" />


