INSERT INTO Instructors (
first_name, last_name, specialty)
VALUES ('Lars',
        'Larsson',
        'Strength');

INSERT INTO Workouts (
workout_name, type, max_nr_of_participants, price_insek, instructor_id, deleted)
VALUES ('Benchpress90',
        'Pair',
        '2',
        '500.0',
        '1', false),

       ('Benchpress90',
        'Pair',
        '2',
        '500.0',
        '1', true);