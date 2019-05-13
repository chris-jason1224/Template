package com.cj.common.model;

import com.cj.common.model.base.IBaseObject;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.TargetIdProperty;
import io.objectbox.relation.ToMany;

/**
 * Author:chris - jason
 * Date:2019-05-10.
 * Package:com.cj.fun_orm
 */
@Entity
public class StudentEntity implements IBaseObject {

    @Id
    private long id;

    private String name;
    private int age;

    public StudentEntity(){}

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
