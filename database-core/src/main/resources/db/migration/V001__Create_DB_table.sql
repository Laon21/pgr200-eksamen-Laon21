create table if not exists TALKS (talk_ID SERIAL primary key, TITLE varchar not null, DESCRIPTION text, TOPIC text);

create table if not exists TRACKS (track_ID varchar(10) primary key);

create table if not exists DAYS (day date primary key);

create table if not exists TIME_SLOTS(start_time time, finish_time time, talk_id int4 references conference_talk,day date references days, track varchar(10) references TRACKS, primary key (start_time,talk_id));

create table if not exists CONFERENCE (day date references DAYS, ID int4 references TRACKS, primary key (day,ID));
