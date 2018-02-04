CREATE TABLE files.files (
  file_uuid               UUID NOT NULL,
  user_uuid               UUID NOT NULL,
  file_name               VARCHAR,
  file_size               NUMERIC,
  file_status             VARCHAR(20),
  deleted                 BOOLEAN,
  file_link               VARCHAR,
  file_last_modified_date TIMESTAMP,
  PRIMARY KEY (file_uuid)
);

create index filesUserIdx on files.files (user_uuid);
create index filesFilesIdx on files.files (file_uuid);
