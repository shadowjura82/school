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
	car_id integer references car (id),
	constraint car_is_not_null not null (car_id)
	constraint fk_car foreign key car_id references
);