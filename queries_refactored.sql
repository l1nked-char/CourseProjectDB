-- =================================================================================================
-- Файл: queries_refactored.sql
-- Описание: Оптимизированная и отрефакторенная версия файла queries.sql.
-- Основные изменения:
-- 1. Исправлены ошибки в расчетах совокупных доходов (GROUP BY) путем предварительной агрегации в CTE.
-- 2. Функции без параметров преобразованы в представления (VIEW) для улучшения производительности и читаемости.
-- 3. Улучшено форматирование и добавлены комментарии для сложных запросов.
-- 4. Удалены некорректные и дублирующиеся функции.
-- =================================================================================================

-- =================================================================================================
-- Раздел 1: Симметричное внутреннее соединение с условием (по внешнему ключу)
-- =================================================================================================

-- Запрос 1: Услуги конкретного отеля
-- Описание: Получение всех услуг для указанного отеля с фильтрацией по идентификатору отеля.
-- Тип: Функция, возвращающая таблицу.
--роль: администратор отеля, сотрудник отеля
CREATE OR REPLACE FUNCTION get_hotel_services_by_hotel(hotel_id_in integer)
RETURNS TABLE(
    service_id integer,
    service_name_id integer,
    start_of_period date,
    end_of_period date,
    price_per_one numeric,
    can_be_booked boolean
) AS $$
    SELECT hs.service_id, hs.service_name_id, hs.start_of_period, hs.end_of_period,
           hs.price_per_one, hs.can_be_booked
    FROM public.hotel_services hs
    WHERE hs.hotel_id = hotel_id_in;
$$ LANGUAGE sql;

-- Запрос 2: Удобства конкретного номера
-- Описание: Получение всех удобств для указанного номера с фильтрацией по room_id.
-- Тип: Функция, возвращающая таблицу.
--роль: администратор отеля, сотрудник отеля
CREATE OR REPLACE FUNCTION get_room_conveniences_by_room(room_id_in integer)
RETURNS TABLE(
    conv_id integer,
    room_id integer,
    conv_name_id integer,
    price_per_one numeric,
    amount integer,
    start_date date,
    conv_name character varying
) AS $$
    SELECT rc.conv_id, rc.room_id, rc.conv_name_id, rc.price_per_one,
           rc.amount, rc.start_date, cd.conv_name
    FROM public.room_conveniences rc
    INNER JOIN public.conveniences_dict cd ON rc.conv_name_id = cd.conv_name_id
    WHERE rc.room_id = room_id_in;
$$ LANGUAGE sql;

-- =================================================================================================
-- Раздел 2: Симметричное внутреннее соединение с условием (по датам)
-- =================================================================================================

-- Запрос 3: Статусы номеров на период
-- Описание: Получение статусов доступности номеров на указанный период дат.
-- Примечание: Реализация взята из файла schema.sql как более полная и корректная.
--роль: сотрудник отеля
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
$function$;


-- Запрос 4: Ежедневные счета отеля
-- Описание: Получение ежедневных счетов для отеля. Использует представление v_daily_invoices.
--роль: сотрудник отеля
CREATE OR REPLACE FUNCTION get_daily_invoices_by_hotel(hotel_id_in integer)
RETURNS TABLE(
    invoice_number character varying,
    booking_number character varying,
    total_amount numeric,
    issue_date date,
    is_paid boolean
) AS $$
    SELECT v.invoice_number, v.booking_number, v.total_amount,
           v.issue_date, v.is_paid
    FROM public.v_daily_invoices v
    WHERE v.hotel_id = hotel_id_in
    ORDER BY v.issue_date DESC, v.booking_number DESC;
$$ LANGUAGE sql;

-- =================================================================================================
-- Раздел 3: Симметричное внутреннее соединение без условия -> ПРЕОБРАЗОВАНО В VIEW
-- =================================================================================================

