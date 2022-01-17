package com.example.id1212.ChatroomApp.entities.file;

import org.springframework.data.repository.CrudRepository;

public interface DBFileRepository extends CrudRepository<DBFile, String> {
    DBFile findDBFileByFileId(String fileId);
}