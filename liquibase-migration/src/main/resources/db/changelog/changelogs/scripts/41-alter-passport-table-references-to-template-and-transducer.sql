alter table public.passport
    DROP COLUMN template_id;
GO

alter table public.passport
    add column template_id UUID REFERENCES public.pass_template (id) ON DELETE SET NULL;

GO

alter table public.passport
    add column pt_id UUID REFERENCES public.transducer (id) ON DELETE SET NULL;

GO

alter table public.passport
    drop column if exists pt_art;

GO

alter table public.passport
    add column pt_art varchar DEFAULT 'unknown' REFERENCES public.transducer (art) ON DELETE SET DEFAULT;

GO