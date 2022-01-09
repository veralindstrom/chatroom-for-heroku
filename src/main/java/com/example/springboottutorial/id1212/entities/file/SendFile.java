package com.example.springboottutorial.id1212.entities.file;

import javax.persistence.*;
import java.io.File;
import java.io.Serializable;

@Entity
@Table(name="file")
public class SendFile implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer fileId;
    private File file;
    private String category;

    public Integer getfileId() {
        return fileId;
    }

    public void setfileId(Integer fileId) {
        this.fileId = fileId;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getCategory(){ return category; }

    public void setCategory(String category) { this.category = category; }
}
