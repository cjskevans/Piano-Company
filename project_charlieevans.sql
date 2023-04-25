--Charlie Evans 5537302

--2a
CREATE TABLE client(
    id INT,
    name VARCHAR(64) NOT NULL,
    email VARCHAR(64),
    PRIMARY KEY (id),
    CHECK (ID > 0)
);

CREATE TABLE piano(
  serial_number VARCHAR(64),
  make VARCHAR(64) NOT NULL,
  model VARCHAR(64) NOT NULL,
  year DATE NOT NULL,
  msrp INT NOT NULL,
  tradein_value INT, --if null, it is not a trade in
  PRIMARY KEY(serial_number)
);

CREATE TABLE acoustic_piano(
    serial_number VARCHAR(64),
    is_grand BOOLEAN NOT NULL, --A false value means it is upright
    PRIMARY KEY(serial_number),
    FOREIGN KEY(serial_number) references piano(serial_number) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE digital_piano(
  serial_number VARCHAR(64),
  PRIMARY KEY(serial_number),
  FOREIGN KEY(serial_number) references piano(serial_number) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE receipt(
    receipt_id VARCHAR(64),
    purchase_date DATE NOT NULL,
    amount_paid INT NOT NULL,
    salesperson VARCHAR(64) NOT NULL,
    client_id INT NOT NULL,
    PRIMARY KEY(receipt_id),
    FOREIGN KEY(client_id) references client(id),
    CHECK (purchase_date > '2017-01-01'), --Opening day for Duluth Grand Pianos
    CHECK (amount_paid > 0)
);

CREATE TABLE tune(
    tune_date DATE,
    serial_number VARCHAR(64),
    tuner_name VARCHAR(64),
    PRIMARY KEY(tune_date, serial_number),
    FOREIGN KEY(serial_number) references piano(serial_number) ON DELETE CASCADE ON UPDATE CASCADE,
    CHECK (tune_date > '2017-01-01') --Opening day for Duluth Grand Pianos
);

--2b
INSERT INTO client(id, name, email)
VALUES (123, 'John Doe', 'johndoe1@gmail.com'),
       (862, 'Charlie Evans', 'evan0712@d.umn.edu'),
       (634, 'Janice Smith', 'jansmith12@gmai.com'),
       (191, 'Kyle Jacobson', 'kj@gmail.com'),
       (434, 'Debbie Reynolds', 'debren@gmail.com'),
       (992, 'Cassandra Olsen', 'cassolsen@gmail.com'),
       (297, 'George Klein', 'gklein@yahoo.com');

INSERT INTO piano(serial_number, make, model, year, msrp, tradein_value)
VALUES ('1234-5A5H', 'Yamaha', 'TRX', '1949-1-1',22000, 12000),
       ('AV54-33NC', 'Yamaha', 'X11', '2011-1-1',88000, null),
       ('L3RC-6A4F', 'Fazioli', 'Beginner', '1992-1-1',1800, 1000),
       ('L3RT-225T', 'Fazioli', 'Beginner', '1998-1-1',1900, null),
       ('XT75-22N6', 'Fazioli', 'Professional', '1988-1-1',3400, 3000),
       ('M431-7HYT', 'Sauter', 'One', '2023-1-1',1200, 88000),
       ('M431-6G43', 'Kawai', 'Ultra 5', '1997-1-1',68500, 64000),
       ('M431-L452', 'Sauter', 'Two', '1967-1-1', 99000, 92000),
       ('M431-BN49', 'Kawai', 'Deluxe', '2008-1-1', 240000, 225000),
       ('BN56-7FG2', 'Kawai', 'Ultra 2', '2015-1-1',156000, null);

INSERT INTO acoustic_piano(serial_number, is_grand)
VALUES('1234-5A5H', true),
      ('AV54-33NC', true),
      ('L3RC-6A4F', false),
      ('L3RT-225T', false),
      ('M431-6G43', false),
      ('M431-L452', true);

INSERT INTO digital_piano(serial_number)
VALUES('XT75-22N6'),
      ('M431-7HYT'),
      ('BN56-7FG2'),
      ('M431-BN49');

--prepopulated receipt data
INSERT INTO receipt(receipt_id, purchase_date, amount_paid, salesperson, client_id)
VALUES('01245', '2019-03-07', 13000, 'James Richard', 862),
      ('01449', '2020-09-07', 2200, 'Dwight Eisenhower', 634),
      ('01891', '2021-01-05', 6700, 'John Rockefeller', 123),
      ('02009', '2021-02-27', 76000, 'Dwight Eisenhower', 634),
      ('02190', '2021-05-19', 9500, 'James Richard', 191),
      ('02854', '2021-06-12', 8820, 'James Richard', 862),
      ('03011', '2021-09-30', 57500, 'Jerry Neumann', 434),
      ('03554', '2021-12-22', 62000, 'John Rockefeller', 434);

INSERT INTO tune(tune_date, serial_number, tuner_name)
VALUES('2022-09-30','AV54-33NC', 'Santa Claus'),
      ('2023-03-09', 'L3RC-6A4F', 'Jerry Neumann'),
      ('2023-03-27', '1234-5A5H', 'Barack Obama'),
      ('2023-05-30', 'AV54-33NC', 'Santa Claus');

--We create only one index for the database, since all other lookups will be based on primary keys
--which are already indexed. This index is necessary since make and model are not primary keys of
--piano therefore they do not have an index, but they are looked up often in the program. This is a
--dense primary index
CREATE INDEX make_model_index ON piano(make, model);


