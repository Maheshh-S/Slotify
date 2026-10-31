-- Seed events
INSERT INTO events (name, location, date) VALUES
('Tech Conference 2024', 'San Francisco, CA', '2024-06-15 09:00:00'),
('Developer Summit', 'New York, NY', '2024-07-20 10:00:00'),
('AI Workshop', 'Austin, TX', '2024-08-10 14:00:00');

-- Seed seats for event 1 (50 seats)
DO $$
DECLARE
    i INT;
BEGIN
    FOR i IN 1..50 LOOP
        INSERT INTO seats (event_id, seat_number, status, version)
        VALUES (1, 'A' || LPAD(i::TEXT, 2, '0'), 'AVAILABLE', 0);
    END LOOP;
END $$;

-- Seed seats for event 2 (50 seats)
DO $$
DECLARE
    i INT;
BEGIN
    FOR i IN 1..50 LOOP
        INSERT INTO seats (event_id, seat_number, status, version)
        VALUES (2, 'B' || LPAD(i::TEXT, 2, '0'), 'AVAILABLE', 0);
    END LOOP;
END $$;

-- Seed seats for event 3 (50 seats)
DO $$
DECLARE
    i INT;
BEGIN
    FOR i IN 1..50 LOOP
        INSERT INTO seats (event_id, seat_number, status, version)
        VALUES (3, 'C' || LPAD(i::TEXT, 2, '0'), 'AVAILABLE', 0);
    END LOOP;
END $$;