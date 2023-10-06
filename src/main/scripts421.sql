alter table student add constraint age_constraint check (age >= 16);
alter table student add constraint unique_name_constraint unique not null (name);
alter table student add constraint default_age default 20;
alter table faculty add constraint unique_name_color unique (name,color);