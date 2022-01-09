package com.example.springboottutorial.id1212.entities.file;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface DBFileRepository extends CrudRepository<DBFile, String> {
    DBFile findDBFileByFileId(String fileId);
}