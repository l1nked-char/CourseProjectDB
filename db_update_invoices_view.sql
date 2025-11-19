-- Функция для расчета полной стоимости бронирования (проживание + удобства + доп. услуги)
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
    -- 1. Get booking details
    SELECT
        th.room_id, th.check_in_date, th.amount_of_nights, th.occupied_space, th.can_be_split
    INTO
        v_room_id, v_check_in_date, v_amount_of_nights, v_occupied_space, v_can_be_split
    FROM public.tenants_history th
    WHERE th.booking_number = booking_number_in;

    -- 2. Get room details
    SELECT
        hr.price_per_person, hr.max_people
    INTO
        base_price_per_person, v_max_people
    FROM public.hotel_rooms hr
    WHERE hr.room_id = v_room_id;

    -- 3. Determine effective people count for payment
    IF v_can_be_split THEN
        effective_people_count := v_occupied_space;
    ELSE
        effective_people_count := v_max_people;
    END IF;

    -- 4. Calculate daily cost of conveniences in the room
    SELECT COALESCE(SUM(rc.price_per_one * rc.amount), 0)
    INTO conveniences_cost_per_night
    FROM public.room_conveniences rc
    WHERE rc.room_id = v_room_id AND rc.start_date <= v_check_in_date;

    -- 5. Calculate total room cost for the stay
    -- The cost includes room price per person and conveniences
    room_cost := (base_price_per_person * effective_people_count + conveniences_cost_per_night) * v_amount_of_nights;

    -- 6. Calculate cost of additional services
    SELECT COALESCE(SUM(hs.price_per_one * sh.amount), 0)
    INTO services_cost
    FROM public.services_history sh
    JOIN public.hotel_services hs ON sh.service_id = hs.service_id
    WHERE sh.history_id = booking_number_in;

    -- 7. Sum everything up
    total_cost := room_cost + services_cost;

    RETURN total_cost;
END;
$function$;

-- View for generating invoices for guests checking out today
CREATE OR REPLACE VIEW public.v_daily_invoices AS
SELECT
    ('INV-' || to_char(CURRENT_DATE, 'YYYYMMDD') || '-' || th.booking_number) AS invoice_number,
    th.booking_number,
    public.calculate_total_cost_for_booking(th.booking_number) AS total_amount,
    CURRENT_DATE AS issue_date,
    false AS is_paid,
    hr.hotel_id
FROM
    public.tenants_history th
JOIN
    public.hotel_rooms hr ON th.room_id = hr.room_id
WHERE
    (th.check_in_date + th.amount_of_nights) = CURRENT_DATE
    AND th.check_in_status = 'занят';

-- Function to get invoices from the view by hotel, to be used by the application
CREATE OR REPLACE FUNCTION public.get_daily_invoices_by_hotel(hotel_id_in integer)
 RETURNS TABLE(invoice_number character varying, booking_number character varying, total_amount numeric, issue_date date, is_paid boolean)
 LANGUAGE sql
AS $function$
    SELECT
        v.invoice_number,
        v.booking_number,
        v.total_amount,
        v.issue_date,
        v.is_paid
    FROM public.v_daily_invoices v
    WHERE v.hotel_id = hotel_id_in
    ORDER BY v.issue_date DESC, v.booking_number DESC;
$function$;

-- Function to get detailed booking information, including the calculated total cost
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
$function$;

-- Function to get the detailed breakdown of an invoice (room, conveniences, services)
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
$function$;