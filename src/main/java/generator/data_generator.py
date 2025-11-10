import psycopg2
from faker import Faker
import random
from datetime import date, timedelta, datetime

# Настройки подключения к БД
DB_CONFIG = {
    'host': '192.168.50.82',
    'database': 'CourseProject2',
    'user': 'postgres',
    'password': '1357920_egor',
    'port': '5432'
}

fake = Faker('ru_RU')

roles = ["owner_role", "admin_role", "employee_role"]

def connect_db():
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        return conn
    except Exception as e:
        print(f"Ошибка подключения: {e}")
        return None

def clear_database(conn):
    clear_sql = """
    -- Отключаем проверку внешних ключей для безопасного удаления
    SET session_replication_role = 'replica';
    
    TRUNCATE TABLE services_history, tenants_history, users, room_conveniences, hotel_services, hotel_rooms,
                    tenants, hotel, cities, social_statuses, conveniences_dict, services_dict, types_of_room RESTART IDENTITY CASCADE;
    
    -- Включаем проверку внешних ключей обратно
    SET session_replication_role = 'origin';
    """

    with conn.cursor() as cur:
        try:
            cur.execute("""
                        SELECT username FROM users;
                        """)
            users = [row[0] for row in cur.fetchall()]

            cur.execute(clear_sql)
            conn.commit()

            for i in range(len(users)):
                cur.execute("""
                            SELECT * FROM public.delete_user(%s)
                            """, (f'{users[i]}', ))
            cur.execute(clear_sql)
            conn.commit()
            print("База данных полностью очищена, последовательности сброшены")
        except Exception as e:
            conn.rollback()
            print(f"Ошибка при очистке базы данных: {e}")
            raise

def generate_cities(conn, count=10):
    cities = ['Москва', 'Санкт-Петербург', 'Новосибирск', 'Екатеринбург', 'Казань',
              'Нижний Новгород', 'Челябинск', 'Самара', 'Омск', 'Ростов-на-Дону']

    with conn.cursor() as cur:
        for city in cities[:count]:
            cur.execute("INSERT INTO cities (city_name) VALUES (%s) RETURNING city_id", (city,))
        conn.commit()
        print(f"Добавлено {len(cities[:count])} городов")

def generate_social_statuses(conn):
    """Генерация социальных статусов"""
    statuses = ['Студент', 'Работающий', 'Пенсионер', 'Безработный', 'Предприниматель']

    with conn.cursor() as cur:
        for status in statuses:
            cur.execute("INSERT INTO social_statuses (status_name) VALUES (%s)", (status,))
        conn.commit()
        print(f"Добавлено {len(statuses)} социальных статусов")

def generate_conveniences_dict(conn):
    """Генерация удобств"""
    conveniences = ['Wi-Fi', 'Телевизор', 'Кондиционер', 'Мини-бар', 'Сейф',
                    'Фен', 'Утюг', 'Кофеварка', 'Балкон', 'Вид на море']

    with conn.cursor() as cur:
        for conv in conveniences:
            cur.execute("INSERT INTO conveniences_dict (conv_name) VALUES (%s)", (conv,))
        conn.commit()
        print(f"Добавлено {len(conveniences)} удобств")

def generate_services_dict(conn):
    """Генерация услуг"""
    services = ['Завтрак', 'Обед', 'Ужин', 'SPA', 'Трансфер',
                'Экскурсия', 'Прачечная', 'Парковка', 'Бизнес-центр', 'Фитнес-центр']

    with conn.cursor() as cur:
        for service in services:
            cur.execute("INSERT INTO services_dict (service_name) VALUES (%s)", (service,))
        conn.commit()
        print(f"Добавлено {len(services)} услуг")

def generate_room_types(conn):
    """Генерация типов комнат"""
    room_types = [
        ('Стандарт',),
        ('Люкс',),
        ('Полулюкс',),
        ('Сьют',),
        ('Делюкс',),
        ('Эконом',),
        ('Бизнес',),
        ('Студия',)
    ]

    with conn.cursor() as cur:
        for room_type in room_types:
            cur.execute("INSERT INTO types_of_room (room_type_name) VALUES (%s)", room_type)
        conn.commit()
        print(f"Добавлено {len(room_types)} типов комнат")

