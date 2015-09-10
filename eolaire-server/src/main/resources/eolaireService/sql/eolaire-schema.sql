
--
-- Tables
--

CREATE TABLE entity_type (
  id                INTEGER PRIMARY KEY,
  name              CHAR(32) NOT NULL,
  CONSTRAINT uq_entity_type_name UNIQUE (name)
);

CREATE TABLE item (
  id                INTEGER PRIMARY KEY,
  name              VARCHAR(1024) NOT NULL,
  type_id           INTEGER NOT NULL,
  CONSTRAINT fk_item_type FOREIGN KEY (type_id) REFERENCES entity_type(id) ON DELETE CASCADE
);

CREATE TABLE item_relation (
  lhs               INTEGER NOT NULL,
  rhs               INTEGER NOT NULL,
  type_id           INTEGER NOT NULL,
  metadata          BINARY,
  CONSTRAINT pk_item_relation PRIMARY KEY (lhs, rhs, type_id),
  CONSTRAINT fk_item_relation_lhs FOREIGN KEY (lhs) REFERENCES item(id) ON DELETE CASCADE,
  CONSTRAINT fk_item_relation_rhs FOREIGN KEY (rhs) REFERENCES item(id) ON DELETE CASCADE,
  CONSTRAINT fk_item_relation_type FOREIGN KEY (type_id) REFERENCES entity_type(id) ON DELETE CASCADE
);

CREATE TABLE item_profile (
  item_id           INTEGER NOT NULL,
  description       TEXT,
  date_created      DATETIME NOT NULL,
  date_updated      DATETIME NOT NULL,
  flags             INTEGER NOT NULL,
  metadata          BINARY,
  CONSTRAINT pk_item_profile PRIMARY KEY (item_id),
  CONSTRAINT fk_item_profile FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE
);

--
-- Sequences
--

CREATE SEQUENCE seq_entity_type       START WITH 100;
CREATE SEQUENCE seq_item              START WITH 1000;


--
-- Indexes
--

CREATE INDEX idx_item_typename ON item (type_id, name);

CREATE INDEX idx_item_relation_type_rhs ON item_relation (type_id, rhs);

CREATE INDEX idx_item_profile_updated ON item_profile (date_updated);
