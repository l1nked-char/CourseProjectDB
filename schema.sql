-- DROP SCHEMA public;

CREATE SCHEMA public AUTHORIZATION pg_database_owner;

-- DROP TYPE public."booking_status";

CREATE TYPE public."booking_status" AS ENUM (
	'занят',
	'забронирован',
	'снят с бронирования',
	'выселились досрочно',
	'не заселились');

-- DROP TYPE public."document_type";

CREATE TYPE public."document_type" AS ENUM (
	'Паспорт РФ',
	'Паспорт иностранного гражданина',
	'Свидетельство о рождении',
	'Временное удостоверение',
	'Загранпаспорт РФ');

-- DROP SEQUENCE public.cities_city_id_seq;

CREATE SEQUENCE public.cities_city_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.cities_city_id_seq1;

CREATE SEQUENCE public.cities_city_id_seq1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.convenieces_dict_conv_name_id_seq;

CREATE SEQUENCE public.convenieces_dict_conv_name_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.conveniences_dict_conv_name_id_seq;

CREATE SEQUENCE public.conveniences_dict_conv_name_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.hotel_hotel_id_seq;

CREATE SEQUENCE public.hotel_hotel_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.hotel_hotel_id_seq1;

CREATE SEQUENCE public.hotel_hotel_id_seq1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.hotel_rooms_room_id_seq;

CREATE SEQUENCE public.hotel_rooms_room_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.hotel_rooms_room_id_seq1;

CREATE SEQUENCE public.hotel_rooms_room_id_seq1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.hotel_services_service_id_seq;

CREATE SEQUENCE public.hotel_services_service_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.hotel_services_service_id_seq1;

CREATE SEQUENCE public.hotel_services_service_id_seq1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.room_conveniences_conv_id_seq;

CREATE SEQUENCE public.room_conveniences_conv_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.room_conveniences_conv_id_seq1;

CREATE SEQUENCE public.room_conveniences_conv_id_seq1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.services_dict_service_name_id_seq;

CREATE SEQUENCE public.services_dict_service_name_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.services_dict_service_name_id_seq1;

CREATE SEQUENCE public.services_dict_service_name_id_seq1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.services_history_row_id_seq;

CREATE SEQUENCE public.services_history_row_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.services_history_row_id_seq1;

CREATE SEQUENCE public.services_history_row_id_seq1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.social_statuses_status_id_seq;

CREATE SEQUENCE public.social_statuses_status_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.social_statuses_status_id_seq1;

CREATE SEQUENCE public.social_statuses_status_id_seq1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.tenants_tenant_id_seq;

CREATE SEQUENCE public.tenants_tenant_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.tenants_tenant_id_seq1;

CREATE SEQUENCE public.tenants_tenant_id_seq1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.types_of_room_room_type_id_seq;

CREATE SEQUENCE public.types_of_room_room_type_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.types_of_room_room_type_id_seq1;

CREATE SEQUENCE public.types_of_room_room_type_id_seq1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.users_employee_id_seq;

CREATE SEQUENCE public.users_employee_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.users_employee_id_seq1;

CREATE SEQUENCE public.users_employee_id_seq1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
	CACHE 1
	NO CYCLE;-- public.cities определение

-- Drop table

-- DROP TABLE public.cities;

CREATE TABLE public.cities ( city_id int4 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL, city_name varchar NOT NULL, CONSTRAINT cities_pkey PRIMARY KEY (city_id));


-- public.conveniences_dict определение

-- Drop table

-- DROP TABLE public.conveniences_dict;

CREATE TABLE public.conveniences_dict ( conv_name_id int4 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL, conv_name varchar NOT NULL, CONSTRAINT convenieces_dict_pkey PRIMARY KEY (conv_name_id));


-- public.services_dict определение

-- Drop table

-- DROP TABLE public.services_dict;

CREATE TABLE public.services_dict ( service_name_id int4 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL, service_name varchar NOT NULL, CONSTRAINT services_dict_pkey PRIMARY KEY (service_name_id));


-- public.social_statuses определение

-- Drop table

-- DROP TABLE public.social_statuses;

CREATE TABLE public.social_statuses ( status_id int4 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL, status_name varchar NOT NULL, CONSTRAINT social_statuses_pkey PRIMARY KEY (status_id));


-- public.types_of_room определение

-- Drop table

-- DROP TABLE public.types_of_room;

CREATE TABLE public.types_of_room ( room_type_id int4 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL, room_type_name varchar NULL, CONSTRAINT types_of_room_pkey PRIMARY KEY (room_type_id));


-- public.hotel определение

-- Drop table

-- DROP TABLE public.hotel;

CREATE TABLE public.hotel ( hotel_id int4 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL, hotel_city int4 NOT NULL, hotel_address varchar NOT NULL, CONSTRAINT hotel_pkey PRIMARY KEY (hotel_id), CONSTRAINT hotel_hotel_city_fkey FOREIGN KEY (hotel_city) REFERENCES public.cities(city_id) ON DELETE CASCADE ON UPDATE CASCADE);


-- public.hotel_rooms определение

-- Drop table

-- DROP TABLE public.hotel_rooms;

CREATE TABLE public.hotel_rooms ( room_id int4 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL, hotel_id int4 NOT NULL, max_people int4 NOT NULL, price_per_person numeric NOT NULL, room_number int4 NOT NULL, type_of_room_id int4 NULL, CONSTRAINT hotel_rooms_pkey PRIMARY KEY (room_id), CONSTRAINT hotel_rooms_hotel_id_fkey FOREIGN KEY (hotel_id) REFERENCES public.hotel(hotel_id) ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT hotel_rooms_type_of_room_id_fkey FOREIGN KEY (type_of_room_id) REFERENCES public.types_of_room(room_type_id) ON DELETE CASCADE ON UPDATE CASCADE);


-- public.hotel_services определение

-- Drop table

-- DROP TABLE public.hotel_services;

CREATE TABLE public.hotel_services ( service_id int4 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL, hotel_id int4 NOT NULL, service_name_id int4 NOT NULL, start_of_period date NOT NULL, end_of_period date NOT NULL, price_per_one numeric NOT NULL, can_be_booked bool NOT NULL, CONSTRAINT hotel_services_pkey PRIMARY KEY (service_id), CONSTRAINT services_exclude_non_splittable_overlap EXCLUDE USING gist (hotel_id WITH =, service_name_id WITH =, daterange(start_of_period, end_of_period, '[)'::text) WITH &&), CONSTRAINT hotel_services_hotel_id_fkey FOREIGN KEY (hotel_id) REFERENCES public.hotel(hotel_id) ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT hotel_services_service_name_id_fkey FOREIGN KEY (service_name_id) REFERENCES public.services_dict(service_name_id) ON DELETE CASCADE ON UPDATE CASCADE);
CREATE INDEX services_exclude_non_splittable_overlap ON public.hotel_services USING gist (hotel_id, service_name_id, daterange(start_of_period, end_of_period, '[)'::text));


-- public.room_conveniences определение

-- Drop table

-- DROP TABLE public.room_conveniences;

CREATE TABLE public.room_conveniences ( room_id int4 NOT NULL, conv_name_id int4 NOT NULL, price_per_one numeric NOT NULL, amount int4 NOT NULL, start_date date NOT NULL, conv_id int4 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL, end_date date NOT NULL, CONSTRAINT conveniences_exclude_non_splittable_overlap EXCLUDE USING gist (room_id WITH =, conv_name_id WITH =, daterange(start_date, end_date, '[)'::text) WITH &&), CONSTRAINT room_conveniences_pk PRIMARY KEY (conv_id), CONSTRAINT room_conveniences_conv_name_id_fkey FOREIGN KEY (conv_name_id) REFERENCES public.conveniences_dict(conv_name_id) ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT room_conveniences_room_id_fkey FOREIGN KEY (room_id) REFERENCES public.hotel_rooms(room_id) ON DELETE CASCADE ON UPDATE CASCADE);
CREATE INDEX conveniences_exclude_non_splittable_overlap ON public.room_conveniences USING gist (room_id, conv_name_id, daterange(start_date, end_date, '[)'::text));


-- public.tenants определение

-- Drop table

-- DROP TABLE public.tenants;

CREATE TABLE public.tenants ( tenant_id int4 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL, first_name varchar NOT NULL, "name" varchar NOT NULL, patronymic varchar NULL, city_id int4 NOT NULL, birth_date date NOT NULL, social_status int4 NOT NULL, series int4 NULL, "number" int4 NULL, "document_type" public."document_type" NULL, email varchar NOT NULL, CONSTRAINT tenants_pkey PRIMARY KEY (tenant_id), CONSTRAINT tenants_city_id_fkey FOREIGN KEY (city_id) REFERENCES public.cities(city_id) ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT tenants_social_status_fkey FOREIGN KEY (social_status) REFERENCES public.social_statuses(status_id) ON DELETE CASCADE ON UPDATE CASCADE);


-- public.tenants_history определение

-- Drop table

-- DROP TABLE public.tenants_history;

CREATE TABLE public.tenants_history ( room_id int4 NOT NULL, booking_date date NULL, check_in_date date NULL, check_in_status public."booking_status" NOT NULL, occupied_space int4 DEFAULT 1 NOT NULL, amount_of_nights int4 NOT NULL, can_be_split bool NOT NULL, tenant_id int4 NOT NULL, booking_number varchar NOT NULL, CONSTRAINT exclude_non_splittable_overlap EXCLUDE USING gist (room_id WITH =, daterange(check_in_date, (check_in_date + amount_of_nights), '[)'::text) WITH &&) WHERE (((can_be_split = false) AND (check_in_status = ANY (ARRAY['занят'::booking_status, 'забронирован'::booking_status])))), CONSTRAINT tenants_history_unique UNIQUE (booking_number), CONSTRAINT tenants_history_hotel_rooms_fk FOREIGN KEY (room_id) REFERENCES public.hotel_rooms(room_id), CONSTRAINT tenants_history_tenants_fk FOREIGN KEY (tenant_id) REFERENCES public.tenants(tenant_id));
CREATE INDEX exclude_non_splittable_overlap ON public.tenants_history USING gist (room_id, daterange(check_in_date, (check_in_date + amount_of_nights), '[)'::text)) WHERE ((can_be_split = false) AND (check_in_status = ANY (ARRAY['занят'::booking_status, 'забронирован'::booking_status])));


-- public.users определение

-- Drop table

-- DROP TABLE public.users;

CREATE TABLE public.users ( employee_id int4 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL, hotel_id int4 NULL, username varchar NOT NULL, CONSTRAINT users_pkey PRIMARY KEY (employee_id), CONSTRAINT users_hotel_id_fkey FOREIGN KEY (hotel_id) REFERENCES public.hotel(hotel_id) ON DELETE CASCADE ON UPDATE CASCADE);


-- public.services_history определение

-- Drop table

-- DROP TABLE public.services_history;

CREATE TABLE public.services_history ( row_id int4 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL, history_id varchar NOT NULL, service_id int4 NOT NULL, amount int4 NOT NULL, order_date date NOT NULL, CONSTRAINT services_history_pkey PRIMARY KEY (row_id), CONSTRAINT services_history_service_id_fkey FOREIGN KEY (service_id) REFERENCES public.hotel_services(service_id) ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT services_history_tenants_history_fk FOREIGN KEY (history_id) REFERENCES public.tenants_history(booking_number) ON DELETE CASCADE ON UPDATE CASCADE);


-- public.v_all_conveniences_with_rooms исходный текст

CREATE OR REPLACE VIEW public.v_all_conveniences_with_rooms
AS SELECT rc.conv_id,
          hr.room_id,
          cd.conv_name_id,
          rc.price_per_one,
          rc.amount,
          rc.start_date,
          cd.conv_name
   FROM hotel_rooms hr
            LEFT JOIN room_conveniences rc ON hr.room_id = rc.room_id
            RIGHT JOIN conveniences_dict cd ON rc.conv_name_id = cd.conv_name_id;


-- public.v_all_hotels исходный текст

CREATE OR REPLACE VIEW public.v_all_hotels
AS SELECT h.hotel_id,
          c.city_name AS hotel_city,
          h.hotel_address,
          h.hotel_city AS city_id
   FROM hotel h
            JOIN cities c ON h.hotel_city = c.city_id;


-- public.v_all_rooms_with_conveniences исходный текст

CREATE OR REPLACE VIEW public.v_all_rooms_with_conveniences
AS SELECT rc.conv_id,
          hr.room_id,
          cd.conv_name_id,
          rc.price_per_one,
          rc.amount,
          rc.start_date,
          cd.conv_name
   FROM hotel_rooms hr
            LEFT JOIN room_conveniences rc ON hr.room_id = rc.room_id
            LEFT JOIN conveniences_dict cd ON rc.conv_name_id = cd.conv_name_id;


-- public.v_all_users исходный текст

CREATE OR REPLACE VIEW public.v_all_users
AS SELECT u.employee_id AS user_id,
          u.username,
          r.rolname AS role_name,
          u.hotel_id,
          r_main.rolcanlogin = false AS user_locked,
          (c.city_name::text || '-'::text) || h.hotel_address::text AS hotel_info
   FROM users u
            JOIN hotel h USING (hotel_id)
            JOIN cities c ON h.hotel_city = c.city_id
            JOIN pg_roles r_main ON r_main.rolname = u.username::text
     JOIN pg_roles r ON pg_has_role(u.username::name, r.oid, 'member'::text) AND r.rolname <> u.username::text
   WHERE r.rolname <> 'admin_role'::name
   ORDER BY u.employee_id;