-- Запрос 5: Все отели с городами
-- Описание: Получение полного списка всех отелей с информацией о городах.
-- Тип: Представление (VIEW).
--роль: администратор отеля
CREATE OR REPLACE VIEW v_all_hotels AS
    SELECT h.hotel_id, c.city_name AS hotel_city, h.hotel_address, h.hotel_city AS city_id
    FROM hotel h
    INNER JOIN cities c ON h.hotel_city = c.city_id;

-- Запрос 6: Все пользователи системы
-- Описание: Получение списка всех пользователей с их ролями и статусами.
-- Тип: Представление (VIEW).
--роль: администратор отеля
CREATE OR REPLACE VIEW v_all_users AS
    SELECT u.employee_id AS user_id, u.username, r.rolname AS role_name, u.hotel_id,
           r_main.rolcanlogin = false AS user_locked
    FROM public.users u
    INNER JOIN pg_roles r_main ON r_main.rolname = u.username
    INNER JOIN pg_roles r ON pg_has_role(u.username, r.oid, 'member')
                          AND r.rolname <> u.username
    WHERE r.rolname != 'admin_role'
    ORDER BY u.username;

-- Запрос 7: Номера с типами и отелями
-- Описание: Получение всех номеров со связанной информацией о типах и отелях.
-- Тип: Представление (VIEW).
--роль: администратор отеля
CREATE OR REPLACE VIEW v_rooms_with_types_and_hotels AS
    SELECT h.hotel_address, hr.room_number, tor.room_type_name,
           hr.max_people, hr.price_per_person
    FROM hotel_rooms hr
    INNER JOIN hotel h ON hr.hotel_id = h.hotel_id
    INNER JOIN types_of_room tor ON hr.type_of_room_id = tor.room_type_id;

-- =================================================================================================
-- Раздел 4: Левое внешнее соединение
-- =================================================================================================

-- Запрос 8: Текущие статусы номеров
-- Описание: Получение текущего статуса всех номеров (включая свободные).
-- Тип: Представление (VIEW).
--роль: сотрудник отеля
CREATE OR REPLACE VIEW public.v_current_room_statuses AS
    WITH current_occupancy AS (
        SELECT th.room_id, sum(th.occupied_space) AS current_occupied_space
        FROM tenants_history th
        WHERE th.check_in_status = 'занят'::booking_status
          AND CURRENT_DATE >= th.check_in_date
          AND CURRENT_DATE < (th.check_in_date + th.amount_of_nights)
        GROUP BY th.room_id
    )
    SELECT hr.hotel_id, hr.room_id, hr.room_number, tor.room_type_name,
           hr.max_people, hr.price_per_person,
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

-- Запрос 9: Все номера с удобствами
-- Описание: Получение всех номеров включая те, у которых нет удобств.
-- Тип: Представление (VIEW).
--роль: администратор отеля
CREATE OR REPLACE VIEW v_all_rooms_with_conveniences AS
    SELECT hr.room_number, cd.conv_name, rc.price_per_one
    FROM hotel_rooms hr
    LEFT JOIN room_conveniences rc ON hr.room_id = rc.room_id
    LEFT JOIN conveniences_dict cd ON rc.conv_name_id = cd.conv_name_id;

-- =================================================================================================
-- Раздел 5: Правое внешнее соединение
-- =================================================================================================

-- Запрос 10: Все удобства с номерами
-- Описание: Получение всех удобств включая те, которые не назначены номерам.
-- Тип: Представление (VIEW).
--роль: администратор отеля
CREATE OR REPLACE VIEW v_all_conveniences_with_rooms AS
    SELECT cd.conv_name, hr.room_number
    FROM hotel_rooms hr
    LEFT JOIN room_conveniences rc ON hr.room_id = rc.room_id
    RIGHT JOIN conveniences_dict cd ON rc.conv_name_id = cd.conv_name_id;

-- =================================================================================================
-- Раздел 6: Сложные отчеты и аналитика (с рефакторингом)
-- =================================================================================================

