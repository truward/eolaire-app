-- Fixture Data
-- NOTE: There should be no zero values assigned to 'id' fields.

INSERT INTO entity_type (id, name) VALUES (1, 'author');
INSERT INTO entity_type (id, name) VALUES (2, 'language');
INSERT INTO entity_type (id, name) VALUES (3, 'person');
INSERT INTO entity_type (id, name) VALUES (5, 'book');
INSERT INTO entity_type (id, name) VALUES (6, 'movie');
INSERT INTO entity_type (id, name) VALUES (7, 'series');

INSERT INTO item (id, name, type_id) VALUES (150, 'en', 2);
INSERT INTO item (id, name, type_id) VALUES (151, 'ru', 2);
INSERT INTO item (id, name, type_id) VALUES (1000, 'Arkady Strugatsky', 3);
INSERT INTO item (id, name, type_id) VALUES (1001, 'Boris Strugatsky', 3);
INSERT INTO item (id, name, type_id) VALUES (1100, 'Far Rainbow', 5);
INSERT INTO item (id, name, type_id) VALUES (1200, 'Noon Universe', 7);

-- Far Rainbow -> Arkady Strugatsky (author)
INSERT INTO item_relation (lhs, rhs, type_id) VALUES (1100, 1000, 1);
-- Far Rainbow -> Boris Strugatsky (author)
INSERT INTO item_relation (lhs, rhs, type_id) VALUES (1100, 1001, 1);
-- Far Rainbow -> ru (language)
INSERT INTO item_relation (lhs, rhs, type_id) VALUES (1100, 151, 2);
-- Noon -> Far Rainbow (series)
INSERT INTO item_relation (lhs, rhs, type_id) VALUES (1200, 1100, 7);

-- Item Profile
INSERT INTO item_profile (item_id, description, date_created, date_updated, flags, metadata)
  VALUES (1000, 'Fine Author', '2015-05-27 21:42:54 UTC', '2015-05-28 11:17:05 UTC', 1, '');

INSERT INTO item_profile (item_id, description, date_created, date_updated, flags)
  VALUES (1001, 'Another Fine Author', '2015-05-28 09:13:19 UTC', '2015-05-28 11:16:34 UTC', 1);

COMMIT;

--
-- EOF
--
