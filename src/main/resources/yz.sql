create database yz;

use yz;


CREATE TABLE users (
    id int auto_increment primary key,
    sex varchar(5) NULL default '男',
    password varchar(100) default '',
    nick_name varchar(50) NULL,
    birthday datetime DEFAULT CURRENT_TIMESTAMP,
    reg_time timestamp DEFAULT CURRENT_TIMESTAMP,
    head_picture varchar(255) NULL,
    disable bit(1) default false,
    open_code varchar(100) NULL,
    amount decimal(10,2)   default 0,
    yongjin decimal(10,2)  default 0,
    yongjin_code varchar(100) ,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE roles (
    id int auto_increment primary key,
    role_name varchar(20) NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into roles values(1, 'BUYER');
insert into roles values(2, 'JIANHUO');
insert into roles values(3, 'ADMIN');



CREATE TABLE user_roles (
    user_id int,
    roles_id int
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table user_tokes(
    id int auto_increment primary key,
    user_id int,
    token varchar(1000)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create table user_amonut_log(
    id int auto_increment primary key,
    user_id int not null ,
    amount decimal(10,2) default 0 not null ,
    remark varchar(500) not null ,
    log_type enum('YUE', 'YONGJIN') not null ,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create table shipping_address(
    id int auto_increment primary key,
    user_id int not null ,
    province varchar(100),
    city varchar(100),
    area varchar(100),
    shipping_address varchar(200),
    shipping_address_details varchar(300),
    link_person varchar(20),
    phone varchar(12),
    default_address bit(1) default 0,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

)ENGINE=InnoDB DEFAULT CHARSET=utf8;

select child.id, child.name, parent.id, parent.name from product_category child inner join product_category parent on child.parent_id = parent.id where child.id in (1);

create table product_category(
    id int auto_increment primary key,
    name varchar(100) ,
    image varchar(200),
    sort int default 0,
    parent_id int,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

)ENGINE=InnoDB DEFAULT CHARSET=utf8;


 create table product_zhuanqu_category(
    id int auto_increment primary key,
    name varchar(100) ,
    image varchar(200),
    delete_able bit(1) default true,
    enable bit(1) default true,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

)ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table product_zhuanqu_category AUTO_INCREMENT=11;
insert into product_zhuanqu_category(id, name, delete_able, enable) values (1, '砍价专区', false, true);


create table products(
    id int auto_increment primary key comment '商品ID',
    name varchar(200) not null comment '商品名称',
    size varchar(200) not null comment '商品包装',
    first_category_id int not null,
    second_category_id int not null comment '商品关联二级分类',
    sanzhung bit(1) default false comment '是否散装',
    show_able bit(1) default true comment '上下架标志',
    code varchar(100) comment '条形码',
    stock int default 0 comment '库存',

    origin_price decimal(10,2) not null comment '原价',
    cost_price decimal(10,2) not null comment '成本价',
    current_price decimal(10, 2) not null comment '售价',

    supplier_id int comment '供应商',
    sort int default 0 not null comment '排序',

    profile_img varchar(200) not null comment '封面图片',
    lunbo_imgs varchar(600) not null comment '轮播图片',
    detail_imgs varchar(2000) comment '商品详情页图片',

    sales_cnt int default 0,
    comment_cnt int default 0,

    zhuanqu_id int comment '专区ID',
    zhuanqu_price decimal(10,2) comment '专区价格(砍价底价)',
    zhuanqu_endTime long comment '特价结束时间',
    max_kanjia_person int default 0 comment '最多支持多少人砍价',
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create table product_suppliers(
    id int auto_increment primary key,
    name varchar(100) not null ,
    contact_person varchar(50) not null ,
    phone varchar(12) default true not null ,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create table product_kanjia(
    id int auto_increment primary key,
    user_id int not null ,
    product_id int not null ,
    helper_ids varchar(4000),
    terminal bit(1) default false not null ,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create table shopping_cart(
    id int auto_increment primary key,
    user_id int not null ,
    product_id int not null ,
    product_cnt varchar(4000) not null ,
    cart_price decimal(10,2),
    kanjia_product boolean default false,
    selected boolean default true,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 订单表：  订单号 |  主订单金额 | 主订单状态 | 主订单已支付金额 | 主订单使用余额金额 |
-- 主订单使用佣金金额 | 差价订单金额 | 差价订单状态 | 差价订单已支付金额 | 差价订单使用余额金额 |

-- 差价订单使用佣金金额 | address_id | 拣货员 | 发货员 | 下单人 | 下单时间 |

-- 订单表Item：  订单ID |  商品ID | 商品名称| 商品包装 | 商品图片 | 商品最终价格 | 商品个数 | 是否为散装 |
-- 实际总重量 | 实际总价格 |

create table orders(
    id int auto_increment primary key,
    order_num varchar(64) not null ,
    user_id int not null ,

    address_id int not null ,
    address_detail varchar(400),
    address_contract varchar(100),
    yongjin_code varchar(100),
    -- 钱
    status varchar(50) not null comment '给用户看的状态信息， 待支付，待发货，待收货，待评论，已完成，超时取消，手动取消',
    total_cost_price decimal(10, 2) not null ,

    total_price decimal(10, 2) not null ,
    use_yongjin decimal(10, 2) not null ,
    use_yue decimal(10, 2) not null ,
    need_pay_money decimal(10, 2) not null ,
    had_pay_money decimal(10, 2) not null ,

    chajia_status varchar(50) not null default 'NO' comment 'NO 不是差价订单， WAIT_PAY差价订单待支付，HAD_PAY差价订单已支付',
    chajia_price decimal(10, 2) not null ,
    chajia_use_yongjin decimal(10, 2) not null ,
    chajia_use_yue decimal(10, 2) not null ,
    chajia_need_pay_money decimal(10, 2) not null ,
    chajia_had_pay_money decimal(10, 2) not null ,

    message varchar(300),
    deleted boolean default false,

    jianhuoyuan_id int ,
    jianhuo_status varchar(20) default 'NOT_JIANHUO' comment 'ING DONE',

    fahuoyuan_id int,
    has_fahuo boolean default false,

    drawback_status varchar(20) default 'NONE' comment ' NONE 代表没有, WAIT_APPROVE 待审核/审批中， APPROVE_PASS 通过/退款成功，APPROVE_REJECT 审核不通过/申请失败',
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create table orders_item(
    id int auto_increment primary key,
    order_id int,

    product_id int ,
    product_name varchar(200),
    product_profile_img varchar(200),
    product_size varchar(200),
    product_cnt int,
    product_total_price decimal(10, 2),
    product_unit_price  decimal(10, 2),

    product_sanzhuang boolean default false,
    chajia_total_weight decimal(10, 2),
    chajia_total_price decimal(10, 2),

    jianhuo_success boolean default false,
    jianhuo_time timestamp,

    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table order_drawback(
    id int auto_increment primary key,
    order_id int,
    drawback_type varchar(50) comment '退货/退款',
    drawback_reason varchar(200),
    drawback_detail varchar(300),
    drawback_pay_price decimal(10, 2),
    drawback_yue decimal(10, 2),
    drawback_yongjin decimal(10, 2),
    drawback_imgs varchar(1000) comment '最多三张照片',

    approve_user_id int,
    approve_comment varchar(300),
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create table order_yongjin_percent(
    id int auto_increment primary key,
    yongjin_percent decimal(5, 2),

    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8;



create table product_comment(
    id int auto_increment primary key,
    user_id int,
    product_id int,
    product_name varchar(200),
    product_profile_img varchar(200),
    product_size varchar(200),
    good boolean default true,
    comment varchar(300),
    images varchar(1000),
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table product_append_comment(
    id int auto_increment primary key,
    product_comment_id int,
    user_id int,
    good boolean default true,
    comment varchar(300),
    images varchar(1000),
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8;




create table toutiao(
    id int auto_increment primary key,
    title varchar(100),
    content varchar(500),
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8;




create table statistics(
    id int auto_increment primary key,
    day bigint,
    total_price decimal(10, 2),
    total_cnt decimal(10, 2),
	total_cost decimal(10, 2),
	total_profit decimal(10, 2)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table lunbo(
    id int auto_increment primary key,
    image varchar(200),
    link_type varchar(20),
    link_id int
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create table tixian_approve(
    id int auto_increment primary key,
    user_id int,
    amount varchar(20),

    approve_status varchar(40) default 'WAIT_APPROVE' not null ,

    approve_id int,
    approve_comment varchar(300),
    created_time timestamp DEFAULT CURRENT_TIMESTAMP ,
	modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create table my_search(
    id int auto_increment primary key,
    search_term varchar(200),
    user_id int,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table hot_search(
    id int auto_increment primary key,
    search_term varchar(200),
    cnt int
)ENGINE=InnoDB DEFAULT CHARSET=utf8;



select t1.id, t1.name,t1.parent_id,t2.id, t2.name from product_category t1 inner join product_category t2 on t1.parent_id = t2.id where t1.id =   154

select * from product_tejia_category
select * from products
select * from user_amonut_log
select * from shipping_address limit 1, 4
select role_name from roles left join user_roles on roles.id = user_roles.roles_id where user_roles.user_id = 1

select * from product_category
 select user_id,amount, remark , modified_time from user_amonut_log where user_id = 9 and log_type = 'YUE'



create table t1 (

    id int ,
    name varchar(10),
    t2_id int

)
select * from t1
create table t2 (

    id int ,
    name varchar(10)
)
select * from t2
select * from roles
select * from t1 left join t2 on t1.t2_id = t2.id