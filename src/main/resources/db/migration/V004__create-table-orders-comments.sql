create table if not exists public.orders_comments
(
    id          uuid      not null,
    order_id    uuid      not null,
    description text      not null,
    created_at  timestamp not null,
    updated_at  timestamp not null,
    constraint orders_comments_pkey
        primary key (id),
    constraint orders_comments_order_id_fk
        foreign key (order_id) references public.orders
            on update cascade on delete cascade
);

create unique index orders_comments_unique on public.orders_comments (id);
create index if not exists orders_comments_index on public.orders_comments (id, order_id);

