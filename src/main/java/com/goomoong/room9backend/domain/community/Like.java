package com.goomoong.room9backend.domain.community;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Like {

    @Id @GeneratedValue
    private Long id;
}
