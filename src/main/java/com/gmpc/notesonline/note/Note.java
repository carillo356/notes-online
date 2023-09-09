package com.gmpc.notesonline.note;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gmpc.notesonline.user.GMPCUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.util.Date;
@Entity
public class Note implements Serializable {
    @Id
    private String id;
    private String title;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    @ManyToOne
    private GMPCUser owner;

    public Note() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public GMPCUser getOwner() {
        return owner;
    }

    public void setOwner(GMPCUser owner) {
        this.owner = owner;
    }

}
