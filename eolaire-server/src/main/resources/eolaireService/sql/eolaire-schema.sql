
-- Entity Type
CREATE TABLE entity_type (
  id                INTEGER PRIMARY KEY,
  name              CHAR(32) NOT NULL,
  CONSTRAINT uq_entity_type_name UNIQUE (name)
);

CREATE TABLE item (
  id                INTEGER PRIMARY KEY,
  name              VARCHAR(1024) NOT NULL,
  type_id           INTEGER NOT NULL,
  CONSTRAINT fk_item_type FOREIGN KEY (type_id) REFERENCES entity_type(id)
);

CREATE TABLE item_relation (
  lhs               INTEGER NOT NULL,
  rhs               INTEGER NOT NULL,
  type_id           INTEGER NOT NULL,
  CONSTRAINT pk_item_relation PRIMARY KEY (lhs, rhs, type_id),
  CONSTRAINT fk_item_relation_lhs FOREIGN KEY (lhs) REFERENCES item(id),
  CONSTRAINT fk_item_relation_rhs FOREIGN KEY (rhs) REFERENCES item(id),
  CONSTRAINT fk_item_relation_type FOREIGN KEY (type_id) REFERENCES entity_type(id)
);

--
-- Sequences
--

CREATE SEQUENCE seq_entity_type       START WITH 100;
CREATE SEQUENCE seq_item              START WITH 100000;
