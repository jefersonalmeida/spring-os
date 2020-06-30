create table if not exists public.roles
(
    id   uuid         not null,
    name varchar(100) not null,
    constraint roles_pkey
        primary key (id)
);

create unique index if not exists roles_unique on public.roles (id, name);
create index if not exists roles_idx on public.roles (id, name);