def generate_hotels(conn, count=5):
    """Генерация отелей"""
    with conn.cursor() as cur:
        # Получаем список городов
        cur.execute("SELECT city_id FROM cities")
        city_ids = [row[0] for row in cur.fetchall()]

        hotels_data = []
        for i in range(count):
            city_id = random.choice(city_ids)
            address = fake.address().replace('\n', ', ')
            hotels_data.append((city_id, f"Отель {fake.company()}", address))

        for hotel in hotels_data:
            cur.execute("INSERT INTO hotel (hotel_city, hotel_address) VALUES (%s, %s) RETURNING hotel_id",
                        (hotel[0], hotel[2]))

        conn.commit()
        print(f"Добавлено {count} отелей")

def generate_hotel_rooms(conn, rooms_per_hotel=20):
    """Генерация номеров в отелях"""
    with conn.cursor() as cur:
        # Получаем список отелей и типов комнат
        cur.execute("SELECT hotel_id FROM hotel")
        hotel_ids = [row[0] for row in cur.fetchall()]

        cur.execute("SELECT room_type_id FROM types_of_room")
        room_type_ids = [row[0] for row in cur.fetchall()]

        room_number = 1
        for hotel_id in hotel_ids:
            for i in range(rooms_per_hotel):
                max_people = random.randint(1, 4)
                price_per_person = random.randint(1000, 5000)
                room_type_id = random.choice(room_type_ids)

                cur.execute("""
                            INSERT INTO hotel_rooms (hotel_id, max_people, price_per_person, room_number, type_of_room_id)
                            VALUES (%s, %s, %s, %s, %s)
                            """, (hotel_id, max_people, price_per_person, room_number, room_type_id))
                room_number += 1

        conn.commit()
        print(f"Добавлено {len(hotel_ids) * rooms_per_hotel} номеров")

def generate_room_conveniences(conn):
    """Генерация удобств в номерах"""
    with conn.cursor() as cur:
        cur.execute("SELECT room_id FROM hotel_rooms")
        room_ids = [row[0] for row in cur.fetchall()]

        cur.execute("SELECT conv_name_id FROM conveniences_dict")
        conv_ids = [row[0] for row in cur.fetchall()]

        for room_id in room_ids:
            # Добавляем 2-4 удобства в каждый номер
            num_conveniences = random.randint(2, 4)
            room_conveniences = random.sample(conv_ids, num_conveniences)

            for conv_id in room_conveniences:
                price = random.randint(100, 1000)
                amount = random.randint(1, 2)

                start_date = datetime.now().date()

                cur.execute("""
                            INSERT INTO room_conveniences (room_id, conv_name_id, price_per_one, amount, start_date)
                            VALUES (%s, %s, %s, %s, %s)
                            """, (room_id, conv_id, price, amount, start_date))

        conn.commit()
        print(f"Добавлены удобства для {len(room_ids)} номеров")

def generate_hotel_services(conn):
    """Генерация услуг отелей"""
    with conn.cursor() as cur:
        cur.execute("SELECT hotel_id FROM hotel")
        hotel_ids = [row[0] for row in cur.fetchall()]

        cur.execute("SELECT service_name_id FROM services_dict")
        service_ids = [row[0] for row in cur.fetchall()]

        for hotel_id in hotel_ids:
            # Добавляем 3-6 услуг для каждого отеля
            num_services = random.randint(3, 6)
            hotel_services = random.sample(service_ids, num_services)

            for service_id in hotel_services:
                start_date = date(2024, 1, 1)
                end_date = date(2024, 12, 31)
                price = random.randint(500, 3000)
                can_be_booked = random.choice([True, False])

                cur.execute("""
                            INSERT INTO hotel_services (hotel_id, service_name_id, start_of_period, end_of_period, price_per_one, can_be_booked)
                            VALUES (%s, %s, %s, %s, %s, %s)
                            """, (hotel_id, service_id, start_date, end_date, price, can_be_booked))

        conn.commit()
        print(f"Добавлены услуги для {len(hotel_ids)} отелей")

