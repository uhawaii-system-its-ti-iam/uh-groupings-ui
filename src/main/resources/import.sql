insert into unique_uh_number_v (UH_NUMBER) values (89999999);
insert into unique_uh_number_v (UH_NUMBER) values (10000001);
insert into unique_uh_number_v (UH_NUMBER) values (10000002);
insert into unique_uh_number_v (UH_NUMBER) values (10000003);
insert into unique_uh_number_v (UH_NUMBER) values (10000004);

insert into message (MSG_ID, MSG_ENABLED, MSG_TYPE_ID, MSG_TEXT) values (1, 'Y', 1, 'University of Hawaii Information Technology Services resides in a state-of-the-art, six-story, 74,000-square-foot facility located on the Manoa campus.');
insert into message (MSG_ID, MSG_ENABLED, MSG_TYPE_ID, MSG_TEXT) values (2, 'Y', 1, 'The access to this system is restricted.<br/>If you believe you should have access, <br/> please send an email to <a href=''mailto:duckart@hawaii.edu''>duckart@hawaii.edu</a>.');
insert into message (MSG_ID, MSG_ENABLED, MSG_TYPE_ID, MSG_TEXT) values (3, 'N', 1, 'For Future Use.');

insert into role(id, version, authority) values(1, 1, 'ROLE_ADMIN');
insert into role(id, version, authority) values(2, 1, 'ROLE_USER');

insert into type(id, version, description) values(1, 1, 'Bank');
insert into type(id, version, description) values(2, 1, 'Federal');
insert into type(id, version, description) values(3, 1, 'State');
insert into type(id, version, description) values(4, 1, 'UH');
