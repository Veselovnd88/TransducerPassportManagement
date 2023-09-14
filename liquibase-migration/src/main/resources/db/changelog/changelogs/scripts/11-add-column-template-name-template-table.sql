alter table public.pass_template
    ADD COLUMN template_name varchar UNIQUE NOT NULL default '';

GO