def generate_tenants(conn, count=100):
    """Генерация жильцов"""
    with conn.cursor() as cur:
        cur.execute("SELECT city_id FROM cities")
        city_ids = [row[0] for row in cur.fetchall()]

        cur.execute("SELECT status_id FROM social_statuses")
        status_ids = [row[0] for row in cur.fetchall()]

        document_types = ['Паспорт РФ', 'Паспорт иностранного гражданина',
                          'Свидетельство о рождении', 'Временное удостоверение', 'Загранпаспорт РФ']

        for _ in range(count):
            first_name = fake.first_name()
            last_name = fake.last_name()
            patronymic = fake.middle_name() if random.random() > 0.1 else None
            city_id = random.choice(city_ids)
            birth_date = fake.date_of_birth(minimum_age=18, maximum_age=80)
            social_status = random.choice(status_ids)
            series = random.randint(1000, 9999) if random.random() > 0.2 else None
            number = random.randint(100000, 999999) if random.random() > 0.2 else None
            doc_type = random.choice(document_types) if series and number else None
            email = fake.unique.email()

            cur.execute("""
                        INSERT INTO tenants (first_name, name, patronymic, city_id, birth_date,
                                             social_status, series, number, document_type, email)
                        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                        """, (first_name, last_name, patronymic, city_id, birth_date, social_status,
                              series, number, doc_type, email))

        conn.commit()
        print(f"Добавлено {count} жильцов")

def generate_tenants_history(conn, count=200):
    """Генерация истории бронирований без пересечений дат для одной комнаты"""
    with conn.cursor() as cur:
        # Получаем комнаты с информацией о максимальной вместимости
        cur.execute("""
                    SELECT room_id, max_people, hotel_id
                    FROM hotel_rooms
                    ORDER BY room_id
                    """)
        rooms_data = cur.fetchall()

        cur.execute("SELECT tenant_id FROM tenants")
        tenant_ids = [row[0] for row in cur.fetchall()]

        booking_statuses = ['занят', 'забронирован', 'снят с бронирования',
                            'выселились досрочно', 'не заселились']

        # Для каждой комнаты храним историю бронирований
        room_bookings = {}

        # Счетчик добавленных бронирований
        added_bookings = 0

        # Пока не наберем нужное количество бронирований
        while added_bookings < count and rooms_data:
            # Выбираем случайную комнату
            room_id, max_people, hotel_id = random.choice(rooms_data)

            # Если для этой комнаты еще нет записей в словаре, создаем пустой список
            if room_id not in room_bookings:
                room_bookings[room_id] = []

            # Выбираем случайного жильца
            tenant_id = random.choice(tenant_ids)

            # Определяем возможный диапазон дат для бронирования (последние 2 года)
            start_date = date.today()
            end_date = start_date + timedelta(days=730)  # 2 года назад

            # Пытаемся найти свободный интервал для бронирования
            max_attempts = 50
            for attempt in range(max_attempts):
                # Генерируем случайную дату заезда
                check_in_date = fake.date_between_dates(
                    date_start=start_date,
                    date_end=end_date + timedelta(days=1)
                )

                # Генерируем количество ночей (от 1 до 14)
                nights = random.randint(1, 14)
                check_out_date = check_in_date + timedelta(days=nights)

                # Проверяем, что дата выезда не в будущем (для реалистичности)
                if check_out_date > end_date:
                    check_out_date = end_date
                    nights = (check_out_date - check_in_date).days
                    if nights == 0:
                        continue

                # Проверяем, что дата бронирования раньше даты заезда
                booking_date = check_in_date - timedelta(days=random.randint(1, 30))

                # Проверяем пересечение с существующими бронированиями для этой комнаты
                has_overlap = False
                for existing_booking in room_bookings[room_id]:
                    existing_check_in = existing_booking[0]
                    existing_check_out = existing_booking[1]

                    # Проверяем пересечение: новое бронирование начинается ДО окончания существующего
                    # и заканчивается ПОСЛЕ начала существующего
                    if (check_in_date < existing_check_out and
                            check_out_date > existing_check_in):
                        has_overlap = True
                        break

                # Если пересечения нет, добавляем бронирование
                if not has_overlap:
                    # Выбираем случайный статус
                    status = random.choice(booking_statuses)

                    occupied_space = random.randint(1, min(max_people, 4))

                    can_be_split = random.choice([True, False])

                    booking_number = f"BN-{check_in_date.strftime('%Y%m%d')}-{room_id}"

                    cur.execute("""
                                INSERT INTO tenants_history
                                (room_id, booking_date, check_in_date, check_in_status,
                                 occupied_space, amount_of_nights, can_be_split, tenant_id, booking_number)
                                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                                """, (room_id, booking_date, check_in_date, status, occupied_space,
                                      nights, can_be_split, tenant_id, booking_number))

                    room_bookings[room_id].append((check_in_date, check_out_date))
                    added_bookings += 1
                    break

            if room_id in room_bookings and not room_bookings[room_id]:
                rooms_data = [room for room in rooms_data if room[0] != room_id]

        conn.commit()
        print(f"Добавлено {added_bookings} записей истории бронирований без пересечений дат")

        if added_bookings < count:
            print(f"Предупреждение: добавлено только {added_bookings} из {count} записей")