-- public.v_current_room_statuses исходный текст

CREATE OR REPLACE VIEW public.v_current_room_statuses
AS WITH current_occupancy AS (
         SELECT th.room_id,
            sum(th.occupied_space) AS current_occupied_space
           FROM tenants_history th
          WHERE th.check_in_status = 'занят'::booking_status AND CURRENT_DATE >= th.check_in_date AND CURRENT_DATE < (th.check_in_date + th.amount_of_nights)
          GROUP BY th.room_id
        )
SELECT hr.hotel_id,
       hr.room_id,
       hr.room_number,
       tor.room_type_name,
       hr.max_people,
       hr.price_per_person,
       CASE
           WHEN co.current_occupied_space IS NULL OR co.current_occupied_space = 0 THEN 'свободен'::text
           WHEN co.current_occupied_space >= hr.max_people THEN 'занят'::text
           ELSE 'свободен для заселения'::text
           END AS status,
       GREATEST(0::bigint, hr.max_people - COALESCE(co.current_occupied_space, 0::bigint)) AS available_space
FROM hotel_rooms hr
         JOIN types_of_room tor ON hr.type_of_room_id = tor.room_type_id
         LEFT JOIN current_occupancy co ON hr.room_id = co.room_id
ORDER BY hr.room_number;


-- public.v_daily_invoices исходный текст

CREATE OR REPLACE VIEW public.v_daily_invoices
AS SELECT (('INV-'::text || to_char(CURRENT_DATE::timestamp with time zone, 'YYYYMMDD'::text)) || '-'::text) || th.booking_number::text AS invoice_number,
    th.booking_number,
    calculate_total_cost_for_booking(th.booking_number) AS total_amount,
    CURRENT_DATE AS issue_date,
    false AS is_paid,
    hr.hotel_id
   FROM tenants_history th
       JOIN hotel_rooms hr ON th.room_id = hr.room_id
   WHERE (th.check_in_date + th.amount_of_nights) = CURRENT_DATE AND th.check_in_status = 'занят'::booking_status;


-- public.v_rooms_with_types_and_hotels исходный текст

CREATE OR REPLACE VIEW public.v_rooms_with_types_and_hotels
AS SELECT h.hotel_id,
          (c.city_name::text || ' - '::text) || h.hotel_address::text AS hotel_info,
              hr.room_id,
          hr.room_number,
          tor.room_type_name,
          hr.max_people,
          hr.price_per_person,
          hr.type_of_room_id
   FROM hotel_rooms hr
            JOIN hotel h ON hr.hotel_id = h.hotel_id
            JOIN types_of_room tor ON hr.type_of_room_id = tor.room_type_id
            JOIN cities c ON h.hotel_city = c.city_id;



-- DROP FUNCTION public.add_city(varchar);