-- Запрос 11: Детализированный отчет о доходах
-- Описание: Детальный анализ доходов по номерам с разделением на проживание и услуги. +
--роль: владелец отеля

CREATE OR REPLACE FUNCTION get_detailed_income_report()
RETURNS TABLE(
    hotel_address varchar,
    room_number integer,
    base_room_income numeric,
    conveniences_income numeric,
    services_income numeric,
    total_income numeric
) AS $$
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
$$ LANGUAGE plpgsql;

-- 7. Итоговый запрос без условия
-- Запрос 12: Сводка по доходам отелей
-- Описание: Общая сводка по доходам всех отелей без дополнительных условий
-- Тип: Итоговый запрос без условия
--роль: владелец отелей
CREATE OR REPLACE FUNCTION get_hotel_income_summary()
RETURNS TABLE(
    hotel_address varchar,
    base_room_income numeric,
    conveniences_income numeric,
    services_income numeric,
    total_income numeric,
    total_bookings bigint
) AS $$
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
$$ LANGUAGE plpgsql;

-- 8. Итоговый запрос с итоговыми данными "всего", "в том числе"
-- Запрос 13: Разбивка доходов по категориям
-- Описание: Детальная разбивка доходов с показателями "всего" и "в том числе" по категориям
-- Тип: Итоговый запрос с итоговыми данными "всего", "в том числе"
--роль: администратор отеля, владелец отеля
CREATE OR REPLACE FUNCTION get_income_breakdown_by_category()
RETURNS TABLE(
    category_type varchar,
    subcategory varchar,
    income numeric,
    percentage numeric
) AS $$
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
$$ LANGUAGE plpgsql;

-- 9. Итоговые запросы с условием на данные
-- Запрос 14: Доходы по городам с фильтром
-- Описание: Анализ доходов по городам с фильтрацией по шаблону названия города
-- Тип: Итоговые запросы с условием на данные (по маске)
--роль: Владелец отелей
CREATE OR REPLACE FUNCTION get_income_by_city_pattern(city_pattern varchar)
RETURNS TABLE(
    city_name varchar,
    base_room_income numeric,
    conveniences_income numeric,
    services_income numeric,
    total_income numeric,
    average_stay_duration numeric
) AS $$
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
$$ LANGUAGE plpgsql;

-- Запрос 15: Высокодоходные номера
-- Описание: Поиск номеров с доходом выше заданного порога
-- Тип: Итоговые запросы с условием на данные (по значению)
--роль: администратор отеля, владелец отеля
CREATE OR REPLACE FUNCTION get_high_income_rooms(total_income_threshold numeric)
RETURNS TABLE(
    room_number integer,
    room_type varchar,
    base_income numeric,
    conveniences_income numeric,
    services_income numeric,
    total_income numeric,
    occupancy_rate numeric
) AS $$
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
$$ LANGUAGE plpgsql;

-- 10. Итоговый запрос с условием на группы
-- Запрос 16: Премиальные услуги
-- Описание: Анализ услуг с минимальным количеством использования и доходом
-- Тип: Итоговый запрос с условием на группы
--роль: администратор отеля, владалец отелей
CREATE OR REPLACE FUNCTION get_premium_services_usage(min_usage_count integer, min_income numeric)
RETURNS TABLE(
    service_name varchar,
    usage_count bigint,
    total_income numeric,
    avg_income_per_use numeric
) AS $$
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
$$ LANGUAGE plpgsql;


-- 11. Итоговый запрос с условием на данные и на группы
-- Запрос 17: Анализ эффективности номеров
-- Описание: Комплексный анализ номеров с условиями на данные и группы
-- Тип: Итоговый запрос с условием на данные и на группы
--роль: администратор отеля, владалец отелей
CREATE OR REPLACE FUNCTION get_room_efficiency_analysis(min_occupancy_rate numeric, min_total_income numeric)
RETURNS TABLE(
    room_number integer,
    room_type varchar,
    base_income numeric,
    conveniences_income numeric,
    services_income numeric,
    total_income numeric,
    occupancy_rate numeric,
    efficiency_rating text
) AS $$
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
$$ LANGUAGE plpgsql;


