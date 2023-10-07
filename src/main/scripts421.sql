alter table student add constraint age_constraint check (age >= 16);
alter table student add constraint unique_name_constraint unique (name);
alter table student alter column name set not null;
alter table student alter column age set default 20;
alter table faculty add constraint unique_name_color unique (name,color);