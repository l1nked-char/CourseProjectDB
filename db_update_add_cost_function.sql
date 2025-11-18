-- Новая функция для расчета стоимости бронирования
CREATE OR REPLACE FUNCTION public.calculate_booking_cost(
    room_id_in integer,
    check_in_date_in date,
    check_out_date_in date,
    people_count_in integer,
    occupy_entire_room_in boolean
)
RETURNS numeric AS $$
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
    IF nights_count <= 0 THEN
        RETURN 0;
    END IF;

    -- 2. Получаем базовую цену и вместимость номера
    SELECT price_per_person, max_people
    INTO base_price_per_person, max_people_in_room
    FROM public.hotel_rooms
    WHERE room_id = room_id_in;

    -- 3. Рассчитываем суточную стоимость удобств в номере
    SELECT COALESCE(SUM(price_per_one * amount), 0)
    INTO conveniences_cost_per_night
    FROM public.room_conveniences
    WHERE room_id = room_id_in AND start_date <= check_in_date_in; -- Учитываем только те, что действуют на момент заезда

    -- 4. Определяем, за скольких человек будет оплата
    IF occupy_entire_room_in THEN
        -- Если занимают весь номер, платят как за максимальное количество мест
        effective_people_count := max_people_in_room;
    ELSE
        -- Иначе, платят за фактическое количество заселяющихся
        effective_people_count := people_count_in;
    END IF;

    -- 5. Рассчитываем итоговую стоимость: (базовая цена за человека + общая цена удобств) * количество платящих людей * количество ночей
    total_cost := (base_price_per_person + conveniences_cost_per_night) * effective_people_count * nights_count;

    RETURN total_cost;
END;
$$ LANGUAGE plpgsql;
