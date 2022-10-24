create sequence hibernate_sequence start 1 increment 1;
create table author
(
    id              int8 not null,
    uuid            varchar(255),
    created_at      timestamp,
    last_updated_at timestamp,
    version         int8 not null,
    last_name       varchar(255),
    name            varchar(255),
    year_of_birth   int4,
    primary key (id)
);
create table author_book
(
    book_id   int8 not null,
    author_id int8 not null,
    primary key (book_id, author_id)
);
create table book
(
    id              int8 not null,
    uuid            varchar(255),
    created_at      timestamp,
    last_updated_at timestamp,
    version         int8 not null,
    available       int8,
    book_cover_id   int8,
    price           numeric(19, 2),
    title           varchar(255),
    book_year       int4,
    primary key (id)
);
create table order_item
(
    id              int8 not null,
    uuid            varchar(255),
    created_at      timestamp,
    last_updated_at timestamp,
    version         int8 not null,
    quantity        int4 not null,
    book_id         int8,
    order_id        int8,
    primary key (id)
);
create table orders
(
    id              int8 not null,
    uuid            varchar(255),
    created_at      timestamp,
    last_updated_at timestamp,
    version         int8 not null,
    delivery        varchar(255),
    order_status    varchar(255),
    recipient_id    int8,
    primary key (id)
);
create table recipient
(
    id               int8 not null,
    uuid             varchar(255),
    created_at       timestamp,
    last_updated_at  timestamp,
    version          int8 not null,
    email            varchar(255),
    last_name        varchar(255),
    name             varchar(255),
    phone            varchar(255),
    apartment_number varchar(255),
    building_number  varchar(255),
    city             varchar(255),
    district         varchar(255),
    street           varchar(255),
    zip_code         varchar(255),
    primary key (id)
);
create table role
(
    id              int8 not null,
    uuid            varchar(255),
    created_at      timestamp,
    last_updated_at timestamp,
    version         int8 not null,
    role            varchar(255),
    primary key (id)
);
create table role_user
(
    user_id int8 not null,
    role_id int8 not null,
    primary key (user_id, role_id)
);
create table upload
(
    id               int8 not null,
    uuid             varchar(255),
    created_at       timestamp,
    last_updated_at  timestamp,
    version          int8 not null,
    content_type     varchar(255),
    file_name        varchar(255),
    path             varchar(255),
    server_file_name varchar(255),
    primary key (id)
);
create table users
(
    id                int8 not null,
    uuid              varchar(255),
    created_at        timestamp,
    last_updated_at   timestamp,
    version           int8 not null,
    matching_password varchar(255),
    password          varchar(255),
    username          varchar(255),
    primary key (id)
);
alter table book
    add constraint UK_g0286ag1dlt4473st1ugemd0m unique (title);
alter table recipient
    add constraint UK_1ntglg90ib39wa1x51wk1fxho unique (email);
alter table role
    add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);
alter table author_book
    add constraint FKg7j6ud9d32ll232o9mgo90s57 foreign key (author_id) references author;
alter table author_book
    add constraint FKn8665s8lv781v4eojs8jo3jao foreign key (book_id) references book;
alter table order_item
    add constraint FKb033an1f8qmpbnfl0a6jb5njs foreign key (book_id) references book;
alter table order_item
    add constraint FKt4dc2r9nbvbujrljv3e23iibt foreign key (order_id) references orders;
alter table orders
    add constraint FKcxwo1jbmo15jih4b5qjclvye8 foreign key (recipient_id) references recipient;
alter table role_user
    add constraint FKiqpmjd2qb4rdkej916ymonic6 foreign key (role_id) references role;
alter table role_user
    add constraint FKhvai2k29vlwpt9wod4sw4ghmn foreign key (user_id) references users;