def generate_services_history(conn, count=300):
    """Генерация истории услуг"""
    with conn.cursor() as cur:
        cur.execute("SELECT booking_number FROM tenants_history")
        booking_numbers = [row[0] for row in cur.fetchall()]

        cur.execute("SELECT service_id FROM hotel_services")
        service_ids = [row[0] for row in cur.fetchall()]

        for _ in range(count):
            booking_number = random.choice(booking_numbers)
            service_id = random.choice(service_ids)
            amount = random.randint(1, 5)

            cur.execute("""
                        INSERT INTO services_history (history_id, service_id, amount)
                        VALUES (%s, %s, %s)
                        """, (booking_number, service_id, amount))

        conn.commit()
        print(f"Добавлено {count} записей истории услуг")

def generate_users(conn, count=10):
    """Генерация пользователей"""
    with conn.cursor() as cur:
        cur.execute("SELECT hotel_id FROM hotel")
        hotel_ids = [row[0] for row in cur.fetchall()]

        for i in range(count):
            hotel_id = random.choice(hotel_ids)
            username = f"user_{i+1}"
            password = "test123"
            user_role = random.choice(roles)

            cur.execute("""
                        SELECT * FROM public.create_user_with_role(%s, %s, %s, %s)
                        """, (username, password, user_role, hotel_id))

        conn.commit()
        print(f"Добавлено {count} пользователей")

def main():
    """Основная функция генерации данных"""
    conn = connect_db()
    if not conn:
        return

    try:
        clear_database(conn)
        print("Начало генерации данных...")
        # Генерация справочных данных
        generate_cities(conn)
        generate_social_statuses(conn)
        generate_conveniences_dict(conn)
        generate_services_dict(conn)
        generate_room_types(conn)

        # Генерация основных данных
        generate_hotels(conn, count=5)
        generate_hotel_rooms(conn, rooms_per_hotel=15)
        generate_room_conveniences(conn)
        generate_hotel_services(conn)
        generate_tenants(conn, count=150)
        generate_tenants_history(conn, count=1000)
        generate_services_history(conn, count=400)
        generate_users(conn, count=8)

        print("Генерация данных завершена успешно!")

    except Exception as e:
        print(f"Ошибка при генерации данных: {e}")
        conn.rollback()
    finally:
        conn.close()

if __name__ == "__main__":
    main()