CREATE OR REPLACE FUNCTION public.add_city(city_name_in character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
BEGIN
	PERFORM 1 FROM public.cities WHERE city_name = city_name_in;

	IF NOT FOUND THEN
		INSERT INTO public.cities (city_name) VALUES(city_name_in);
ELSE
		RAISE EXCEPTION 'Город с таким именем уже существует';
END IF;
END;
$function$
;

-- DROP FUNCTION public.add_convenience(varchar);

CREATE OR REPLACE FUNCTION public.add_convenience(conv_name_in character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
BEGIN
INSERT INTO public.conveniences_dict (conv_name)
VALUES (conv_name_in);
END;
$function$
;

-- DROP FUNCTION public.add_hotel_service(int4, int4, date, date, numeric, bool);

CREATE OR REPLACE FUNCTION public.add_hotel_service(hotel_id_in integer, service_name_id_in integer, start_of_period_in date, end_of_period_in date, price_per_one_in numeric, can_be_booked_in boolean)
 RETURNS void
 LANGUAGE sql
AS $function$
	INSERT INTO public.hotel_services (hotel_id, service_name_id, start_of_period, end_of_period, price_per_one, can_be_booked)
	VALUES(hotel_id_in, service_name_id_in, start_of_period_in, end_of_period_in, price_per_one_in, can_be_booked_in);
$function$
;

-- DROP FUNCTION public.add_new_hotel(int4, varchar);

CREATE OR REPLACE FUNCTION public.add_new_hotel(hotel_city_in integer, hotel_address_in character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
BEGIN
INSERT INTO public.hotel (hotel_city, hotel_address) VALUES (hotel_city_in, hotel_address_in);
END;
$function$
;

-- DROP FUNCTION public.add_room(int4, int4, numeric, int4, int4);

CREATE OR REPLACE FUNCTION public.add_room(hotel_id_in integer, max_people_in integer, price_per_person_in numeric, room_number_in integer, type_of_room_id_in integer)
 RETURNS void
 LANGUAGE sql
AS $function$
	INSERT INTO hotel_rooms (hotel_id, max_people, price_per_person, room_number, type_of_room_id)
	VALUES (hotel_id_in, max_people_in, price_per_person_in, room_number_in, type_of_room_id_in);
$function$
;

-- DROP FUNCTION public.add_room_convenience(int4, int4, numeric, int4, date);

CREATE OR REPLACE FUNCTION public.add_room_convenience(room_id_in integer, conv_name_id_in integer, price_per_one_in numeric, amount_in integer, start_date_in date)
 RETURNS void
 LANGUAGE sql
AS $function$
	INSERT INTO public.room_conveniences (room_id, conv_name_id, price_per_one, amount, start_date)
	VALUES(room_id_in, conv_name_id_in, price_per_one_in, amount_in, start_date_in);
$function$
;

-- DROP FUNCTION public.add_service(varchar);

CREATE OR REPLACE FUNCTION public.add_service(service_name_in character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
BEGIN
	PERFORM 1 FROM public.services_dict WHERE service_name = service_name_in;

	IF NOT FOUND THEN
		INSERT INTO public.services_dict (service_name) VALUES(service_name_in);
ELSE
		RAISE EXCEPTION 'Услуга с таким именем уже существует';
END IF;
END;
$function$
;

-- DROP FUNCTION public.add_service_history(varchar, int4, int4);

CREATE OR REPLACE FUNCTION public.add_service_history(history_id_in character varying, service_id_in integer, amount_in integer)
 RETURNS void
 LANGUAGE sql
AS $function$
	INSERT INTO public.services_history (history_id, service_id, amount) VALUES(history_id_in, service_id_in, amount_in);
$function$
;

-- DROP FUNCTION public.add_social_status(varchar);

CREATE OR REPLACE FUNCTION public.add_social_status(status_name_in character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
BEGIN
	PERFORM 1 FROM public.social_statuses WHERE status_name = status_name_in;

	IF NOT FOUND THEN
		INSERT INTO public.social_statuses (status_name) VALUES(status_name_in);
ELSE
		RAISE EXCEPTION 'Социальный статус с таким именем уже существует';
END IF;
END;
$function$
;

-- DROP FUNCTION public.add_tenant(varchar, varchar, varchar, int4, date, int4, varchar, int4, int4, varchar);

CREATE OR REPLACE FUNCTION public.add_tenant(first_name_in character varying, name_in character varying, patronymic_in character varying, city_id_in integer, birth_date_in date, social_status_in integer, email_in character varying, series_in integer DEFAULT NULL::integer, number_in integer DEFAULT NULL::integer, document_type_in character varying DEFAULT NULL::character varying)
 RETURNS void
 LANGUAGE sql
AS $function$
	INSERT INTO public.tenants (first_name, "name", patronymic, city_id, birth_date, social_status, series, "number", "document_type", email)
	VALUES (first_name_in, name_in, patronymic_in, city_id_in, birth_date_in, social_status_in, series_in, number_in, document_type_in::document_type, email_in);
$function$
;

-- DROP FUNCTION public.add_tenant_history(int4, int4, date, date, varchar, int4, int4, bool);

CREATE OR REPLACE FUNCTION public.add_tenant_history(tenant_id_in integer, room_id_in integer, booking_date_in date, check_in_date_in date, check_in_status_in character varying, occupied_space_in integer, amount_of_nights_in integer, can_be_split_in boolean)
 RETURNS void
 LANGUAGE sql
AS $function$
	INSERT INTO public.tenants_history (booking_number, tenant_id, room_id, booking_date, check_in_date, check_in_status, occupied_space, amount_of_nights, can_be_split)
	VALUES (public.generate_booking_number(check_in_date_in, room_id_in, tenant_id_in), tenant_id_in, room_id_in, booking_date_in, check_in_date_in, check_in_status_in::booking_status, occupied_space_in, amount_of_nights_in, can_be_split_in)
$function$
;

-- DROP FUNCTION public.add_type_of_room(varchar);

CREATE OR REPLACE FUNCTION public.add_type_of_room(room_type_name_in character varying)
 RETURNS void
 LANGUAGE sql
AS $function$
	INSERT INTO public.types_of_room (room_type_name)
    VALUES (room_type_name_in);
$function$
;

-- DROP FUNCTION public.ban_user(varchar);

CREATE OR REPLACE FUNCTION public.ban_user(username_in character varying)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
BEGIN
	PERFORM pg_terminate_backend(pid)
	FROM pg_stat_activity
	WHERE usename = username_in;

EXECUTE format('ALTER ROLE %I NOLOGIN', username_in);
END;
$function$
;

-- DROP FUNCTION public.book_a_room(int4, int4, date, varchar, int4, int4, bool);

CREATE OR REPLACE FUNCTION public.book_a_room(tenant_id_in integer, room_id_in integer, booking_date_in date, check_in_status_in character varying, occupied_space_in integer, amount_of_nights_in integer, can_be_splited_in boolean)
 RETURNS character varying
 LANGUAGE plpgsql
AS $function$
	DECLARE
booking_number_out VARCHAR;
BEGIN
INSERT INTO public.tenants_history (booking_number, tenant_id, room_id, booking_date, check_in_status, amount_of_nights, can_be_splited)
VALUES (public.generate_booking_number(booking_date_in, room_id_in), tenant_id_in, room_id_in, booking_date_in, check_in_status_in, occupied_space_in, amount_of_nights_in, can_be_splited_in)
    RETURNING booking_number INTO booking_number_out;

RETURN booking_number_out;
END;
$function$
;

-- DROP FUNCTION public.calculate_booking_cost(int4, date, date, int4, bool);

CREATE OR REPLACE FUNCTION public.calculate_booking_cost(room_id_in integer, check_in_date_in date, check_out_date_in date, people_count_in integer, occupy_entire_room_in boolean)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE
nights_count INT;
    base_price_per_person NUMERIC;
    max_people_in_room INT;
    conveniences_cost_per_night NUMERIC;
    effective_people_count INT;
    total_cost NUMERIC;
BEGIN
    -- 1. Рассчитываем количество ночей
    nights_count := check_out_date_in - check_in_date_in;

    -- 2. Получаем базовую цену и вместимость номера
SELECT price_per_person, max_people
INTO base_price_per_person, max_people_in_room
FROM public.hotel_rooms
WHERE room_id = room_id_in;

-- 3. Рассчитываем суточную стоимость удобств в номере
SELECT COALESCE(SUM(price_per_one * amount), 0)
INTO conveniences_cost_per_night
FROM public.room_conveniences
WHERE room_id = room_id_in AND check_in_date_in BETWEEN rc.start_date AND rc.end_date; -- Учитываем только те, что действуют на момент заезда

-- 4. Определяем, за скольких человек будет оплата
IF occupy_entire_room_in THEN
        -- Если занимают весь номер, платят как за максимальное количество мест
        effective_people_count := max_people_in_room;
ELSE
        -- Если нет, платят за фактическое количество заселяющихся
        effective_people_count := people_count_in;
END IF;

    -- 5. Итоговая стоимость: (базовая цена за человека + общая цена удобств) * количество платящих людей * количество ночей
    total_cost := (base_price_per_person + conveniences_cost_per_night) * effective_people_count * nights_count;

RETURN total_cost;
END;
$function$
;

-- DROP FUNCTION public.calculate_total_cost_for_booking(varchar);

CREATE OR REPLACE FUNCTION public.calculate_total_cost_for_booking(booking_number_in character varying)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE
total_cost NUMERIC := 0;
    v_room_id INT;
    v_check_in_date DATE;
    v_amount_of_nights INT;
    v_occupied_space INT;
    v_can_be_split BOOLEAN;
    v_max_people INT;
    effective_people_count INT;
    room_cost NUMERIC;
    conveniences_cost_per_night NUMERIC;
    services_cost NUMERIC;
    base_price_per_person NUMERIC;
BEGIN
    -- Подзапрос для получения деталей бронирования
SELECT th.room_id, th.check_in_date, th.amount_of_nights,
       th.occupied_space, th.can_be_split
INTO v_room_id, v_check_in_date, v_amount_of_nights,
    v_occupied_space, v_can_be_split
FROM public.tenants_history th
WHERE th.booking_number = booking_number_in;

-- Подзапрос для получения деталей номера
SELECT hr.price_per_person, hr.max_people
INTO base_price_per_person, v_max_people
FROM public.hotel_rooms hr
WHERE hr.room_id = v_room_id;

-- Определение количества платящих людей
IF v_can_be_split THEN
        effective_people_count := v_occupied_space;
ELSE
        effective_people_count := v_max_people;
END IF;

    -- Подзапрос для расчета стоимости удобств
SELECT COALESCE(SUM(rc.price_per_one * rc.amount), 0)
INTO conveniences_cost_per_night
FROM public.room_conveniences rc
WHERE rc.room_id = v_room_id AND rc.start_date <= v_check_in_date;

-- Расчет стоимости номера
room_cost := (base_price_per_person * effective_people_count + conveniences_cost_per_night) * v_amount_of_nights;

    -- Подзапрос для расчета стоимости услуг
SELECT COALESCE(SUM(hs.price_per_one * sh.amount), 0)
INTO services_cost
FROM public.services_history sh
         INNER JOIN public.hotel_services hs ON sh.service_id = hs.service_id
WHERE sh.history_id = booking_number_in;

-- Итоговая стоимость
total_cost := room_cost + services_cost;

RETURN total_cost;
END;
$function$
;

-- DROP FUNCTION public.change_user_hotel(int4, int4);

CREATE OR REPLACE FUNCTION public.change_user_hotel(user_id_in integer, hotel_id_in integer)
 RETURNS void
 LANGUAGE sql
AS $function$
UPDATE public.users SET hotel_id=hotel_id_in WHERE employee_id=user_id_in;
$function$
;

-- DROP FUNCTION public.change_user_password(varchar, varchar);

CREATE OR REPLACE FUNCTION public.change_user_password(username_in character varying, password_in character varying)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
BEGIN
EXECUTE format('ALTER USER %I WITH PASSWORD %L;', username_in, password_in);
END;
$function$
;

-- DROP FUNCTION public.change_user_role(varchar, varchar);

CREATE OR REPLACE FUNCTION public.change_user_role(username_in character varying, new_role character varying)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
DECLARE
prev_role TEXT;
BEGIN
SELECT rolname INTO prev_role
FROM pg_roles
WHERE pg_has_role(username_in, oid, 'member') AND rolname <> username_in;

EXECUTE format('REVOKE %I FROM %I', prev_role, username_in);
EXECUTE format('GRANT %s TO %I', new_role, username_in);
END;
$function$
;

-- DROP FUNCTION public.change_username(int4, varchar);

CREATE OR REPLACE FUNCTION public.change_username(user_id_in integer, newname character varying)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
DECLARE
username_old TEXT;
BEGIN
SELECT username INTO username_old FROM users WHERE employee_id = user_id_in;

IF NOT FOUND THEN
		RAISE EXCEPTION 'Пользователь не найден';
END IF;

	IF username_old = newname THEN
		RETURN;
END IF;

UPDATE users SET username = newname
WHERE employee_id = user_id_in;

EXECUTE format('ALTER USER %I RENAME TO %I;', username_old, newname);
EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Ошибка при смене имени пользователя: %', SQLERRM;
END;
$function$
;

-- DROP FUNCTION public.create_user_with_role(varchar, varchar, varchar, int4);

CREATE OR REPLACE FUNCTION public.create_user_with_role(p_username character varying, p_password character varying, p_role character varying, p_hotel_id integer)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
DECLARE
v_user_exists BOOLEAN;
BEGIN

SELECT EXISTS(SELECT 1 FROM pg_catalog.pg_user WHERE usename = p_username) INTO v_user_exists;
IF v_user_exists THEN
        RAISE EXCEPTION 'Пользователь % уже существует', p_username;
END IF;

EXECUTE format('CREATE USER %I WITH PASSWORD %L IN ROLE %s', p_username, p_password, p_role);

IF p_role = 'admin_role' THEN
		p_hotel_id = NULL;
END IF;

INSERT INTO public.users (hotel_id, username)
VALUES (p_hotel_id, p_username);

RAISE NOTICE 'Пользователь % создан с ролью %', p_username, p_role;
END;
$function$
;

-- DROP FUNCTION public.delete_user(varchar);

CREATE OR REPLACE FUNCTION public.delete_user(p_username character varying)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
BEGIN

DELETE FROM public.users WHERE username = p_username;

EXECUTE format('DROP OWNED BY %I', p_username);
EXECUTE format('DROP USER IF EXISTS %I', p_username);

RAISE NOTICE 'Пользователь % успешно удален', p_username;
EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Ошибка при удалении пользователя %: %', p_username, SQLERRM;
END;
$function$
;

-- DROP FUNCTION public.edit_city(int4, varchar);

CREATE OR REPLACE FUNCTION public.edit_city(city_id_in integer, city_name_in character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
BEGIN
	PERFORM 1 FROM public.cities WHERE city_name = city_name_in;

	IF NOT FOUND THEN
UPDATE public.cities SET city_name = city_name_in WHERE city_id = city_id_in;
ELSE
		RAISE EXCEPTION 'Город с таким именем уже существует';
END IF;
END;
$function$
;

-- DROP FUNCTION public.edit_convenience(int4, varchar);

CREATE OR REPLACE FUNCTION public.edit_convenience(conv_name_id_in integer, conv_name_in character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
BEGIN
UPDATE public.conveniences_dict
SET conv_name = conv_name_in
WHERE conv_name_id = conv_name_id_in;
END;
$function$
;

-- DROP FUNCTION public.edit_hotel(int4, int4, varchar);

CREATE OR REPLACE FUNCTION public.edit_hotel(hotel_id_in integer, city_id_in integer, hotel_address_in character varying)
 RETURNS void
 LANGUAGE sql
AS $function$
UPDATE hotel SET hotel_city = city_id_in, hotel_address = hotel_address_in
WHERE hotel_id = hotel_id_in;
$function$
;

-- DROP FUNCTION public.edit_hotel_service(int4, int4, int4, date, date, numeric, bool);

CREATE OR REPLACE FUNCTION public.edit_hotel_service(service_id_in integer, hotel_id_in integer, service_name_id_in integer, start_of_period_in date, end_of_period_in date, price_per_one_in numeric, can_be_booked_in boolean)
 RETURNS void
 LANGUAGE sql
AS $function$
UPDATE public.hotel_services SET hotel_id=hotel_id_in, service_name_id=service_name_id_in, start_of_period=start_of_period_in, end_of_period=end_of_period_in, price_per_one=price_per_one_in, can_be_booked=can_be_booked_in WHERE service_id=service_id_in;
$function$
;

-- DROP FUNCTION public.edit_room(int4, int4, int4, numeric, int4, int4);

CREATE OR REPLACE FUNCTION public.edit_room(room_id_in integer, hotel_id_in integer, max_people_in integer, price_per_person_in numeric, room_number_in integer, type_of_room_id_in integer)
 RETURNS void
 LANGUAGE sql
AS $function$
UPDATE public.hotel_rooms SET hotel_id=hotel_id_in, max_people=max_people_in, price_per_person=price_per_person_in, room_number=room_number_in, type_of_room_id=type_of_room_id_in
WHERE room_id=room_id_in;
$function$
;

-- DROP FUNCTION public.edit_room_convenience(int4, int4, numeric, int4, date);

CREATE OR REPLACE FUNCTION public.edit_room_convenience(conv_id_in integer, conv_name_id_in integer, price_per_one_in numeric, amount_in integer, start_date_in date)
 RETURNS void
 LANGUAGE sql
AS $function$
UPDATE public.room_conveniences
SET conv_name_id = conv_name_id_in, price_per_one=price_per_one_in, amount=amount_in, start_date=start_date_in
WHERE conv_id=conv_id_in;
$function$
;

-- DROP FUNCTION public.edit_service(int4, varchar);

CREATE OR REPLACE FUNCTION public.edit_service(service_name_id_in integer, service_name_in character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
BEGIN
	PERFORM 1 FROM public.services_dict WHERE service_name = service_name_in;

	IF NOT FOUND THEN
UPDATE public.services_dict SET service_name=service_name_in WHERE service_name_id=service_name_id_in;
ELSE
		RAISE EXCEPTION 'Услуга с таким именем уже существует';
END IF;
END;
$function$
;

-- DROP FUNCTION public.edit_service_history(int4, varchar, int4, int4);

CREATE OR REPLACE FUNCTION public.edit_service_history(row_id_in integer, history_id_in character varying, service_id_in integer, amount_in integer)
 RETURNS void
 LANGUAGE sql
AS $function$
UPDATE public.services_history SET history_id=history_id_in, service_id=service_id_in, amount=amount_in WHERE row_id=row_id_in;
$function$
;

-- DROP FUNCTION public.edit_social_status(int4, varchar);

CREATE OR REPLACE FUNCTION public.edit_social_status(status_id_in integer, status_name_in character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
BEGIN
	PERFORM 1 FROM public.social_statuses WHERE status_name = status_name_in;

	IF NOT FOUND THEN
UPDATE public.social_statuses SET status_name=status_name_in WHERE status_id = status_id_in;
ELSE
		RAISE EXCEPTION 'Социальный статус с таким именем уже существует';
END IF;
END;
$function$
;

-- DROP FUNCTION public.edit_tenant(int4, varchar, varchar, varchar, int4, date, int4, varchar, int4, int4, varchar);

CREATE OR REPLACE FUNCTION public.edit_tenant(tenant_id_in integer, first_name_in character varying, name_in character varying, patronymic_in character varying, city_id_in integer, birth_date_in date, social_status_in integer, email_in character varying, series_in integer DEFAULT NULL::integer, number_in integer DEFAULT NULL::integer, document_type_in character varying DEFAULT NULL::character varying)
 RETURNS void
 LANGUAGE sql
AS $function$
UPDATE public.tenants
SET first_name=first_name_in, "name"=name_in, patronymic=patronymic_in, city_id=city_id_in, birth_date=birth_date_in, social_status=social_status_in, series=series_in, "number"=number_in, "document_type"=document_type_in::document_type, email=email_in
WHERE tenant_id = tenant_id_in;
$function$
;

-- DROP FUNCTION public.edit_tenant_history(varchar, int4, int4, date, date, varchar, int4, int4, bool);

CREATE OR REPLACE FUNCTION public.edit_tenant_history(booking_number_in character varying, tenant_id_in integer, room_id_in integer, booking_date_in date, check_in_date_in date, check_in_status_in character varying, occupied_space_in integer, amount_of_nights_in integer, can_be_split_in boolean)
 RETURNS void
 LANGUAGE sql
AS $function$
UPDATE public.tenants_history SET room_id=room_id_in, booking_date=booking_date_in, check_in_date=check_in_date_in, check_in_status=check_in_status_in::booking_status, occupied_space=occupied_space_in, amount_of_nights=amount_of_nights_in, can_be_split=can_be_split_in, tenant_id=tenant_id_in WHERE booking_number=booking_number_in;
$function$
;

-- DROP FUNCTION public.edit_type_of_room(int4, varchar);

CREATE OR REPLACE FUNCTION public.edit_type_of_room(room_type_id_in integer, room_type_name_in character varying)
 RETURNS void
 LANGUAGE sql
AS $function$
UPDATE public.types_of_room
SET room_type_name = room_type_name_in
WHERE room_type_id = room_type_id_in;
$function$
;

-- DROP FUNCTION public.generate_booking_number(date, int4, int4);

CREATE OR REPLACE FUNCTION public.generate_booking_number(booking_date date, room_id_in integer, tenant_id integer)
 RETURNS character varying
 LANGUAGE plpgsql
AS $function$
DECLARE
room_number INT;
BEGIN
SELECT hr.room_number INTO room_number
FROM hotel_rooms hr WHERE room_id = room_id_in;

RETURN 'BN-' || to_char(booking_date, 'YYYYMMDD') || '-'  || room_number::varchar || '-' || tenant_id::varchar;
END;
$function$
;

-- DROP FUNCTION public.get_active_booking(int4, int4);

CREATE OR REPLACE FUNCTION public.get_active_booking(room_id_in integer, tenant_id_in integer)
 RETURNS character varying
 LANGUAGE plpgsql
AS $function$
DECLARE
booking_number_out VARCHAR;
BEGIN
SELECT booking_number
INTO booking_number_out
FROM public.tenants_history
WHERE room_id = room_id_in
  AND tenant_id = tenant_id_in
  AND check_in_status = 'занят'::booking_status
      AND CURRENT_DATE >= check_in_date
      AND CURRENT_DATE < (check_in_date + amount_of_nights)
    LIMIT 1;

RETURN booking_number_out;
END;
$function$
;

-- DROP FUNCTION public.get_all_cities();

CREATE OR REPLACE FUNCTION public.get_all_cities()
 RETURNS TABLE(city_id integer, city_name character varying)
 LANGUAGE sql
AS $function$
SELECT * FROM cities;
$function$
;

-- DROP FUNCTION public.get_all_cities_filtered(varchar);

CREATE OR REPLACE FUNCTION public.get_all_cities_filtered(city_name_in character varying)
 RETURNS TABLE(city_id integer, city_name character varying)
 LANGUAGE sql
AS $function$
SELECT * FROM cities WHERE city_name ILIKE '%' || city_name_in || '%'
ORDER BY city_id;
$function$
;

-- DROP FUNCTION public.get_all_conveniences();

CREATE OR REPLACE FUNCTION public.get_all_conveniences()
 RETURNS TABLE(conv_name_id integer, conv_name character varying)
 LANGUAGE sql
AS $function$
SELECT * FROM conveniences_dict;
$function$
;

-- DROP FUNCTION public.get_all_conveniences_filtered(varchar);

CREATE OR REPLACE FUNCTION public.get_all_conveniences_filtered(conv_name_in character varying)
 RETURNS TABLE(conv_name_id integer, conv_name character varying)
 LANGUAGE sql
AS $function$
SELECT * FROM conveniences_dict WHERE conv_name ILIKE '%' || conv_name_in || '%'
ORDER BY conv_name_id;
$function$
;

-- DROP FUNCTION public.get_all_hotels();

CREATE OR REPLACE FUNCTION public.get_all_hotels()
 RETURNS TABLE(hotel_id integer, hotel_city character varying, hotel_address character varying, city_id integer)
 LANGUAGE sql
AS $function$
SELECT h.hotel_id, h.hotel_city, h.hotel_address, h.city_id FROM v_all_hotels h;
$function$
;

-- DROP FUNCTION public.get_all_hotels_filtered(varchar, varchar);

CREATE OR REPLACE FUNCTION public.get_all_hotels_filtered(hotel_city_in character varying, hotel_address_in character varying)
 RETURNS TABLE(hotel_id integer, hotel_city character varying, hotel_address character varying, city_id integer)
 LANGUAGE sql
AS $function$
SELECT h.hotel_id, h.hotel_city, h.hotel_address, h.city_id FROM v_all_hotels h
WHERE h.hotel_city ILIKE '%' || hotel_city_in || '%' AND
	  h.hotel_address ILIKE '%' || hotel_address_in || '%'
ORDER BY h.hotel_id;
$function$
;

-- DROP FUNCTION public.get_all_services();

CREATE OR REPLACE FUNCTION public.get_all_services()
 RETURNS TABLE(service_name_id integer, service_name character varying)
 LANGUAGE sql
AS $function$
SELECT sd.service_name_id, sd.service_name FROM public.services_dict sd;
$function$
;

-- DROP FUNCTION public.get_all_services_filtered(varchar);

CREATE OR REPLACE FUNCTION public.get_all_services_filtered(service_name_in character varying)
 RETURNS TABLE(service_name_id integer, service_name character varying)
 LANGUAGE sql
AS $function$
SELECT sd.service_name_id, sd.service_name FROM public.services_dict sd WHERE service_name ILIKE '%' || service_name_in || '%'
ORDER BY service_name_id;
$function$
;

-- DROP FUNCTION public.get_all_social_statuses();

CREATE OR REPLACE FUNCTION public.get_all_social_statuses()
 RETURNS TABLE(status_id integer, status_name character varying)
 LANGUAGE sql
AS $function$
SELECT ss.status_id, ss.status_name FROM public.social_statuses ss;
$function$
;

-- DROP FUNCTION public.get_all_social_statuses_filtered(varchar);

CREATE OR REPLACE FUNCTION public.get_all_social_statuses_filtered(status_name_in character varying)
 RETURNS TABLE(status_id integer, status_name character varying)
 LANGUAGE sql
AS $function$
SELECT ss.status_id, ss.status_name FROM public.social_statuses ss
WHERE ss.status_name ILIKE '%' || status_name_in || '%'
ORDER BY ss.status_id;
$function$
;

-- DROP FUNCTION public.get_all_tenants();

CREATE OR REPLACE FUNCTION public.get_all_tenants()
 RETURNS TABLE(tenant_id integer, first_name character varying, name character varying, patronymic character varying, city_id integer, birth_date date, social_status_id integer, series integer, number integer, document_type character varying, email character varying)
 LANGUAGE sql
AS $function$
SELECT tenant_id, first_name, "name", patronymic, city_id, birth_date, social_status, series, "number", "document_type", email
FROM public.tenants;
$function$
;

-- DROP FUNCTION public.get_all_tenants_filtered(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION public.get_all_tenants_filtered(first_name_in character varying, name_in character varying, patronymic_in character varying, city_name_in character varying, birth_date_in character varying, social_status_name_in character varying, series_in character varying, number_in character varying, document_type_in character varying, email_in character varying)
 RETURNS TABLE(tenant_id integer, first_name character varying, name character varying, patronymic character varying, city_id integer, birth_date date, social_status_id integer, series integer, number integer, document_type character varying, email character varying)
 LANGUAGE sql
AS $function$
SELECT tenant_id, first_name, "name", patronymic, city_id, birth_date, social_status, series, "number", "document_type", email
FROM public.tenants t
         JOIN cities c using(city_id)
         JOIN social_statuses ss ON t.social_status = ss.status_id
WHERE t.first_name ILIKE '%' || first_name_in || '%' AND
	  t."name" ILIKE '%' || name_in || '%' AND
	  t.patronymic ILIKE '%' || patronymic_in || '%' AND
	  c.city_name ILIKE '%' || city_name_in || '%' AND
	  t.birth_date::varchar ILIKE '%' || birth_date_in || '%' AND
	  ss.status_name ILIKE '%' || social_status_name_in || '%' AND
	  t.series::varchar ILIKE '%' || series_in || '%' AND
	  t."number"::varchar ILIKE '%' || number_in || '%' AND
	  t.document_type::varchar ILIKE '%' || document_type_in || '%' AND
	  t.email ILIKE '%' || email_in || '%'
ORDER BY tenant_id;

$function$
;

-- DROP FUNCTION public.get_all_types_of_room();

CREATE OR REPLACE FUNCTION public.get_all_types_of_room()
 RETURNS TABLE(type_id integer, type_name character varying)
 LANGUAGE sql
AS $function$
SELECT * FROM public.types_of_room;
$function$
;

-- DROP FUNCTION public.get_all_types_of_room_filtered(varchar);

CREATE OR REPLACE FUNCTION public.get_all_types_of_room_filtered(type_name_in character varying)
 RETURNS TABLE(type_id integer, type_name character varying)
 LANGUAGE sql
AS $function$
SELECT * FROM public.types_of_room WHERE room_type_name ILIKE '%' || type_name_in || '%'
ORDER BY room_type_id;
$function$
;

-- DROP FUNCTION public.get_all_users();

CREATE OR REPLACE FUNCTION public.get_all_users()
 RETURNS TABLE(user_id integer, username character varying, role_name character varying, hotel_id integer, user_locked boolean)
 LANGUAGE sql
AS $function$
SELECT
    u.user_id,
    u.username,
    u.role_name,
    u.hotel_id,
    u.user_locked
FROM v_all_users u;
$function$
;

-- DROP FUNCTION public.get_all_users_filtered(varchar, varchar, varchar, bool);

CREATE OR REPLACE FUNCTION public.get_all_users_filtered(username_in character varying, role_name_in character varying, hotel_info_in character varying, user_locked_in boolean)
 RETURNS TABLE(user_id integer, username character varying, role_name character varying, hotel_id integer, user_locked boolean)
 LANGUAGE sql
AS $function$
SELECT u.user_id,
       u.username,
       u.role_name,
       u.hotel_id,
       u.user_locked
FROM v_all_users u
WHERE u.username ILIKE '%' || COALESCE(username_in, '') || '%' AND
	  u.role_name ILIKE '%' || COALESCE(role_name_in, '') || '%' AND
	  (user_locked_in is NULL or u.user_locked = user_locked_in) AND
	  u.hotel_info ILIKE '%' || hotel_info_in || '%';
$function$
;

-- DROP FUNCTION public.get_booking_data(varchar);

CREATE OR REPLACE FUNCTION public.get_booking_data(email_in character varying)
 RETURNS TABLE(book_id character varying, book_date date, room_number integer, status booking_status, amount_of_nigths integer, can_be_split boolean)
 LANGUAGE plpgsql
AS $function$
BEGIN
SELECT booking_number, booking_date, th.room_number, check_in_status, th.amount_of_nights, th.can_be_split
FROM tenants_history th
         JOIN hotel_rooms hr ON th.room_id = hr.room_id
         JOIN tenants ten ON ten.tenant_id = th.tenant_id
WHERE ten.email = email_in
ORDER BY booking_date ASC;
END;
$function$
;

-- DROP FUNCTION public.get_booking_details(varchar);

CREATE OR REPLACE FUNCTION public.get_booking_details(booking_number_in character varying)
 RETURNS TABLE(booking_number character varying, tenant_name text, room_number text, check_in_date date, check_out_date date, status character varying, total_cost numeric)
 LANGUAGE plpgsql
AS $function$
BEGIN
RETURN QUERY
SELECT
    th.booking_number,
    (t.first_name || ' ' || t."name" || ' ' || COALESCE(t.patronymic, ''))::text,
    hr.room_number::text,
    th.check_in_date,
    (th.check_in_date + th.amount_of_nights)::date,
    th.check_in_status::varchar,
    public.calculate_total_cost_for_booking(th.booking_number)
FROM public.tenants_history th
         JOIN public.tenants t ON th.tenant_id = t.tenant_id
         JOIN public.hotel_rooms hr ON th.room_id = hr.room_id
WHERE th.booking_number = booking_number_in;
END;
$function$
;

-- DROP FUNCTION public.get_booking_details_filtered(varchar, varchar, varchar, varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION public.get_booking_details_filtered(booking_number_in character varying, tenant_name_in character varying DEFAULT NULL::character varying, room_number_in character varying DEFAULT NULL::character varying, check_in_date_in character varying DEFAULT NULL::character varying, check_out_date_in character varying DEFAULT NULL::character varying, status_in character varying DEFAULT NULL::character varying, total_cost_in character varying DEFAULT NULL::character varying)
 RETURNS TABLE(booking_number character varying, tenant_name text, room_number text, check_in_date date, check_out_date date, status character varying, total_cost numeric)
 LANGUAGE sql
AS $function$
SELECT * FROM public.get_booking_details(booking_number_in) bd
WHERE bd.tenant_name ILIKE '%' || COALESCE(tenant_name_in, '') || '%'
        AND bd.room_number ILIKE '%' || COALESCE(room_number_in, '') || '%'
        AND bd.check_in_date::varchar ILIKE '%' || COALESCE(check_in_date_in, '') || '%'
        AND bd.check_out_date::varchar ILIKE '%' || COALESCE(check_out_date_in, '') || '%'
        AND bd.status ILIKE '%' || COALESCE(status_in, '') || '%'
        AND bd.total_cost::varchar ILIKE '%' || COALESCE(total_cost_in, '') || '%'
ORDER BY booking_number;
$function$
;

-- DROP FUNCTION public.get_combined_income_sources();

CREATE OR REPLACE FUNCTION public.get_combined_income_sources()
 RETURNS TABLE(income_source character varying, source_type character varying, amount numeric)
 LANGUAGE plpgsql
AS $function$
BEGIN
RETURN QUERY
-- Базовая стоимость номеров по типам
SELECT
    'Номер ' || hr.room_number || ' (' || tor.room_type_name || ')' as income_source,
    'Базовая стоимость' as source_type,
    SUM(hr.price_per_person * (th.occupied_space * th.can_be_split::int + hr.max_people * (1 - th.can_be_split::int)) * th.amount_of_nights) as income
FROM tenants_history th
         INNER JOIN hotel_rooms hr ON th.room_id = hr.room_id
         INNER JOIN types_of_room tor ON hr.type_of_room_id = tor.room_type_id
WHERE th.check_in_status = 'занят'
GROUP BY hr.room_number, tor.room_type_name

UNION ALL

-- Доход от удобств
SELECT
    cd.conv_name || ' (комната ' || hr.room_number || ')' as income_source,
    'Удобства' as source_type,
    SUM(rc.price_per_one * rc.amount * th.amount_of_nights) as income
FROM tenants_history th
         INNER JOIN room_conveniences rc ON th.room_id = rc.room_id
    AND th.check_in_date BETWEEN rc.start_date AND rc.end_date
         INNER JOIN hotel_rooms hr ON th.room_id = hr.room_id
         INNER JOIN conveniences_dict cd ON rc.conv_name_id = cd.conv_name_id
WHERE th.check_in_status = 'занят'
GROUP BY cd.conv_name, hr.room_number

UNION ALL

-- Доход от услуг
SELECT
    sd.service_name as income_source,
    'Доп. услуги' as source_type,
    SUM(sh.amount * hs.price_per_one) as income
FROM services_history sh
         INNER JOIN hotel_services hs ON sh.service_id = hs.service_id
         INNER JOIN services_dict sd ON hs.service_name_id = sd.service_name_id
         INNER JOIN tenants_history th ON sh.history_id = th.booking_number
WHERE th.check_in_status = 'занят'
  AND sh.order_date BETWEEN hs.start_of_period AND hs.end_of_period
GROUP BY sd.service_name

ORDER BY income DESC;
END;
$function$
;

-- DROP FUNCTION public.get_current_hotel_id();

CREATE OR REPLACE FUNCTION public.get_current_hotel_id()
 RETURNS integer
 LANGUAGE sql
 SECURITY DEFINER
AS $function$
SELECT hotel_id FROM users where username = SESSION_USER;
$function$
;

-- DROP FUNCTION public.get_current_room_statuses_view(int4);

CREATE OR REPLACE FUNCTION public.get_current_room_statuses_view(hotel_id_in integer)
 RETURNS TABLE(room_id integer, room_number integer, room_type_name character varying, max_people integer, price_per_person numeric, status character varying, available_space integer)
 LANGUAGE sql
AS $function$
SELECT room_id, room_number, room_type_name, max_people, price_per_person, status, available_space FROM public.v_current_room_statuses WHERE hotel_id = hotel_id_in;
$function$
;

-- DROP FUNCTION public.get_current_room_statuses_view_filtered(int4, varchar, varchar, varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION public.get_current_room_statuses_view_filtered(hotel_id_in integer, room_number_in character varying DEFAULT NULL::character varying, room_type_name_in character varying DEFAULT NULL::character varying, max_people_in character varying DEFAULT NULL::character varying, price_per_person_in character varying DEFAULT NULL::character varying, status_in character varying DEFAULT NULL::character varying, available_space_in character varying DEFAULT NULL::character varying)
 RETURNS TABLE(room_id integer, room_number integer, room_type_name character varying, max_people integer, price_per_person numeric, status character varying, available_space integer)
 LANGUAGE sql
AS $function$
SELECT * FROM public.get_current_room_statuses_view(hotel_id_in) rs
WHERE rs.room_number::varchar ILIKE '%' || COALESCE(room_number_in, '') || '%'
        AND rs.room_type_name ILIKE '%' || COALESCE(room_type_name_in, '') || '%'
        AND rs.max_people::varchar ILIKE '%' || COALESCE(max_people_in, '') || '%'
        AND rs.price_per_person::varchar ILIKE '%' || COALESCE(price_per_person_in, '') || '%'
        AND rs.status ILIKE '%' || COALESCE(status_in, '') || '%'
        AND rs.available_space::varchar ILIKE '%' || COALESCE(available_space_in, '') || '%'
ORDER BY room_id;
$function$
;

-- DROP FUNCTION public.get_customer_segmentation();

CREATE OR REPLACE FUNCTION public.get_customer_segmentation()
 RETURNS TABLE(customer_segment character varying, customer_count bigint, avg_base_spent numeric, avg_conveniences_spent numeric, avg_services_spent numeric, avg_total_spent numeric)
 LANGUAGE plpgsql
AS $function$
BEGIN
RETURN QUERY
    WITH customer_spending AS (
        SELECT
            t.tenant_id,
            SUM(hr.price_per_person * (th.occupied_space * th.can_be_split::int + hr.max_people * (1 - th.can_be_split::int)) * th.amount_of_nights) as base_spent,
            (
                SELECT COALESCE(SUM(rc.price_per_one * rc.amount * th_inner.amount_of_nights), 0)
                FROM tenants_history th_inner
                JOIN room_conveniences rc ON th_inner.room_id = rc.room_id
                    AND th_inner.check_in_date BETWEEN rc.start_date AND rc.end_date
                WHERE th_inner.tenant_id = t.tenant_id AND th_inner.check_in_status = 'занят'
            ) as conveniences_spent,
            (
                SELECT COALESCE(SUM(sh.amount * hs.price_per_one), 0)
                FROM services_history sh
                JOIN hotel_services hs ON sh.service_id = hs.service_id
                JOIN tenants_history th_inner ON sh.history_id = th_inner.booking_number
                WHERE th_inner.tenant_id = t.tenant_id AND th_inner.check_in_status = 'занят'
                  AND sh.order_date BETWEEN hs.start_of_period AND hs.end_of_period
            ) as services_spent
        FROM tenants t
        JOIN tenants_history th ON t.tenant_id = th.tenant_id
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        WHERE th.check_in_status = 'занят'
        GROUP BY t.tenant_id
    ),
    customer_total_spending AS (
        SELECT
            *,
            (base_spent + conveniences_spent + services_spent) as total_spent
        FROM customer_spending
    )
SELECT
    CASE
        WHEN total_spent > 50000 THEN 'VIP'
        WHEN total_spent > 20000 THEN 'Постоянный'
        WHEN total_spent > 5000 THEN 'Стандартный'
        ELSE 'Эконом'
        END as customer_segment,
    COUNT(*) as customer_count,
    AVG(base_spent) as avg_base_spent,
    AVG(conveniences_spent) as avg_conveniences_spent,
    AVG(services_spent) as avg_services_spent,
    AVG(total_spent) as avg_total_spent
FROM customer_total_spending
GROUP BY customer_segment
ORDER BY avg_total_spent DESC;
END;
$function$
;

-- DROP FUNCTION public.get_daily_invoices_by_hotel(int4);

CREATE OR REPLACE FUNCTION public.get_daily_invoices_by_hotel(hotel_id_in integer)
 RETURNS TABLE(invoice_number character varying, booking_number character varying, total_amount numeric, issue_date date, is_paid boolean)
 LANGUAGE sql
AS $function$
SELECT v.invoice_number, v.booking_number, v.total_amount,
       v.issue_date, v.is_paid
FROM public.v_daily_invoices v
WHERE v.hotel_id = hotel_id_in
ORDER BY v.issue_date DESC, v.booking_number DESC;
$function$
;

-- DROP FUNCTION public.get_daily_invoices_by_hotel_filtered(int4, varchar, varchar, varchar, varchar, bool);

CREATE OR REPLACE FUNCTION public.get_daily_invoices_by_hotel_filtered(hotel_id_in integer, invoice_number_in character varying DEFAULT NULL::character varying, booking_number_in character varying DEFAULT NULL::character varying, total_amount_in character varying DEFAULT NULL::character varying, issue_date_in character varying DEFAULT NULL::character varying, is_paid_in boolean DEFAULT NULL::boolean)
 RETURNS TABLE(invoice_number character varying, booking_number character varying, total_amount numeric, issue_date date, is_paid boolean)
 LANGUAGE sql
AS $function$
SELECT * FROM public.get_daily_invoices_by_hotel(hotel_id_in) di
WHERE di.invoice_number ILIKE '%' || COALESCE(invoice_number_in, '') || '%'
        AND di.booking_number ILIKE '%' || COALESCE(booking_number_in, '') || '%'
        AND di.total_amount::varchar ILIKE '%' || COALESCE(total_amount_in, '') || '%'
        AND di.issue_date::varchar ILIKE '%' || COALESCE(issue_date_in, '') || '%'
        AND (is_paid_in IS NULL OR di.is_paid = is_paid_in)
ORDER BY booking_number;
$function$
;

-- DROP FUNCTION public.get_detailed_income_report();

CREATE OR REPLACE FUNCTION public.get_detailed_income_report()
 RETURNS TABLE(hotel_address character varying, room_number integer, base_room_income numeric, conveniences_income numeric, services_income numeric, total_income numeric)
 LANGUAGE plpgsql
AS $function$
BEGIN
RETURN QUERY
    WITH room_base_income AS (
        SELECT
            hr.room_id,
            hr.room_number,
            h.hotel_address,
            SUM(hr.price_per_person *
                (th.occupied_space * th.can_be_split::int +
                 hr.max_people * (1 - th.can_be_split::int)) * th.amount_of_nights) as base_rev
        FROM tenants_history th
        INNER JOIN hotel_rooms hr ON th.room_id = hr.room_id
        INNER JOIN hotel h ON hr.hotel_id = h.hotel_id
        WHERE th.check_in_status = 'занят'
        GROUP BY hr.room_id, hr.room_number, h.hotel_address
    ),
    room_conveniences_income AS (
        SELECT
            th.room_id,
            SUM(rc.price_per_one * rc.amount * th.amount_of_nights) as conveniences_rev
        FROM tenants_history th
        INNER JOIN room_conveniences rc ON th.room_id = rc.room_id
            AND th.check_in_date BETWEEN rc.start_date AND rc.end_date
        WHERE th.check_in_status = 'занят'
        GROUP BY th.room_id
    ),
    services_income AS (
        SELECT
            th.room_id,
            SUM(sh.amount * hs.price_per_one) as services_rev
        FROM services_history sh
        INNER JOIN hotel_services hs ON sh.service_id = hs.service_id
        INNER JOIN tenants_history th ON sh.history_id = th.booking_number
        WHERE th.check_in_status = 'занят'
          AND sh.order_date BETWEEN hs.start_of_period AND hs.end_of_period
        GROUP BY th.room_id
    )
SELECT
    rbr.hotel_address,
    rbr.room_number,
    rbr.base_rev as base_room_income,
    COALESCE(rcr.conveniences_rev, 0) as conveniences_income,
    COALESCE(sr.services_rev, 0) as services_income,
    rbr.base_rev + COALESCE(rcr.conveniences_rev, 0) + COALESCE(sr.services_rev, 0) as total_income
FROM room_base_income rbr
         LEFT JOIN room_conveniences_income rcr ON rbr.room_id = rcr.room_id
         LEFT JOIN services_income sr ON rbr.room_id = sr.room_id
ORDER BY total_income DESC;
END;
$function$
;

-- DROP FUNCTION public.get_high_income_rooms(numeric);

CREATE OR REPLACE FUNCTION public.get_high_income_rooms(total_income_threshold numeric)
 RETURNS TABLE(room_number integer, room_type character varying, base_income numeric, conveniences_income numeric, services_income numeric, total_income numeric, occupancy_rate numeric)
 LANGUAGE plpgsql
AS $function$
BEGIN
RETURN QUERY
    WITH date_range AS (
        SELECT
            (SELECT MIN(check_in_date) FROM tenants_history) as min_date,
            (SELECT MAX(check_in_date) FROM tenants_history) as max_date
    ),
    total_days AS (
        SELECT GREATEST((max_date - min_date) + 1, 1) as days
        FROM date_range
    ),
    room_stats AS (
        SELECT
            hr.room_id,
            hr.room_number,
            tor.room_type_name,
            SUM(hr.price_per_person * (th.occupied_space * th.can_be_split::int + hr.max_people * (1 - th.can_be_split::int)) * th.amount_of_nights) as base_income,
            COUNT(th.booking_number) as booking_count
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        JOIN types_of_room tor ON hr.type_of_room_id = tor.room_type_id
        WHERE th.check_in_status = 'занят'
        GROUP BY hr.room_id, hr.room_number, tor.room_type_name
    ),
    conveniences_stats AS (
        SELECT
            hr.room_id,
            SUM(rc.price_per_one * rc.amount * th.amount_of_nights) as conveniences_income
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        JOIN room_conveniences rc ON th.room_id = rc.room_id
            AND th.check_in_date BETWEEN rc.start_date AND rc.end_date
        WHERE th.check_in_status = 'занят'
        GROUP BY hr.room_id
    ),
    services_stats AS (
        SELECT
            hr.room_id,
            SUM(sh.amount * hs.price_per_one) as services_income
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        JOIN services_history sh ON th.booking_number = sh.history_id
        JOIN hotel_services hs ON sh.service_id = hs.service_id
        WHERE th.check_in_status = 'занят'
          AND sh.order_date BETWEEN hs.start_of_period AND hs.end_of_period
        GROUP BY hr.room_id
    )
SELECT
    rs.room_number,
    rs.room_type_name,
    rs.base_income,
    COALESCE(cs.conveniences_income, 0) as conveniences_income,
    COALESCE(ss.services_income, 0) as services_income,
    rs.base_income + COALESCE(cs.conveniences_income, 0) + COALESCE(ss.services_income, 0) as total_income,
    (rs.booking_count * 100.0 / (SELECT days FROM total_days)) as occupancy_rate
FROM room_stats rs
         LEFT JOIN conveniences_stats cs ON rs.room_id = cs.room_id
         LEFT JOIN services_stats ss ON rs.room_id = ss.room_id
WHERE (rs.base_income + COALESCE(cs.conveniences_income, 0) + COALESCE(ss.services_income, 0)) > total_income_threshold
ORDER BY total_income DESC;
END;
$function$
;

-- DROP FUNCTION public.get_hotel_id_by_room_id(int4);

CREATE OR REPLACE FUNCTION public.get_hotel_id_by_room_id(room_id_in integer)
 RETURNS integer
 LANGUAGE sql
 SECURITY DEFINER
AS $function$
SELECT hotel_id FROM hotel_rooms WHERE room_id = room_id_in;
$function$
;

-- DROP FUNCTION public.get_hotel_id_by_service_id(int4);

CREATE OR REPLACE FUNCTION public.get_hotel_id_by_service_id(service_id_in integer)
 RETURNS integer
 LANGUAGE sql
 SECURITY DEFINER
AS $function$
SELECT hotel_id FROM hotel_services WHERE service_id = service_id_in;
$function$
;

-- DROP FUNCTION public.get_hotel_id_by_tenant_id(int4);

CREATE OR REPLACE FUNCTION public.get_hotel_id_by_tenant_id(tenant_id_in integer)
 RETURNS integer
 LANGUAGE sql
 SECURITY DEFINER
AS $function$
SELECT hr.hotel_id FROM tenants_history th
                            JOIN hotel_rooms hr ON th.room_id = hr.room_id
WHERE th.tenant_id = tenant_id_in;
$function$
;

-- DROP FUNCTION public.get_hotel_income_summary();

CREATE OR REPLACE FUNCTION public.get_hotel_income_summary()
 RETURNS TABLE(hotel_address character varying, base_room_income numeric, conveniences_income numeric, services_income numeric, total_income numeric, total_bookings bigint)
 LANGUAGE plpgsql
AS $function$
BEGIN
RETURN QUERY
    WITH room_base_income AS (
        SELECT
            hr.hotel_id,
            SUM(hr.price_per_person * (th.occupied_space * th.can_be_split::int + hr.max_people * (1 - th.can_be_split::int)) * th.amount_of_nights) as income
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        WHERE th.check_in_status = 'занят'
        GROUP BY hr.hotel_id
    ),
    conveniences_income AS (
        SELECT
            hr.hotel_id,
            SUM(rc.price_per_one * rc.amount * th.amount_of_nights) as income
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        JOIN room_conveniences rc ON th.room_id = rc.room_id
            AND th.check_in_date BETWEEN rc.start_date AND rc.end_date
        WHERE th.check_in_status = 'занят'
        GROUP BY hr.hotel_id
    ),
    services_income AS (
        SELECT
            hr.hotel_id,
            SUM(sh.amount * hs.price_per_one) as income
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        JOIN services_history sh ON th.booking_number = sh.history_id
        JOIN hotel_services hs ON sh.service_id = hs.service_id
        WHERE th.check_in_status = 'занят'
          AND sh.order_date BETWEEN hs.start_of_period AND hs.end_of_period
        GROUP BY hr.hotel_id
    ),
    booking_counts AS (
        SELECT hr.hotel_id, COUNT(th.booking_number) as bookings
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        WHERE th.check_in_status = 'занят'
        GROUP BY hr.hotel_id
    )
SELECT
    h.hotel_address,
    COALESCE(rbi.income, 0) as base_room_income,
    COALESCE(ci.income, 0) as conveniences_income,
    COALESCE(si.income, 0) as services_income,
    COALESCE(rbi.income, 0) + COALESCE(ci.income, 0) + COALESCE(si.income, 0) as total_income,
    COALESCE(bc.bookings, 0) as total_bookings
FROM hotel h
         LEFT JOIN room_base_income rbi ON h.hotel_id = rbi.hotel_id
         LEFT JOIN conveniences_income ci ON h.hotel_id = ci.hotel_id
         LEFT JOIN services_income si ON h.hotel_id = si.hotel_id
         LEFT JOIN booking_counts bc ON h.hotel_id = bc.hotel_id
ORDER BY total_income DESC;
END;
$function$
;

-- DROP FUNCTION public.get_hotel_kpi();

CREATE OR REPLACE FUNCTION public.get_hotel_kpi()
 RETURNS TABLE(metric_name character varying, metric_value numeric, metric_unit character varying)
 LANGUAGE plpgsql
AS $function$
BEGIN
RETURN QUERY
    WITH date_filter AS (
        SELECT booking_number, room_id
        FROM tenants_history
        WHERE check_in_status = 'занят'
          AND check_in_date >= CURRENT_DATE - INTERVAL '30 days'
    ),
    income_stats AS (
        SELECT
            (SELECT SUM(hr.price_per_person * (th.occupied_space * th.can_be_split::int + hr.max_people * (1 - th.can_be_split::int)) * th.amount_of_nights)
             FROM tenants_history th JOIN hotel_rooms hr ON th.room_id = hr.room_id WHERE th.booking_number IN (SELECT booking_number FROM date_filter)) as total_base_income,

            (SELECT SUM(rc.price_per_one * rc.amount * th.amount_of_nights)
             FROM tenants_history th JOIN room_conveniences rc ON th.room_id = rc.room_id
                 AND th.check_in_date BETWEEN rc.start_date AND rc.end_date
                 WHERE th.booking_number IN (SELECT booking_number FROM date_filter)) as total_conveniences_income,

            (SELECT SUM(sh.amount * hs.price_per_one)
             FROM services_history sh JOIN hotel_services hs ON sh.service_id = hs.service_id
                 WHERE sh.history_id IN (SELECT booking_number FROM date_filter)
                 AND sh.order_date BETWEEN hs.start_of_period AND hs.end_of_period) as total_services_income,

            (SELECT COUNT(DISTINCT room_id) FROM date_filter) as occupied_rooms_count
    )
SELECT
    'Средний доход на номер' as metric_name,
    (rs.total_base_income + rs.total_conveniences_income + rs.total_services_income) /
    NULLIF(rs.occupied_rooms_count, 0) as metric_value,
    'руб' as metric_unit
FROM income_stats rs
WHERE rs.occupied_rooms_count > 0

UNION ALL

SELECT
    'Доля удобств в выручке' as metric_name,
    (rs.total_conveniences_income * 100.0 / NULLIF(rs.total_base_income + rs.total_conveniences_income + rs.total_services_income, 0)) as metric_value,
    '%' as metric_unit
FROM income_stats rs
WHERE (rs.total_base_income + rs.total_conveniences_income + rs.total_services_income) > 0

UNION ALL

SELECT
    'Доля услуг в выручке' as metric_name,
    (rs.total_services_income * 100.0 / NULLIF(rs.total_base_income + rs.total_conveniences_income + rs.total_services_income, 0)) as metric_value,
    '%' as metric_unit
FROM income_stats rs
WHERE (rs.total_base_income + rs.total_conveniences_income + rs.total_services_income) > 0;
END;
$function$
;

-- DROP FUNCTION public.get_hotel_services_by_hotel(int4);

CREATE OR REPLACE FUNCTION public.get_hotel_services_by_hotel(hotel_id_in integer)
 RETURNS TABLE(service_id integer, service_name_id integer, start_of_period date, end_of_period date, price_per_one numeric, can_be_booked boolean)
 LANGUAGE sql
AS $function$
SELECT hs.service_id, hs.service_name_id, hs.start_of_period, hs.end_of_period,
       hs.price_per_one, hs.can_be_booked
FROM public.hotel_services hs
WHERE hs.hotel_id = hotel_id_in;
$function$
;

-- DROP FUNCTION public.get_hotel_services_by_hotel_filtered(int4, varchar, varchar, varchar, varchar, bool);

CREATE OR REPLACE FUNCTION public.get_hotel_services_by_hotel_filtered(hotel_id_in integer, service_name_in character varying DEFAULT NULL::character varying, start_of_period_in character varying DEFAULT NULL::character varying, end_of_period_in character varying DEFAULT NULL::character varying, price_per_one_in character varying DEFAULT NULL::character varying, can_be_booked_in boolean DEFAULT NULL::boolean)
 RETURNS TABLE(service_id integer, service_name_id integer, start_of_period date, end_of_period date, price_per_one numeric, can_be_booked boolean)
 LANGUAGE sql
AS $function$
SELECT
    hs.service_id,
    hs.service_name_id,
    hs.start_of_period,
    hs.end_of_period,
    hs.price_per_one,
    hs.can_be_booked
FROM public.hotel_services hs
         JOIN public.services_dict sd ON hs.service_name_id = sd.service_name_id
WHERE hs.hotel_id = hotel_id_in
  AND sd.service_name ILIKE '%' || COALESCE(service_name_in, '') || '%'
        AND hs.start_of_period::varchar ILIKE '%' || COALESCE(start_of_period_in, '') || '%'
        AND hs.end_of_period::varchar ILIKE '%' || COALESCE(end_of_period_in, '') || '%'
        AND hs.price_per_one::varchar ILIKE '%' || COALESCE(price_per_one_in, '') || '%'
        AND (can_be_booked_in IS NULL OR hs.can_be_booked = can_be_booked_in)
ORDER BY hs.service_id;
$function$
;

-- DROP FUNCTION public.get_income_breakdown_by_category();

CREATE OR REPLACE FUNCTION public.get_income_breakdown_by_category()
 RETURNS TABLE(category_type character varying, subcategory character varying, income numeric, percentage numeric)
 LANGUAGE plpgsql
AS $function$
DECLARE
total_income_value numeric;
BEGIN
    -- Подсчет общего дохода
SELECT COALESCE(SUM(total), 0) INTO total_income_value
FROM (
         -- Доход от номеров
         SELECT SUM(hr.price_per_person * (th.occupied_space * th.can_be_split::int + hr.max_people * (1 - th.can_be_split::int)) * th.amount_of_nights) as total
         FROM tenants_history th
                  JOIN hotel_rooms hr ON th.room_id = hr.room_id
         WHERE th.check_in_status = 'занят'

         UNION ALL

         -- Доход от удобств
         SELECT SUM(rc.price_per_one * rc.amount * th.amount_of_nights) as total
         FROM tenants_history th
                  JOIN room_conveniences rc ON th.room_id = rc.room_id
             AND th.check_in_date BETWEEN rc.start_date AND rc.end_date
         WHERE th.check_in_status = 'занят'

         UNION ALL

         -- Доход от услуг
         SELECT SUM(sh.amount * hs.price_per_one) as total
         FROM services_history sh
                  JOIN hotel_services hs ON sh.service_id = hs.service_id
                  JOIN tenants_history th ON sh.history_id = th.booking_number
         WHERE th.check_in_status = 'занят'
           AND sh.order_date BETWEEN hs.start_of_period AND hs.end_of_period
     ) all_income;

RETURN QUERY
    WITH income_breakdown AS (
        -- По типам номеров
        SELECT
            'По типам номеров'::varchar as category_type,
            COALESCE(tor.room_type_name::varchar, 'Всего') as subcategory,
            SUM(hr.price_per_person * (th.occupied_space * th.can_be_split::int + hr.max_people * (1 - th.can_be_split::int)) * th.amount_of_nights) as income
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        JOIN types_of_room tor ON hr.type_of_room_id = tor.room_type_id
        WHERE th.check_in_status = 'занят'
        GROUP BY ROLLUP(tor.room_type_name)

        UNION ALL

        -- По видам удобств
        SELECT
            'По видам удобств'::varchar as category_type,
            COALESCE(cd.conv_name::varchar, 'Всего') as subcategory,
            SUM(rc.price_per_one * rc.amount * th.amount_of_nights) as income
        FROM tenants_history th
        JOIN room_conveniences rc ON th.room_id = rc.room_id
            AND th.check_in_date BETWEEN rc.start_date AND rc.end_date
        JOIN conveniences_dict cd ON rc.conv_name_id = cd.conv_name_id
        WHERE th.check_in_status = 'занят'
        GROUP BY ROLLUP(cd.conv_name)

        UNION ALL

        -- По видам услуг
        SELECT
            'По видам услуг'::varchar as category_type,
            COALESCE(sd.service_name::varchar, 'Всего') as subcategory,
            SUM(sh.amount * hs.price_per_one) as income
        FROM services_history sh
        JOIN hotel_services hs ON sh.service_id = hs.service_id
        JOIN services_dict sd ON hs.service_name_id = sd.service_name_id
        JOIN tenants_history th ON sh.history_id = th.booking_number
        WHERE th.check_in_status = 'занят'
          AND sh.order_date BETWEEN hs.start_of_period AND hs.end_of_period
        GROUP BY ROLLUP(sd.service_name)
    )
SELECT
    ib.category_type,
    ib.subcategory,
    ib.income,
    CASE
        WHEN total_income_value > 0 THEN (ib.income / total_income_value * 100)
        ELSE 0
        END as percentage
FROM income_breakdown ib
WHERE ib.income > 0
ORDER BY ib.category_type, ib.income DESC;
END;
$function$
;

-- DROP FUNCTION public.get_income_by_city_pattern(varchar);

CREATE OR REPLACE FUNCTION public.get_income_by_city_pattern(city_pattern character varying)
 RETURNS TABLE(city_name character varying, base_room_income numeric, conveniences_income numeric, services_income numeric, total_income numeric, average_stay_duration numeric)
 LANGUAGE plpgsql
AS $function$
BEGIN
RETURN QUERY
    WITH all_bookings AS (
        SELECT th.booking_number, h.hotel_city, th.amount_of_nights
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        JOIN hotel h ON hr.hotel_id = h.hotel_id
        JOIN cities c ON h.hotel_city = c.city_id
        WHERE th.check_in_status = 'занят' AND c.city_name ILIKE '%' || city_pattern || '%'
    ),
    room_income AS (
        SELECT
            h.hotel_city,
            SUM(hr.price_per_person * (th.occupied_space * th.can_be_split::int + hr.max_people * (1 - th.can_be_split::int)) * th.amount_of_nights) as income
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        JOIN hotel h ON hr.hotel_id = h.hotel_id
        WHERE th.booking_number IN (SELECT booking_number FROM all_bookings)
        GROUP BY h.hotel_city
    ),
    conveniences_income AS (
        SELECT
            h.hotel_city,
            SUM(rc.price_per_one * rc.amount * th.amount_of_nights) as income
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        JOIN hotel h ON hr.hotel_id = h.hotel_id
        JOIN room_conveniences rc ON th.room_id = rc.room_id
            AND th.check_in_date BETWEEN rc.start_date AND rc.end_date
        WHERE th.booking_number IN (SELECT booking_number FROM all_bookings)
        GROUP BY h.hotel_city
    ),
    services_income AS (
        SELECT
            h.hotel_city,
            SUM(sh.amount * hs.price_per_one) as income
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        JOIN hotel h ON hr.hotel_id = h.hotel_id
        JOIN services_history sh ON th.booking_number = sh.history_id
        JOIN hotel_services hs ON sh.service_id = hs.service_id
        WHERE th.booking_number IN (SELECT booking_number FROM all_bookings)
          AND sh.order_date BETWEEN hs.start_of_period AND hs.end_of_period
        GROUP BY h.hotel_city
    )
SELECT
    c.city_name,
    COALESCE(ri.income, 0) as base_room_income,
    COALESCE(ci.income, 0) as conveniences_income,
    COALESCE(si.income, 0) as services_income,
    COALESCE(ri.income, 0) + COALESCE(ci.income, 0) + COALESCE(si.income, 0) as total_income,
    AVG(ab.amount_of_nights) as average_stay_duration
FROM cities c
         JOIN all_bookings ab ON c.city_id = ab.hotel_city
         LEFT JOIN room_income ri ON c.city_id = ri.hotel_city
         LEFT JOIN conveniences_income ci ON c.city_id = ci.hotel_city
         LEFT JOIN services_income si ON c.city_id = si.hotel_city
GROUP BY c.city_name, ri.income, ci.income, si.income
ORDER BY total_income DESC;
END;
$function$
;

-- DROP FUNCTION public.get_invoice_details(varchar);

CREATE OR REPLACE FUNCTION public.get_invoice_details(booking_number_in character varying)
 RETURNS TABLE(item_description text, quantity integer, unit_price numeric, total_price numeric)
 LANGUAGE plpgsql
AS $function$
DECLARE
v_room_id INT;
    v_check_in_date DATE;
    v_amount_of_nights INT;
    v_occupied_space INT;
    v_can_be_split BOOLEAN;
    v_max_people INT;
    effective_people_count INT;
    base_price_per_person NUMERIC;
    room_number_text TEXT;
BEGIN
    -- Get booking details
SELECT
    th.room_id, th.check_in_date, th.amount_of_nights, th.occupied_space, th.can_be_split
INTO
    v_room_id, v_check_in_date, v_amount_of_nights, v_occupied_space, v_can_be_split
FROM public.tenants_history th
WHERE th.booking_number = booking_number_in;

-- Get room details
SELECT
    hr.price_per_person, hr.max_people, hr.room_number
INTO
    base_price_per_person, v_max_people, room_number_text
FROM public.hotel_rooms hr
WHERE hr.room_id = v_room_id;

-- Determine effective people count for payment
IF v_can_be_split THEN
        effective_people_count := v_occupied_space;
ELSE
        effective_people_count := v_max_people;
END IF;

    -- Row for the cost of stay (room + conveniences)
RETURN QUERY
SELECT
    ('Проживание в номере ' || room_number_text)::text AS item_description,
    v_amount_of_nights AS quantity,
    (base_price_per_person * effective_people_count + (
        SELECT COALESCE(SUM(rc.price_per_one * rc.amount), 0)
        FROM public.room_conveniences rc
        WHERE rc.room_id = v_room_id AND rc.start_date <= v_check_in_date
    ))::numeric AS unit_price,
    ( (base_price_per_person * effective_people_count + (
        SELECT COALESCE(SUM(rc.price_per_one * rc.amount), 0)
        FROM public.room_conveniences rc
        WHERE rc.room_id = v_room_id AND rc.start_date <= v_check_in_date
    )) * v_amount_of_nights
        )::numeric AS total_price;

-- Rows for additional services
RETURN QUERY
SELECT
    sd.service_name::text AS item_description,
    sh.amount AS quantity,
    hs.price_per_one AS unit_price,
    (sh.amount * hs.price_per_one)::numeric AS total_price
FROM public.services_history sh
         JOIN public.hotel_services hs ON sh.service_id = hs.service_id
         JOIN public.services_dict sd ON hs.service_name_id = sd.service_name_id
WHERE sh.history_id = booking_number_in;

END;
$function$
;

-- DROP FUNCTION public.get_monthly_income_trend();

CREATE OR REPLACE FUNCTION public.get_monthly_income_trend()
 RETURNS TABLE(year_month character varying, base_room_income numeric, conveniences_income numeric, services_income numeric, total_income numeric, income_growth numeric)
 LANGUAGE plpgsql
AS $function$
BEGIN
RETURN QUERY
    WITH monthly_base_income AS (
        SELECT
            TO_CHAR(th.check_in_date, 'YYYY-MM') as year_month,
            SUM(hr.price_per_person * (th.occupied_space * th.can_be_split::int + hr.max_people * (1 - th.can_be_split::int)) * th.amount_of_nights) as income
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        WHERE th.check_in_status = 'занят'
        GROUP BY 1
    ),
    monthly_conveniences_income AS (
        SELECT
            TO_CHAR(th.check_in_date, 'YYYY-MM') as year_month,
            SUM(rc.price_per_one * rc.amount * th.amount_of_nights) as income
        FROM tenants_history th
        JOIN room_conveniences rc ON th.room_id = rc.room_id
            AND th.check_in_date BETWEEN rc.start_date AND rc.end_date
        WHERE th.check_in_status = 'занят'
        GROUP BY 1
    ),
    monthly_services_income AS (
        SELECT
            TO_CHAR(th.check_in_date, 'YYYY-MM') as year_month,
            SUM(sh.amount * hs.price_per_one) as income
        FROM tenants_history th
        JOIN services_history sh ON th.booking_number = sh.history_id
        JOIN hotel_services hs ON sh.service_id = hs.service_id
        WHERE th.check_in_status = 'занят'
          AND sh.order_date BETWEEN hs.start_of_period AND hs.end_of_period
        GROUP BY 1
    ),
    with_totals AS (
        SELECT
            year_month,
            base_room_income,
            conveniences_income,
            services_income,
            base_room_income + conveniences_income + services_income as total_income
        FROM (
            SELECT
                COALESCE(mbi.year_month, mci.year_month, msi.year_month) as year_month,
                COALESCE(mbi.income, 0) as base_room_income,
                COALESCE(mci.income, 0) as conveniences_income,
                COALESCE(msi.income, 0) as services_income
            FROM monthly_base_income mbi
            FULL JOIN monthly_conveniences_income mci ON mbi.year_month = mci.year_month
            FULL JOIN monthly_services_income msi ON COALESCE(mbi.year_month, mci.year_month) = msi.year_month
        ) combined
    )
SELECT
    wt.year_month,
    wt.base_room_income,
    wt.conveniences_income,
    wt.services_income,
    wt.total_income,
    (wt.total_income - LAG(wt.total_income, 1, 0) OVER (ORDER BY wt.year_month)) as income_growth
FROM with_totals wt
ORDER BY wt.year_month;
END;
$function$
;

-- DROP FUNCTION public.get_premium_services_usage(int4, numeric);

CREATE OR REPLACE FUNCTION public.get_premium_services_usage(min_usage_count integer, min_income numeric)
 RETURNS TABLE(service_name character varying, usage_count bigint, total_income numeric, avg_income_per_use numeric)
 LANGUAGE plpgsql
AS $function$
BEGIN
RETURN QUERY
SELECT
    sd.service_name,
    COUNT(*) as usage_count,
    SUM(sh.amount * hs.price_per_one) as total_income,
    AVG(sh.amount * hs.price_per_one) as avg_income_per_use
FROM services_history sh
         INNER JOIN hotel_services hs ON sh.service_id = hs.service_id
         INNER JOIN services_dict sd ON hs.service_name_id = sd.service_name_id
WHERE sh.order_date BETWEEN hs.start_of_period AND hs.end_of_period
GROUP BY sd.service_name, sd.service_name_id
HAVING COUNT(*) >= min_usage_count
   AND SUM(sh.amount * hs.price_per_one) >= min_income
ORDER BY total_income DESC;
END;
$function$
;

-- DROP FUNCTION public.get_room_conveniences_by_room(int4);

CREATE OR REPLACE FUNCTION public.get_room_conveniences_by_room(room_id_in integer)
 RETURNS TABLE(conv_id integer, room_id integer, conv_name_id integer, price_per_one numeric, amount integer, start_date date, conv_name character varying)
 LANGUAGE sql
AS $function$
SELECT rc.conv_id, rc.room_id, rc.conv_name_id, rc.price_per_one,
       rc.amount, rc.start_date, rc.conv_name
FROM public.v_all_conveniences_with_rooms rc
WHERE rc.room_id = room_id_in;
$function$
;

-- DROP FUNCTION public.get_room_conveniences_by_room_filtered(int4, varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION public.get_room_conveniences_by_room_filtered(room_id_in integer, conv_name_in character varying DEFAULT NULL::character varying, price_per_one_in character varying DEFAULT NULL::character varying, amount_in character varying DEFAULT NULL::character varying, start_date_in character varying DEFAULT NULL::character varying)
 RETURNS TABLE(conv_id integer, room_id integer, conv_name_id integer, price_per_one numeric, amount integer, start_date date, conv_name character varying)
 LANGUAGE sql
AS $function$
SELECT rc.conv_id, rc.room_id, rc.conv_name_id, rc.price_per_one,
       rc.amount, rc.start_date, rc.conv_name
FROM public.v_all_rooms_with_conveniences rc
WHERE rc.room_id = room_id_in
  AND rc.conv_name ILIKE '%' || COALESCE(conv_name_in, '') || '%'
        AND rc.price_per_one::varchar ILIKE '%' || COALESCE(price_per_one_in, '') || '%'
        AND rc.amount::varchar ILIKE '%' || COALESCE(amount_in, '') || '%'
        AND rc.start_date::varchar ILIKE '%' || COALESCE(start_date_in, '') || '%'
ORDER BY rc.conv_id;
$function$
;

-- DROP FUNCTION public.get_room_efficiency_analysis(numeric, numeric);

CREATE OR REPLACE FUNCTION public.get_room_efficiency_analysis(min_occupancy_rate numeric, min_total_income numeric)
 RETURNS TABLE(room_number integer, room_type character varying, base_income numeric, conveniences_income numeric, services_income numeric, total_income numeric, occupancy_rate numeric, efficiency_rating text)
 LANGUAGE plpgsql
AS $function$
BEGIN
RETURN QUERY
    WITH date_range AS (
        SELECT
            (SELECT MIN(check_in_date) FROM tenants_history) as min_date,
            (SELECT MAX(check_in_date) FROM tenants_history) as max_date
    ),
    total_days AS (
        SELECT GREATEST((max_date - min_date) + 1, 1) as days
        FROM date_range
    ),
    room_stats_raw AS (
        SELECT
            hr.room_id,
            hr.room_number,
            tor.room_type_name,
            SUM(hr.price_per_person * (th.occupied_space * th.can_be_split::int + hr.max_people * (1 - th.can_be_split::int)) * th.amount_of_nights) as base_income,
            (
                SELECT COALESCE(SUM(rc.price_per_one * rc.amount * th_inner.amount_of_nights), 0)
                FROM tenants_history th_inner
                JOIN room_conveniences rc ON th_inner.room_id = rc.room_id
                    AND th_inner.check_in_date BETWEEN rc.start_date AND rc.end_date
                WHERE th_inner.room_id = hr.room_id AND th_inner.check_in_status = 'занят'
            ) as conveniences_income,
            (
                SELECT COALESCE(SUM(sh.amount * hs.price_per_one), 0)
                FROM services_history sh
                JOIN hotel_services hs ON sh.service_id = hs.service_id
                JOIN tenants_history th_inner ON sh.history_id = th_inner.booking_number
                WHERE th_inner.room_id = hr.room_id AND th_inner.check_in_status = 'занят'
                  AND sh.order_date BETWEEN hs.start_of_period AND hs.end_of_period
            ) as services_income,
            (COUNT(th.booking_number) * 100.0 / (SELECT days FROM total_days)) as occupancy_rate
        FROM tenants_history th
        JOIN hotel_rooms hr ON th.room_id = hr.room_id
        JOIN types_of_room tor ON hr.type_of_room_id = tor.room_type_id
        WHERE th.check_in_status = 'занят'
        GROUP BY hr.room_id, tor.room_type_name
    ),
    room_stats_final AS (
        SELECT
            *,
            (base_income + conveniences_income + services_income) as total_income
        FROM room_stats_raw
    )
SELECT
    rsf.room_number,
    rsf.room_type_name,
    rsf.base_income,
    rsf.conveniences_income,
    rsf.services_income,
    rsf.total_income,
    rsf.occupancy_rate,
    CASE
        WHEN rsf.occupancy_rate > 80 AND rsf.total_income > 100000 THEN 'Высокая'
        WHEN rsf.occupancy_rate > 60 AND rsf.total_income > 50000 THEN 'Средняя'
        ELSE 'Низкая'
        END as efficiency_rating
FROM room_stats_final rsf
WHERE rsf.occupancy_rate > min_occupancy_rate AND rsf.total_income > min_total_income
ORDER BY total_income DESC;
END;
$function$
;

-- DROP FUNCTION public.get_room_info(int4);

CREATE OR REPLACE FUNCTION public.get_room_info(room_id_in integer)
 RETURNS character varying
 LANGUAGE sql
AS $function$
SELECT 'Комната №' || hr.room_number || '(' || tor.room_type_name || ')' FROM hotel_rooms hr
                                                                                  JOIN types_of_room tor ON tor.room_type_id = hr.type_of_room_id;
$function$
;

-- DROP FUNCTION public.get_rooms_by_hotel(int4);

CREATE OR REPLACE FUNCTION public.get_rooms_by_hotel(hotel_id_in integer)
 RETURNS TABLE(room_id integer, hotel_id integer, max_people integer, price_per_person numeric, room_number integer, type_of_room_id integer, hotel_info character varying, room_type_name character varying)
 LANGUAGE sql
AS $function$
SELECT
    hr.room_id,
    hr.hotel_id,
    hr.max_people,
    hr.price_per_person,
    hr.room_number,
    hr.type_of_room_id,
    hr.hotel_info,
    hr.room_type_name
FROM public.v_rooms_with_types_and_hotels hr
WHERE hr.hotel_id = hotel_id_in
ORDER BY hr.room_id;
$function$
;

-- DROP FUNCTION public.get_rooms_by_hotel_filtered(int4, varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION public.get_rooms_by_hotel_filtered(hotel_id_in integer, room_number_in character varying DEFAULT NULL::character varying, room_type_name_in character varying DEFAULT NULL::character varying, max_people_in character varying DEFAULT NULL::character varying, price_per_person_in character varying DEFAULT NULL::character varying)
 RETURNS TABLE(room_id integer, hotel_id integer, max_people integer, price_per_person numeric, room_number integer, type_of_room_id integer, hotel_info character varying, room_type_name character varying)
 LANGUAGE sql
AS $function$
SELECT
    hr.room_id,
    hr.hotel_id,
    hr.max_people,
    hr.price_per_person,
    hr.room_number,
    hr.type_of_room_id,
    hr.hotel_info,
    hr.room_type_name
FROM public.v_rooms_with_types_and_hotels hr
         LEFT JOIN types_of_room tor ON hr.type_of_room_id = tor.room_type_id
WHERE hr.hotel_id = hotel_id_in
  AND hr.room_number::varchar ILIKE '%' || COALESCE(room_number_in, '') || '%'
        AND tor.room_type_name ILIKE '%' || COALESCE(room_type_name_in, '') || '%'
        AND hr.max_people::varchar ILIKE '%' || COALESCE(max_people_in, '') || '%'
        AND hr.price_per_person::varchar ILIKE '%' || COALESCE(price_per_person_in, '') || '%'
ORDER BY hr.room_id;
$function$
;

-- DROP FUNCTION public.get_rooms_never_booked();

CREATE OR REPLACE FUNCTION public.get_rooms_never_booked()
 RETURNS TABLE(room_number integer, room_type character varying, max_people integer, price_per_person numeric)
 LANGUAGE plpgsql
AS $function$
BEGIN
RETURN QUERY
SELECT hr.room_number, tor.room_type_name, hr.max_people, hr.price_per_person
FROM hotel_rooms hr
         INNER JOIN types_of_room tor ON hr.type_of_room_id = tor.room_type_id
WHERE hr.room_id NOT IN (
    SELECT DISTINCT room_id
    FROM tenants_history
    WHERE check_in_status IN ('занят', 'забронирован')
);
END;
$function$
;

-- DROP FUNCTION public.get_rooms_statuses_on_period(int4, date, date, int4);

CREATE OR REPLACE FUNCTION public.get_rooms_statuses_on_period(hotel_id_in integer, check_in_date_in date, check_out_date_in date, number_of_people integer DEFAULT 1)
 RETURNS TABLE(room_id integer, room_number integer, room_type_name character varying, max_people integer, price_per_person numeric, status text, available_space bigint)
 LANGUAGE sql
AS $function$
    -- CTE для определения комнат, забронированных "целиком" в заданный период
    WITH unavailable_non_splittable AS (
        SELECT DISTINCT th.room_id
        FROM public.tenants_history th
        WHERE th.check_in_status IN ('занят', 'забронирован')
          AND th.can_be_split = false
          AND daterange(th.check_in_date, th.check_in_date + th.amount_of_nights) && daterange(check_in_date_in, check_out_date_in)
    ),
    -- CTE для нахождения "критических" дат для проверки загруженности (начало периода и все даты заезда в этот период)
    relevant_dates AS (
        SELECT
            hr.room_id,
            d.dt AS check_date
        FROM public.hotel_rooms hr
        CROSS JOIN LATERAL (
            SELECT check_in_date_in AS dt
            UNION
            SELECT th.check_in_date
            FROM public.tenants_history th
            WHERE th.room_id = hr.room_id
              AND th.check_in_status IN ('занят', 'забронирован')
              AND th.can_be_split = true
              AND daterange(th.check_in_date, th.check_in_date + th.amount_of_nights) && daterange(check_in_date_in, check_out_date_in)
              AND th.check_in_date > check_in_date_in
              AND th.check_in_date < check_out_date_in
        ) d
        WHERE hr.hotel_id = get_current_hotel_id()
    ),
    -- CTE для расчета занятых мест в каждой комнате на каждую "критическую" дату
    occupancy_on_dates AS (
        SELECT
            rd.room_id,
            rd.check_date,
            COALESCE(SUM(th.occupied_space), 0) AS occupied_on_date
        FROM relevant_dates rd
        LEFT JOIN public.tenants_history th ON th.room_id = rd.room_id
            AND th.check_in_status IN ('занят', 'забронирован')
            AND th.can_be_split = true
            AND daterange(th.check_in_date, th.check_in_date + th.amount_of_nights) @> rd.check_date
        GROUP BY rd.room_id, rd.check_date
    ),
    -- CTE для нахождения пиковой загруженности для каждой комнаты в течение всего периода
    peak_room_occupancy AS (
        SELECT
            room_id,
            MAX(occupied_on_date) AS max_occupied
        FROM occupancy_on_dates
        GROUP BY room_id
    )
    -- Финальный запрос
SELECT
    hr.room_id,
    hr.room_number,
    tor.room_type_name,
    hr.max_people,
    hr.price_per_person,
    CASE
        WHEN uns.room_id IS NOT NULL THEN 'занят'
        WHEN COALESCE(pro.max_occupied, 0) >= hr.max_people THEN 'занят'
        ELSE 'свободен для заселения'
        END AS status,
    GREATEST(0, hr.max_people - COALESCE(pro.max_occupied, 0)) AS available_space
FROM public.hotel_rooms hr
         JOIN public.types_of_room tor ON hr.type_of_room_id = tor.room_type_id
         LEFT JOIN peak_room_occupancy pro ON hr.room_id = pro.room_id
         LEFT JOIN unavailable_non_splittable uns ON hr.room_id = uns.room_id
WHERE
    hr.hotel_id = hotel_id_in AND GREATEST(0, hr.max_people - COALESCE(pro.max_occupied, 0)) >= number_of_people
ORDER BY hr.room_number;
$function$
;

-- DROP FUNCTION public.get_rooms_statuses_on_period_filtered(int4, date, date, int4, varchar, varchar, varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION public.get_rooms_statuses_on_period_filtered(hotel_id_in integer, check_in_date_in date, check_out_date_in date, number_of_people integer DEFAULT 1, room_number_in character varying DEFAULT NULL::character varying, room_type_name_in character varying DEFAULT NULL::character varying, max_people_in character varying DEFAULT NULL::character varying, price_per_person_in character varying DEFAULT NULL::character varying, status_in character varying DEFAULT NULL::character varying, available_space_in character varying DEFAULT NULL::character varying)
 RETURNS TABLE(room_id integer, room_number integer, room_type_name character varying, max_people integer, price_per_person numeric, status text, available_space bigint)
 LANGUAGE sql
AS $function$
SELECT * FROM public.get_rooms_statuses_on_period(hotel_id_in, check_in_date_in, check_out_date_in, number_of_people) rs
WHERE rs.room_number::varchar ILIKE '%' || COALESCE(room_number_in, '') || '%'
        AND rs.room_type_name ILIKE '%' || COALESCE(room_type_name_in, '') || '%'
        AND rs.max_people::varchar ILIKE '%' || COALESCE(max_people_in, '') || '%'
        AND rs.price_per_person::varchar ILIKE '%' || COALESCE(price_per_person_in, '') || '%'
        AND rs.status ILIKE '%' || COALESCE(status_in, '') || '%'
        AND rs.available_space::varchar ILIKE '%' || COALESCE(available_space_in, '') || '%'
ORDER BY room_id;
$function$
;

-- DROP FUNCTION public.get_service_history_by_booking(varchar);

CREATE OR REPLACE FUNCTION public.get_service_history_by_booking(history_id_in character varying)
 RETURNS TABLE(row_id integer, service_id integer, amount integer, service_name_id integer)
 LANGUAGE sql
AS $function$
SELECT sh.row_id, sh.service_id, sh.amount, hs.service_name_id
FROM public.services_history sh
         JOIN public.hotel_services hs ON sh.service_id = hs.service_id
WHERE history_id = history_id_in;
$function$
;

-- DROP FUNCTION public.get_service_history_by_booking_filtered(varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION public.get_service_history_by_booking_filtered(history_id_in character varying, service_name_in character varying DEFAULT NULL::character varying, amount_in character varying DEFAULT NULL::character varying)
 RETURNS TABLE(row_id integer, service_id integer, amount integer, service_name_id integer)
 LANGUAGE sql
AS $function$
SELECT
    sh.row_id,
    sh.service_id,
    sh.amount,
    hs.service_name_id
FROM public.services_history sh
         JOIN public.hotel_services hs ON sh.service_id = hs.service_id
         JOIN public.services_dict sd ON hs.service_name_id = sd.service_name_id
WHERE sh.history_id = history_id_in
  AND sd.service_name ILIKE '%' || COALESCE(service_name_in, '') || '%'
        AND sh.amount::varchar ILIKE '%' || COALESCE(amount_in, '') || '%'
ORDER BY row_id;
$function$
;

-- DROP FUNCTION public.get_tenant_history_by_hotel(int4);

CREATE OR REPLACE FUNCTION public.get_tenant_history_by_hotel(hotel_id_in integer)
 RETURNS TABLE(room_id integer, booking_date date, check_in_date date, check_in_status character varying, occupied_space integer, amount_of_nights integer, can_be_split boolean, tenant_id integer, booking_number character varying)
 LANGUAGE sql
AS $function$
SELECT th.room_id, th.booking_date, th.check_in_date, th.check_in_status, th.occupied_space, th.amount_of_nights, th.can_be_split, th.tenant_id, th.booking_number
FROM public.tenants_history th
         JOIN public.hotel_rooms hr ON th.room_id = hr.room_id
WHERE hr.hotel_id = hotel_id_in
ORDER BY booking_number;
$function$
;

-- DROP FUNCTION public.get_tenant_history_by_hotel_filtered(int4, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, bool);

CREATE OR REPLACE FUNCTION public.get_tenant_history_by_hotel_filtered(hotel_id_in integer, booking_number_in character varying, room_info character varying, tenant_info character varying, booking_date_in character varying, check_in_date_in character varying, check_in_status_in character varying, occupied_space_in character varying, amount_of_nights_in character varying, can_be_split_in boolean)
 RETURNS TABLE(room_id integer, booking_date date, check_in_date date, check_in_status character varying, occupied_space integer, amount_of_nights integer, can_be_split boolean, tenant_id integer, booking_number character varying)
 LANGUAGE sql
AS $function$
SELECT th.room_id, th.booking_date, th.check_in_date, th.check_in_status, th.occupied_space, th.amount_of_nights, th.can_be_split, th.tenant_id, th.booking_number
FROM public.get_tenant_history_by_hotel(hotel_id_in) th
         JOIN tenants t using(tenant_id)
WHERE th.booking_date::varchar ILIKE '%' || booking_date_in || '%' AND
	  th.check_in_date::varchar ILIKE '%' || check_in_date_in || '%' AND
	  th.check_in_status ILIKE '%' || check_in_status_in || '%' AND
	  th.occupied_space::varchar ILIKE '%' || occupied_space_in || '%' AND
	  th.amount_of_nights::varchar ILIKE '%' || amount_of_nights_in || '%' AND
	  th.can_be_split = can_be_split_in AND
	  th.booking_number ILIKE '%' || booking_number_in || '%' AND
	  public.get_tenant_info(th.tenant_id) ILIKE '%' || tenant_info || '%' AND
	  public.get_room_info(th.room_id) ILIKE '%' || room_info || '%'
ORDER BY booking_number;
$function$
;

-- DROP FUNCTION public.get_tenant_info(int4);

CREATE OR REPLACE FUNCTION public.get_tenant_info(tenant_id_in integer)
 RETURNS character varying
 LANGUAGE sql
AS $function$
SELECT t.first_name || ' ' || t."name" || ' ' || t.patronymic || ' ' || t.birth_date::varchar || ' ' || t.email FROM tenants t WHERE t.tenant_id = tenant_id_in
    $function$
;

-- DROP FUNCTION public.get_user_role();

CREATE OR REPLACE FUNCTION public.get_user_role()
 RETURNS character varying
 LANGUAGE sql
AS $function$
SELECT rolname
FROM pg_roles
WHERE pg_has_role(SESSION_USER, oid, 'member') AND rolname <> SESSION_USER;
$function$
;

-- DROP FUNCTION public.unban_user(varchar);

CREATE OR REPLACE FUNCTION public.unban_user(username_in character varying)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$
BEGIN
EXECUTE format('ALTER ROLE %I LOGIN', username_in);
END;
$function$
;