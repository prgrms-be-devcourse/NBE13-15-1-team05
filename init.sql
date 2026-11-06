-- 한글·특수문자(ñ, ó)가 깨지지 않도록 클라이언트 문자셋을 utf8mb4로 지정
SET NAMES utf8mb4;

create database if not exists cafe;
use cafe;

create table if not exists category
(
    id   bigint auto_increment
        primary key,
    name varchar(255) not null
);

create table if not exists menu
(
    id          bigint auto_increment
        primary key,
    available   bit          not null,
    description varchar(300) not null,
    name        varchar(255) not null,
    price       int          not null,
    category_id bigint       not null,
    constraint fk_menu_category
        foreign key (category_id) references category (id)
);

create table if not exists orders
(
    id            bigint auto_increment
        primary key,
    address       varchar(200)                 not null,
    cancelled_at  datetime(6)                  null,
    created_at    datetime(6)                  not null,
    email         varchar(100)                 not null,
    memo          varchar(200)                 not null,
    postcode      varchar(5)                   not null,
    shipping_date date                         not null,
    status        enum ('CANCELLED', 'PLACED') not null,
    total_amount  int                          not null
);

create table if not exists order_item
(
    id         bigint auto_increment
        primary key,
    menu_name  varchar(100) not null,
    quantity   int          not null,
    unit_price int          not null,
    menu_id    bigint       not null,
    order_id   bigint       not null,
    constraint fk_orderitem_menu
        foreign key (menu_id) references menu (id),
    constraint fk_orderitem_order
        foreign key (order_id) references orders (id)
);


-- 초기 카데고리
insert into category(name)
values ('싱글오리진'),
       ('블렌드');

-- 초기 메뉴
insert into menu(available, description, name, price, category_id)
values (true, '콜롬비아 나리뇨 원두를 담은 커피빈 패키지입니다.', 'Columbia Nariñó', 5000, 1),
       (true, '브라질 세하 두 카파라오 원두를 담은 커피빈 패키지입니다.', 'Brazil Serra Do Caparaó', 7000, 1),
       (true, '콜롬비아 킨디오 원두를 담은 커피빈 패키지입니다.', 'Columbia Quindío (White Wine Extended Fermentation)', 9000, 1),
       (true, '에티오피아 시다모 원두를 담은 커피빈 패키지입니다.', 'Ethiopia Sidamo', 11000, 1);