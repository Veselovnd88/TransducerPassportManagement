alter table public.pass_template
    ADD COLUMN template_name varchar UNIQUE default 'noname';

GO