create table car(
	id integer primary key,
	made text,
	model text,
	price numeric
);

create table human(
	id integer primary key,
	name text,
	age integer,
	is_driver boolean,
	car_id integer not null references car (id)
);