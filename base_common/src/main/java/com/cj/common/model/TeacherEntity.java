package com.cj.common.model;
import com.cj.common.model.base.IBaseObject;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Author:chris - jason
 * Date:2019-05-10.
 * Package:com.cj.fun_orm
 */
@Entity
public class TeacherEntity implements IBaseObject {
    @Id
    private long id;
    private String name;
    private String subject;

    public TeacherEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
