alter table public.passport
    drop column if exists template_id;

GO

alter table public.passport
    add column template_id UUID;

GO

alter table public.passport
    drop column if exists serial_id;

GO

alter table public.passport
    add column pt_art varchar default 'unknown';

GO

alter table public.passport
    add column serial_number varchar DEFAULT 'unknown';