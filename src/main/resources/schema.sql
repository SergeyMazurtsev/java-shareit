drop table if exists comments cascade;
drop table if exists bookings cascade;
drop table if exists items cascade;
drop table if exists requests cascade;
drop table if exists users cascade;

create table if not exists users
(
    id    bigint generated always as identity primary key,
    name  varchar(255) not null,
    email varchar(320) not null,
    constraint uq_email unique (email)
);

create table if not exists requests
(
    id           bigint generated always as identity primary key,
    description  varchar(255) not null,
    requestor_id bigint,
    constraint fk_request_to_user foreign key (requestor_id) references users (id) on delete cascade
);

create table if not exists items
(
    id           bigint generated always as identity primary key,
    name         varchar(255) not null,
    description  varchar(320) not null,
    is_available boolean,
    owner_id     bigint,
    request_id   bigint,

    constraint fk_item_to_user foreign key (owner_id) references users (id) on delete cascade,
    constraint fk_item_to_request foreign key (request_id) references requests (id) on delete cascade
);

create table if not exists bookings
(
    id         bigint generated always as identity primary key,
    start_date timestamp without time zone not null,
    end_date   timestamp without time zone not null,
    item_id    bigint,
    booker_id  bigint,
    status     varchar(50),
    constraint fk_booking_to_item foreign key (item_id) references items (id) on delete cascade,
    constraint fk_booking_to_user foreign key (booker_id) references users (id) on delete cascade
);

create table if not exists comments
(
    id        bigint generated always as identity primary key,
    text      varchar(320) not null,
    item_id   bigint,
    author_id bigint,
    created   timestamp without time zone not null,
    constraint fk_comments_to_item foreign key (item_id) references items (id) on delete cascade,
    constraint fk_comments_to_user foreign key (author_id) references users (id) on delete cascade
)