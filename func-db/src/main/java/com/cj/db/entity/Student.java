package com.cj.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by mayikang on 2018/10/12.
 */

@Entity
public class Student {


    @Id
    private Long id;

    @NotNull
    private String name;

    @Generated(hash = 225149174)
    public Student(Long id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    @Generated(hash = 1556870573)
    public Student() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }




}
