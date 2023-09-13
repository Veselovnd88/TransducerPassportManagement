insert into public.customer(customer_name, inn, created_at)
values ('АО МТЗ ТРАНСМАШ', '7707019672', '2023-09-13');

GO

insert into public.customer(customer_name, inn, created_at)
values ('ООО НПП "ТЕХНОПРОЕКТ"', '5835034400', '2023-09-13');

GO

insert into public.transducer(art, tr_name, code, pressure_type, model, output_code, pressure_range, accuracy,
                              electrical_output, thread, connector, pin_out, options, created_at)
values ('801877',
        '801877 Датчик избыточного давления RDZ-S-20.11, 0…1 МПа, 0.5%, 0.5…5 В, М12х1.25, разъем 2РМДТ18Б4Ш5В1В',
        'RDZ-S-20.11-G-0/1-MPa-050-M12T-RMD',
        'RELATIVE', 'RDZ-S', '20.11', '0...1 MPa', '0.5%', '0.5...5 В', 'M12x1.25', '2РМДТ18Б4Ш5В1В',
        '1+, 2-, 3 вых.', 'стальной разъем', '2023-09-13');

GO

insert into public.transducer(art, tr_name, code, pressure_type, model, output_code, pressure_range, accuracy,
                              electrical_output, thread, connector, pin_out, options, created_at)
values ('802049',
        '802049 Датчик избыточного давления RDZ-S-10.0, 0…1 МПа, 0.5%, 4...20 мА, М12х1, разъем 2РМДТ18Б4Ш5В1В',
        'RDZ-S-10.0-G-0/1-MPA-050-M12E-RMD',
        'RELATIVE', 'RDZ-S', '10.0', '0...1 MPa', '0.5%', '4...20 мА', 'M12x1', '2РМДТ18Б4Ш5В1В',
        '1+, 3-', 'стальной разъем', '2023-09-13');

GO