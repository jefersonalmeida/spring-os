create table if not exists public.users
(
    id                uuid         not null,
    active            boolean      not null,
    name              varchar(100) not null,
    email             varchar(100) not null,
    phone             varchar(20),
    password          varchar(200),
    img               varchar(255),
    activation_token  varchar(255),
    email_verified_at timestamp,
    created_at        timestamp,
    updated_at        timestamp,
    constraint users_pkey
        primary key (id)
);

create unique index if not exists users_unique on public.users (id);
create index if not exists users_idx on public.users (id, email, phone);

create table if not exists public.users_roles
(
    user_id uuid not null,
    role_id uuid not null,
    constraint users_roles_role_id_fk
        foreign key (role_id) references public.roles
            on update cascade on delete cascade,
    constraint users_roles_user_id_fk
        foreign key (user_id) references public.users
            on update cascade on delete cascade
);

create index if not exists users_roles_idx on public.users_roles (user_id, role_id);