-- 12. Запрос на запросе по принципу итогового запроса
-- Запрос 18: Тренды доходов по месяцам
-- Описание: Анализ месячных трендов доходов с расчетом роста
-- Тип: Запрос на запросе по принципу итогового запроса
--роль: администратор отеля, владелец отеля

LAG - оконная функция в SQL, которая позволяет извлекать значение столбца из предыдущей строки в результирующем наборе.

CREATE OR REPLACE FUNCTION get_monthly_income_trend()
RETURNS TABLE(
    year_month varchar,
    base_room_income numeric,
    conveniences_income numeric,
    services_income numeric,
    total_income numeric,
    income_growth numeric
) AS $$
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
$$ LANGUAGE plpgsql;


-- 13. Запрос с использованием объединения
-- Запрос 19: Отчет по всем источникам доходов
-- Тип: Запрос с использованием объединения
--роль: администратор отеля, владелец отеля

CREATE OR REPLACE FUNCTION get_combined_income_sources()
RETURNS TABLE(
    income_source varchar,
    source_type varchar,
    amount numeric
) AS $$
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
$$ LANGUAGE plpgsql;

-- 14. ЗАПРОСЫ С ПОДЗАПРОСАМИ
--
-- пояснение по KPI, нужно будет добавить в пз
-- KPI отеля (Key Performance Indicators) — это ключевые показатели эффективности, которые помогают измерить и
-- оценить качество работы гостиницы, достичь стратегических целей и повысить прибыль.
--
-- Запрос 20: Измерение KPI отелей
-- Тип: Запросы с подзапросами (с использованием IN)
--роль: администратор отеля, владелец отеля

CREATE OR REPLACE FUNCTION get_hotel_kpi()
RETURNS TABLE(
    metric_name varchar,
    metric_value numeric,
    metric_unit varchar
) AS $$
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
$$ LANGUAGE plpgsql;

-- Запрос 21: Поиск номеров, которые никогда не бронировались
-- Тип: Запросы с подзапросами (с использованием NOT IN)
--роль: администратор отеля
CREATE OR REPLACE FUNCTION get_rooms_never_booked()
RETURNS TABLE(
    room_number integer,
    room_type varchar,
    max_people integer,
    price_per_person numeric
) AS $$
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
$$ LANGUAGE plpgsql;

-- аналогичный запрос можно использовать в будущем, например, для определения бонусов, скидок в зависимости от категории клиента, вариант развития на будущее
-- Запрос 22: Разбиение клиентов по категориям в зависимости от количества потраченных денег
-- Тип: Запросы с подзапросами (с использованием CASE)
--роль: администратор отеля, владелец отеля
CREATE OR REPLACE FUNCTION get_customer_segmentation()
RETURNS TABLE(
    customer_segment varchar,
    customer_count bigint,
    avg_base_spent numeric,
    avg_conveniences_spent numeric,
    avg_services_spent numeric,
    avg_total_spent numeric
) AS $$
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
$$ LANGUAGE plpgsql;


-- Запрос 23: Расчет стоимости бронирования
-- Тип: Запросы с подзапросами (операциями над итоговыми данными)
--роль: сотрудник отеля
CREATE OR REPLACE FUNCTION calculate_total_cost_for_booking(booking_number_in character varying)
RETURNS numeric AS $$
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
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION public.count_price_for_room_without_conveniences(price_per_person numeric, occupied_space integer, can_be_split boolean, max_people integer, amount_of_nights integer)
RETURNS numeric
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN price_per_person *
    (occupied_space * can_be_split::int +
     max_people * (1 - can_be_split::int)) * amount_of_nights
END;
$$;