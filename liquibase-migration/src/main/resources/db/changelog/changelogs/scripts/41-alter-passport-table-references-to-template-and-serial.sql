alter table public.passport
    DROP COLUMN template_id;
GO

alter table public.passport
    add column template_id UUID REFERENCES public.pass_template (id) ON DELETE SET NULL;

GO

alter table public.passport
    drop column if exists pt_art;

GO

alter table public.passport
    drop column if exists serial_number;

GO

alter table public.passport
    add column serial_id UUID REFERENCES public.serial_number (id) ON DELETE SET NULL;

GO
