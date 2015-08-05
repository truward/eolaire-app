-- Fixture Data
-- NOTE: There should be no zero values assigned to 'id' fields.

INSERT INTO entity_type (id, name) VALUES (1, 'author');
INSERT INTO entity_type (id, name) VALUES (2, 'language');
INSERT INTO entity_type (id, name) VALUES (3, 'person');
INSERT INTO entity_type (id, name) VALUES (5, 'book');

INSERT INTO item (id, name, type_id) VALUES (150, 'en', 2);
INSERT INTO item (id, name, type_id) VALUES (151, 'ru', 2);
INSERT INTO item (id, name, type_id) VALUES (1000, 'Arkady Strugatsky', 3);
INSERT INTO item (id, name, type_id) VALUES (1001, 'Boris Strugatsky', 3);
INSERT INTO item (id, name, type_id) VALUES (1100, 'Far Rainbow', 5);

-- Far Rainbow -> Arkady Strugatsky (author)
INSERT INTO item_relation (lhs, rhs, type_id) VALUES (1100, 1000, 1);
-- Far Rainbow -> Boris Strugatsky (author)
INSERT INTO item_relation (lhs, rhs, type_id) VALUES (1100, 1001, 1);
-- Far Rainbow -> ru (language)
INSERT INTO item_relation (lhs, rhs, type_id) VALUES (1100, 151, 2);

COMMIT;

--
-- EOF
--
