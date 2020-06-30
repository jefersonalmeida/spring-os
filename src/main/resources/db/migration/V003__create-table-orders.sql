create table if not exists public.orders
(
    id          uuid           not null,
    user_id     uuid           not null,
    description varchar(100)   not null,
    price       numeric(19, 6) not null,
    status      varchar(255)   not null,
    opened_at   timestamp      not null,
    finished_at timestamp,
    constraint orders_pkey
        primary key (id),
    constraint orders_user_id_fk
        foreign key (user_id) references public.users
            on update cascade on delete cascade
);

create unique index if not exists orders_unique on public.users (id);
create index if not exists orders_idx on public.orders (id, user_id, status);

